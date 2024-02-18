package com.example.tabnav_test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.tabnav_test.share.Share;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class m_log_main extends AppCompatActivity
{
    //Globale Variablen
    String masch_id;
    ArrayList<String> datalist = new ArrayList<String>();

     //RecycleView
    RecyclerView m_log_entrys;

    //ImageButtons
    ImageButton  import_export;
    ImageButton  date_shift_forward;
    ImageButton  date_shift_backward;
    ImageButton  calendar;
    ImageButton  cancel_select_date;


    //TextView
    TextView selectet_date;

    //Instanzen
    m_log_entrys_adapter_main m_log_entrys_adapter_main;
    Basic_funct bsf;
    m_database_ops mdo;
    Share share;

    //LinearLayout
    LinearLayout layout_date;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            Uri uri = data.getData();
            try {

                ArrayList imported_entrys = readTextFromUri(uri);

                try {

                    // TODO: 29.01.23 counter aktuallisieren nach import

                    JSONObject head = new JSONObject(String.valueOf(imported_entrys.get(0))); //head

                    Boolean file_type = head.getString("FILE_TYPE").contains("BACKUP_MASCHINEN_ENTRYS");
                    Boolean maschiid = head.getString("MASCH_ID").equals(masch_id);
                    Boolean id_check = masch_id.contains(mdo.check_maschine(head.getString("NAME"), head.getString("NR")));

                    bsf.log(String.valueOf(file_type));
                    bsf.log(String.valueOf(maschiid));
                    bsf.log(String.valueOf(id_check));

                    if (file_type && maschiid && id_check) {
                        imported_entrys.remove(0);
                        int importet = 0;
                        for (int e = 1; e < imported_entrys.size(); e++) {
                            ContentValues cv = new ContentValues();
                            String[] row = imported_entrys.get(e).toString().split(",");
                            bsf.log(row[6]);

                            cv.put("ID", row[0]);
                            cv.put("PROJ_NR", Basic_funct.PROJ_NR); //fixme projektnummer
                            cv.put("MASCH_ID", row[1]);
                            cv.put("DATE", row[2]);
                            cv.put("TIME", row[3]);
                            cv.put("NR", row[4]);
                            cv.put("NAME", row[5]);
                            cv.put("COUNTER", Double.parseDouble(row[6]));
                            if (mdo.add_log_entry(cv) != -1) {
                                importet++;
                            }
                        }
                        bsf.succes_msg("Es wurden " + importet + " Einträge importiert", m_log_main.this);
                        reload_datalist();
                        mdo.refresh_counter(masch_id);
                    } else {
                        bsf.error_msg("Import Fehler: Falsche Maschine oder ungültige Backupfile", m_log_main.this);
                        reload_datalist();
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mlog_main);

        // Extras
        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                masch_id= null;
            } else
            {
                masch_id= extras.getString("MASCH_ID");
            }
        } else
        {
            masch_id= (String) savedInstanceState.getSerializable("MASCH_ID");
        }

        //Instanzen
        mdo  =new m_database_ops(getApplicationContext());
        bsf = new Basic_funct();
        share = new Share();



        //RecyclerView
        m_log_entrys = findViewById(R.id.rcv);

        m_log_entrys_adapter_main = new m_log_entrys_adapter_main( bsf.to_ArrayList(mdo.get_log_entrys_byid(masch_id)));
        m_log_entrys.setAdapter(m_log_entrys_adapter_main);
        m_log_entrys.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        reload_datalist();

        //ImageButton

        import_export = findViewById(R.id.imageButton22);

        date_shift_backward = findViewById(R.id.imageButton29);
        date_shift_forward = findViewById(R.id.imageButton30);
        calendar =findViewById(R.id.imageButton27);
        cancel_select_date =findViewById(R.id.imageButton28);


        //TextViews
         selectet_date = findViewById(R.id.textView15);


         //LinearLayout
        layout_date  = findViewById(R.id.layout_date);


        //Einträge abholen und in datalist umwandel





        try
        {
            selectet_date.setText(bsf.date_refresh());
        }
        catch (Exception e)
        {
                   Log.e("m_log_main : selectet_date.setText ->", e.getMessage());
        }

        selectet_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog dpd = new DatePickerDialog(m_log_main.this);

                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {


                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1 , i2);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());


                        selectet_date.setText(dateString);
                        try {
                            entrys_date_like();
                        }catch (Exception e)
                        {
                            Log.e("m_log_main :selectet_date ->", e.getMessage());
                        }


                    }
                });
                dpd.show();

            }
        });

        date_shift_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    selectet_date.setText(bsf.time_day_shift(selectet_date.getText().toString(),"",1));
                    entrys_date_like();
                }catch (Exception e)
                {
                    Log.e("m_log_main :date_shift_forward ->", e.getMessage());
                }




            }
        });

        date_shift_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    selectet_date.setText(bsf.time_day_shift(selectet_date.getText().toString(),"",-1));
                    entrys_date_like();
                     }
                        catch (Exception e)
                     {
                        Log.e("m_log_main :date_shift_forward ->", e.getMessage());
                     }


        }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try {
                    if(layout_date.getVisibility() == View.VISIBLE)
                    {
                        layout_date.setVisibility(View.GONE);
                    }else
                    {
                        layout_date.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e)
                {
                    Log.e("m_log_main :calendar ->", e.getMessage());
                }



            }
        });

        cancel_select_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    reload_datalist();
                    layout_date.setVisibility(View.GONE);
                }catch (Exception e)
                {
                      Log.e("m_log_main :cancel_select_date ->", e.getMessage());
                }

            }
        });

        import_export.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                PopupMenu popup = new PopupMenu(m_log_main.this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.m_log_main_export_import, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {

                        switch(menuItem.getItemId())
                        {
                                case R.id.export_backup:
                                    try {

                                        datalist = bsf.to_ArrayList(mdo.get_log_entrys_byid(masch_id));
                                        export_backup(datalist);
                                    }catch (Exception e)
                                    {
                                        exmsg("300120231014A",e);
                                    }

                                break;

                                case R.id.export_csv:

                                    try {
                                        datalist = bsf.to_ArrayList(mdo.get_log_entrys_byid(masch_id));
                                        export_csv(datalist);
                                    }catch (Exception e)
                                    {
                                        exmsg("300120231014B",e);
                                    }


                                break;

                                case R.id.export_txt:

                                    try {
                                        datalist = bsf.to_ArrayList(mdo.get_log_entrys_byid(masch_id));
                                        export_txt(datalist);
                                    }catch (Exception e)
                                    {
                                        exmsg("300120231014C",e);
                                    }

                                break;
                                case R.id.import_data:

                                    try
                                    {
                                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("text/*");

                                        // Optionally, specify a URI for the file that should appear in the
                                        // system file picker when it loads.
                                        startActivityForResult(intent, 0);

                                    }catch (Exception e)
                                    {
                                        exmsg("300120231009",e);

                                    }

                                    // Toast.makeText(m_log_main.this,"Import",Toast.LENGTH_LONG).show();
                                break;
                            case R.id.delet_all_entrys:

                                    try
                                    {
                                        // f//inal Boolean confirm_value = false;

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_log_main.this);
                                        alertDialogBuilder.setTitle("Bestätigen");
                                        alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                                        alertDialogBuilder.setMessage("Alle Einräge Löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {
                                                mdo.delet_all_log_entrys(masch_id);
                                                reload_datalist();

                                                dialogInterface.cancel();
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


                                    }catch (Exception e)
                                    {
                                        exmsg("300120231009",e);

                                    }

                                    // Toast.makeText(m_log_main.this,"Import",Toast.LENGTH_LONG).show();
                                break;

                            case R.id.dummydata:

                                mdo.log_entrys_gendummy(masch_id,10);
                                reload_datalist();
                                break;



                        }
                        return false;
                    }
                });
            }
        });

    }



    //Private Funktionen
    private void export_backup(ArrayList<String> datalist)
    {
        Basic_funct bsf = new Basic_funct();
        try {
            ArrayList<String> data = new ArrayList<String>();
            String[] masch_arg = datalist.get(0).split(",");


            String nr = masch_arg[4];
            String name = masch_arg[5];
            JSONObject head  = new JSONObject();
            head.put("FILE_TYPE","BACKUP_MASCHINEN_ENTRYS");
            head.put("MASCH_ID",masch_id);
            head.put("NAME",name);
            head.put("NR",nr);
            head.put("TABLENAME",SQL_finals.TB_NAME_MASCHINEN_ENTRYS);
            head.put("SIZE",datalist.size());
            head.put("CREATED",bsf.date_refresh()+" "+bsf.time_refresh());
            data.add(String.valueOf(head));
            data.addAll(datalist);

            String date = bsf.get_date_for_filename();

            String filename = "Backup_" + name + "[" + nr + "]_id_" + masch_id + "@" + date;
            Share share = new Share();
            String path = share.file_export(data, filename, "backup");

            bsf.succes_msg("Backup erfolgreich\n" + path, m_log_main.this);

        } catch (Exception e)
        {
            bsf.error_msg("Backup fehlgeschlagen" + e.getMessage(), m_log_main.this);
        }

    }


    private void export_csv(ArrayList<String> list)
    {
        Basic_funct bsf = new Basic_funct();

        try {

            ArrayList<String> data = new ArrayList<String>();

            String[] masch_arg = list.get(0).split(",");

            String nr = masch_arg[4];
            String name = masch_arg[5];


            data.add("DATE,TIME,COUNTER,COUNTER_DIFF");

            for (String v : list) {
                String[] row = v.split(",");
                    /* row Index
                    0 ->  ID
                    1 ->  MASCH_ID
                    2 -> DATE
                    3 -> TIME
                    4 -> NR
                    5 -> NAME
                    6 -> COUNTER
                    7- > COUNTER_DIFF
                    */
                data.add(bsf.convert_date(row[2],"format_database_to_readable") + "," + row[3] + "," + row[6] + "," + row[7]);
            }

            String date = bsf.get_date_for_filename();

            String filename = "log_" + name + "[" + nr + "]_id_" + masch_id + "@" + date;
            share = new Share();
            String path = share.file_export(data, filename, "csv");

            bsf.succes_msg("Export erfolgreich\n" + path, m_log_main.this);

        } catch (Exception e)
        {
            bsf.error_msg("Export fehlgeschlagen" + e.getMessage(), m_log_main.this);
        }
    }



    private void export_txt(ArrayList<String> list)
    {
        Basic_funct bsf = new Basic_funct();

        try {

            ArrayList<String> data = new ArrayList<String>();

            String[] masch_arg = list.get(0).split(",");

            String nr = masch_arg[4];
            String name = masch_arg[5];


            for (String v : list) {
                String[] row = v.split(",");
                    /* row Index
                    0 ->  ID
                    1 ->  MASCH_ID
                    2 -> DATE
                    3 -> TIME
                    4 -> NR
                    5 -> NAME
                    6 -> COUNTER
                    7- > COUNTER_DIFF
                    */
                data.add(bsf.convert_date(row[2],"format_database_to_readable") + "\t" + row[3] + "\t" + row[6] + "\t\t" + row[7]);
            }

            String date = bsf.get_date_for_filename();

            String filename = "log_" + name + "[" + nr + "]_id_" + masch_id + "@" + date;
            share = new Share();
            String path = share.file_export(data, filename, "txt");

            bsf.succes_msg("Export erfolgreich\n" + path, m_log_main.this);

        } catch (Exception e)
        {
            bsf.error_msg("Export fehlgeschlagen" + e.getMessage(), m_log_main.this);
        }
    }


    private void entrys_date_like()
    {

        try {

            String date_val = null;

            try{date_val=bsf.convert_date(selectet_date.getText().toString(),"format_database");}
            catch (Exception e){exmsg("300120230944A",e);}

            ArrayList<String> data = new ArrayList<String>();
            String []  dataset = new String[0];


            try{dataset= mdo.get_log_entrys_byid(masch_id);}
            catch (Exception e){exmsg("300120230944B",e);}

            for(String i: dataset)
            {
                String []row = i.split(",");
                if(row[2].equals(date_val))
                {
                    data.add(i);
                }
            }

            try{m_log_entrys_adapter_main.refresh_dataset(data);}
            catch (Exception e){exmsg("300120230944C",e);}
        }
        catch
        (Exception e )
        {
            exmsg("26012023214D",e);
        }

    }

    private ArrayList<String> readTextFromUri(Uri uri) throws IOException
    {

        ArrayList<String> stringBuilder = new ArrayList<String>();

        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream))))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.add(line);
            }
        }
        return stringBuilder;
    }

    private void reload_datalist()
    {
        try
        {
            String[] data= mdo.get_log_entrys_byid(masch_id);
            ArrayList<String> list =  bsf.to_ArrayList(data);
            Collections.reverse(list);
            m_log_entrys_adapter_main.refresh_dataset(list);

        }catch (Exception e)
        {
            exmsg("230120230938",e);
        }

    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: m_log_main ->","ID: "+msg+" Message:" + e.getMessage());
    }
}

