package com.example.tabnav_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompatSideChannelService;

import java.util.Arrays;


public class log_fav extends SQLiteOpenHelper implements SQL_finals
{
    private  String where ="";
    private Context context;
    static final String RROJ_NR="0";


    public log_fav(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }


    public String log_set_unset_flags(String id, String proj_id, String flag_name)
    {
        String flag_state="null";
        int response=0;

        flag_state =  log_get_flag_value(id,proj_id,flag_name);

        //Hin und herschalten (Toogeln)
      switch(flag_state)
      {

          case "TRUE":
              flag_state="FALSE";
              break;
          case "FALSE":
              flag_state="TRUE";
              break;
      }

        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {id,proj_id};
        where = "ID=? AND PROJ_NR=?";

        switch(flag_name)
        {
            case "FAV_FLAG":

                ContentValues values_flav = new ContentValues();
                values_flav.put("FAV_FLAG", flag_state);
                response = db.update(PROJ_LOG,values_flav,where,selectionArgs);

                break;
            case "CHECK_FLAG":

                ContentValues values_ckeck = new ContentValues();
                values_ckeck.put("CHECK_FLAG", flag_state);
                response = db.update(PROJ_LOG,values_ckeck,where,selectionArgs);

                break;

        }

        return  flag_state;


    }


    public String log_get_flag_value(String id, String  proj_id,String flag_name)
    {
        String value="null";

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {id,proj_id};
        where = "ID=? AND PROJ_NR=?";

        Cursor cursor = db.query(PROJ_LOG,null,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        switch(flag_name)
        {
            case "FAV_FLAG":
                value = cursor.getString(cursor.getColumnIndexOrThrow("FAV_FLAG"));
                break;

            case "CHECK_FLAG":
                value = cursor.getString(cursor.getColumnIndexOrThrow("CHECK_FLAG"));
                break;


            default:

                value="Error: Kein Flagwert";

        }
        Log.d("FLAG: ",flag_name+":"+value);

        cursor.close();
        return value;
    }


    public long log_add_entry(String proj_id, String date, String time,String category,String note)
    {
     Log.d("Notiz:",note);

        long newRowId = -1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
                Basic_funct bsf = new Basic_funct();

                ContentValues values = new ContentValues();
                values.put("ID", bsf.gen_ID());
                values.put("PROJ_NR", proj_id);
                values.put("DATE", date);
                values.put("TIME", time);
                values.put("CATEGORY", category);
                values.put("NOTE", note);
                values.put("FAV_FLAG","FALSE");
                values.put("CHECK_FLAG","FALSE");
                values.put("UPLOAD_FLAG","FALSE");

// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(PROJ_LOG,null,values);


        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }

        return newRowId;

    }

    public long addOne(String proj_id, String key, String value)
    {
        long newRowId = -1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = { proj_id,key,value };
            String[] projection = {"NAME"};
            where = "ID=? AND NAME=? AND VALUE=?";

            Cursor cursor = db.query(TB_NAME_LOG_CONF,projection,where,selectionArgs,null,null,null);

            // Create a new map of values, where column names are the keys
            if ( cursor.getCount()==0)
            {
                ContentValues values = new ContentValues();
                values.put("ID", proj_id);
                values.put("NAME", key);
                values.put("VALUE", value);

// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(TB_NAME_LOG_CONF,null,values);
            }
            cursor.close();

        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }


        return newRowId;

    }



    public String[] getall(String proj_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id,"ITHEM_CATEGORY"};
        where = "ID=? AND NAME=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_LOG_CONF,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();


        return  strings;
    }

    public long log_add_category(String proj_id, String value)
    {
        long newRowId = -1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = { proj_id,"ITHEM_CATEGORY",value };
            String[] projection = {"NAME"};
            where = "ID=? AND NAME=? AND VALUE=?";

            Cursor cursor = db.query(TB_NAME_LOG_CONF,projection,where,selectionArgs,null,null,null);

            // Create a new map of values, where column names are the keys
            if ( cursor.getCount()==0)
            {
                ContentValues values = new ContentValues();
                values.put("ID", proj_id);
                values.put("NAME", "ITHEM_CATEGORY");
                values.put("VALUE", value);

// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(TB_NAME_LOG_CONF,null,values);
            }
            cursor.close();

        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }


        return newRowId;

    }


    public String[] getallcategorys(String proj_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id,"ITHEM_CATEGORY"};
        where = "ID=? AND NAME=?";
        int i=1;

        Cursor cursor = db.query(TB_NAME_LOG_CONF,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()+1];
        strings[0]="None";
        while (cursor.moveToNext())
        {
            String value = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();


        return  strings;
    }


    //Favoriten
    public String[] getalllogfav(String proj_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id,"ITHEM_FAV","ITHEM_FAV_GLOBAL"};
        where = "ID=? AND NAME=? OR NAME=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_LOG_CONF,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value = cursor.getString(cursor.getColumnIndexOrThrow("VALUE"));
            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();

        return  strings;
    }

    public int modifylogfav(String proj_id, String name, String new_name)
    {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = {proj_id,"ITHEM_FAV","ITHEM_FAV_GLOBAL",name};
            where = "ID=? AND (NAME=? OR NAME=?) AND VALUE=? ";
            int i=0;

            ContentValues values = new ContentValues();

            values.put("VALUE", new_name);

            int response = db.update(TB_NAME_LOG_CONF,values,where,selectionArgs);

            return response;

    }

    public int deletlogfav(String proj_id, String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {proj_id,"ITHEM_FAV","ITHEM_FAV_GLOBAL",name};
        where = "ID=? AND (NAME=? OR NAME=?) AND VALUE=? ";


        int response = db.delete(TB_NAME_LOG_CONF,where,selectionArgs);

        return response;
    }

    public String favsetglobal(String proj_id, String name, boolean b)
    {

        String message;
        int response;
        SQLiteDatabase db = this.getWritableDatabase();

        if(b==true)
        {

            String[] selectionArgs = {proj_id,"ITHEM_FAV",name};
            where = "ID=? AND NAME=? AND VALUE=? ";

            ContentValues values = new ContentValues();
            values.put("NAME", "ITHEM_FAV_GLOBAL");

            response = db.update(TB_NAME_LOG_CONF,values,where,selectionArgs);
            message = "["+response+"]"+name +" ist jezt Global";
        }
        else
        {
            String[] selectionArgs = {proj_id,"ITHEM_FAV_GLOBAL",name};
            where = "ID=? AND NAME=? AND VALUE=? ";

            ContentValues values = new ContentValues();
            values.put("NAME", "ITHEM_FAV");

            response = db.update(TB_NAME_LOG_CONF,values,where,selectionArgs);
            message =  "["+response+"]"+name +" ist nicht mehr Global";

        }
            return message;

    }

    public boolean favgetglobal(String proj_id, String name)
    {
        boolean global_status =false;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id,name};
        where = "ID=? AND VALUE=?";

        Cursor cursor = db.query(TB_NAME_LOG_CONF,null,where,selectionArgs,null,null,null);

        cursor.moveToFirst();
        String value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        switch(value)
        {
            case "ITHEM_FAV_GLOBAL":

                    global_status=true;
                break;

            case "ITHEM_FAV":

                    global_status=false;
                break;

        }
        cursor.close();

        return global_status;
    }


    public int modifylogcategory(String proj_id, String name, String toName)
    {

            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = {proj_id,"ITHEM_CATEGORY","ITHEM_CATEGORY_GLOBAL",name};
            where = "ID=? AND (NAME=? OR NAME=?) AND VALUE=? ";
            int i=0;

            ContentValues values = new ContentValues();

            values.put("VALUE", toName);

            int response = db.update(TB_NAME_LOG_CONF,values,where,selectionArgs);

            return response;

    }

    public int deletlogcategory(String proj_id, String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {proj_id,"ITHEM_CATEGORY","ITHEM_CATEGORY_GLOBAL",name};
        where = "ID=? AND (NAME=? OR NAME=?) AND VALUE=? ";


        int response = db.delete(TB_NAME_LOG_CONF,where,selectionArgs);

        return response;
    }

    public int delet_log_entry(String id, String proj_nr)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {id,proj_nr};
        where = "ID=? AND PROJ_NR=?";

        int response = db.delete(PROJ_LOG,where,selectionArgs);

        return response;
    }

    public int delet_log_entry_if_check_true(String id, String proj_nr)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {id,proj_nr,"TRUE"};
        where = "ID=? AND PROJ_NR=? AND CHECK_FLAG=?";

        int response = db.delete(PROJ_LOG,where,selectionArgs);

        return response;
    }
    public String[] getalllogdata(String proj_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id};
        where = "PROJ_NR=?";
        int i=0;

        Cursor cursor = db.query(PROJ_LOG,null,where,selectionArgs,null,null,"DATE DESC");
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value ="";
            value += cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            value += ","+cursor.getString(cursor.getColumnIndexOrThrow("PROJ_NR"));
            value += ","+cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            value += ","+cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            value += ","+cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY"));
            value += ","+cursor.getString(cursor.getColumnIndexOrThrow("NOTE"));
            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();

        return  strings;
    }


    public String[] log_search_data(String proj_id,String search_string)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] search_arg=search_string.split(","); //search text, date_from, date_to,category


        Log.d("SQL_daten0:",search_arg[0]);
        Log.d("SQL_daten1:",search_arg[1]);
        Log.d("SQL_daten2:",search_arg[2]);
        Log.d("SQL_daten3:",search_arg[3]);

        where=" WHERE (PROJ_NR="+proj_id+")"; //Start 0

       if(search_arg[0].equals("null")==false) //SEARCHVALUE
        {
            where +=  " AND (NOTE LIKE '%"+search_arg[0]+"%')";
         }

      if(search_arg[1].equals("null")==false) //DATE
        {
           where +=  " AND (DATE BETWEEN '"+search_arg[1]+"' AND '"+search_arg[2]+"')";

       }
        if(search_arg[3].equals("null") ||search_arg[3].equals("None")==false) //CATEGORY
        {
            where +=  " AND (CATEGORY='"+search_arg[3]+"')";
        }


       Log.d("SQL_string",where);

        String[] strings = new String[0];

        // where = "PROJ_NR=?";
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+PROJ_LOG+" "+where,null);

            strings = new String[cursor.getCount()];
            int i=0;
          //  cursor.moveToFirst();
            while (cursor.moveToNext())
            {
                String value ="";
                value += cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                value += ","+cursor.getString(cursor.getColumnIndexOrThrow("PROJ_NR"));
                value += ","+cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
                value += ","+cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
                value += ","+cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY"));
                value += ","+cursor.getString(cursor.getColumnIndexOrThrow("NOTE"));
                strings[i]=value;
                i++;
                Log.d("SQL_daten:",value);
            }

            cursor.close();
            return strings;

        }catch (Exception e)
        {
            Log.d("SQL_fehler:",e.getMessage());

            return   strings;
        }

    }
}
