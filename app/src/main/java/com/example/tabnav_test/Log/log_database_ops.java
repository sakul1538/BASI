package com.example.tabnav_test.Log;

import android.app.AlertDialog;
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
import com.example.tabnav_test.Log_data;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.db_controlls;
import com.example.tabnav_test.projekt_ops;

import java.util.ArrayList;

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

            return colum != -1;
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

        return colum != -1;

    }

    public Boolean delet(String id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        long colum   = dbw.delete(BASI_LOG,"ID=?",new String[]{id});

        return colum > -1;
    }

    public Boolean delet_all(String projekt_id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        long colum   = dbw.delete(BASI_LOG,"PROJEKT_NR=?",new String[]{projekt_id});

        return colum > -1;
    }

    public int delet_all_done(String projekt_id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        int colum   = dbw.delete(BASI_LOG,"PROJEKT_NR=? AND CHECK_FLAG='true'",new String[]{projekt_id});

        return colum;


    }

    public Boolean delet_all_exept_fav_flag(String projekt_id,Context context2)
    {
        Boolean response= false;
        String entry_count  = String.valueOf(this.entry_count(projekt_id));

        SQLiteDatabase dbw = this.getWritableDatabase();
        int colum   = dbw.delete(BASI_LOG,"PROJEKT_NR=? AND FAV_FLAG='false'",new String[]{projekt_id});

        if(colum>0)
        {
            Toast.makeText(context2, colum +" von "+ entry_count+"  Gelöscht!", Toast.LENGTH_SHORT).show();
            response = true;
        }

        else
        {
            Toast.makeText(context2, "Error:\n  deletet<0", Toast.LENGTH_SHORT).show();
        }


        return response;
        //eintragszähler  implementieren
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
                output[c.getPosition()] = row.substring(0,row.length()-1);
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

        if (set_state)
        {
            update_data.put("CHECK_FLAG", "true");

        } else if (!set_state)
        {
            update_data.put("CHECK_FLAG", "false");
        }

       long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        return response == 1;
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

        if (set_state)
        {
            update_data.put("FAV_FLAG", "true");

        } else if (!set_state)
        {
            update_data.put("FAV_FLAG", "false");
        }

        long response=  dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
        return response == 1;
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
        return response == 1;
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
        return response == 1;
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
                output[cursor.getPosition()] = row.substring(0, row.length() - 1);

            }
        }

        return output;
    }

    public String [] full_search(String projekt_id,String where)
    {

        try {
            SQLiteDatabase dbr = this.getReadableDatabase();

            Cursor cursor = dbr.query(BASI_LOG,null,where ,null,null,null,null);

            if(cursor.getCount() >0)
            {
                int array_pos= 0;
                String [] output_array =new String[cursor.getCount()];

                while(cursor.moveToNext())
                {
                    String colect = "";

                    for(String colum: cursor.getColumnNames())
                    {
                     colect += cursor.getString(cursor.getColumnIndexOrThrow(colum))+",";
                    }
                    Log.d("BASIss",colect);
                    output_array[array_pos] = colect.substring(0,colect.length()-1);
                    array_pos++;
                }
                cursor.close();

                return output_array;
            }
            else
            {

                String[] output_array = new String[1];
                String row_alt = "";

                for(String name : cursor.getColumnNames())
                {
                    row_alt += "NULL,";
                }

                output_array[0]=row_alt.substring(0,row_alt.length()-1);
                cursor.close();

                Log.d("BASI","CURSOR<1");

                return  output_array;
            }

        } catch (IllegalArgumentException e)
        {

            throw new RuntimeException(e);
        }

    }



}
