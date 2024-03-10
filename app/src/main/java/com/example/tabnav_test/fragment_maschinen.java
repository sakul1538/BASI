package com.example.tabnav_test;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_maschinen#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO: 17.01.23  Log Einträge diffrenz am Richtigen Ort berechen (Eigene funktion schreiben)
// TODO: 17.01.23 datum mit forwar/backwart funkton ausstatten
public class fragment_maschinen extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    static final String RROJ_NR = "0";

    String error_msg = "";


    TextView m_date;
    TextView m_time;
    TextView m_counter_status;
    TextView m_counter_new_value;
    TextView m_entry_label;

    AutoCompleteTextView m_maschine_autocomplete_select;

    ImageButton m_refresh_timedate_button;
    ImageButton m_save_entry;
    ImageButton m_reset_form;
    ImageButton m_settings_button;
    ImageButton m_maschine_get_maschine_infos;
    ImageButton m_maschine_reset_field;
    ImageButton m_date_forward;
    ImageButton m_date_backward;
    ImageButton time_preset;

    LinearLayout m_time_and_date_frame;
    LinearLayout m_maschine_autocomplete_select_layout;

    RadioButton m_radiobutton_zaehlerstand;
    RadioButton m_radiobutton_addition;
    RadioGroup m_counter_type;


    RecyclerView m_log_entrys;

    m_log_entrys_adapter m_log_entrys_adapter;

    ArrayAdapter<String> maschinen_liste;
    String [] m_list;
    m_database_ops mdo;


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
    public static fragment_maschinen newInstance(String param1, String param2)
    {
        fragment_maschinen fragment = new fragment_maschinen();
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

        m_list =mdo.get_list_for_autocomplete(RROJ_NR,"NAME");

        maschinen_liste = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, m_list);
        m_maschine_autocomplete_select.setAdapter(maschinen_liste);

        try {
            String [] t =m_extract_nr_name(m_maschine_autocomplete_select.getText().toString());
            String maschi_id = mdo.check_maschine(t[0],t[1]);

            m_log_entrys_adapter.refresh_dataset(mdo.get_log_entrys_byid(maschi_id));
            refresh_entry_counter(maschi_id,"REFRESH"); //Einträge neu Zählen
            m_counter_status.setText("Zählerstand: "+ mdo.counter_getmax(maschi_id)); //Zählenstand Aktuallisieren

        }catch (Exception e)
        {
                Log.e("framgent_maschinen on_Resume:","Daten aktualisieren fehlgeschlagen");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mdo = new m_database_ops(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        View view = inflater.inflate(R.layout.fragment_maschinen, container, false);
        Context context = getContext();

        Basic_funct bsf = new Basic_funct();

        Calendar calendar = Calendar.getInstance();

        //TextViews
        m_date = view.findViewById(R.id.m_date);
        m_time = view. findViewById(R.id.m_time);
        m_counter_status = view. findViewById(R.id.m_counter_status);
        m_entry_label = view. findViewById(R.id.m_entry_label);


        //ImageButtons
        m_refresh_timedate_button = view.findViewById(R.id.m_refresh_datetime);
        m_settings_button = view.findViewById(R.id.m_settings);
        m_maschine_get_maschine_infos = view.findViewById(R.id.m_maschine_get_maschine_infos);
        m_maschine_reset_field = view.findViewById(R.id.m_maschine_reset_field);
        m_save_entry = view.findViewById(R.id.m_save_entry);
        m_reset_form = view.findViewById(R.id.m_reset_form);
        m_date_forward = view.findViewById(R.id.imageButton20);
        m_date_backward = view.findViewById(R.id.imageButton23);
        time_preset = view.findViewById(R.id.time_preset);

        //Layouts
        m_time_and_date_frame = view.findViewById(R.id.m_time_and_date_frame);
        m_maschine_autocomplete_select_layout = view.findViewById(R.id.m_maschine_autocomplete_select_layout);

        //TextEdit
        m_counter_new_value = view.findViewById(R.id.m_counter_new_value);
        m_maschine_autocomplete_select = view.findViewById(R.id.m_maschine_autocomplete_select);

        //RadioButtons
        m_radiobutton_addition = view.findViewById(R.id.m_radiobutton_addition);
        m_radiobutton_zaehlerstand = view.findViewById(R.id.m_radiobutton_zaehlerstand);
        m_counter_type = view.findViewById(R.id.m_counter_type);

        //RecyclerView
        m_log_entrys = view.findViewById(R.id.m_log_entrys);


        String [] dataset = {""};
        m_log_entrys_adapter= new m_log_entrys_adapter(dataset,fragment_maschinen.this);
        m_log_entrys.setAdapter(m_log_entrys_adapter);




        m_log_entrys.setLayoutManager(new LinearLayoutManager(getContext()));
        m_log_entrys_adapter.refresh_dataset(dataset);
        m_log_entrys.setVisibility(View.VISIBLE);


        m_list =mdo.get_list_for_autocomplete(RROJ_NR,"NAME");
        maschinen_liste = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, m_list);
        m_maschine_autocomplete_select.setAdapter(maschinen_liste);

        radiobutton_color_refresh();

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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1,i2);
                        SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
                        String datum = datumformat.format(calendar.getTime());

                        m_date.setText(datum);
                        m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.orange));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();

            }
        });

        m_date_forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
             m_date.setText(bsf.time_day_shift(m_date.getText().toString(),"",1));
             m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.orange));

            }
        });

        m_date_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                m_date.setText(bsf.time_day_shift(m_date.getText().toString(),"",-1));
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });



        m_time.setText(bsf.time_refresh());
        m_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {


                TimePickerDialog tpd = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,i,i1);
                        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm");
                        String time= zeitformat.format(calendar.getTime());

                        m_time.setText(time);
                        m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.orange));

                    }
                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                tpd.show();

            }

        });


        time_preset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               m_time.setText("17:00");
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        });


        m_refresh_timedate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                m_date.setText(bsf.date_refresh());
                m_time.setText(bsf.time_refresh());
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.grey));

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

        m_maschine_reset_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                m_maschine_autocomplete_select.setText("");
                m_counter_status.setText("Zählerstand:");
            }
        });

        m_maschine_autocomplete_select.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {


                String [] t =m_extract_nr_name(maschinen_liste.getItem(i));
                String id = mdo.check_maschine(t[0],t[1]);
                String counter = mdo.get_maschine_counter(id);
                m_counter_status.setText("Zählerstand: "+counter);

                refresh_entry_counter(id,"REFRESH");


                try
                {
                    String[]data = mdo.get_log_entrys_byid(id);


                    Log.d("BASI",data[0]);

                    m_log_entrys.setVisibility(View.VISIBLE);
                    m_log_entrys_adapter.refresh_dataset(data);
                    m_log_entrys_adapter.refresh_dataset(data);


                }catch (Exception e)
                {
                    Log.d("BASI_DATA","error");

                }
                keyboard_off(view);
                /*m_log_entrys_adapter= new m_log_entrys_adapter();
                m_log_entrys.setAdapter(m_log_entrys_adapter);
                m_log_entrys.setLayoutManager(new LinearLayoutManager(getContext()));*/

            }
        });

        m_counter_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                radiobutton_color_refresh();
            }
        });

        m_maschine_get_maschine_infos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    String maschine = m_maschine_autocomplete_select.getText().toString();
                    if(maschine !="")
                    {
                        String [] t =m_extract_nr_name(maschine);
                        String response = mdo.check_maschine(t[0],t[1]);
                        if( response != "null")
                        {

                            Basic_func_img bsfimg = new Basic_func_img();

                            View info_promptsView = getLayoutInflater().inflate(R.layout.m_info_dialog, null);

                            TextView m_info_name_value = info_promptsView.findViewById(R.id.m_info_name_value);
                            TextView m_info_nr_value = info_promptsView.findViewById(R.id.m_info_nr_value);
                            TextView m_info_created_date_value = info_promptsView.findViewById(R.id.m_info_created_date);
                            TextView m_info_created_time_value = info_promptsView.findViewById(R.id.m_info_created_time);
                            TextView m_info_calegory_value = info_promptsView.findViewById(R.id.m_info_category_value);
                            TextView m_info_note_value = info_promptsView.findViewById(R.id.m_info_note_value);
                            TextView m_info_counter_value = info_promptsView.findViewById(R.id.m_info_counter_value);
                            TextView m_info_entry_counter_value = info_promptsView.findViewById(R.id.m_info_entry_counter_value);
                            TextView m_info_last_entry_value = info_promptsView.findViewById(R.id.m_info_entry_last_value);

                            ImageView m_info_imageview = info_promptsView.findViewById(R.id.m_info_imageview);

                            ContentValues cv = mdo.get_maschine(response);

                            m_info_name_value.setText(cv.get("NAME").toString());
                            m_info_nr_value.setText(cv.get("NR").toString());
                            m_info_created_date_value.setText(bsf.convert_date(cv.get("DATE").toString(),"format_database_to_readable"));
                            m_info_created_time_value.setText(cv.get("TIME").toString());
                            m_info_calegory_value.setText(cv.get("CATEGORY").toString());
                            m_info_note_value.setText(cv.get("NOTE").toString());
                            m_info_counter_value.setText(mdo.get_maschine_counter(cv.get("ID").toString()));


                            m_info_entry_counter_value.setText(mdo.log_get(cv.get("ID").toString(),"COUNT_ENTRYS"));

                            String last_entry = bsf.convert_date(mdo.log_get(cv.get("ID").toString(),"LAST_ENTRY"),"format_database_to_readable");

                            m_info_last_entry_value.setText(last_entry);

                            Bitmap m_info_pic = bsfimg.Bitmap_adjust(Basic_funct.APP_DCIM_MASCHINEN +"/"+cv.get("PIC_SRC").toString(),0);

                            m_info_imageview.setImageBitmap(m_info_pic);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setView(info_promptsView);
                            alertDialogBuilder.setTitle("Info");

                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                        else
                        {
                            bsf.error_msg("Keine Maschine Gefunden oder Keine Gültige Eingabe!",context);
                        }
                    }
                    else
                    {
                        bsf.error_msg("Keine Maschine Gefunden oder Keine Gültige Eingabe!",context);
                    }
                }catch (Exception e)
                {
                    Log.d("BASI", e.getMessage());
                    bsf.error_msg("Keine Maschine Gefunden oder Keine Gültige Eingabe!",context);
                }


            }
        });

        m_save_entry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                error_msg = "";
                String[] values = new String[5];
                ContentValues  entry = new ContentValues();

                if(m_maschine_autocomplete_select.getText().toString() =="" || m_maschine_autocomplete_select.getText().toString().isEmpty())
                {
                    error_msg += "- Keine Maschine Ausgewählt!\n";

                }

                if(m_counter_new_value.getText().toString() =="" || m_counter_new_value.getText().toString().isEmpty())
                {
                    error_msg += "- Zählerstand nicht voranden! \n";
                }
                if(error_msg != "")
                {
                    bsf.error_msg(error_msg,context);
                }
                else
                {

                    //Datum
                    try {
                        String date = bsf.convert_date(m_date.getText().toString(), "format_database");
                        if (date == "null")
                        {
                            error_msg += "- Datumskonventierung fehlgeschlagen! (Datum = null)\n";

                        } else {

                            entry.put("DATE",date);
                        }

                    } catch (Exception e)
                    {
                        error_msg += "- Datumskonventierung fehlgeschlagen! (Exeption:" + e.getMessage() + ")\n";

                    }

                    //Zeit
                    entry.put("TIME",m_time.getText().toString());

                    //Maschine
                    String[] maschine_attr;
                    String masch_id = "null";

                    try {

                        maschine_attr = m_extract_nr_name(m_maschine_autocomplete_select.getText().toString());
                        masch_id = mdo.check_maschine(maschine_attr[0], maschine_attr[1]);

                        String counter_old = "null";

                        if (masch_id != "null")
                        {
                            entry.put("NAME",maschine_attr[0]);
                            entry.put("NR",maschine_attr[1]);
                            entry.put("MASCH_ID",masch_id);


                                if (m_radiobutton_zaehlerstand.isChecked())
                                {
                                    entry.put("COUNTER",m_counter_new_value.getText().toString());
                                }

                                if (m_radiobutton_addition.isChecked())
                                {

                                    Double int_counter_new = Double.valueOf(m_counter_new_value.getText().toString());
                                    Double int_counter_old = Double.valueOf(mdo.get_maschine_counter(masch_id));

                                    entry.put("COUNTER",String.valueOf(int_counter_old + int_counter_new));
                                }
                        }
                        else
                        {
                            error_msg += "- Maschinen in Dantenbank nicht gefunden! (masch_id:" + masch_id + ")\n";
                        }


                    } catch (Exception e)
                    {
                        error_msg += "- Maschine nicht gefunden! (Exception:" + e.getMessage() + ")\n";
                        masch_id = "null";
                    }
                }

                if(error_msg.equals(""))
                {

                    Double int_counter_new = Double.valueOf(entry.get("COUNTER").toString());
                    Double int_counter_old = Double.valueOf(mdo.get_maschine_counter(entry.get("MASCH_ID").toString()));

                    if (int_counter_old > int_counter_new)
                    {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Achtung!");
                        alertDialogBuilder.setMessage( "Zählerstand gleich oder niedriger!").setPositiveButton("Akzeptieren", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                entry.put("COUNTER",int_counter_new);
                                long response = mdo.add_log_entry(entry);
                                if(response != -1)
                                {
                                    Toast.makeText(context,"Eintrag erstellt!",Toast.LENGTH_SHORT).show();
                                    String[]data = mdo.get_log_entrys_byid((String) entry.get("MASCH_ID"));
                                    mdo.refresh_counter(entry.get("MASCH_ID").toString());
                                    m_log_entrys.setVisibility(View.VISIBLE);
                                    m_log_entrys_adapter.refresh_dataset(data);
                                    m_counter_new_value.setText("");

                                    refresh_entry_counter(entry.get("MASCH_ID").toString(),"REFRESH");

                                }
                                else
                                {
                                    bsf.error_msg("Eintrag konnte nicht erstellt werden!",context);
                                }
                            }
                        });

                        alertDialogBuilder.setNegativeButton("Wert Verwerfen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                m_counter_new_value.setText("");
                                dialogInterface.cancel();

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                    else //Wenn Alter zähler nicht grösser ist als neuer: Normaler Eintrag erzeugen, mit Couterrefresh
                    {
                        Double counter_new = Double.valueOf(entry.get("COUNTER").toString()); // FIXME: 20.01.23 Counter von Anfang an als Double speichern.
                        entry.put("COUNTER",counter_new);
                        long response = mdo.add_log_entry(entry);

                        if(response != -1)
                        {
                            Toast.makeText(context,"Eintrag erstellt!",Toast.LENGTH_SHORT).show();
                            String[]data = mdo.get_log_entrys_byid((String) entry.get("MASCH_ID"));

                            m_log_entrys.setVisibility(View.VISIBLE);
                            m_log_entrys_adapter.refresh_dataset(data);
                            mdo.refresh_counter(entry.get("MASCH_ID").toString());
                            m_counter_status.setText("Zählerstand: "+mdo.counter_getmax(entry.get("MASCH_ID").toString()));

                            m_counter_new_value.setText("");

                            refresh_entry_counter(entry.get("MASCH_ID").toString(),"REFRESH");

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        }
                        else
                        {
                            bsf.error_msg("Eintrag konnte nicht erstellt werden!",context);
                        }
                    }
                }
                else
                {
                    bsf.error_msg(error_msg,context);
                }
            }
        });

        m_reset_form.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                reset_form(0);
            }
        });

        return view;
    }

    private void radiobutton_color_refresh()
    {
        final int MARK_COLOR = R.color.orange;
        //RadioButtons farbsteuerung Zähler
        if(m_radiobutton_zaehlerstand.isChecked())
        {
            m_radiobutton_zaehlerstand.setTextColor(getResources().getColor(MARK_COLOR));
        }
        else
        {
            m_radiobutton_zaehlerstand.setTextColor(getResources().getColor(R.color.black));
        }

        //RadioButtons farbsteuerung addierung

        if(m_radiobutton_addition.isChecked())
        {
            m_radiobutton_addition.setTextColor(getResources().getColor(MARK_COLOR));
        }
        else
        {
            m_radiobutton_addition.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void  reset_form(int type)
    {
        Basic_func_img bsf = new Basic_func_img();
        switch(type)
        {
            case 0:
                m_date.setText(bsf.date_refresh());
                m_time.setText(bsf.time_refresh());
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.hellgrün));
                m_maschine_autocomplete_select.setText("");
                m_counter_new_value.setText("");
                m_log_entrys.setVisibility(View.GONE);
                m_counter_status.setText(getResources().getText(R.string.Maschinen_label_stundne));
                refresh_entry_counter("","RESET");

                break;
            case 1:

                m_date.setText(bsf.date_refresh());
                m_time.setText(bsf.time_refresh());
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.hellgrün));
                m_maschine_autocomplete_select.setText("");
                m_counter_new_value.setText("");
                m_counter_status.setText(getResources().getText(R.string.Maschinen_label_stundne));
                m_log_entrys.setVisibility(View.VISIBLE);
                break;

            default:

                m_date.setText(bsf.date_refresh());
                m_time.setText(bsf.time_refresh());
                m_time_and_date_frame.setBackgroundColor(getResources().getColor(R.color.hellgrün));
                m_maschine_autocomplete_select.setText("");
                m_counter_new_value.setText("");
                m_counter_status.setText(getResources().getText(R.string.Maschinen_label_stundne));
                m_log_entrys.setVisibility(View.GONE);

        }


    }


    private String[]  m_extract_nr_name(String value)
    {
        String[] t = new String[2];
        try
        {
            if(value =="" || value.isEmpty())
            {
                value="0[0]";
                value = value.replace("[","/");
                value = value.replace("]","/");
                t = value.split("/");
                t[0]="";
                t[1]="";

            }
            else
            {
                value = value.replace("[","/");
                value = value.replace("]","/");
                t  = value.split("/");
            }

        }catch (Exception e)
        {
            t[0]="";
            t[1]="";
        }

        return t;
    }

    private void keyboard_off(View view)
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public void refresh_entry_counter(String masch_id,String todo)
    {

        switch(todo)
        {

            case "REFRESH":

                m_entry_label.setText("Einräge: " +mdo.log_get(masch_id,"COUNT_ENTRYS"));

                break;
            case "RESET":

                m_entry_label.setText("Einräge: ");
                break;

        }


    }

}
