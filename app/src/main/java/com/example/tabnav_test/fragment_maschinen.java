package com.example.tabnav_test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_maschinen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_maschinen extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    static final String RROJ_NR = "0";

    TextView m_date;
    TextView m_time;

    ImageButton m_refresh_timedate_button;
    ImageButton m_settings_button;

    Spinner select_maschine;

    ImageButton m_save_entry;
    ImageButton m_reset_all;









    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_maschinen()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_maschinen.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_maschinen newInstance(String param1, String param2) {
        fragment_maschinen fragment = new fragment_maschinen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_maschinen, container, false);

        Basic_funct bsf = new Basic_funct();
        Context context = getContext();
        Calendar calendar = Calendar.getInstance();



        //TextViews
        m_date = (TextView)  view.findViewById(R.id.m_date);
        m_time = (TextView)  view. findViewById(R.id.m_time);


        //ImageButtons
        m_refresh_timedate_button =  (ImageButton) view.findViewById(R.id.m_refresh_datetime);
        m_settings_button = (ImageButton) view.findViewById(R.id.m_settings);




        m_date.setText(bsf.date_refresh());
        m_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


                DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {

                        m_date.setText(bsf.convert_time_for_database(i,i1,i2));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();

            }
        });


        m_time.setText(bsf.time_refresh());
        m_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String hour = String.valueOf(i);
                        String min = String.valueOf(i1);
                        m_time.setText(hour + ":" + min);

                    }
                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                tpd.show();

            }

        });

        m_refresh_timedate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                m_date.setText(bsf.date_refresh());
                m_time.setText(bsf.time_refresh());

            }
        });


        m_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent m_settings = new Intent(getContext(), m_conf_maschine.class);
                startActivity(m_settings);

            }
        });





        return view;


    }
}