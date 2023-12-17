package com.example.tabnav_test;

import static com.example.tabnav_test.R.id.check_fav_state;
import static com.example.tabnav_test.R.id.delet_check_true;
import static com.example.tabnav_test.R.id.erledigt_false;
import static com.example.tabnav_test.R.id.erledigt_true;
import static com.example.tabnav_test.R.id.log_actions_data;
import static com.example.tabnav_test.R.id.log_data_view;
import static com.example.tabnav_test.R.id.log_filter_refresh_date;
import static com.example.tabnav_test.R.id.log_search_button;
import static com.example.tabnav_test.R.id.log_search_field;
import static com.example.tabnav_test.R.id.log_search_filter_dialog_button;
import static com.example.tabnav_test.R.id.log_show_reset_button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Log_data extends AppCompatActivity
{

    RecyclerView rcv1;
    ImageButton log_search;
    ImageButton log_filter_reset;
    ImageButton log_search_filter_dialog;
    ImageButton log_action_data_button;
    EditText log_search_edit;

    Button filter_reset;


    RadioButton log_filter_radio_check_true;
    RadioButton log_filter_radio_check_false;
    CheckBox    log_filter_check_fav_state;


    //Suchvariablen

    String date_from_value = null;
    String date_to_value = null;
    String text_value = null;
    String category_value = "None";
    String check_state="None";
    String fav_state="None";
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
        Calendar calendar = Calendar.getInstance();
        log_fav spinnerops = new log_fav(getApplicationContext());


        rcv1 = (RecyclerView) findViewById(log_data_view);

        log_search = (ImageButton) findViewById(log_search_button);
        log_search_edit = (EditText) findViewById(log_search_field);
        log_filter_reset = (ImageButton) findViewById(log_show_reset_button);
        log_search_filter_dialog = (ImageButton) findViewById(log_search_filter_dialog_button);


        log_action_data_button = (ImageButton) findViewById(log_actions_data);

        log_action_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_show_action_data_popup(view);

            }
        });


        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        log_fav log_data = new log_fav(getApplicationContext());

        recv_daten= log_data.getalllogdata(RROJ_NR);

        log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
        rcv1.setAdapter(lca);


        rcv1.setLayoutManager(new LinearLayoutManager(Log_data.this));

        log_search_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_search_edit.setText(""); //Löschen des Suchfelder

            }
        });

        log_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_search_values();

                text_value = bsf.URLencode(log_search_edit.getText().toString());

                String search_string = pack_search_values();

                Toast.makeText(Log_data.this, search_string, Toast.LENGTH_LONG).show();

                recv_daten = log_data.log_search_data(RROJ_NR, search_string);

                // Toast.makeText(Log_data.this,t,Toast.LENGTH_SHORT).show();

                log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
                rcv1.setAdapter(lca);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        log_filter_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_search_values();

                log_search_edit.setText(""); //Löschen des Suchfelder

                recv_daten= log_data.getalllogdata(RROJ_NR);

                log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
                rcv1.setAdapter(lca);

                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        log_search_filter_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View promptsView = getLayoutInflater().inflate(R.layout.log_search_filter_dialog, null);

                ImageButton date_refresh = (ImageButton) promptsView.findViewById(log_filter_refresh_date);
                TextView date_from = (TextView) promptsView.findViewById(R.id.textView23);
                TextView date_to = (TextView) promptsView.findViewById(R.id.textView21);

                log_filter_radio_check_true =(RadioButton) promptsView.findViewById(erledigt_true);
                log_filter_radio_check_false=(RadioButton) promptsView.findViewById(erledigt_false);
                log_filter_check_fav_state =(CheckBox) promptsView.findViewById(check_fav_state);

                filter_reset =(Button) promptsView.findViewById(R.id.log_filter_reset);




                date_from.setText(bsf.date_refresh_database().toString());
                date_to.setText(bsf.date_refresh_database().toString());


                Spinner log_search_filter_category = (Spinner) promptsView.findViewById(R.id.log_search_category_field);

                filter_reset.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {


                        log_filter_radio_check_true.setChecked(false);
                        log_filter_radio_check_false.setChecked(false);
                        log_filter_check_fav_state.setChecked(false);
                        date_from.setText(bsf.date_refresh_database().toString());
                        date_to.setText(bsf.date_refresh_database().toString());
                        log_search_filter_category.setSelection(0);

                        String date_from_value = null;
                        String date_to_value = null;
                        String text_value = null;
                        String category_value = "None";
                        String check_state="None";
                        String fav_state="None";
                    }
                });

                log_filter_radio_check_true.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(log_filter_radio_check_true.isChecked() ==true)
                        {
                            check_state="TRUE";
                            Log.d("BASI: ",check_state);

                        }

                    }
                });

                log_filter_radio_check_false.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(log_filter_radio_check_false.isChecked() ==true)
                        {
                            check_state="FALSE";
                            Log.d("BASI: ",check_state);

                        }

                    }
                });

                log_filter_check_fav_state.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if(log_filter_check_fav_state.isChecked() ==true)
                        {
                            fav_state="TRUE";
                            Log.d("BASI: ",fav_state);

                        }

                        if(log_filter_check_fav_state.isChecked() ==false)
                        {
                            fav_state="FALSE";
                            Log.d("BASI: ",fav_state);

                        }


                    }
                });

                //Datum

                date_from.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog dpd = new DatePickerDialog(Log_data.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                String date = bsf.convert_time_for_database(i, i1, i2);
                                date_from.setText(date);
                                //date_to.setText(date);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        dpd.show();
                    }

                });


                date_to.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog dpd = new DatePickerDialog(Log_data.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                date_to.setText(bsf.convert_time_for_database(i, i1, i2));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        dpd.show();

                    }

                });


                date_refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        date_from.setText(bsf.date_refresh_database().toString());
                        date_to.setText(bsf.date_refresh_database().toString());

                    }
                });

                //Kategorie


                String items[] = spinnerops.getallcategorys();

                ArrayAdapter<String> log_category_items = new ArrayAdapter<String>(Log_data.this, android.R.layout.simple_dropdown_item_1line, items);
                log_search_filter_category.setAdapter(log_category_items);


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Log_data.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setTitle(R.string.log_search_filter_dialog_title);


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        text_value = log_search_edit.getText().toString();
                        date_from_value = date_from.getText().toString();
                        date_to_value = date_to.getText().toString();
                        category_value = log_search_filter_category.getSelectedItem().toString();


                        recv_daten = log_data.log_search_data(RROJ_NR, pack_search_values());


                        log_show_data_adapter lca = new log_show_data_adapter(recv_daten);
                        rcv1.setAdapter(lca);

                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);


                    }

                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });




                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }
        });


    }


    public void reset_search_values()
    {
        String date_from_value = null;
        String date_to_value = null;
        String text_value = null;
        String category_value = "None";
        String check_state="None";
        String fav_state="None";

    }

    public String pack_search_values() {

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

