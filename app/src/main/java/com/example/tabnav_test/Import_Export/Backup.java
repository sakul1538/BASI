package com.example.tabnav_test.Import_Export;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.material.material_database_ops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class Backup extends SQLiteOpenHelper implements SQL_finals
{
    Context context;


    public Backup(@Nullable Context context)
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

    public void create_backup(String table,String where , String [] where_args,String [] select_colums,String path,String filename) throws JSONException
    {

        Basic_funct bsf = new Basic_funct();
        material_database_ops mdo= new material_database_ops(context);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table,select_colums,where,where_args,null,null,null);
        String[] colums = cursor.getColumnNames();

        JSONArray data = new JSONArray();

        while (cursor.moveToNext())
        {
            JSONObject t = new JSONObject();
            for(String c: colums)
            {
                try {
                    t.put(c,  cursor.getString(cursor.getColumnIndexOrThrow(c.toString())));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }
            data.put(t);
        }
        File f = new File(path);
        f.mkdirs();
        try {
            f.createNewFile();

            FileWriter fw = new FileWriter(path+filename);
            fw.write(data.toString());
            fw.close();

            AlertDialog.Builder create_backup_report_dialog  = new AlertDialog.Builder(context);
            create_backup_report_dialog.setTitle("Export Report");
            create_backup_report_dialog.setMessage("Backup gespeichert unter: \n\n"+path+filename);
            create_backup_report_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            create_backup_report_dialog.setNegativeButton("URL Kopieren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    bsf.copy_to_clipboard(path+filename,context);
                }
            });

            create_backup_report_dialog.show();

        } catch (IOException e)
        {
            Toast.makeText(context, "Backup erstellen Fehlgeschlagen!:  \n"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void restore_backup(String table,String filename, Boolean overwrite) throws FileNotFoundException {
        Log.d("TABLE",table);
        Log.d("Overwrite",overwrite.toString());

        String source_path = filename.replace("/document/primary:", Environment.getExternalStorageDirectory().toString()+"/");
        Log.d("filename",filename);
        SQLiteDatabase dbw = this.getWritableDatabase();
        SQLiteDatabase dbr = this.getReadableDatabase();

        Cursor cursor = dbr.query(table,null,null,null,null,null,null);
        String []colums = cursor.getColumnNames();
        String where ="";
        for(String c : colums)
        {
            where+= c+"=? AND ";
        }
        where = where.substring(0,where.lastIndexOf("AND"));

        Log.d("BASI WHERE",where);

        material_database_ops mdo = new material_database_ops(context);

        InputStream in2 = null;
        try {
            in2 = new FileInputStream(new File(source_path));
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(in2,"UTF-8"));
                reader.beginArray();
                while(reader.hasNext())
                {
                    reader.beginObject();
                    ContentValues output_data = new ContentValues();
                    String args_string= "";

                    while (reader.hasNext())
                    {
                        String name =reader.nextName();
                        String value =reader.nextString();
                        args_string+=value+",";
                        output_data.put(name,value);
                    }
                    String[] where_args = args_string.split(",");

                    Cursor find_similar = dbr.query(table,null,where,where_args,null,null,null);
                    if(find_similar.getCount()==0)
                    {
                        dbw.insert(table,null,output_data);
                    }
                    reader.endObject();
                }
                reader.endArray();

            } catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

}
