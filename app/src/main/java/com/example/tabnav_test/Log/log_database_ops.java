package com.example.tabnav_test.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.db_controlls;

public class log_database_ops  extends SQLiteOpenHelper implements SQL_finals
{
    Context context;

    public log_database_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null,1);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
    }

    public boolean add( ContentValues data)
    {
        try {
            SQLiteDatabase dbw = this.getWritableDatabase();

            long colum = dbw.insert(BASI_LOG,null,data);
            dbw.close();

            if(colum != -1)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public  ContentValues get_entry(String id)
    {
        SQLiteDatabase dbr  = this.getReadableDatabase();

        ContentValues output = new ContentValues();
        Cursor cursor  = dbr.query(BASI_LOG,null,"ID=?",new String[]{id},null,null,null);


        if(cursor.getCount() ==1)
        {
            cursor.moveToNext();

            for (String colum :cursor.getColumnNames())
            {
               output.put(colum,cursor.getString(cursor.getColumnIndexOrThrow(colum)));
            }
        }
        cursor.close();
        dbr.close();
        return output;
    }

    public  boolean udpate(ContentValues update_data)
    {
            SQLiteDatabase dbw = this.getWritableDatabase();
            long colum  = dbw.update(BASI_LOG,update_data,"ID=?",new String[]{update_data.get("ID").toString()});
            dbw.close();

            if(colum != -1)
            {
                return true;
            }
            else
            {
                return false;
            }

    }

    public Boolean delet(String id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        long colum   = dbw.delete(BASI_LOG,"ID=?",new String[]{id});

        if(colum >-1)
        {
            return  true;
        }
        else
        {
            return false;
        }
    }

    public String[] get_entrys(String projekt_id)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();

        Cursor c  =dbr.query(BASI_LOG,null,"PROJEKT_NR=?",new String[]{projekt_id},null,null,null);

        if(c.getCount() >0)
        {
            String[] output = new String[c.getCount()];
            while(c.moveToNext())
            {
                String row = "";

                for(String name : c.getColumnNames())
                {
                    row += c.getString(c.getColumnIndexOrThrow(name))+",";
                }
                output[c.getPosition()] = row.substring(0,row.length()-1).toString();
            }

            dbr.close();
            c.close();
            return output;
        }
        else
        {
            String[] output = new String[1];
            String row_alt = "";

            for(String name : c.getColumnNames())
            {
                row_alt += "NULL,";
            }

            output[0]=row_alt.substring(0,row_alt.length()-1);
            dbr.close();
            c.close();

            return output;
        }
    }

    public int entry_count(String projekt_id)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();

        Cursor c  =dbr.query(BASI_LOG,null,"PROJEKT_NR=?",new String[]{projekt_id},null,null,null);
        dbr.close();
        c.close();

        return c.getCount();
    }

    public String[] get_colum_names()
    {
        SQLiteDatabase dbr = this.getReadableDatabase();

        Cursor c  =dbr.query(BASI_LOG,null,null,null,null,null,null);
        String[] colums = c.getColumnNames();

        for(String i:colums)
        {
            Log.d("BASI",i);

        }
        dbr.close();
        c.close();

        return colums;
    }

    public Boolean get_check(String id)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG,new String[]{"CHECK_FLAG"},"ID=?",new String[]{id},null,null,null);
        if(cursor.getCount() == 1)
        {
            //Rückgabewert als Return

        }

            return false;

    }

    public void set_check(String id)
    {

    }




}
