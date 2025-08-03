package com.example.tabnav_test.Listen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tabnav_test.SQL_finals;

import java.util.UUID;

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

            String [] block = new String[cursor.getCount()];
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                String data = "";
                data += cursor.getString(cursor.getColumnIndexOrThrow("ID"))+",";
                data += cursor.getString(cursor.getColumnIndexOrThrow("NAME"))+",";

                //test NOTE for empty
                if(cursor.getString(cursor.getColumnIndexOrThrow("NOTE")).isEmpty())
                {
                    data += " ";
                }
                else
                {
                    data += cursor.getString(cursor.getColumnIndexOrThrow("NOTE"))+"";
                }
                block[cursor.getPosition()] = data;
                cursor.moveToNext();
            }
            cursor.close();
            dbr.close();


            return block;
        }
        else
        {
            return new String[0];
        }
    }

    public int delete_list(String list_id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        String [] where_args = {list_id};
        String where = "ID=?";
        int i = dbw.delete(TB_LISTS_MAIN,where,where_args);
        dbw.close();
        return i;
    }

    public int update_list(String list_id,String name,String note)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        String [] where_args = {list_id};
        String where = "ID=?";
        ContentValues cv = new ContentValues();
        cv.put("NAME",name);
        cv.put("NOTE",note);
        int i = dbw.update(TB_LISTS_MAIN,cv,where,where_args);
        dbw.close();
        return i;
    }
    public String get_items_id(String list_id)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();
        String [] where_args = {list_id};
        String where = "ID=?";
        String items_id= "";
        Cursor cursor = dbr.query(TB_LISTS_MAIN,null,where,where_args,null,null,null);
        if(cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            items_id= cursor.getString(cursor.getColumnIndexOrThrow("ITEMS_ID"));
            cursor.close();
            dbr.close();

        }
        return items_id;

    }

    public String get_items_form_list(String itemsId)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();
        String [] where_args = {itemsId};
        String where = "PARENT_ID=?";
        Cursor cursor = dbr.query(TB_LISTS_ITEMS,null,where,where_args,null,null,null);
        String data = "";
        if(cursor.getCount() > 0)
        {

            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {

                data += cursor.getString(cursor.getColumnIndexOrThrow("NAME"))+",";
                cursor.moveToNext();
            }
            return data;

            }
        else
        {
            return data;
        }

    }

    public long add_item(String parent_id, String value)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("ID", String.valueOf(UUID.randomUUID()));
        cv.put("NAME",value);
        cv.put("PARENT_ID",parent_id);
        cv.put("POSITION","0");
        cv.put("CHECK_FLAG","0");

        long i = dbw.insert(TB_LISTS_ITEMS,null,cv);

        return i;
    }
}
