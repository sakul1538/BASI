package com.example.tabnav_test;


import static com.example.tabnav_test.R.id.check_date;
import static com.example.tabnav_test.R.id.delet_check_true;

import static com.example.tabnav_test.R.id.log_actions_data;
import static com.example.tabnav_test.R.id.log_data_view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;

public class Log_data extends AppCompatActivity
{
    RecyclerView rcv1;
    ImageButton log_filter_button;
    ImageButton log_action_data_button;
    EditText log_search_edit;




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


        log_database_ops log = new log_database_ops(getApplicationContext());


        rcv1 = findViewById(log_data_view);

        log_action_data_button = findViewById(log_actions_data);

        log_filter_button  =findViewById(R.id.log_filter_button);

        log_action_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_show_action_data_popup(view);

            }
        });


        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        projekt_ops projekt =new projekt_ops(getApplicationContext());

        recv_daten= log.get_entrys(projekt.projekt_get_selected_id());
        log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
        rcv1.setAdapter(lca);

        rcv1.setLayoutManager(new LinearLayoutManager(Log_data.this));

        log_filter_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder filter_dialog = new AlertDialog.Builder(Log_data.this);
                View promptsView = getLayoutInflater().inflate(R.layout.log_search_filter_dialog, null);
                filter_dialog.setView(promptsView);
                filter_dialog.setTitle("x");


                TextView date_from = promptsView.findViewById(R.id.date_from);
                TextView date_to= promptsView.findViewById(R.id.date_to);
                EditText search_text_value = promptsView.findViewById(R.id.search_text_value);

                ImageButton refresh_date = promptsView.findViewById(R.id.refresh_date);

                CheckBox check_date = promptsView.findViewById(R.id.check_date);
                CheckBox check_text = promptsView.findViewById(R.id.check_text);
                CheckBox done_flag = promptsView.findViewById(R.id.done_flag);
                CheckBox undone_flag = promptsView.findViewById(R.id.undone_flag);
                CheckBox favorite_flag = promptsView.findViewById(R.id.favorite_flag);

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

                            where += "DATE BETWEEN "+date_from_value+ " AND "  +date_to_value;
                        }
                        if(check_text.isChecked())
                        {
                            where += " AND NOTIZ="+ bsf.URLencode(search_text_value.getText().toString());
                        }


                        Boolean done= done_flag.isChecked();
                        Boolean undone =undone_flag.isChecked();

                        if(done && undone == true)
                        {
                            where += " AND CHECK_FLAG=true OR CHECK_FLAG=false ";
                            }
                        else
                            {
                                if(done == true)
                                {
                                    where += " AND CHECK_FLAG=true";
                                }
                                    if(undone == true)
                                {
                                    where += "AND  CHECK_FLAG=false";

                                }
                        }

                        if(favorite_flag.isChecked())
                        {

                           where += " AND FAV_FLAG=true";
                        }
                        Log.d("BASI",where);
                    }
                });
                filter_dialog.show();
            }
        });
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
                log_fav log = new log_fav(getApplicationContext());

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

                                for (int n = 0; n < recv_daten.length; n++)
                                {
                                    String[] recv_daten_parts = recv_daten[n].split(",");

                                    int response =log.delet_log_entry(recv_daten_parts[0],RROJ_NR);

                                    if(response == 1)
                                    {
                                        Log.d("BASI:DELET ENTRY ID;",recv_daten_parts[0]);
                                    }
                                    else
                                    {
                                        Log.d("BASI:ERROR ENTRY ID;",recv_daten_parts[0]);

                                    }

                                }
                                recv_daten = log.getalllogdata(RROJ_NR);

                                log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
                                rcv1.setAdapter(lca);
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


                    case delet_check_true:


                        for (int i = 0; i < recv_daten.length; i++)
                        {
                            String[] recv_daten_parts = recv_daten[i].split(",");

                            int response =log.delet_log_entry_if_check_true(recv_daten_parts[0],RROJ_NR);

                        }

                        recv_daten = log.log_search_data(RROJ_NR, pack_search_values());

                        log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
                        rcv1.setAdapter(lca);

                        break;
                }
                return false;
            }
        });
    }

    public void ms(String[] message) {
        for (int i = 0; i < message.length; i++)
        {
            Log.d("EINTRAG", message[i]);

        }
    }
}

