package com.example.tabnav_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class m_database_ops  extends SQLiteOpenHelper implements SQL_finals
{

    public m_database_ops(@Nullable Context context) {
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

    public long add_manschine(ContentValues data)
    {
        Basic_funct bsf = new Basic_funct();


            long newRowId = -1;
            try
            {
                SQLiteDatabase db = this.getWritableDatabase();

                data.put("ID", bsf.gen_ID());
                data.put("DATE", bsf.date_refresh());
                data.put("TIME", bsf.time_refresh());

// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(TB_NAME_MASCHINEN_ITEMS,null,data);


            }catch (Exception e)
            {


            }

            return newRowId;

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

        return  strings;

    }

    public int delet_id(String proj_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {proj_id};
        String where = "ID=?";


        int response = db.delete(TB_NAME_MASCHINEN_ITEMS,where,selectionArgs);

        return response;
    }



}



