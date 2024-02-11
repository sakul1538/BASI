package com.example.tabnav_test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tabnav_test.static_finals;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class db_ops extends SQLiteOpenHelper implements SQL_finals
{
    // ----------------------------------------------------------------- Variablen
    // ----------------------------------------------------------------- Variablen  String, char
    // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
    // ----------------------------------------------------------------- Variablen 	Boolean
    // ----------------------------------------------------------------- Instanzen
    // ----------------------------------------------------------------- TextView
    // ----------------------------------------------------------------- AutoCompleteTextView
    // ----------------------------------------------------------------- EditText
    // ----------------------------------------------------------------- Button
    // ----------------------------------------------------------------- ImageButtons
    // ----------------------------------------------------------------- ImageView
    // ----------------------------------------------------------------- ListView
    // ----------------------------------------------------------------- RecyclerView
    // ----------------------------------------------------------------- Spinner
    // ----------------------------------------------------------------- CheckBox
    // ----------------------------------------------------------------- RadioButton
    // ----------------------------------------------------------------- Switch
    // ----------------------------------------------------------------- SeekBar
    // ----------------------------------------------------------------- ProgressBar
    // ----------------------------------------------------------------- Switch
    // ----------------------------------------------------------------- ScrollView
    // ----------------------------------------------------------------- Layouts
    // ----------------------------------------------------------------- END
    public db_ops(@Nullable Context context)
    {

        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

        sqLiteDatabase.execSQL("CREATE TABLE "+BASI_PROJEKTE+" (ID TEXT,DATE TEXT,PROJEKT_NR TEXT,NAME TEXT,DIR_ROOT TEXT,DIR_SUB TEXT,STATUS_FLAG TEXT)");

        //Alte Tabellen
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_KAMERA_CONF+" (ID TEXT,NAME TEXT,DIR TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_LOG_CONF+" (ID TEXT,NAME TEXT,VALUE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+PROJ_LOG+" (ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,CATEGORY TEXT,NOTE TEXT,FAV_FLAG TEXT,CHECK_FLAG TEXT,UPLOAD_FLAG)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_MASCHINEN_ITEMS+" (ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME_MASCHINEN_ENTRYS+" (ID TEXT,PROJ_NR TEXT,MASCH_ID TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,COUNTER DOUBLE)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_PROJEKTE+" (ID TEXT,NAME TEXT,SRC TEXT,SELECT_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_ZULIEFERER+" (ID TEXT,NAME TEXT,DATE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_TYP+" (ID TEXT,NAME TEXT,EINHEIT TEXT,FAV_FLAG TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_MATERIAL_LOG+" (ID TEXT,PROJEKT_ID TEXT,DATUM TEXT,LSNR TEXT,LIEFERANT_ID TEXT,MATERIAL_ID TEXT,MENGE TEXT,EINHEIT_ID TEXT,SRC TEXT,NOTIZ TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }
    public void init(){
        SQLiteDatabase dbr = this.getReadableDatabase();
        Cursor cursor  = dbr.query(BASI_PROJEKTE,new String[]{"ID"},null,null,null,null,null);
        if(cursor.getCount() == 0)
        {

            // "ID,DATE,PROJEKT_NR,NAME,DIR_ROOT,DIR_SUB,STATUS_FLAG"
            Basic_funct bsf = new Basic_funct();
            SQLiteDatabase dbw = this.getWritableDatabase();
            ContentValues init_data = new ContentValues();
            init_data.put("ID",bsf.gen_UUID());
            init_data.put("DATE",bsf.date_refresh_database());
            init_data.put("PROJEKT_NR",("1"));
            init_data.put("NAME","DEFAULT");
            init_data.put("DIR_ROOT", Environment.getExternalStorageDirectory().toString());
            init_data.put("DIR_SUB","[{\"NAME\":\"DEFAULT\",\"DIR\":\"\"}]");
            init_data.put("STATUS_FLAG","1");

            try {
                dbw.insert(BASI_PROJEKTE,null,init_data);

            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            dbw.close();
        }
        dbr.close();
    }

    public boolean entry_exist(String table,ContentValues args)
    {
        try {
            SQLiteDatabase dbr = this.getReadableDatabase();
            String where = "";
            String[] where_args = new String[args.size()];
            int array_pointer =0;
            for(String key: args.keySet())
            {
               where += key+"=? AND ";
               where_args[array_pointer] = args.get(key).toString();
               array_pointer++;
            }
            where = where.substring(0,where.lastIndexOf("AND"));
            Cursor cursor  = dbr.query(table,null,where,where_args,null,null,null);
            int items = cursor.getCount();
            dbr.close();
            cursor.close();

            if(items>0)
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

    public boolean insert(String tablename, ContentValues input_data)
    {
        long row_nr =-1;

        try {
            SQLiteDatabase dbw = this.getWritableDatabase();
            row_nr = dbw.insert(tablename,null,input_data);
            dbw.close();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if(row_nr ==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean update(String tablename, ContentValues update_data)
    {
        long response =-1;

        try {
            SQLiteDatabase dbw = this.getWritableDatabase();
            response = dbw.update(tablename,update_data,"ID=?",new String[]{update_data.get("ID").toString()});
            dbw.close();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if(response ==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}


