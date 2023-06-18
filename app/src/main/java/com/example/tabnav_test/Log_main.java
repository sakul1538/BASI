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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    ImageButton reset_main;
    ImageButton save_log;
    ImageButton show_log;

    ImageButton add_kat;
    ImageButton fav_button;
    ImageButton fav_button_settings;
    ImageButton category_settings_button;
    ImageButton date_refresh = null;
    ImageButton date_forward;
    ImageButton date_backward;



    LinearLayout date_time_bg;


    AutoCompleteTextView acTextView;




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

        //ImageButtons
        reset_note = (ImageButton) view.findViewById(R.id.ls_note_reset);
        reset_main = (ImageButton) view.findViewById(R.id.reset_all);
        save_log = (ImageButton) view.findViewById(R.id.save_entry);
        date_forward = (ImageButton) view.findViewById(R.id.imageButton26);
        date_backward = (ImageButton) view.findViewById(R.id.imageButton25);


        add_kat = (ImageButton) view.findViewById(R.id.add_category);
        fav_button = (ImageButton) view.findViewById(R.id.fav_button);
        fav_button_settings = (ImageButton) view.findViewById(R.id.fav_button_settings);

        kategory = (Spinner) view.findViewById(R.id.spinner);
        category_settings_button  =(ImageButton) view.findViewById(R.id.category_settings_button);


        //Date&Time Refresh
        date.setText(bsf.date_refresh_rev2());
        time.setText(bsf.time_refresh());
        show_log = (ImageButton) view.findViewById(R.id.show_log);

        //Layouts

        date_time_bg = (LinearLayout) view.findViewById(R.id.log_date_time_background);

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

                        date_time_bg.setBackgroundColor(getResources().getColor(R.color.orange));

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

                         Calendar calendar = Calendar.getInstance();
                         calendar.set(i,i1 , i2);

                         SimpleDateFormat   dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                         String date_value = dateFormat.format(calendar.getTime());

                         date.setText(date_value);
                        date_time_bg.setBackgroundColor(getResources().getColor(R.color.orange));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });

        date_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                date.setText(bsf.time_day_shift(date.getText().toString(),"",1));
                date_time_bg.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });
        date_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                date.setText(bsf.time_day_shift(date.getText().toString(),"",-1));
                date_time_bg.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });

        fav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(acTextView.getText().toString().equals("") == false)
                {
                    try {

                        int resonse = (int) spinnerops.addOne(RROJ_NR, ITHEM_FAV, acTextView.getText().toString());

                        if (resonse == -1)
                        {
                            bsf.error_msg("Eintrag konnte nicht erstellt werden!\n-> Interner Fehler oder schon vorhanden",context);
                        } else
                        {
                            bsf.succes_msg("Neuer Eintrag \""+acTextView.getText().toString()+"\" wurde erstellt!",context);
                            refresh_fav();
                        }
                    } catch (Exception e)
                    {
                        exmsg("050220231117",e);
                        bsf.error_msg("Fehler:"+e.getMessage().toString(),context);

                    }
                }
                else
                {
                    bsf.error_msg("Notiz ist leer!",context);
                }
            }
        });

        save_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    String d = bsf.convert_date(date.getText().toString(),"format_database");

                    String t = time.getText().toString();
                    String c = kategory.getSelectedItem().toString();
                    String n = acTextView.getText().toString();
                    n = bsf.URLencode(n);

                    int response= (int) spinnerops.log_add_entry(RROJ_NR,d,t,c,n);

                    Toast.makeText(getContext(),"Antwort_"+response,Toast.LENGTH_LONG).show();

                    hideKeyboard(context,view);


                } catch (Exception e)
                {
                    exmsg("050220231057",e);
                    bsf.error_msg("Es konnte kein Eintrag erstellt werden",context);
                }

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
            public void onClick(View view)
            {

                try {
                    date.setText(bsf.date_refresh_rev2());
                    time.setText(bsf.time_refresh());
                    date_time_bg.setBackgroundColor(getResources().getColor(R.color.hellgrün));
                } catch (Exception e)
                {
                    exmsg("050220231059",e);
                    bsf.error_msg("Datum aktuallisierung fehlgeschlagen",context);
                    e.printStackTrace();
                }
            }
        });

        fav_button_settings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    Intent fav_settings = new Intent(getContext(), log_conf_fav.class);
                    startActivity(fav_settings);
                } catch (Exception e)
                {
                    exmsg("050220231101",e);
                    bsf.error_msg("Fehler:"+e.getMessage().toString(),context);
                }
            }
        });

        show_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    Intent fav_settings = new Intent(getContext(), Log_data.class);
                    startActivity(fav_settings);

                } catch (Exception e)
                {
                    exmsg("050220231102",e);
                    bsf.error_msg("Fehler:"+e.getMessage().toString(),context);
                }
            }
        });
        category_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    Intent fav_settings = new Intent(getContext(), log_conf_categorys.class);
                    startActivity(fav_settings);
                } catch (Exception e)
                {
                    exmsg("050220231103",e);
                    bsf.error_msg("Fehler:"+e.getMessage().toString(),context);
                    e.printStackTrace();
                }
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
                                bsf.error_msg("Eintrag konnte nicht erstellt werden!" ,context);
                            }
                            else
                            {
                                bsf.succes_msg("Neuer Eintrag wurder erstellt!",context);
                                refresh_spinner();
                            }

                        }
                        catch (Exception e)
                        {
                            exmsg("050220231105",e);
                            bsf.error_msg("Fehler:"+e.getMessage().toString(),context);
                            e.printStackTrace();
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

    private void refresh_spinner()
    {

        try {
            String[] kat_nativ = spinnerops.getallcategorys(RROJ_NR);

            // Log.d("Adresse",kat_nativ[0]);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, kat_nativ);

            //   ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,items);
            //spinnerArrayAdapter.setDropDownViewResource(R.layout.kamera_spinner_list);

            kategory.setAdapter(spinnerArrayAdapter);
        } catch (Exception e)
        {
            exmsg("050220231133",e);
            Toast.makeText(getContext(), "050220231133 -> Aktuallisierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
        }
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


    private void refresh_fav()
    {

        try {
            String[] favs = spinnerops.getalllogfav(RROJ_NR);
            ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, favs);
            acTextView.setThreshold(1);
            acTextView.setAdapter(favArrayAdapter);
        } catch (Exception e)
        {
            exmsg("050220231135",e);
            Toast.makeText(getContext(), "050220231135 -> Aktuallisierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: Log_main ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }







}

