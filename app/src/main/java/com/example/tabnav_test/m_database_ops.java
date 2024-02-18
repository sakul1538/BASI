package com.example.tabnav_test;

import static java.util.Calendar.DAY_OF_MONTH;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tabnav_test.material.material_database_ops;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class m_database_ops  extends SQLiteOpenHelper implements SQL_finals
{
 Context context2;

    public m_database_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        context2 = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
       // sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_MASCHINEN_ENTRYS+" (ID TEXT,NAME TEXT,DIR TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    public String add_manschine(ContentValues data)
    {
        Basic_funct bsf = new Basic_funct();
        String response ="null";
            try
            {
                data.put("ID", bsf.gen_ID());
                data.put("DATE", bsf.date_refresh_database());
                data.put("TIME", bsf.time_refresh());

                SQLiteDatabase rdb = this.getReadableDatabase();
                String[] columns = {"ID","NAME","NR"};

                Cursor cursor = rdb.query(TB_NAME_MASCHINEN_ITEMS,columns,null,null,null,null,null);

                while (cursor.moveToNext())
                {
                   String id=  cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                   String nr =cursor.getString(cursor.getColumnIndexOrThrow("NR"));
                   String name =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));

                   while(id.contains(data.get("ID").toString()))
                   {
                       data.put("ID", bsf.gen_ID()); //Neue ID Generieren
                       Log.w("BASI","ID Vorhanden, neue ID Generieren");
                   }
                }
                cursor.close();


                SQLiteDatabase wdb = this.getWritableDatabase();

                /// the row ID of the newly inserted row, or -1 if an error occurred
                long newRowId = wdb.insert(TB_NAME_MASCHINEN_ITEMS,null,data);
                if(newRowId == -1)
                {
                    response ="null";
                }
                else
                {
                    response = data.get("ID").toString();

                }

                wdb.close();
                rdb.close();

            }catch (Exception e)
            {
                Log.d("BASI","add_manschine:"+ e);

            }
            return response;
        }


    public String[] get_maschinen(String proj_id)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id};
        String where = "PROJ_NR=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {

            String value = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NR"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTE"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("PIC_SRC"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG"));

            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();
        db.close();

        return  strings;
    }









    public String[] get_list_for_autocomplete(String proj_id,String type)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id,};
        String where = "PROJ_NR=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value = null;

            if (type.equals("NAME")) {
                value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                value += "[" + cursor.getString(cursor.getColumnIndexOrThrow("NR")) + "]";
            }
            
            strings[i]=value;
            i++;
        }
        cursor.close();
        db.close();

        return  strings;

    }


    public int delet_masch_id(String masch_id)
    {
        int response=-1;
        //Löscht die Maschine selbst
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = {masch_id};
            String where = "ID=?";
            response = db.delete(TB_NAME_MASCHINEN_ITEMS,where,selectionArgs);
            where = "MASCH_ID=?";
            response = db.delete(TB_NAME_MASCHINEN_ENTRYS,where,selectionArgs);
            }catch (Exception e)
        {
            Log.d("Error->", e.getMessage());
            response=-1;
        }
        return response;
    }


    public ContentValues get_maschine(String id)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {id};
        String where = "ID=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        ContentValues data = new ContentValues();

        data.put("ID",cursor.getString(cursor.getColumnIndexOrThrow("ID")));
        data.put("DATE",cursor.getString(cursor.getColumnIndexOrThrow("DATE")));
        data.put("TIME",cursor.getString(cursor.getColumnIndexOrThrow("TIME")));
        data.put("NR",cursor.getString(cursor.getColumnIndexOrThrow("NR")));
        data.put("NAME",cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
        data.put("CATEGORY",cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY")));
        data.put("COUNTER",cursor.getString(cursor.getColumnIndexOrThrow("COUNTER")));
        data.put("NOTE",cursor.getString(cursor.getColumnIndexOrThrow("NOTE")));
        data.put("PIC_SRC",cursor.getString(cursor.getColumnIndexOrThrow("PIC_SRC")));
        data.put("ONOFF_FLAG",cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG")));

        cursor.close();

        return  data;

    }

    public String get_maschine_counter(String id)
    {

        SQLiteDatabase db = this.getReadableDatabase();


        String[] selectionArgs = {id};
        String where = "ID=?";

        String counter = "0";

        try{
            Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
            if(cursor.getCount() !=0)
            {
                cursor.moveToFirst();
                counter= cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));
                if(counter.contains("null"))
                {
                    counter ="0";
                }
                cursor.close();
            }
            else
            {
                counter = "0";
            }


        }catch (Exception e)
        {
            Log.w("BASI", e.getMessage());
        }
        return  counter;

    }

    public String check_maschine(String name,String nr)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {name,nr};
        String where = "NAME=? AND NR=?";
        int i=0;
        String id = "null";
        int c = 0;

        try{
            Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
            if(cursor.getCount() !=0)
            {
                cursor.moveToFirst();
                 id= cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                 cursor.close();
            }
            else
            {
                id="null";
            }


        }catch (Exception e)
        {
            Log.w("BASI", e.getMessage());
        }
        return  id;

    }

    public int get_cateogry_index(String value)
    {
         ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context2, android.R.layout.simple_dropdown_item_1line,context2.getResources().getStringArray(R.array.m_categorys));

          int index_pos= spinnerArrayAdapter.getPosition(value);

    return index_pos;

    }

    public Bitmap getBitmap(String path)
    {
        Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + path);

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 300, 200, true);

        return bMapScaled;
    }

    public void dialog(Context context)
    {

        Uri imgUri = null;

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.m_add_maschine_dialog, null);

        EditText name = promptsView.findViewById(R.id.m_name);
        EditText nr = promptsView.findViewById(R.id.m_nr);
        Spinner category = promptsView.findViewById(R.id.m_category);
        EditText note = promptsView.findViewById(R.id.m_note);
        EditText counter = promptsView.findViewById(R.id.m_counter);
        ImageButton add_image = promptsView.findViewById(R.id.imageButton13);
        ImageView m_pic = promptsView.findViewById(R.id.imageView);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context.getApplicationContext());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Maschine hinzufügen");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {





             //   m_conf_maschine_adapter mcma = new m_conf_maschine_adapter(mdo.get_maschinen(RROJ_NR));
                //m_rcv.setAdapter(mcma);


                //(ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");

                dialogInterface.cancel();

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


    public int update_manschine(String proj_id,String id,ContentValues data)
    {

        int response =0;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { proj_id,id };
        String where = "PROJ_NR=? AND ID=?";

        response = db.update(TB_NAME_MASCHINEN_ITEMS,data,where,selectionArgs);

        return response;

    }

    //Einträge erstellen der Log
    public long add_log_entry(ContentValues data)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Basic_funct bsf = new Basic_funct();
        long newRowId = -1;

        if(!data.containsKey("ID"))
        {
                data.put("ID", bsf.gen_ID());
                while(collisiontest(data.get("ID").toString()) >0)
                {
                    data.put("ID", bsf.gen_ID());
                }
                 newRowId = db.insert(TB_NAME_MASCHINEN_ENTRYS, null, data);
        }
        else
        {
           if(collisiontest(data.get("ID").toString()) == 0)
           {
               newRowId = db.insert(TB_NAME_MASCHINEN_ENTRYS, null, data);
           }
        }
        return newRowId;
    }

    //Erstellt den ersten Einrag in der Log, wenn die eine neue Maschine hinzugefügt wird!
    public boolean add_log_entry_init(String id,String nr,String name,String counter) //ID der Maschine
    {
        Basic_funct bsf = new Basic_funct();
        boolean response =false;
        ContentValues data  = new ContentValues();
        Double d_counter= Double.parseDouble(counter);
        data.put("ID", bsf.gen_ID());
        data.put("PROJ_NR", Basic_funct.PROJ_NR);
        data.put("MASCH_ID",id);
        data.put("DATE",bsf.date_refresh_database());
        data.put("TIME",bsf.time_refresh());
        data.put("TIME",bsf.time_refresh());
        data.put("NR",nr);
        data.put("NAME",name);
        data.put("COUNTER",d_counter);

        try
        {

            SQLiteDatabase db = this.getWritableDatabase();
            long newRowId = db.insert(TB_NAME_MASCHINEN_ENTRYS,null,data);
            response = newRowId != -1;

        }catch (Exception e)
        {
            Log.d("BASI","add_log_entry:"+ e);

        }
        return response;
    }


    //Eintrag löschen  in der Log

    public int delet_log_entry(String entry_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { entry_id };
        String where = "ID=?";

        int deletedRows = db.delete(TB_NAME_MASCHINEN_ENTRYS,where,selectionArgs);

        return deletedRows;

    }

    //Löscht ALLE einträge in der Log der Maschine
    public int delet_all_log_entrys(String masch_id)
    {
        int deletedRows=-1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = {masch_id};
            String where = "MASCH_ID=?";
            deletedRows = db.delete(TB_NAME_MASCHINEN_ENTRYS,where,selectionArgs);
            refresh_counter(masch_id);
        }catch (Exception e)
        {
            exmsg("300120231034",e);
            deletedRows=-1;
        }
        return deletedRows;
    }


    public int update_log_entry(String id,ContentValues data)
    {

        int response =0;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { id };
        String where = "ID=?";

        response = db.update(TB_NAME_MASCHINEN_ENTRYS,data,where,selectionArgs);

        return response;

    }


    public String[] get_log_entrys_byid(String id)
    {
             // FIXME: 13.01.23 try/catch/Fehlerhandling

            SQLiteDatabase db = this.getReadableDatabase();
            String[] selectionArgs = {id};
            String where = "MASCH_ID=?";
            String [] data_array={""};

            Cursor cursor = db.query(TB_NAME_MASCHINEN_ENTRYS,null,where,selectionArgs,null,null,"COUNTER ASC");

            if(cursor.getCount() > 0)
            {
                data_array = new String[cursor.getCount()];
                int c = 0;

                cursor.moveToFirst();

                String data = "";
                data += cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("MASCH_ID"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("NR"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                data += "," + cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));
                data += ",0 ";//Leer da erstes element

                Double last_counter_value = Double.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("COUNTER")));

                data_array[c] = data;
                c++;
                if(cursor.getCount() > 1)
                {
                    while (cursor.moveToNext()) {
                        data = "";
                        data += cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("MASCH_ID"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("NR"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                        data += "," + cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));

                        Double current_counter_value = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow("COUNTER")));
                        Double counter_diff = current_counter_value - last_counter_value;
                        last_counter_value = current_counter_value;
                        data += ",+" + counter_diff;
                        //Log.d("BASI", data);

                        data_array[c] = data;
                        c++;
                    }
                    cursor.close();
                }
            }
             db.close();
            return  data_array;

    }

    public void  refresh_counter(String id)
    {
        ContentValues data = new ContentValues();
        data.put("COUNTER", counter_getmax(id));

        int response =0;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { id };
        String where = "ID=?";

        response = db.update(TB_NAME_MASCHINEN_ITEMS,data,where,selectionArgs);
    }

    public String  get_counter(String id)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { id };
        String[] columns = { "COUNTER" };
        String where = "ID=?";

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,columns,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        return  cursor.getString(0);
    }

    public String  counter_getmax(String id)
    {
        SQLiteDatabase rdb = this.getReadableDatabase();
        String[] selectionArgs = {id};
        String[] columns = {"MAX(COUNTER)"};
        String where = "MASCH_ID=?";

        Cursor cursor = rdb.query(TB_NAME_MASCHINEN_ENTRYS,columns,where,selectionArgs,null,null,null);
        cursor.moveToFirst();
        String max = String.valueOf(cursor.getString(0));
        if(max.equals("null"))
        {
            max="0";
        }
        cursor.close();

        return max;
    }



    public String log_get(String masch_id,String get)
    {

        String return_val="null";
        String[] selectionArgs = {masch_id};
        String where = "MASCH_ID=?";
        Cursor cursor;
        SQLiteDatabase rdb = this.getReadableDatabase();

        switch (get)
        {
            case  "COUNT_ENTRYS":

                cursor = rdb.query(TB_NAME_MASCHINEN_ENTRYS,null,where,selectionArgs,null,null,null);
                int entrys =  cursor.getCount();
                return_val= String.valueOf(entrys);
                cursor.close();

            break;

            case  "LAST_ENTRY":

                cursor = rdb.query(TB_NAME_MASCHINEN_ENTRYS,null,where,selectionArgs,null,null,"DATE DESC");
                if(cursor.getCount() >0)
                {
                    cursor.moveToFirst();
                    return_val =cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                    cursor.close();
                }

                break;

        }

        return return_val;
    }

    public Boolean maschine_on_off(String masch_id,String mode, Boolean state)
    {
        String[] selectionArgs = {masch_id};
        String where = "ID=?";
        String[] columns = {"ONOFF_FLAG"};
        Boolean response =true;

        switch (mode)
        {
            case "GET":

                SQLiteDatabase rdb = this.getReadableDatabase();

                Cursor cursor = rdb.query(TB_NAME_MASCHINEN_ITEMS,columns,where,selectionArgs,null,null,null);
                cursor.moveToFirst();
                String onoff_flag =cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG"));
                Log.d("ONOFF_FLAG __>",onoff_flag);


                if(onoff_flag.contains("true"))
                {
                    response=true;
                }
                if(!onoff_flag.contains("false"))
                {
                    response=false;
                }

                cursor.close();
                rdb.close();
                break;

            case"SET":

                SQLiteDatabase wdb = this.getWritableDatabase();
                ContentValues data = new ContentValues();

                if(state)
                {
                    data.put("ONOFF_FLAG","true");
                    Log.d("ONOFF_FLAG","TRUE");
                }
                if(!state)
                {
                    data.put("ONOFF_FLAG","false");
                    Log.d("ONOFF_FLAG","FALSE");
                }

                long r  = wdb.update(TB_NAME_MASCHINEN_ITEMS,data,where,selectionArgs);

                response= r == 1;

                wdb.close();
                break;

        }
        return response;
    }


    public int collisiontest(String id)
    {
        // FIXME: 13.01.23 try/catch/Fehlerhandling

        SQLiteDatabase db = this.getReadableDatabase();

        String[] selectionArgs = {id};
        String where ="ID=?";

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ENTRYS,null,where,selectionArgs,null,null,null);
        Log.d("COUNTER", String.valueOf(cursor.getCount()));

        cursor.close();


        return  cursor.getCount();

    }

    public String[] filter_maschine_category(ArrayList filter_arg)
    {

        //Testen auf eingabe > 0
        if(filter_arg.size()>0)
        {
            String[] selectionArgs = new String[0];
            String where = null;
            try {
                selectionArgs = new String[filter_arg.size()];
                where = "";

                //Schleife für Where und selectionArgs
                for(int cpos=0;cpos<filter_arg.size();cpos++)
                {
                    selectionArgs[cpos] = filter_arg.get(cpos).toString();
                    if (cpos == 0)
                    {
                        where += "CATEGORY=?";

                    } else
                    {
                        where += " OR CATEGORY=?";
                    }
                }
            } catch (Exception e)
            {
              exmsg("020220231028",e);
            }

            //Datenbankabfagen
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try
            {
                db = this.getReadableDatabase();
                cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);

            } catch (Exception e)
            {
                exmsg("0202020231026",e);
            }

            //Test auf 0 der sql Abfrage
            if(cursor.getCount() >0)
            {
                int  apos=0;
                String[] strings = new String[cursor.getCount()];

                try
                {
                    while (cursor.moveToNext())
                    {
                        String value = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NR"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTE"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("PIC_SRC"));
                        value +=","+cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG"));

                        strings[apos]=value;
                        apos++;
                    }
                } catch (IllegalArgumentException e)
                {
                   exmsg("020220231029",e);
                }
                cursor.close();
                db.close();
                //Daten rückgabe
                return strings;
            }
            else
            {
                //Alternative Ausgabe cursor =0 (sql Abfrage)=
                cursor.close();
                db.close();
                String[] string = {"null"};
                return string;
            }
        }
        else
        {
            //Alternative Ausgabe filter_args=0
            String[] string = {"null"};
            return string;
        }
    }

    public String[] filter_maschine_date(String date) //Im Datum in Dantenbankformat
    {
        SQLiteDatabase rdb  = null;
        Cursor cursor = null;

        try {
                //Suche nach einträgen in der Datenbankt mit dem Angegebenen Datum "date"
                rdb = getReadableDatabase();
                String[] selection_args ={date};
                String where = "DATE=?";

                cursor = rdb.query(TB_NAME_MASCHINEN_ENTRYS,null,where,selection_args,"MASCH_ID",null,null);

            } catch (Exception e)
            {
                exmsg("030220231639",e);
            }
        //Falls Einträge gefunden wurden...
        if(cursor.getCount() >0)
        {
            int  apos=0;
            String[] strings = new String[cursor.getCount()];

            try
            {
                while (cursor.moveToNext())
                {
                        //Hole daten der Maschine aus der Datenbank per get_maschine
                        ContentValues cv = get_maschine( cursor.getString(cursor.getColumnIndexOrThrow("MASCH_ID")));

                        String item = cv.get("ID").toString();
                        item+= ","+cv.get("DATE").toString();
                        item+= ","+cv.get("TIME").toString();
                        item+= ","+cv.get("NR").toString();
                        item+= ","+cv.get("NAME").toString();
                        item+= ","+cv.get("CATEGORY").toString();
                        item+= ","+cv.get("COUNTER").toString();
                        item+= ","+cv.get("NOTE").toString();
                        item+= ","+cv.get("PIC_SRC").toString();
                        item+= ","+cv.get("ONOFF_FLAG").toString();

                        strings[apos]=item;
                        apos++;
                    }

            } catch (IllegalArgumentException e)
            {
                exmsg("020220232008",e);
            }
            cursor.close();
            rdb.close();
            //Daten rückgabe
            return strings;
        }
        else
        {
            //Alternative Ausgabe falls cursor =0 (sql Abfrage)=
            cursor.close();
            rdb.close();
            String[] string = {"null"};
            return string;
        }
    }

    public void log_entrys_gendummy(String masch_id,int quantity)
    {
        Basic_funct bsf = new Basic_funct();
        ContentValues data = new ContentValues();
        ContentValues cv = get_maschine(masch_id);


        Calendar calendar = Calendar.getInstance();
        calendar.set(2023,0,0);

        Random rm = new Random();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Double counter = 10.0;

       while(quantity >0)
       {
           data.put("ID", bsf.gen_ID());
           data.put("PROJ_NR", Basic_funct.PROJ_NR);
           data.put("MASCH_ID", masch_id);

           String r = String.valueOf(rm.nextInt());
           r = r.substring(r.length()-1);

           calendar.add(DAY_OF_MONTH, Integer.parseInt(r));
           data.put("DATE", dateFormat.format(calendar.getTime()));
           data.put("TIME", bsf.time_refresh());
           data.put("NR", cv.get("NR").toString());
           data.put("NAME", cv.get("NAME").toString());
           data.put("COUNTER", counter);

           try {

               Log.d("Dummy:",data.toString());


               SQLiteDatabase db = this.getWritableDatabase();
               long newRowId = db.insert(TB_NAME_MASCHINEN_ENTRYS, null, data);

           } catch (Exception e) {
               Log.d("BASI", "add_log_entry:" + e);

           }
           counter = counter + Double.parseDouble(r);
           quantity--;
       }
       refresh_counter(masch_id);
    }




    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: m_database_ops ->","ID: "+msg+" Message:" + e.getMessage());
        e.printStackTrace();
    }

    public void export_complete_csv(Context context)
    {
       Context context3 = context;

     SQLiteDatabase  rdb = getReadableDatabase();
     Cursor maschinen_ids = rdb.query(TB_NAME_MASCHINEN_ITEMS, new String[]{"ID"},null,null,null,null,null);
     ArrayList<String> id = new ArrayList<>();
     ArrayList<String> datablock = new ArrayList<>();
     ArrayList<String> data_output = new ArrayList<>();
     Basic_funct bsf = new Basic_funct();

     if(maschinen_ids.getCount() !=0)
     {
         while (maschinen_ids.moveToNext())
         {
             id.add(maschinen_ids.getString(maschinen_ids.getColumnIndexOrThrow("ID")));
         }
         data_output.add("DATUM,ZEIT,NAME,NR,ZÄHLERSTAND,STUNDEN\n");

         Iterator<String> ids = id.iterator();

         while (ids.hasNext())
         {
             String curr_id = ids.next();
             String[] data = this.get_log_entrys_byid(curr_id);
             for (String d : data)
             {
                 Log.d("DATA",d);
                 String[] col = d.split(",");

                 String date = col[2];
                 String time = col[3];
                 String nr  = col[4];
                 String name  = col[5];
                 String counter  = col[6];
                 String counter_diff  = col[7];
                 datablock.add(date+","+time+","+name+","+nr+","+counter+","+counter_diff+"\n");

             }
         }
         Collections.sort(datablock);
         data_output.addAll(datablock);
         Iterator<String> output = data_output.iterator();
         String file_write_string ="";

         while(output.hasNext())
         {
             file_write_string += output.next();

         }
        Log.d("CSV",file_write_string);
         material_database_ops mdo  = new material_database_ops(context2);

         String path = mdo.get_projekt_root_paht() + static_finals.export_dir_csv;
         Log.d("BASI_ppaht",path);
         String filename= "maschinen_report@"+bsf.get_date_for_filename()+".csv";
         write_csv(path,filename,file_write_string);

         File dir = new File(path);
         dir.mkdirs();
         File f = new File(dir+filename);
         if(f.exists())
         {
             f.delete();
         }
         FileWriter fw = null;
         try {
             fw = new FileWriter(path+filename);
             fw.write(file_write_string);
             fw.close();

             Toast toast= Toast.makeText(context2,
                     "Export CSV Datei unter: \n"+path+filename, Toast.LENGTH_LONG);
             toast.setGravity(Gravity.CENTER,0,0);
             toast.show();

         } catch (IOException e) {
             Toast.makeText(context2, "Export CSV Datei Fehlgeschladen: \n"+ e.getMessage(), Toast.LENGTH_LONG).show();
             throw new RuntimeException(e);
         }
     }

     else
     {
         bsf.error_msg("Keine Maschine gefunden\n",context3);
     }

    }

    private  void write_csv(String path, String filename,String file_write_string)
    {


    }
}



