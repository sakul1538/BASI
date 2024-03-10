package com.example.tabnav_test;




import static com.example.tabnav_test.R.id.colay_main;
import static com.example.tabnav_test.R.id.config_tag;
import static com.example.tabnav_test.R.id.get_dummy_data;
import static com.example.tabnav_test.R.id.log_actions_data;
import static com.example.tabnav_test.R.id.log_data_view;
import static com.example.tabnav_test.R.id.projekt_create;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.text.BoringLayout;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Log.log_database_ops;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

public class Log_data extends AppCompatActivity
{
    RecyclerView rcv1;
    ImageButton log_filter_button;
    ImageButton log_action_data_button;
    ImageButton log_reload_dataset;
    EditText log_search_edit;

    TextView log_title;




    //Suchvariablen

    String date_from_value = null;
    String date_to_value = null;
    String text_value = null;
    String category_value = "None";

    static final String RROJ_NR = "0";

    //Akueller datensatz
    String[] recv_daten;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_data);

        Basic_funct bsf = new Basic_funct();

        rcv1 = findViewById(log_data_view);
        rcv1.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        log_action_data_button = findViewById(log_actions_data);

        log_filter_button  =findViewById(R.id.log_filter_button);
        log_reload_dataset  =findViewById(R.id.log_reload_dataset);
        log_title= findViewById(R.id.textView6);


        load_dataset();
        log_reload_dataset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                load_dataset();
            }
        });




        log_action_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_show_action_data_popup(view);

            }
        });


        log_filter_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder filter_dialog = new AlertDialog.Builder(Log_data.this);
                View promptsView = getLayoutInflater().inflate(R.layout.log_search_filter_dialog, null);
                filter_dialog.setView(promptsView);
                filter_dialog.setTitle("Einträge Filtern");


                TextView date_from = promptsView.findViewById(R.id.date_from);
                TextView date_to= promptsView.findViewById(R.id.date_to);
                EditText search_text_value = promptsView.findViewById(R.id.search_text_value);

                ImageButton refresh_date = promptsView.findViewById(R.id.refresh_date);

                CheckBox check_date = promptsView.findViewById(R.id.check_date);
                CheckBox check_text = promptsView.findViewById(R.id.check_text);
                CheckBox done_flag = promptsView.findViewById(R.id.done_flag);
                CheckBox undone_flag = promptsView.findViewById(R.id.undone_flag);
                CheckBox favorite_flag = promptsView.findViewById(R.id.favorite_flag);

                date_from.setText(bsf.date_refresh());
                date_to.setText(bsf.date_refresh());

                date_from.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        String[] pre_date = date_from.getText().toString().split("[.]");

                        int year = Integer.parseInt(pre_date[2]);
                        int month = Integer.parseInt(pre_date[1])-1;
                        int day = Integer.parseInt(pre_date[0]);
                        DatePickerDialog dpd = new DatePickerDialog(Log_data.this, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                            {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(i,i1 , i2);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                String date_value = dateFormat.format(calendar.getTime());

                                date_from.setText(date_value);
                            }
                        },year,month,day);
                        dpd.show();
                    }});

                date_to.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        String[] pre_date = date_to.getText().toString().split("[.]");

                        int year = Integer.parseInt(pre_date[2]);
                        int month = Integer.parseInt(pre_date[1])-1;
                        int day = Integer.parseInt(pre_date[0]);
                        DatePickerDialog dpd = new DatePickerDialog(Log_data.this, new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                            {

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(i,i1 , i2);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                String date_value = dateFormat.format(calendar.getTime());

                                date_to.setText(date_value);
                            }
                        },year,month,day);
                        dpd.show();
                    }});

                refresh_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        date_to.setText(bsf.date_refresh());

                    }
                });

                filter_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        projekt_ops projekt =new projekt_ops(getApplicationContext());
                        log_database_ops log_dbops = new log_database_ops(getApplicationContext());
                        String projekt_id = projekt.projekt_get_selected_id();

                        String where = "";
                        String [] where_args ={};
                        //Erstelle gültige SQLi anfragen

                        if(check_date.isChecked())
                        {
                            String date_from_value= date_from.getText().toString();
                            String date_to_value= date_to.getText().toString();
                            String[] temp = date_from_value.split("[.]");
                            date_from_value =temp[2]+"-"+temp[1]+"-"+temp[0];
                            temp = date_to_value.split("[.]");
                            date_to_value =temp[2]+"-"+temp[1]+"-"+temp[0];

                            where += "(DATE BETWEEN '"+date_from_value+ "' AND '"  +date_to_value+"') AND ";

                        }
                        if(check_text.isChecked())
                        {
                            where += " (NOTE LIKE '%"+ bsf.URLencode(search_text_value.getText().toString())+"%') AND ";
                        }


                        Boolean done= done_flag.isChecked();
                        Boolean undone =undone_flag.isChecked();

                        if(done && undone == true)
                        {
                            where += " (CHECK_FLAG='true' OR CHECK_FLAG='false') AND ";
                            }
                        else
                            {
                                if(done == true)
                                {
                                    where += " CHECK_FLAG='true' AND ";
                                }
                                    if(undone == true)
                                {
                                    where += " CHECK_FLAG='false' AND ";

                                }
                        }

                        if(favorite_flag.isChecked())
                        {

                           where += " FAV_FLAG='true' AND ";
                        }

                        try {
                            if(!where.equals(""))
                            {
                                where = where.substring(0,where.length()-4);
                                String[] search_output  = log_dbops.full_search(projekt_id,where);
                                if(!search_output[0].contains("NULL,NULL"))
                                {
                                    log_show_data_adapter lca = new log_show_data_adapter(search_output);
                                    rcv1.setAdapter(lca);
                                    rcv1.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    rcv1.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Log_data.this, "Keine Einträge gefunden!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(Log_data.this, "Keine oder Ungültige Argumente!", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e)
                        {
                            Toast.makeText(Log_data.this,e.getMessage().toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                });
                filter_dialog.show();
            }
        });
    }

    public void load_dataset() {

        projekt_ops projekt = new projekt_ops(getApplicationContext());
        log_database_ops log_dbops = new log_database_ops(getApplicationContext());

        recv_daten = log_dbops.get_entrys(projekt.projekt_get_selected_id());
        if (!recv_daten[0].contains("NULL,NULL"))
        {
            rcv1.setVisibility(View.VISIBLE);
            log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
            rcv1.setAdapter(lca);
        }
        else
        {
            rcv1.setVisibility(View.INVISIBLE);
        }

        log_title.setText(getResources().getText(R.string.log_title) + ":" + log_dbops.entry_count(projekt.projekt_get_selected_id()));
    }




    public String pack_search_values()
    {

        String search_filter_arg = "";
        search_filter_arg += text_value + ",";
        search_filter_arg += date_from_value + ",";
        search_filter_arg += date_to_value + ",";
        search_filter_arg += category_value;

        return search_filter_arg;

    }


    public void log_show_action_data_popup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                // Handle item selection
                projekt_ops projekt = new projekt_ops(getApplicationContext());
                log_database_ops log_dbops = new log_database_ops(getApplicationContext());
                String projekt_id = projekt.projekt_get_selected_id();


                switch (menuItem.getItemId())
                {
                    case R.id.delet_all_entrys:

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                        alertDialogBuilder.setTitle("Bestätige:");
                        alertDialogBuilder.setMessage("Alle Angezeigte Elemente aus der Datenbank entfernen?");

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {

                                if( log_dbops.delet_all(projekt.projekt_get_selected_id())== true)
                                {

                                    Toast.makeText(Log_data.this, "Alle Einträge gelöscht!", Toast.LENGTH_SHORT).show();
                                    load_dataset();

                                }
                                else
                                {
                                    Toast.makeText(Log_data.this, "Error Response: false", Toast.LENGTH_SHORT).show();
                                }

                            }

                        });

                        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Toast.makeText(v.getContext(), "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialogBuilder.show();


                       break;


                    case R.id.delet_check_true:

                        try {

                            int deletet = log_dbops.delet_all_done(projekt_id);
                            if(deletet>0)
                            {
                                Toast.makeText(Log_data.this, String.valueOf(deletet) +" Gelöscht!", Toast.LENGTH_SHORT).show();
                                load_dataset();
                            }
                            else
                            {
                                Toast.makeText(Log_data.this, "Error:\n  deletet<0", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e)
                        {
                            Toast.makeText(Log_data.this, "Error\n"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }

                        break;

                    case R.id.delet_all_exept_fav_flag:

                        try {
                            //String pre_entrys_count = String.valueOf(log_dbops.entry_count(projekt_id));
                            if(log_dbops.delet_all_exept_fav_flag(projekt_id,getApplicationContext()))
                            {
                                load_dataset();
                            }

                        } catch (Exception e)
                        {
                            Toast.makeText(Log_data.this, "Error\n"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }

                        break;

                    case get_dummy_data:

                        Context context = getApplicationContext();
                        Basic_funct bsf =new Basic_funct();

                        //Zufällige datums generierne
                        Random random = new Random();

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        String date ="";
                        String time ="";

                        int entry_value = 10;

                        for (int i=0; i<10;i++)
                        {
                            calendar.set(calendar.get(Calendar.YEAR),random.nextInt(11)+1,random.nextInt(30),random.nextInt(24),random.nextInt(60));

                            date = dateFormat.format(calendar.getTime());
                            time = timeFormat.format(calendar.getTime());

                            ContentValues data = new ContentValues();

                            data.put("ID",bsf.gen_UUID());
                            data.put("PROJEKT_NR",projekt.projekt_get_selected_id());
                            data.put("DATE",bsf.convert_date(date,"format_database"));
                            data.put("TIME",time);
                            data.put("NOTE",bsf.URLencode(random_word()));
                            data.put("CHECK_FLAG","false");
                            data.put("FAV_FLAG","false");
                            log_dbops.add(data);
                        }

                        load_dataset();
                        break;


                }
                return false;
            }
        });
    }

    public String random_word()
    {
        String[] germanWords = {
                "Haus", "Auto", "Baum", "Tisch", "Stuhl", "Kuchen", "Katze", "Hund", "Apfel", "Buch",
                "Schule", "Stadt", "Fluss", "Land", "Mensch", "Telefon", "Fenster", "Tür", "Wasser", "Feuer",
                "Wald", "Blume", "Garten", "Kaffee", "Tee", "Brot", "Milch", "Käse", "Himmel", "Sonne",
                "Mond", "Sterne", "Herz", "Hand", "Fuß", "Auge", "Nase", "Mund", "Ohr", "Zunge", "Zahn",
                "Kopf", "Arm", "Bein", "Bauch", "Rücken", "Schulter", "Hals", "Knie", "Ellenbogen", "Finger",
                "Zeh", "Hose", "Schuh", "Mantel", "Jacke", "Hemd", "Rock", "Kleid", "Schal", "Hut",
                "Handtasche", "Geldbeutel", "Schlüssel", "Uhr", "Brille", "Kamera", "Computer", "Laptop", "Drucker",
                "Bildschirm", "Maus", "Tastatur", "Lautsprecher", "Batterie", "Kabel", "Stecker", "Stift", "Bleistift",
                "Radiergummi", "Lineal", "Schere", "Klebstoff", "Papier", "Buchstabe", "Zahl", "Wort", "Satz", "Text",
                "Gedicht", "Geschichte", "Roman", "Zeitung", "Magazin", "Karte", "Plan", "Weg", "Brücke", "Straße"
        };
        Random r = new Random();
        return germanWords[r.nextInt(99)];

    }

    public void ms(String[] message) {
        for (int i = 0; i < message.length; i++)
        {
            Log.d("EINTRAG", message[i]);

        }
    }
}

