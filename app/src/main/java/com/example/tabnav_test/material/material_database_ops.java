package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.projekt_ops;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
        long newRowId=-1;

        SQLiteDatabase db = this.getReadableDatabase();

        String[] where_args = {data.get("ID").toString(),data.get("PROJEKT_NR").toString()};
        String where = "ID=? AND PROJEKT_NR=?";

        Cursor cursor = db.query(BASI_MATERIAL,null, where, where_args, null, null, null);
        int  c = cursor.getCount();

        db.close();
        cursor.close();

        if(c==0)
        {
            SQLiteDatabase wdb = this.getWritableDatabase();
            newRowId = wdb.insert(BASI_MATERIAL,null,data);
            wdb.close();
        }

        return newRowId;
    }

    public long delet_material_log_entry(String entry_id)
    {
       ContentValues entry_data =  this.material_get_entry_id(entry_id);
       String [] selectionArgs = {entry_data.get("PROJEKT_NR").toString(),entry_data.get("DATUM").toString(),entry_data.get("LSNR").toString(),entry_data.get("LIEFERANT_ID").toString()};
       String where = "PROJEKT_NR=? AND DATUM=? AND LSNR=? AND LIEFERANT_ID=?";
       int similar_entrys= this.find_similar(SQL_finals.BASI_MATERIAL,selectionArgs,where);
       int deletedRows =0;

       if(similar_entrys == 1)
       {
           SQLiteDatabase db = this.getWritableDatabase();
           String[] del_selectionArgs = { entry_id };
           String del_where = "ID=?";

           deletedRows = db.delete(BASI_MATERIAL,del_where,del_selectionArgs);
           material_log_delet_media(entry_data);
       }
       else
       {
           SQLiteDatabase db = this.getWritableDatabase();
           String[] del_selectionArgs = { entry_id };
           String del_where = "ID=?";

           deletedRows = db.delete(BASI_MATERIAL,del_where,del_selectionArgs);

       }

        return deletedRows;

    }

    public long delet_all_material_log_entry()
    {
            SQLiteDatabase db = this.getWritableDatabase();
            String[] del_selectionArgs = { get_projekt_id() };
            String del_where = "PROJEKT_NR=?";

            long   deletedRows = db.delete(BASI_MATERIAL,del_where,del_selectionArgs);

            //material_log_delet_media(entry_data);
        return deletedRows;

    }

    public String get_ls_temp_dir()
    {
        projekt_ops projekt= new projekt_ops(context);
        return  projekt.projekt_get_current_root_dir_ls_images();
    }
    public String get_ls_images_dir()
    {
        projekt_ops projekt= new projekt_ops(context);
        return  projekt.projekt_get_current_root_dir_ls_images();
    }

    public void material_log_delet_media(ContentValues data)
    {

        String lieferant =  this.get_zulieferer_param(new String[]{data.get("LIEFERANT_ID").toString()}, "ID=?", new String[]{"NAME"});
        String lsnr = data.get("LSNR").toString();
        String datum = bsf.convert_date(data.get("DATUM").toString(),"format_database_to_readable").replace(".","");

        String filename = bsf.ls_filename_form(lieferant,lsnr,datum,"default_NO_ID");

        String search_path= get_ls_temp_dir();
        Log.d("BASI PROJ_ROOT",search_path);

        File directory = new File(search_path);
        String [] files= directory.list();
        for(String e: files)
        {
            if(e.contains(filename))
            {
                Log.d("FILE: ", e);
                File f= new File(search_path+"/"+e);
                f.delete();
            }
        }
    }
    public void update_material_log_entry(ContentValues data_new )
    {


        //Nach medien Suchen und entsprechend den Dateinamen anpassen.
            //Eintrag nach ID ändern

            ContentValues data_old = this.material_get_entry_id(data_new.get("ID").toString());

            SQLiteDatabase dbw = this.getWritableDatabase();
            String[] selectionArgs = { data_new.get("ID").toString() };
            String where = "ID=?";

            long response = dbw.update(BASI_MATERIAL,data_new,where,selectionArgs);
            if(response >-1)
            {
                update_material_log_all_similar_entrys(data_old,data_new);
                update_material_log_entry_media_filenames(data_old, data_new);
            }

            // Nach weitereren einträgen Suchen, die der Selben LSNR hat und abändern
            dbw.close();



    }

    public void update_material_log_all_similar_entrys(ContentValues data_old, ContentValues data_new)
    {
            SQLiteDatabase dbw = this.getWritableDatabase();

            ContentValues update_with= new ContentValues();

            update_with.put("DATUM",data_new.get("DATUM").toString());
            update_with.put("LSNR",data_new.get("LSNR").toString());
            update_with.put("LIEFERANT_ID",data_new.get("LIEFERANT_ID").toString());

            String[] selectionArgs = { data_old.get("PROJEKT_NR").toString(),
                    data_old.get("DATUM").toString(),
                    data_old.get("LSNR").toString(),
                    data_old.get("LIEFERANT_ID").toString()};

            String where = "PROJEKT_NR=? AND DATUM=? AND LSNR=? AND LIEFERANT_ID=?";

            long response = dbw.update(BASI_MATERIAL,update_with,where,selectionArgs);

    }

    public void update_material_log_entry_media_filenames(ContentValues data_old,ContentValues data_new)
    {

        Log.d("BASI",data_new.get("LIEFERANT_ID").toString());

        String name_zuleferer_old = this.get_zulieferer_param(
                new String[]{data_old.get("LIEFERANT_ID").toString()},
                "ID=?",
                new String[]{"NAME"});

        String name_zuleferer_new = this.get_zulieferer_param(
                new String[]{data_new.get("LIEFERANT_ID").toString()},
                "ID=?",
                new String[]{"NAME"});

        projekt_ops projekt = new projekt_ops(context);
        String proj_src = projekt.projekt_get_current_root_dir_ls_images(); // root_dir+"/Lieferscheine/";

        String file_name_idenifer_old = name_zuleferer_old + "_LSNR_" + data_old.get("LSNR") + "@" + data_old.get("DATUM").toString().replace("-", "");
        String file_name_idenifer_new = name_zuleferer_new + "_LSNR_" + data_new.get("LSNR") + "@" + data_new.get("DATUM").toString().replace("-", "");

        Log.d("proj_src", proj_src);
        Log.d("BASI Idenifer old", file_name_idenifer_old);
        Log.d("BASI Idenifer new", file_name_idenifer_new);
        Log.d("BASI datum new", data_new.get("DATUM").toString());
        Log.d("BASI datum old", data_old.get("DATUM").toString());
        File f = new File(proj_src);
        String[] filelist = f.list();

        for (String files : filelist)
        {
            String filename = proj_src + files;
            Log.d("File:   ", filename);
            File f2 = new File(filename);
            if (f2.isFile()) {
                if (files.contains(file_name_idenifer_old))
                {
                    Log.d("File match:",files);
                   String[] part = filename.split("_ID_");
                    String new_filename =proj_src + file_name_idenifer_new + "_ID_"+part[1];
                    Log.d("BASI filename_new", new_filename);
                    File f_new = new File(new_filename);
                    f2.renameTo(f_new);
                }
            }
        }
    }







    public ContentValues material_get_entry_id(String id) //Gibt einen Spezifischen Eintrag zurück definiert durch die ID des eintrages als ContentValue data
    {

        //Fixme Projekt ID anpassen
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { id };
        String where = "ID=?";

        Cursor cursor = db.query(BASI_MATERIAL,null,where,selectionArgs,null,null,null);
        ContentValues data = new ContentValues();

        if(cursor.getCount() >0)
        {
            cursor.moveToFirst();

            data.put("ID",cursor.getString(cursor.getColumnIndexOrThrow("ID")));
            data.put("PROJEKT_NR",cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR")));
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


    public String[] material_entrys_list() //Gibt alle Einträge des Projektes zurück => alte Version
    {

        //Fixme Projekt ID anpassen

        material_database_ops mdo = new material_database_ops(context);
        String proj_id = get_projekt_id();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { proj_id };
        String where = "PROJEKT_NR=?";

        Cursor cursor = db.query(BASI_MATERIAL,null,where,selectionArgs,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATUM"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MENGE"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
            try {
                strings[i] +=","+bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            }catch (Exception e)
            {
                strings[i] +="null,";
            }

            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ"));
            i++;
        }
        cursor.close();
        db.close();
        return  strings;
    }




    public String[] get_current_projekt_entrys() //Gibt alle Einträge des Projektes zurück => alte Version
    {

        //Fixme Projekt ID anpassen
        String proj_id =this.get_selectet_projekt_root_data().split(",")[1]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { get_projekt_id() };
        String where = "PROJEKT_NR=?";

        Cursor cursor = db.query(BASI_MATERIAL,null,where,selectionArgs,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] ="ID:"+cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=",PROJEKT_NR:"+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR"));
            strings[i] +=",DATUM:"+cursor.getString(cursor.getColumnIndexOrThrow("DATUM"));
            strings[i] +=",LSNR:"+cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            strings[i] +=",LIEFERANT_ID:"+cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID"));
            strings[i] +=",MATERIAL_ID:"+cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
            strings[i] +=",MENGE:"+cursor.getString(cursor.getColumnIndexOrThrow("MENGE"));
            strings[i] +=",EINHEIT_ID:"+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
            strings[i] +=",SRC:"+bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            strings[i] +=",NOTIZ:"+cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ"));
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

         this.test_exist_projects();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {"1"};
        String where = "SELECT_FLAG=?";

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
        String value ="";
        if(cursor.getCount()>0)
        {
            while (cursor.moveToNext())
            {
                value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                value +="["+cursor.getString(cursor.getColumnIndexOrThrow("ID"))+"]";
            }
        }


        cursor.close();
        db.close();
        return  value;
    }

    public String get_selectet_projekt_id()
    {

        return    get_projekt_id();
    }

    public String get_selectet_projekt_root_data()
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

    public String get_projekt_root_paht()
    {


        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {"1"};
        String where = "SELECT_FLAG=?";

        Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        String root =cursor.getString(cursor.getColumnIndexOrThrow("SRC")).replace("primary:",Environment.getExternalStorageDirectory().toString()+"/");

        cursor.close();
        db.close();

        return  root;
    }



    //Zulieferer funktionen
    public long add_zulieferer(String zulieferer_name)
    {
        //TODO  auf Duplikate prüfen$

        String[] selectionArgs = {zulieferer_name};
        long response = 0;
        long newRowId=0;
        //Auf Duplikate prüfen
        int  check_exist = this.find_similar(TB_MATERIAL_ZULIEFERER,selectionArgs,"NAME=?");

        if(check_exist == 0)
        {
            SQLiteDatabase wdb = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put("ID",bsf.gen_ID());
            data.put("NAME",zulieferer_name);
            data.put("DATE",bsf.date_refresh_database()+ " "+bsf.time_refresh());
            newRowId = wdb.insert(TB_MATERIAL_ZULIEFERER,null,data);
        }
        return newRowId;
    }


    public void restore_zulieferer(String source_path,Context  context,Boolean overwrite_mode)
    {
        InputStream in2 = null;
        try {
            in2 = new FileInputStream(new File(source_path));
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(in2, StandardCharsets.UTF_8));

                int insert_counter=0;
                int update_counter=0;
                int skipt_counter=0;

                reader.beginArray();
                while(reader.hasNext())
                {
                    reader.beginObject();
                    ContentValues output_data = new ContentValues();
                    while (reader.hasNext())
                    {
                        output_data.put(reader.nextName(),reader.nextString());
                    }

                    String[] selectionArgs = {output_data.get("NAME").toString()};
                    //Auf Duplikate prüfen
                    int  check_exist = this.find_similar(TB_MATERIAL_ZULIEFERER,selectionArgs,"NAME=?");
                    SQLiteDatabase wdb = this.getWritableDatabase();

                        if(check_exist == 0)
                        {
                            wdb.insert(TB_MATERIAL_ZULIEFERER,null,output_data);
                            insert_counter++;
                        }
                        else
                        {
                            if (overwrite_mode)
                            {
                                String[] update_selectionArgs = { output_data.get("NAME").toString() };
                                String where = "NAME=?";
                                wdb.update(TB_MATERIAL_ZULIEFERER,output_data,where,update_selectionArgs);
                                update_counter++;
                            }
                            else
                            {
                                skipt_counter++;
                            }
                            //Overwirte mode = false => Keine Aktion
                        }
                    reader.endObject();
                }
                reader.endArray();


                String message_string = insert_counter +" Einträge Importiert \n"+update_counter +" Updates der Einträge \n"+ skipt_counter+" Einträge Übersprungen";

                AlertDialog.Builder restore_info_dialog = new AlertDialog.Builder(context);
                restore_info_dialog.setTitle("Import Report");
                restore_info_dialog.setMessage(message_string);
                restore_info_dialog.setPositiveButton("Kopieren", new DialogInterface.OnClickListener()
                {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i)
                    {
                        bsf.copy_to_clipboard(message_string,context);
                    }
                });


                restore_info_dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                restore_info_dialog.show();
                Log.d("BASI Import:",message_string);

            } catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] zulieferer_list_all(String order)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME","ID","DATE" };

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER,columns,null,null,null,null,order);
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
        String id ="null";

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER,columns,where,selectionArgs,null,null,null);

        if(cursor.getCount() >0)
        {
            String[] strings = new String[cursor.getCount()];

            cursor.moveToFirst();
            id=cursor.getString(cursor.getColumnIndexOrThrow("ID"));
        }

        cursor.close();
        db.close();

        return  id;
    }
    public String create_backup_json_zulieferer()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME","ID","DATE" };

        Cursor cursor = db.query(TB_MATERIAL_ZULIEFERER,columns,null,null,null,null,"NAME DESC");
        String strings ="[\n";

        int i=0;
        while (cursor.moveToNext())
        {
            strings +="{\n";
            strings +="\"ID\":\""+cursor.getString(cursor.getColumnIndexOrThrow("ID"))+"\",\n";
            strings +="\"NAME\":\""+cursor.getString(cursor.getColumnIndexOrThrow("NAME"))+"\",\n";
            strings +="\"DATE\":\""+cursor.getString(cursor.getColumnIndexOrThrow("DATE"))+"\"},\n";

        }
        strings =strings.substring(0,strings.length()-2)+"]\n";
        cursor.close();
        db.close();

        return  strings;
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
    public void zulieferer_delet_all()
    {
        //TODO  auf Projekt mit Status 1 darf nicht gelöscht werden!
        //TODO Bestätigen

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TB_MATERIAL_ZULIEFERER,null,null);
    }



    //Artikel funktionen
    public long add_artikel_to_list(String artikel,String einheit)
    {


        String[] selectionArgs = { artikel.trim(),einheit.trim() };
        long response = 0;

        //Auf Duplikate prüfen
        int  check_exist = this.find_similar(TB_MATERIAL_TYP,selectionArgs,"NAME=? AND EINHEIT=?");

        if(check_exist ==0)
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

        String[] strings;

        if(cursor.getCount()>0)
        {
            strings = new String[cursor.getCount()];
            int i=0;
            while (cursor.moveToNext())
            {
                strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                strings[i] +=" ["+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT"))+"]";
                i++;
            }
            cursor.close();
            db.close();
        }
        else
        {
            strings = new String[1];
            strings[0]="Keine Geschpeichert [Stück]";
        }

        return  strings;
    }
    public String[] get_artikel_summary()
    {
        ArrayList<String> final_list = new ArrayList<>();
        SQLiteDatabase dbr = this.getReadableDatabase();
        String[] columns = {"MATERIAL_ID","MENGE", "EINHEIT_ID"};


        Cursor cursor = dbr.query(BASI_MATERIAL,columns,"PROJEKT_NR=?",new String[]{get_selectet_projekt_id()},"MATERIAL_ID",null,null);

        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                Double sum =0.0;
                String material_id= cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
                String einheit= cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
                Cursor cursor2 =dbr.query(BASI_MATERIAL,new String[]{"MENGE"},"PROJEKT_NR=? AND MATERIAL_ID=?",new String[]{get_selectet_projekt_id(),material_id},null,null,null);
                while (cursor2.moveToNext())
                {
                    try {
                        sum= sum +Double.valueOf(cursor2.getString(cursor2.getColumnIndexOrThrow("MENGE")));
                    }
                    catch (Exception e)
                    {
                        
                        sum =sum +0.0;
                    }

                }
                final_list.add(get_artikel_name_by_id(material_id)+","+    bsf.double_round(String.valueOf(sum),2)+" " +einheit);
                cursor2.close();
            }
        }
        String [] final_array = new String[final_list.size()];
       int  final_array_counter=0;
        Iterator<String> iter= final_list.iterator();
        while(iter.hasNext())
        {
            final_array[final_array_counter] =iter.next();
            final_array_counter++;
        }


        return final_array;
    }


    public String[] artikel_list_all_no_unit()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "NAME","EINHEIT" };

        Cursor cursor = db.query(TB_MATERIAL_TYP,columns,null,null,null,null,"NAME ASC");
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

    public String get_artikel_id(String artikel,String einheit)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "NAME=? AND EINHEIT=?";
        String[] selection_args ={artikel,einheit};

        Cursor cursor = db.query(TB_MATERIAL_TYP,null,where,selection_args,null,null,null);
        cursor.moveToNext();

        String id =cursor.getString(cursor.getColumnIndexOrThrow("ID"));

        cursor.close();
        db.close();

        return  id;
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


    public void artikel_delet_all()
    {
        //TODO Bestätigen
        SQLiteDatabase dbw = this.getWritableDatabase();
        int r =  dbw.delete(TB_MATERIAL_TYP,null,null);
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

    public String entry_counter()
    {
        //TODO Bestätigen

        String c ="0";
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] columns = {"ID"};
            String[] where_args = {get_projekt_id()};
            String where = "PROJEKT_NR=?";


            Cursor cursor = db.query(BASI_MATERIAL,columns,where,where_args,null,null,null);
            c = String.valueOf(cursor.getCount());
            db.close();
            cursor.close();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return c;
    }
    public String entry_counter_tab_text()
    {
      return "Einträge ("+entry_counter()+")";

    }

    public long update_artikel(String artikel_from, String einheit_from, String artikel_to, String einheit_to)
    {

        //FIXME Feedback, Fehler ausbügeln
        ContentValues data = new ContentValues();
        data.put("NAME",artikel_to);
        data.put("EINHEIT",einheit_to);

        SQLiteDatabase dbw = this.getWritableDatabase();
        String[] selectionArgs = { artikel_from,einheit_from };
        String where = "NAME=? AND EINHEIT=?";
        long response =dbw.update(TB_MATERIAL_TYP,data,where,selectionArgs);
        dbw.close();

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
        return param;
    }



    public String media_scanner(ContentValues data)
    {
        String name_zuleferer =     this.get_zulieferer_param(
                new String[]{data.get("ZULIEFERER_ID").toString()},
                "ID=?",
                new String[]{"NAME"});

        projekt_ops projekt = new projekt_ops(context);
        String proj_src = projekt.projekt_get_current_root_dir_ls_images(); // root_dir+"/Lieferscheine/";
        String date=bsf.convert_date(data.get("DATUM").toString(),"format_database").replace("-","");

        String file_name_idenifer = name_zuleferer+"_LSNR_"+data.get("LSNR")+"@"+date;
        Log.d("BASI Idenifer",file_name_idenifer);
        File f  =new File(proj_src);
        String []filelist = f.list();
        String localImageSet ="";
        int counter=0;
        for(String files: filelist)
        {
            File f2 = new File(proj_src+files);
            if(f2.isFile())
            {
                if(files.contains(file_name_idenifer))
                {
                    localImageSet +=proj_src+files+",";
                    Log.d("BASI files",files);
                }
            }
        }
        return localImageSet;
    }


    public int  check_similar_ls(ContentValues arg)
    {

        SQLiteDatabase db = this.getReadableDatabase();

        String[] where_args = {arg.get("PROJEKT_NR").toString(),arg.get("LSNR").toString(),arg.get("LIEFERANT_ID").toString()};
        String where = "PROJEKT_NR=?  AND  LSNR=? AND  LIEFERANT_ID=?";


        Cursor cursor = db.query(BASI_MATERIAL,null, where, where_args, null, null, null);
        int  c = cursor.getCount();


        db.close();
        cursor.close();

       return c;
    }

    public int find_similar(String table_name, String[] selectionArgs, String where)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Boolean res = false;
        String[] colums = {"ID"};

        Cursor cursor = db.query(table_name,colums, where, selectionArgs, null, null, null);
        Log.d("BASI FIND SIMILAR IN: "+table_name, String.valueOf(cursor.getCount()));

        db.close();
        cursor.close();

        return cursor.getCount();
    }

    public void material_log_copy_entry(String  id)
    {

        ContentValues data = this.material_get_entry_id(id);
        data.put("ID",bsf.gen_UUID());
        this.add_material_log_entry(data);
    }

    public String get_artikel_name_by_id(String id)
    {
         String artikel_name = this.get_artikel_param(
                new String[]{id},
                "ID=?",
                new String[]{"NAME"});

        return artikel_name;
    }
    public String get_lieferant_name_by_id(String id)
    {
         String lieferant_name = this.get_zulieferer_param(
                new String[]{id},
                "ID=?",
                new String[]{"NAME"});
        return lieferant_name;
    }
    public String get_projekt_id()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_selected_id();
    }
    public String get_projekt_name()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_selected_name();

    }   public String get_projekt_nr()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_selected_nr();
    }

    public String get_projekt_root()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_current_root_dir_images();
    }
    public String get_projekt_export_dir_csv()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_current_root_dir_export_cvs();
    }

    public String get_projekt_export_dir_json()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_current_root_dir_export_json();
    }

  public String get_projekt_backup_dir()
    {
        projekt_ops projekt = new projekt_ops(context);
        return projekt.projekt_get_current_root_dir_backup();
    }




    public String create_backup_artikel()
    {
            SQLiteDatabase db = this.getReadableDatabase();

            String[] columns = { "ID","NAME","EINHEIT","FAV_FLAG" };

            Cursor cursor = db.query(TB_MATERIAL_TYP,columns,null,null,null,null,"NAME DESC");
            String strings ="[\n";

            int i=0;
            while (cursor.moveToNext())
            {
                strings +="{\n";
                strings +="\"ID\":\""+cursor.getString(cursor.getColumnIndexOrThrow("ID"))+"\",\n";
                strings +="\"NAME\":\""+cursor.getString(cursor.getColumnIndexOrThrow("NAME"))+"\",\n";
                strings +="\"EINHEIT\":\""+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT"))+"\",\n";
                strings +="\"FAV_FLAG\":\""+cursor.getString(cursor.getColumnIndexOrThrow("FAV_FLAG"))+"\"},\n";

            }
            strings =strings.substring(0,strings.length()-2)+"]\n";
            cursor.close();
            db.close();

            return  strings;
    }


    public void restore_artikel(String source_path,Context  context,Boolean overwrite_mode)
    {
        InputStream in2 = null;
        try {
            in2 = new FileInputStream(new File(source_path));
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(in2, StandardCharsets.UTF_8));

                int insert_counter=0;
                int update_counter=0;
                int skipt_counter=0;

                reader.beginArray();
                while(reader.hasNext())
                {
                    reader.beginObject();
                    ContentValues output_data = new ContentValues();
                    while (reader.hasNext())
                    {
                        output_data.put(reader.nextName(),reader.nextString());
                    }

                    String[] selectionArgs = {output_data.get("NAME").toString()};
                    //Auf Duplikate prüfen
                    int  check_exist = this.find_similar(TB_MATERIAL_TYP,selectionArgs,"NAME=?");
                    SQLiteDatabase wdb = this.getWritableDatabase();

                    if(check_exist == 0)
                    {
                        wdb.insert(TB_MATERIAL_TYP,null,output_data);
                        insert_counter++;
                    }
                    else
                    {
                        if (overwrite_mode)
                        {
                            String[] update_selectionArgs = { output_data.get("NAME").toString() };
                            String where = "NAME=?";
                            wdb.update(TB_MATERIAL_TYP,output_data,where,update_selectionArgs);
                            update_counter++;
                        }
                        else
                        {
                            skipt_counter++;
                        }
                        //Overwirte mode = false => Keine Aktion
                    }
                    reader.endObject();
                }
                reader.endArray();


                String message_string = insert_counter +" Einträge Importiert \n"+update_counter +" Updates der Einträge \n"+ skipt_counter+" Einträge Übersprungen";

                AlertDialog.Builder restore_info_dialog = new AlertDialog.Builder(context);
                restore_info_dialog.setTitle("Import Report");
                restore_info_dialog.setMessage(message_string);
                restore_info_dialog.setPositiveButton("Kopieren", new DialogInterface.OnClickListener()
                {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i)
                    {
                        bsf.copy_to_clipboard(message_string,context);
                    }
                });

                restore_info_dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                restore_info_dialog.show();

                Log.d("BASI Import:",message_string);

            } catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] get_entry_lsnr_list_all()
    {
        String[] selectionArgs = { this.get_selectet_projekt_id()};
        String[] columns = { "LSNR"};

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(BASI_MATERIAL,columns,"PROJEKT_NR=?",selectionArgs,"LSNR",null,"LSNR ASC");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            Log.d("ID",strings[i]);
            i++;
        }
        cursor.close();
        db.close();

        return  strings;

    }

    public String[] material_log_search(ArrayList where,ArrayList select_args)
    {

        Iterator<String> where_list =where.iterator();
        String where_string="";
        while(where_list.hasNext())
        {
            where_string += where_list.next()+ " AND ";
        }
        where_string = where_string.substring(0,where_string.lastIndexOf("AND")) ;


        Iterator<String> arg_list =select_args.iterator();
        String []arg_array=new String[select_args.size()];
        int c =0;
        while(arg_list.hasNext())
        {
            arg_array[c] = arg_list.next();
            c++;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(BASI_MATERIAL,null,where_string,arg_array,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATUM"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MENGE"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
            try {
                strings[i] +=","+bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            }catch (Exception e)
            {
                strings[i] +="null,";
            }
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ"));
            i++;
        }
        cursor.close();
        db.close();
        return  strings;

    }


    public String[] material_log_search_date(String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String where = "DATUM=? AND PROJEKT_NR=?";
        String[] where_args = {bsf.convert_date(date,"format_database"),get_projekt_id()};

        Cursor cursor = db.query(BASI_MATERIAL,null,where,where_args,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] =cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATUM"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LSNR"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("LIEFERANT_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MATERIAL_ID"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("MENGE"));
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("EINHEIT_ID"));
            try {
                strings[i] +=","+bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow("SRC")));
            }catch (Exception e)
            {
                strings[i] +="null,";
            }
            strings[i] +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTIZ"));
            i++;
        }
        cursor.close();
        db.close();

        return  strings;

    }

    public void test_exist_projects()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String [] colums = {"ID"};
        String where = "ID=?";
        String[] where_args ={"0"};

        Cursor cursor =db.query(SQL_finals.TB_MATERIAL_PROJEKTE,colums,where,where_args,null,null,null);

        if(cursor.getCount()==0)
        {
            ContentValues cv = new ContentValues();
            cv.put("ID", "0");
            cv.put("NAME", "Allgemein");
            cv.put("SRC", Environment.getExternalStorageState());
            cv.put("SELECT_FLAG", "1");
            this.projekt_add(cv);
            select_projekt("0");
        }

        cursor.close();
        db.close();
    }








}
