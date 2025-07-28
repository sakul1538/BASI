package com.example.tabnav_test.Listen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tabnav_test.SQL_finals;

public class list_main_db_ops extends SQLiteOpenHelper implements SQL_finals

{

    public list_main_db_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public String[] get_lists_form_projekt(String proj_id)
    {


        SQLiteDatabase dbr = this.getReadableDatabase();
        //select all rows whit proj_id and typ =1
        String [] where_args = {proj_id,"1"};
        String where = "PROJ_ID=? AND TYP=?";


        Cursor cursor = dbr.query(TB_LISTS_MAIN,null,where,where_args,null,null,null);
        //Put the Data in a String array and return it
        if(cursor.getCount() > 0)
        {
            String block = "";
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                block += cursor.getString(cursor.getColumnIndexOrThrow("NAME"))+",";
                cursor.moveToNext();
            }
            cursor.close();
            dbr.close();
            block = block.substring(0,block.length()-1);

            return block.split(",");
        }
        else
        {
            return new String[0];
        }
    }
}
