package com.example.tabnav_test;

import static android.content.Context.INPUT_METHOD_SERVICE;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Log_main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Log_main extends Fragment  implements TimePickerDialog.OnTimeSetListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    static final String RROJ_NR = "0";
    static final String ITHEM_CATEGORY = "ITHEM_CATEGORY";
    static final String ITHEM_FAV = "ITHEM_FAV";


    log_fav spinnerops;


    Spinner kategory;
    TextView date;
    TextView time;
    EditText note;

    ImageButton reset_note;
    Button reset_main;
    Button save_log;
    Button show_log;

    ImageButton add_kat;
    ImageButton fav_button;
    ImageButton fav_button_settings;
    ImageButton category_settings_button;



    AutoCompleteTextView acTextView;


    ImageButton date_refresh = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Log_main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Log.
     */
    // TODO: Rename and change types and number of parameters
    public static Log_main newInstance(String param1, String param2) {
        Log_main fragment = new Log_main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onResume()
    {
        super.onResume();

        refresh_fav();
        refresh_spinner();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        Basic_funct bsf = new Basic_funct();
        Context context = getContext();


        spinnerops = new log_fav(getContext());


        Calendar calendar = Calendar.getInstance();

        InputMethodManager inputMethodManager = (InputMethodManager)       getSystemService(INPUT_METHOD_SERVICE);





        //TextExit
        date = (TextView) view.findViewById(R.id.log_date);

        time = (TextView) view.findViewById(R.id.log_time);
        acTextView = (AutoCompleteTextView) view.findViewById(R.id.note);
        //Image Buttons
        date_refresh = (ImageButton) view.findViewById(R.id.imageButton8);

        //Button
        reset_note = (ImageButton) view.findViewById(R.id.note_reset);
        reset_main = (Button) view.findViewById(R.id.reset_all);
        save_log = (Button) view.findViewById(R.id.save_entry);


        add_kat = (ImageButton) view.findViewById(R.id.add_category);
        fav_button = (ImageButton) view.findViewById(R.id.fav_button);
        fav_button_settings = (ImageButton) view.findViewById(R.id.fav_button_settings);

        kategory = (Spinner) view.findViewById(R.id.spinner);
        category_settings_button  =(ImageButton) view.findViewById(R.id.category_settings_button);


        //Date&Time Refresh
        date.setText(bsf.date_refresh());
        time.setText(bsf.time_refresh());
        show_log = (Button) view.findViewById(R.id.show_log);

        refresh_fav();
        refresh_spinner();


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String hour = String.valueOf(i);
                        String min = String.valueOf(i1);
                        time.setText(hour + ":" + min);

                    }
                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                tpd.show();

            }

        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {

                        date.setText(bsf.convert_time_for_database(i,i1,i2));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();

            }
        });

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int resonse = (int) spinnerops.addOne(RROJ_NR, ITHEM_FAV, acTextView.getText().toString());

                if (resonse == -1) {

                    Toast.makeText(getContext(), "Eintrag konnte nicht erstellt werden!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "Neuer Eintrag wurde erstellt!" + resonse, Toast.LENGTH_LONG).show();

                    refresh_fav();

                }
            }
        });

        save_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String d = date.getText().toString();

                String t = time.getText().toString(); 
                String c = kategory.getSelectedItem().toString(); 
                String n = acTextView.getText().toString();
                n = bsf.URLencode(n);

                int response= (int) spinnerops.log_add_entry(RROJ_NR,d,t,c,n);

                Toast.makeText(getContext(),"Antwort_"+response,Toast.LENGTH_LONG).show();

                hideKeyboard(context,view);

            }
        });

        reset_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_field("notiz");
            }
        });

        reset_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_field("all");
            }
        });

        //Date&Time Refresh Button manuell
        date_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date.setText(bsf.date_refresh());
                time.setText(bsf.time_refresh());
            }
        });

        fav_button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fav_settings = new Intent(getContext(), log_conf_fav.class);
                startActivity(fav_settings);
            }
        });

        show_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fav_settings = new Intent(getContext(), Log_data.class);
                startActivity(fav_settings);
            }
        });
        category_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fav_settings = new Intent(getContext(), log_conf_categorys.class);
                startActivity(fav_settings);
            }
        });



        add_kat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.log_add_category, container, false);

                EditText log_add_category_name = (EditText) promptsView.findViewById(R.id.log_add_category_name);
                ImageButton  reset = (ImageButton) promptsView.findViewById(R.id.log_add_category_reset);


                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        log_add_category_name.setText("");
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Neue Kategorie");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try {
                            long resonse =  spinnerops.log_add_category(RROJ_NR,log_add_category_name.getText().toString());

                            if(resonse == -1)
                            {

                                Toast.makeText(getContext(),"Eintrag konnte nicht erstellt werden!",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(),"Neuer Eintrag wurde erstellt!"+resonse,Toast.LENGTH_LONG).show();
                                refresh_spinner();
                            }

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });



                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }
        });


        // Inflate the layout for this fragment

        return view;


    }

    private Object getSystemService(String inputMethodService)
    {
        return null;
    }

    private void refresh_spinner() {
        String[] kat_nativ = spinnerops.getallcategorys(RROJ_NR);

        // Log.d("Adresse",kat_nativ[0]);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, kat_nativ);

        //   ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,items);
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.kamera_spinner_list);

        kategory.setAdapter(spinnerArrayAdapter);
    }


    public void reset_field(String field_name) {

        switch (field_name) {

            case "notiz":

                acTextView.setText("");

                break;

            case "all":
                kategory.setSelection(0);
                acTextView.setText("");
                break;

            default:
                break;

        }


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }


    private void refresh_fav() {

        String[] favs = spinnerops.getalllogfav(RROJ_NR);
        ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, favs);
        acTextView.setThreshold(1);
        acTextView.setAdapter(favArrayAdapter);
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }






}

