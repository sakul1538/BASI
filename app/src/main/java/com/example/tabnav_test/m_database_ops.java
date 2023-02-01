package com.example.tabnav_test;

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

import java.util.ArrayList;

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
                data.put("DATE", bsf.date_refresh());
                data.put("TIME", bsf.time_refresh());

                SQLiteDatabase rdb = this.getReadableDatabase();
                String[] columns = {"ID","NAME","NR"};

                Cursor cursor = rdb.query(TB_NAME_MASCHINEN_ITEMS,columns,null,null,null,null,null);

                while (cursor.moveToNext())
                {
                   String id=  cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                   String nr =cursor.getString(cursor.getColumnIndexOrThrow("NR"));
                   String name =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));

                   while(id.contains(data.get("ID").toString()) == true)
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
                Log.d("BASI","add_manschine:"+e.toString());

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
        String[] selectionArgs = {proj_id,"true"};
        String where = "PROJ_NR=? AND ONOFF_FLAG=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value = null;
            
            switch(type)
            {
                case "NAME":
                    value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    value +="["+cursor.getString(cursor.getColumnIndexOrThrow("NR"))+"]";
                    break;
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
            Log.d("Error->",e.getMessage().toString());
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
                if(counter.contains("null")== true)
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
            Log.w("BASI",e.getMessage().toString());
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
            Log.w("BASI",e.getMessage().toString());
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

        if(data.containsKey("ID") == false)
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
        data.put("PROJ_NR",bsf.PROJ_NR);
        data.put("MASCH_ID",id);
        data.put("DATE",bsf.date_refresh());
        data.put("TIME",bsf.time_refresh());
        data.put("TIME",bsf.time_refresh());
        data.put("NR",nr);
        data.put("NAME",name);
        data.put("COUNTER",d_counter);

        try
        {

            SQLiteDatabase db = this.getWritableDatabase();
            long newRowId = db.insert(TB_NAME_MASCHINEN_ENTRYS,null,data);
            if(newRowId == -1)
            {
                response =false;
            }
            else
            {

                response =true;
            }

        }catch (Exception e)
        {
            Log.d("BASI","add_log_entry:"+e.toString());

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
                data += ", ";//Leer da erstes element

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
                        data += ",+" + counter_diff.toString();
                        Log.d("BASI", data);

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
        if(max.equals("null")== true)
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


                if(onoff_flag.contains("true") == true )
                {
                    response=true;
                }
                if(onoff_flag.contains("false") == false)
                {
                    response=false;
                }

                cursor.close();
                rdb.close();
                break;

            case"SET":

                SQLiteDatabase wdb = this.getWritableDatabase();
                ContentValues data = new ContentValues();

                if(state == true)
                {
                    data.put("ONOFF_FLAG","true");
                    Log.d("ONOFF_FLAG","TRUE");
                }
                if(state == false)
                {
                    data.put("ONOFF_FLAG","false");
                    Log.d("ONOFF_FLAG","FALSE");
                }

                long r  = wdb.update(TB_NAME_MASCHINEN_ITEMS,data,where,selectionArgs);

                if(r == 1)
                {
                    response=true;
                }
                else
                {
                    response=false;
                }

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


    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: m_database_ops ->","ID: "+msg+" Message:" +e.getMessage().toString());
    }
}



