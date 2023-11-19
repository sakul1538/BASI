package com.example.tabnav_test.config_favorite_strings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.material.material_database_ops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class config_fav_ops extends SQLiteOpenHelper implements SQL_finals

{
    Context context;

    Basic_funct bsf = new Basic_funct();
    public config_fav_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    public String[] favorite_strings_list(boolean extras) //Gibt alle Einträge des Projektes zurück => alte Version
    {
        material_database_ops mdo = new material_database_ops(context);
        String [] where_args = {mdo.get_selectet_projekt_id(),"0","FAVORITE_STRING"};
        String where = "(ID=? OR ID=?) AND NAME=?";
        SQLiteDatabase dbr = this.getReadableDatabase();
        String[] col = new String[]{"VALUE","ID"};

        Cursor cursor = dbr.query(TB_NAME_LOG_CONF, col, where, where_args, null, null, "VALUE ASC ");
        String[] strings = new String[cursor.getCount()];

        int i = 0;
        while (cursor.moveToNext())
        {
            strings[i] = bsf.URLdecode(cursor.getString(cursor.getColumnIndexOrThrow("VALUE")));
            if(extras==true)
            {
                if(cursor.getString(cursor.getColumnIndexOrThrow("ID")).contentEquals("0"))
                {
                    strings[i] += " (Global)";
                }
            }
            i++;
        }
        cursor.close();
        dbr.close();
        return strings;
    }

    public void favorite_strings_update(String value_old,String value_new,boolean global_status)
    {
        try
        {
            material_database_ops mdo = new material_database_ops(context);
            String [] where_args = {mdo.get_selectet_projekt_id().toString(),"0","FAVORITE_STRING", bsf.URLencode(value_old) };
            String where = "(ID=? OR ID=?) AND (NAME=? AND VALUE=?)";
            ContentValues data_new = new ContentValues();
            data_new.put("VALUE",bsf.URLencode(value_new));
            if(global_status==true)
            {
                data_new.put("ID","0");

            }else
            {
                data_new.put("ID",mdo.get_selectet_projekt_id().toString());
            }
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(TB_NAME_LOG_CONF,data_new,where,where_args);
            db.close();

        }catch (Exception e)
        {
            bsf.exeptiontoast(e,context);
        }
    }

    public void delete_all()
    {
        try
        {
            material_database_ops mdo = new material_database_ops(context);
            String [] where_args = {mdo.get_selectet_projekt_id().toString(),"FAVORITE_STRING" };
            String where = "ID=? AND NAME=?";

            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TB_NAME_LOG_CONF,where,where_args);
            Toast.makeText(context,"Alle Einträge wurden gelöscht!"  , Toast.LENGTH_SHORT).show();
            db.close();

        }catch (Exception e)
        {
            bsf.exeptiontoast(e,context);
        }

    }
    public void delete_element(String name)
    {
        try
        {
            material_database_ops mdo = new material_database_ops(context);
            String [] where_args = {mdo.get_selectet_projekt_id().toString(),"0","FAVORITE_STRING",bsf.URLencode(name)};
            String  where = "(ID=? OR ID=?) AND (NAME=? AND VALUE=?)";
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TB_NAME_LOG_CONF,where,where_args);

            Toast.makeText(context,"Text: "+name +" gelöscht!"  , Toast.LENGTH_SHORT).show();
            db.close();

        }catch (Exception e)
        {
            bsf.exeptiontoast(e,context);
        }

    }

    public void add_favorite_string(String element)
    {
        material_database_ops mdo = new material_database_ops(context);

        SQLiteDatabase dbr = this.getReadableDatabase();
        String[] col = new String[]{"VALUE"};
        String[] selectionArgs = {mdo.get_selectet_projekt_id(),"0","FAVORITE_STRING", bsf.URLencode(element) };
        String  where = "(ID=? OR ID=?) AND (NAME=? AND VALUE=?)";
        Cursor cursor = dbr.query(TB_NAME_LOG_CONF, col,where, selectionArgs, null, null, null);

        if(cursor.getCount()==0)
        {

            try
            {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues data = new ContentValues();
                data.put("NAME","FAVORITE_STRING");
                data.put("VALUE",bsf.URLencode(element.trim()));
                data.put("ID",mdo.get_selectet_projekt_id());

                long row_nr= db.insert(TB_NAME_LOG_CONF,null,data);
                bsf.succes_msg(" Element \'"+element.trim()+"\'  hinzugefügt!",context);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Speicheroptionen");
                alertDialogBuilder.setMessage("Speicherung => GLOBAL oder LOKAL?");

                alertDialogBuilder.setPositiveButton("LOKAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.setNeutralButton("GLOBAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        data.put("ID","0");
                        String []where_args ={mdo.get_selectet_projekt_id(),"FAVORITE_STRING",bsf.URLencode(element)};
                        String where = "ID=? AND NAME=? AND VALUE=?";
                        db.update(TB_NAME_LOG_CONF,data,where,where_args);
                        config_fav.refresh_spinner_favorite_strings_settings(context);
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();



            }catch (Exception e)
            {
                bsf.exeptiontoast(e,context);
            }

        }
        else
        {
         bsf.info_msg("Element "+ element+ " existiert bereits!",context);
        }


    }



}


