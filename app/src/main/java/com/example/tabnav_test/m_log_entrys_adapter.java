package com.example.tabnav_test;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class m_log_entrys_adapter extends RecyclerView.Adapter<m_log_entrys_adapter.ViewHolder>
{

    private  ArrayList<String> datalist = new ArrayList<String>();
    private String[] dataset;
    m_database_ops mdo;
    Basic_funct bsf;
    Context context;

    fragment_maschinen fragmet_context;
    

    public m_log_entrys_adapter(String[] dataset, fragment_maschinen framgent_context)
    {

        if(dataset[0].equals("") == false)
        {
            for(int i= dataset.length;i >= 0;i--)
            {
                this.datalist.add(dataset[i]);
            }

        }
        else
        {
            this.datalist.add("");
        }

        this.fragmet_context = framgent_context;

    }
    @NonNull
    @Override
    public m_log_entrys_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        
        context = parent.getContext();
        


        View view = LayoutInflater.from(context).inflate(R.layout.m_database_layout1, parent, false);


        return  new m_log_entrys_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull m_log_entrys_adapter.ViewHolder holder, int position)
    {

        // FIXME: 13.01.23 Fallbacks optimieren
        mdo = new m_database_ops(context);
        bsf = new Basic_funct();

        if(this.datalist.get(position).equals("") ==true)
        {
            holder.itemView.setVisibility(View.INVISIBLE);
            Log.d("BASI", "Keine Daten");
        }
        else
        {
            holder.itemView.setVisibility(View.VISIBLE);
            int posi = position;
            String [] data = this.datalist.get(posi).split(",");

            /*
             * Arraystruktur data
             * NR        WERT
             * 0     ->  ID
             * 1     ->  MASCH_ID
             * 2     ->  DATE
             * 3     ->  TIME
             * 4     ->  NR
             * 5     ->  NAME
             * 6     ->  COUNTER
             * 7     ->  COUNTER_DIFF
             * */

            holder.m_date_value.setText(bsf.convert_date(data[2],"format_database_to_readable"));
            holder.m_time_value.setText(data[3]);
            holder.m_counter_value.setText(data[6]);

            holder.m_counter_diff_value.setText(bsf.double_round(data[7],2)); //Wird berechnet


            holder.m_delet_imgbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("Warnung");
                    alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
                    alertDialogBuilder.setMessage("Eintrag löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            int response = mdo.delet_log_entry(data[0]);
                           // Toast.makeText(context, String.valueOf(response), Toast.LENGTH_SHORT).show();

                            if (response == 1)
                            {
                                // FIXME: 13.01.23 löschfunktion aktuallisiert nicht mehr!
                                datalist.remove(posi);
                                notifyItemRemoved(posi);
                                notifyItemRangeChanged(posi, datalist.size());
                                mdo.refresh_counter(data[1]);
                                fragmet_context.m_counter_status.setText("Zählerstand:" + mdo.get_maschine_counter(data[1]));
                                fragmet_context.refresh_entry_counter(data[1], "REFRESH");


                                Toast.makeText(context, "Eintrag gelöscht!", Toast.LENGTH_SHORT).show();

                            } else
                            {
                                Toast.makeText(context, "Eintrag NICHT gelöscht!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });

            holder.m_update_imgbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    /*
                     * Arraystruktur data
                     * NR        WERT
                     * 0     ->  ID
                     * 1     ->  MASCH_ID
                     * 2     ->  DATE
                     * 3     ->  TIME
                     * 4     ->  NR
                     * 5     ->  NAME
                     * 6     ->  COUNTER
                     * 7     ->  COUNTER_DIFF
                     * */

                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.m_database_layout_dialog_change_counter,null,false);

                    TextView date = promptsView.findViewById(R.id.textView46);
                    TextView time = promptsView.findViewById(R.id.textView47);
                    EditText counter = promptsView.findViewById(R.id.editTextNumberDecimal2);
                    ImageButton counter_reset = promptsView.findViewById(R.id.imageButton);

                    date.setText(bsf.convert_date(data[2],"format_database_to_readable"));
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            String[] d = data[2].split("-"); //Geschspeichert im Format 2023-01-22

                            Integer year = Integer.valueOf(d[0]);
                            Integer month = Integer.valueOf(d[1])-1;
                            Integer day = Integer.valueOf(d[2]);

                            DatePickerDialog dpd = new DatePickerDialog(context,null,year,month,day);

                            dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                                {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(i,i1 , i2);


                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                                    String dateString = dateFormat.format(calendar.getTime());

                                    date.setText(dateString);
                                }
                            });

                            dpd.show();

                        }
                    });

                    time.setText(data[3]);

                    String[] s = data[3].split(":");

                    time.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {

                            TimePickerDialog tpd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int i, int i1)
                                {

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(0,0,0,i,i1);

                                    SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm");
                                    String timevalue= zeitformat.format(calendar.getTime());

                                    time.setText(timevalue);
                                }
                            },Integer.valueOf(s[0]),Integer.valueOf(s[1]),true);
                                tpd.show();

                        }
                    });

                    counter.setText(data[6]);


                    counter_reset.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            counter.setText("");
                        }
                    });


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder.setTitle("Eintrag ändern");

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                            ContentValues data_new  = new ContentValues();
                            String date_value = bsf.convert_date(date.getText().toString(),"format_database");

                            data_new.put("DATE",date_value);
                            data_new.put("TIME",time.getText().toString());
                            data_new.put("COUNTER",Double.valueOf(counter.getText().toString()));
                            int response = mdo.update_log_entry(data[0],data_new);

                            if(response <1)
                            {
                                bsf.error_msg("Es wurden keine Einträge geändert!",context);
                            }
                            else
                            {
                                bsf.succes_msg("Es wurden "+ String.valueOf(response)+" geändert!",context);
                                mdo.refresh_counter(data[1]);
                                String[] new_data = mdo.get_log_entrys_byid(data[1]);
                                refresh_dataset(new_data);

                                fragmet_context.m_counter_status.setText("Zählerstand:"+ mdo.get_maschine_counter(data[1]));

                            }




                        }
                    });

                    alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                            dialogInterface.cancel();

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();





                }
            });


        }

    }

    @Override
    public int getItemCount()
    {
        return datalist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView m_date_value;
        private final TextView m_time_value;
        private final TextView m_counter_value;
        private final TextView m_counter_diff_value;
        private final ImageButton m_delet_imgbutton;
        private final ImageButton m_update_imgbutton;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            m_date_value = (TextView) itemView.findViewById(R.id.m_date_value);
            m_time_value = (TextView) itemView.findViewById(R.id.m_time_value);
            m_counter_value = (TextView) itemView.findViewById(R.id.m_counter_value);
            m_counter_diff_value = (TextView) itemView.findViewById(R.id.m_counter_diff_value);
            m_delet_imgbutton = (ImageButton) itemView.findViewById(R.id.m_delet_imgbutton);
            m_update_imgbutton = (ImageButton) itemView.findViewById(R.id.m_update_imgbutton);
        }
    }

    public  void refresh_dataset(String[] dataset)
    {
        this.datalist.clear();

            //Array wird gedreht!
            for(int i= dataset.length-1;i >= 0;i--)
            {
                this.datalist.add(dataset[i]);
            }


        notifyDataSetChanged();
    }

}
