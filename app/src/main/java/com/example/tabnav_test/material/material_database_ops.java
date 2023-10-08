package com.example.tabnav_test.material;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.SQL_finals;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class material_database_ops extends SQLiteOpenHelper implements SQL_finals
{
    Basic_funct bsf = new Basic_funct();
    Context context;
    public material_database_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {


    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    //Einträge in der Datenbank

    public long add_material_log_entry(ContentValues data)
    {
        //TODO auf Duplikate Prüfen
        SQLiteDatabase wdb = this.getWritableDatabase();

        long newRowId = wdb.insert(TB_MATERIAL_LOG,null,data);

        return newRowId;
    }

    public long delet_material_log_entry(String entry_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { entry_id };
        String where = "ID=?";

        int deletedRows = db.delete(TB_MATERIAL_LOG,where,selectionArgs);

        return deletedRows;
    }

    public void update_material_log_entry(ContentValues data_new )

    {

        //Eintrag nach ID ändern
        // Nach weitereren einträgen Suchen, die der Selben LSNR hat und abändern
        //Nach medien Suchen und entsprechend den Dateinamen anpassen.

        ContentValues data_old = this.material_get_entry_id(data_new.get("ID").toString());
        Log.d("BASI OLD",data_old.toString());
        Log.d("BASI New",data_new.toString());
        //String s = media_scanner(data_old);
        //Log.d("BASI_Media",s);

        SQLiteDatabase dbw = this.getWritableDatabase();


        String[] selectionArgs = { data_new.get("ID").toString() };
        String where = "ID=?";

        long response = dbw.update(TB_MATERIAL_LOG,data_new,where,selectionArgs);


    }


    public ContentValues material_get_entry_id(String id) //Gibt einen Spezifischen Eintrag zurück definiert durch die ID des eintrages als ContentValue data
    {

        //Fixme Projekt ID anpassen
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { id };
        String where = "ID=?";

        Cursor cursor = db.query(TB_MATERIAL_LOG,null,where,selectionArgs,null,null,null);
        ContentValues data = new ContentValues();

        if(cursor.getCount() >0)
        {
            cursor.moveToFirst();

            data.put("ID",cursor.getString(cursor.getColumnIndexOrThrow("ID")));
            data.put("PROJEKT_ID",cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_ID")));
            data.put("DATUM",cursor.getString(cursor.getColumnIndexOrThrow("DATUM")));
            data.put("LSNR",cursor.getString(cursor.getColumnIndexOrThrow("LSNR")));
            data.put("LIEFERANT_ID",cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID")));
            data.put("MATERIAL_ID",cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID")));
            data.put("MENGE",cursor.getString(cursor.getColumnIndexOrThrow("MENGE")));
            data.put("EINHEIT_ID",cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID")));
            data.put("SRC",cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            data.put("NOTIZ",cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ")));
        }
        else
        {
            data.put("ID","NULL"); //Wenn null = keine Einträge gefunden.
        }
        cursor.close();
        db.close();

       return  data;
    }

    public String[] material_entrys_list() //Gibt alle Einträge des Projektes zurück
    {

        //Fixme Projekt ID anpassen

        material_database_ops mdo = new material_database_ops(context);
        String proj_id =mdo.get_selectet_projekt_root().split(",")[1]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { proj_id };
        String where = "PROJEKT_ID=?";



        Cursor cursor = db.query(TB_MATERIAL_LOG,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATUM"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MENGE"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
            strings[i] +=","+bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ"));
            i++;
        }
        cursor.close();
        db.close();
        return  strings;
    }

    //Projekte Funktionen
    public void projekt_add(ContentValues data)
    {
        //TODO auf Duplikate Prüfen
        SQLiteDatabase wdb = this.getWritableDatabase();

        long newRowId = wdb.insert(TB_MATERIAL_PROJEKTE,null,data);

    }
     public String[] projekt_list_all()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME", "ID" };

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,columns,null,null,null,null,null);
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            strings[i] +="["+cursor.getString(cursor.getColumnIndexOrThrow("ID"))+"]";
            i++;
        }
        cursor.close();
        db.close();

        return  strings;
    }

    public int projekt_delet(String proj_id)
    {
        //FIXIT  auf Projekt mit Status 1 darf nicht gelöscht werden!

        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { proj_id };
        String where = "ID=?";

        int deletedRows = db.delete(TB_MATERIAL_PROJEKTE,where,selectionArgs);

        return deletedRows;

    }

    public int update_projekt(String id,ContentValues data)
    {
        int response =0;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { id };
        String where = "ID=?";

        response = db.update(TB_MATERIAL_PROJEKTE,data,where,selectionArgs);

        return response;
    }

    public String get_projekt_data(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {id};
        String where = "ID=?";

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
        String value ="";
        while (cursor.moveToNext())
        {
            value = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("SRC"));

        }
        cursor.close();
        db.close();

        return  value;

    }

    public void select_projekt(String id)
    {
            //reset von allen
            int response =0;
            SQLiteDatabase db = this.getWritableDatabase();
            String[] selectionArgs = { "1" };
            String where = "SELECT_FLAG=?";
            ContentValues  data = new ContentValues();
            data.put("SELECT_FLAG","0");
            response = db.update(TB_MATERIAL_PROJEKTE,data,where,selectionArgs);

            //Projekt selektieren
            String [] selectionArgs_select ={id};
           String where_select = "ID=?";

            ContentValues  data_select = new ContentValues();
            data.put("SELECT_FLAG","1");

            response = db.update(TB_MATERIAL_PROJEKTE,data,where_select,selectionArgs_select);

    }

    public String get_selectet_projekt()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {"1"};
        String where = "SELECT_FLAG=?";

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
        String value ="";
        while (cursor.moveToNext())
        {
            value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            value +="["+cursor.getString(cursor.getColumnIndexOrThrow("ID"))+"]";
        }
        cursor.close();
        db.close();
        return  value;
    }

    public String get_selectet_projekt_root()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {"1"};
        String where = "SELECT_FLAG=?";

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
        String value ="";

        while (cursor.moveToNext())
        {
            value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("SRC"));
        }
        cursor.close();
        db.close();
        return  value;
    }


    //Zulieferer funktionen
    public void add_zulieferer(String zulieferer_name)
    {
        //TODO  auf Duplikate prüfen$

        String[] selectionArgs = {zulieferer_name};
        long response = 0;

        //Auf Duplikate prüfen
        Boolean check_exist = this.find_similar(TB_MATERIAL_ZULIEFERER,selectionArgs,"NAME=?");

        if(check_exist == false)
        {
            SQLiteDatabase wdb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("ID",bsf.gen_ID());
            data.put("NAME",zulieferer_name);
            data.put("DATE",bsf.date_refresh()+ " "+bsf.time_refresh());
            long newRowId = wdb.insert(TB_MATERIAL_ZULIEFERER,null,data);
        }

    }

    public String[] zulieferer_list_all()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME","ID","DATE" };

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER,columns,null,null,null,null,"DATE DESC");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            i++;
        }
        cursor.close();
        db.close();

        return  strings;

    }

    public String get_id_zulieferer(String zulieferer_name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] selectionArgs = { zulieferer_name };
        String where = "NAME=?";
        String[] columns = { "ID" };

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER,columns,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        cursor.moveToFirst();
        String id=cursor.getString(cursor.getColumnIndexOrThrow("ID"));

        cursor.close();
        db.close();

        return  id;
    }


    public String get_zulieferer_param(String[] selectionArgs, String where,String[] colum )
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String param="null";

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER, colum, where, selectionArgs, null, null, null);
        if(cursor.getCount() >0)
        {
            String[] strings = new String[cursor.getCount()];

            cursor.moveToFirst();
            param= cursor.getString(cursor.getColumnIndexOrThrow(colum[0]));
        }


        cursor.close();
        db.close();

        return param;
    }




    public void update_zulieferer(String from_name, String to_name)
    {

        ContentValues data = new ContentValues();
        data.put("NAME",to_name);

        SQLiteDatabase dbw = this.getWritableDatabase();
        String[] selectionArgs = { from_name };
        String where = "NAME=?";
        dbw.update(TB_MATERIAL_ZULIEFERER,data,where,selectionArgs);

    }

    public void zulieferer_delet(String zulieferer_name)
    {
        //TODO  auf Projekt mit Status 1 darf nicht gelöscht werden!
        //TODO Bestätigen

        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { zulieferer_name };
        String where = "NAME=?";

        db.delete(TB_MATERIAL_ZULIEFERER,where,selectionArgs);

    }


    //Artikel funktionen
    public long add_artikel_to_list(String artikel,String einheit)
    {


        String[] selectionArgs = { artikel.trim(),einheit.trim() };
        long response = 0;

        //Auf Duplikate prüfen
        Boolean check_exist = this.find_similar(TB_MATERIAL_TYP,selectionArgs,"NAME=? AND EINHEIT=?");

        if(check_exist == false)
        {
            SQLiteDatabase wdb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("ID",bsf.gen_ID());
            data.put("NAME",artikel.trim());
            data.put("EINHEIT",einheit.trim());
            response = wdb.insert(TB_MATERIAL_TYP,null,data);
        }

        return  response;
    }


    public String[] artikel_list_all()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME","EINHEIT" };

        Cursor cursor = db.query(TB_MATERIAL_TYP,columns,null,null,null,null,"NAME ASC");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            strings[i] +=" ["+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT"))+"]";
            i++;
        }
        cursor.close();
        db.close();

        return  strings;
    }
    public void artikel_delet(String artikel_name,String artikel_einheit)
    {
        //TODO Bestätigen

        SQLiteDatabase dbw = this.getWritableDatabase();
        String[] selectionArgs = { artikel_name.trim(),artikel_einheit.trim() };
        String where = "NAME=? AND EINHEIT=?";

       int r =  dbw.delete(TB_MATERIAL_TYP,where,selectionArgs);
        Log.d("BASI",String.valueOf(r) );
        dbw.close();


    }
        public String artikel_counter()
        {
            //TODO Bestätigen

            SQLiteDatabase db = this.getReadableDatabase();
            String[] columns = {"ID"};

            Cursor cursor = db.query(TB_MATERIAL_TYP,columns,null,null,null,null,null);
            String c =  String.valueOf(cursor.getCount());

            db.close();

            return c;
        }

    public long update_artikel(String artikel, String einheit, String artikel_to, String einheit_to)
    {

        //FIXME Feedback, Fehler ausbügeln
        ContentValues data = new ContentValues();
        data.put("NAME",artikel_to);
        data.put("EINHEIT",einheit_to);

        SQLiteDatabase dbw = this.getWritableDatabase();
        String[] selectionArgs = { artikel,einheit };
        String where = "NAME=? AND EINHEIT=?";
        long response =dbw.update(TB_MATERIAL_TYP,data,where,selectionArgs);

        return  response;
    }

    public String get_artikel_param(String[] selectionArgs, String where,String[] colum )
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String param="null";

        Cursor cursor = db.query(TB_MATERIAL_TYP, colum,where, selectionArgs, null, null, null);
        if(cursor.getCount() >0)
        {
            String[] strings = new String[cursor.getCount()];

            cursor.moveToFirst();
            param= cursor.getString(cursor.getColumnIndexOrThrow(colum[0]));
        }

        cursor.close();
        db.close();

        return param;
    }

    public String media_scanner(ContentValues data)
    {
        String name_zuleferer =     this.get_zulieferer_param(
                new String[]{data.get("ZULIEFERER_ID").toString()},
                "ID=?",
                new String[]{"NAME"});

        String proj_src = this.get_selectet_projekt_root()
                .split(",")[2]
                .replace("primary:", Environment.getExternalStorageDirectory()+"/")+"/Lieferscheine";

        String idenifer = name_zuleferer+"_LSNR_"+data.get("LSNR")+"@"+data.get("DATUM").toString().replace(".","");
        File f  =new File(proj_src);
        String []filelist = f.list();
        String localImageSet ="";
        int counter=0;
        for(String files: filelist)
        {
            File f2 = new File(proj_src+"/"+files);
            if(f2.isFile())
            {
                if(files.contains(idenifer) == true)
                {
                    localImageSet +=proj_src+"/"+files+",";
                }
            }
        }

        return localImageSet;

    }

    public boolean find_similar(String table_name,String[] selectionArgs,String where)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Boolean res = false;
        String[] colums = {"ID"};

        Cursor cursor = db.query(table_name,colums, where, selectionArgs, null, null, null);
        Log.d("BASI FIND SIMILAR", String.valueOf(cursor.getCount()));

        if(cursor.getCount() >0) //Wenn grösser als 0, ist vorhanden
        {
         res= true;
        }
        db.close();
        cursor.close();

        return res;


    }
}
