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
import com.example.tabnav_test.projekt_ops;

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

    public Boolean delet_all(String projekt_id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        long colum   = dbw.delete(BASI_LOG,"PROJEKT_NR=?",new String[]{projekt_id});

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

        Cursor c  =dbr.query(BASI_LOG,null,"PROJEKT_NR=?",new String[]{projekt_id},null,null,"DATE DESC, TIME DESC");

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
        Boolean state = false;
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG,null,"ID=?",new String[]{id},null,null,null);
        cursor.moveToNext();
        if(cursor.getCount() == 1)
        {
            if(cursor.getString(cursor.getColumnIndexOrThrow("CHECK_FLAG")).equals("true"))
            {
                state =true;

            }
        }
            return state;

    }

    public Boolean set_check(String id,Boolean set_state)
    {

        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues update_data = new ContentValues();

        if (set_state ==true)
        {
            update_data.put("CHECK_FLAG", "true");

        } else if (set_state == false)
        {
            update_data.put("CHECK_FLAG", "false");
        }

       long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        if(response ==1)
        {
            return true;
        }else
        {
            return  false;
        }
    }

    public Boolean get_flav_flag(String id)
    {
        Boolean state = false;
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG,null,"ID=?",new String[]{id},null,null,null);
        cursor.moveToNext();
        if(cursor.getCount() == 1)
        {
            if(cursor.getString(cursor.getColumnIndexOrThrow("FAV_FLAG")).equals("true"))
            {
                state =true;
            }
        }
        return state;

    }

    public Boolean set_fav_flag(String id,Boolean set_state)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues update_data = new ContentValues();

        if (set_state ==true)
        {
            update_data.put("FAV_FLAG", "true");

        } else if (set_state == false)
        {
            update_data.put("FAV_FLAG", "false");
        }

        long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        if(response ==1)
        {
            return true;
        }else
        {
            return  false;
        }
    }

    public String get_time(String id)
    {
        String time= "NULL";
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG,null,"ID=?",new String[]{id},null,null,null);
        cursor.moveToNext();
        if(cursor.getCount() == 1)
        {
          time= cursor.getString(cursor.getColumnIndexOrThrow("TIME"));

        }
        return time;
    }

    public Boolean set_time(String id,String new_time)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues update_data = new ContentValues();
        update_data.put("TIME", new_time);

        long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        if(response ==1)
        {
            return true;
        }else
        {
            return  false;
        }
    }
    public String get_date(String id)
    {
        String date= "NULL";
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG,null,"ID=?",new String[]{id},null,null,null);
        cursor.moveToNext();
        if(cursor.getCount() == 1)
        {
            date= cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
        }
        return date;
    }
    public Boolean set_date(String id,String new_date)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        ContentValues update_data = new ContentValues();
        update_data.put("DATE",new_date);

        long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        if(response ==1)
        {
            return true;
        }else
        {
            return  false;
        }
    }
    public String[] search_text(String projekt_id, String text_value)
    {
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor = dbr.query(BASI_LOG, null, "PROJEKT_NR=? AND (NOTE LIKE '%"+text_value+"%')", new String[]{projekt_id}, null, null, null);
        String[] output =  new String[cursor.getCount()];

        if (cursor.getCount() > 0)
        {
            output = new String[cursor.getCount()];
            while (cursor.moveToNext())
            {
                String row = "";

                for (String name : cursor.getColumnNames())
                {
                    row += cursor.getString(cursor.getColumnIndexOrThrow(name)) + ",";
                }
                output[cursor.getPosition()] = row.substring(0, row.length() - 1).toString();

            }
        }

        return output;
    }

    public void full_search(String projekt_id,String where, String []where_args)
    {
        try {
            SQLiteDatabase dbr = this.getReadableDatabase();


            Cursor cursor = dbr.query(BASI_LOG,null,where,where_args,null,null,null);

            if(cursor.getCount() >0)
            {
                while(cursor.moveToNext())
                {
                    String output = "" ;

                    for(String colum: cursor.getColumnNames())
                    {
                        output += colum+"="+cursor.getString(cursor.getColumnIndexOrThrow(colum))+",";
                    }
                    Log.d("BASI",output);
                }
            }
            else
            {
                Log.d("BASI","CURSOR<1");
            }
        } catch (IllegalArgumentException e)
        {

            Log.d("BASI",e.getMessage().toString());

            throw new RuntimeException(e);
        }


    }














}
