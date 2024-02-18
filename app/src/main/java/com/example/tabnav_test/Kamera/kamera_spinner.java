package com.example.tabnav_test.Kamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.db_controlls;

public class kamera_spinner<ID> extends SQLiteOpenHelper implements db_controlls, SQL_finals
{

    private  String where ="";
    private Context context;

    public kamera_spinner(@Nullable Context context)
    {

        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        /*
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_KAMERA_CONF+" (ID TEXT,NAME TEXT,DIR TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_LOG_CONF+" (ID TEXT,NAME TEXT,VALUE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+PROJ_LOG+" (ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,CATEGORY TEXT,NOTE TEXT,FAV_FLAG TEXT,CHECK_FLAG TEXT,UPLOAD_FLAG)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_MASCHINEN_ITEMS+" (ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_MASCHINEN_ENTRYS+" (ID TEXT,PROJ_NR TEXT,MASCH_ID TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,COUNTER DOUBLE)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_PROJEKTE+" (ID TEXT,NAME TEXT,SRC TEXT,SELECT_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_ZULIEFERER+" (ID TEXT,NAME TEXT,DATE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_TYP+" (ID TEXT,NAME TEXT,EINHEIT TEXT,FAV_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_LOG+" (ID TEXT,PROJEKT_ID TEXT,DATUM TEXT,LSNR TEXT,LIEFERANT_ID TEXT,MATERIAL_ID TEXT,MENGE TEXT,EINHEIT_ID TEXT,SRC TEXT,NOTIZ TEXT)");
    */
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    @Override
    public int updateOne(String proj_id, String name, String new_name, String new_dir)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { proj_id,name };
        where = "ID=? AND NAME=?";

        ContentValues values = new ContentValues();
        values.put("NAME", new_name);
        values.put("DIR", new_dir);

        int response = db.update(TB_NAME_KAMERA_CONF,values,where,selectionArgs);

        return response;

    }

    @Override
    public int deletOne(String proj_id, String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { proj_id,name };
        where = "ID=? AND NAME=?";

        int deletedRows = db.delete(TB_NAME_KAMERA_CONF,where,selectionArgs);

        return deletedRows;

    }

    @Override
    public int deletAll(String proj_id)
    {
        return 0;
    }

    @Override
    public long addOne(String proj_id, String name, String value)
    {
        long newRowId = -1;
        
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = { proj_id,name };
            String[] projection = {"NAME"};
            where = "ID=? AND NAME=?";
            
            Cursor cursor = db.query(TB_NAME_KAMERA_CONF,projection,where,selectionArgs,null,null,null);

            // Create a new map of values, where column names are the keys
            if ( cursor.getCount()==0)
            {
                ContentValues values = new ContentValues();
                values.put("ID", proj_id);
                values.put("NAME", name);
                values.put("DIR", value);
// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(TB_NAME_KAMERA_CONF,null,values);
            }
            cursor.close();    
            
        }catch (Exception e)
        {
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
        }
     

        return newRowId;

    }


    public String[] getOne(String proj_id, String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { proj_id,name };
        where = "ID=? AND NAME=?";
        String[] projection = {"NAME","DIR"};

        Cursor cursor =
                db.query(TB_NAME_KAMERA_CONF,projection,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        String dir_name= cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        String dir_url= cursor.getString(cursor.getColumnIndexOrThrow("DIR"));
        cursor.close();
        Log.d("DIR:::::",dir_url);
        String[] response = {dir_name,dir_url};

        return response;
    }

    @Override
    public String[] getall(String proj_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id};
        where = "ID=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_KAMERA_CONF,null,"ID=?",selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {
            String value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            strings[i]=value;
            i++;
         Log.d("Wert",value);
        }
        cursor.close();


        
        return strings;
    }

    @Override
    public String[] getallkeys() {
        return new String[0];
    }

    @Override
    public String[] getvalues() {
        return new String[0];
    }


}




