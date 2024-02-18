package com.example.tabnav_test.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.db_controlls;

public class log_database_ops  extends SQLiteOpenHelper implements SQL_finals
{
    Context context;

    public log_database_ops(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("CREATE TABLE "+BASI_LOG+" (ID TEXT,PROJEKT_NR TEXT,DATE TEXT,TIME TEXT,NOTE TEXT,CHECK_FLAG TEXT,FAV_FLAG TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
    }

    public boolean add(String projekt_id, ContentValues data)
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

    public  boolean udpate(String id,ContentValues update_data)
    {
        try {
            SQLiteDatabase dbw = this.getWritableDatabase();
            long colum  = dbw.update(BASI_LOG,update_data,"ID=?",new String[]{id});
            dbw.close();
            if(colum != -1)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Boolean delet(String id)
    {
        SQLiteDatabase dbw = this.getWritableDatabase();
        long colum   = dbw.delete(BASI_LOG,"ID=?",new String[]{id});
        
        return  false;

    }

}
