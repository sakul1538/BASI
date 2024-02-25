package com.example.tabnav_test;

import static android.content.Context.INPUT_METHOD_SERVICE;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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

import com.example.tabnav_test.Log.log_database_ops;
import com.example.tabnav_test.config_favorite_strings.config_fav;
import com.example.tabnav_test.config_favorite_strings.config_fav_ops;
import com.example.tabnav_test.material.material_database_ops;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Log_main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Log_main extends Fragment
{

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
        date = view.findViewById(R.id.log_date);

        time = view.findViewById(R.id.log_time);
        acTextView = view.findViewById(R.id.note);
        //Image Buttons
        date_refresh = view.findViewById(R.id.imageButton8);

        //ImageButtons
        reset_note = view.findViewById(R.id.ls_note_reset);
        reset_main = view.findViewById(R.id.reset_all);
        save_log = view.findViewById(R.id.save_entry);
        date_forward = view.findViewById(R.id.imageButton26);
        date_backward = view.findViewById(R.id.imageButton25);


        fav_button = view.findViewById(R.id.fav_button);
        fav_button_settings = view.findViewById(R.id.fav_button_settings);



        //Date&Time Refresh
        date.setText(bsf.date_refresh());
        time.setText(bsf.time_refresh());
        show_log = view.findViewById(R.id.show_log);

        //Layouts

        date_time_bg = view.findViewById(R.id.log_date_time_background);


        acTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    refresh_fav();
                }
            }
        });

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
                config_fav_ops cfops = new config_fav_ops(getContext());
                cfops.add_favorite_string(acTextView.getText().toString());
                refresh_fav();
            }
        });

        save_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    projekt_ops projekt = new projekt_ops(context);
                    log_database_ops log_dbops = new log_database_ops(getContext());

                    ContentValues data = new ContentValues();

                    data.put("ID",bsf.gen_UUID());
                    data.put("PROJEKT_NR",projekt.projekt_get_selected_id());
                    data.put("DATE",bsf.convert_date(date.getText().toString(),"format_database"));
                    data.put("TIME",time.getText().toString());
                    data.put("NOTE",bsf.URLencode(acTextView.getText().toString()));
                    data.put("CHECK_FLAG","false");
                    data.put("FAV_FLAG","false");

                    if(log_dbops.add(data))
                    {

                        Toast.makeText(context, "Eintrag erstellt", Toast.LENGTH_SHORT).show();

                    }else
                    {
                        Toast.makeText(context, "Eintrag konnte nicht erstellt werden!", Toast.LENGTH_SHORT).show();

                    }

                } catch (Exception e)
                {
                    exmsg("050220231057",e);
                    bsf.error_msg("Es konnte kein Eintrag erstellt werden\n"+e.getMessage().toString(),context);
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
                    date.setText(bsf.date_refresh());
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
                    config_fav favorites = new config_fav(getContext());
                    favorites.show_dialog(container);
                    acTextView.clearFocus();

                   /* Intent fav_settings = new Intent(getContext(), log_conf_fav.class);
                    startActivity(fav_settings);*/
                } catch (Exception e)
                {
                    exmsg("050220231101",e);
                    bsf.error_msg("Fehler:"+ e.getMessage(),context);
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
                    bsf.error_msg("Fehler:"+ e.getMessage(),context);
                }
            }
        });
        // Inflate the layout for this fragment

        return view;


    }

    private Object getSystemService(String inputMethodService)
    {
        return null;
    }


    public void reset_field(String field_name) {

        switch (field_name) {

            case "notiz":
                acTextView.setText("");
                break;

            case "all":
                Basic_funct bsf = new Basic_funct();
                acTextView.setText("");
                date.setText(bsf.date_refresh());
                time.setText(bsf.time_refresh());

                break;

            default:
                break;

        }
    }

    private void refresh_fav()
    {
        try {
           config_fav_ops cfops = new config_fav_ops(getContext());
            ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cfops.favorite_strings_list(false));
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
        Log.e("Exception: Log_main ->","ID: "+msg+" Message:" + e.getMessage());
        e.printStackTrace();
    }

}

