package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.number.SimpleNotation;
import android.icu.util.Output;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.SQL_finals;

import org.json.JSONArray;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
        //TODO auf Duplikate Prüfen
        SQLiteDatabase wdb = this.getWritableDatabase();


        long newRowId = wdb.insert(TB_MATERIAL_LOG,null,data);

        return newRowId;
    }

    public long delet_material_log_entry(String entry_id)
    {
       ContentValues entry_data =  this.material_get_entry_id(entry_id);
       String [] selectionArgs = {entry_data.get("PROJEKT_ID").toString(),entry_data.get("DATUM").toString(),entry_data.get("LSNR").toString(),entry_data.get("LIEFERANT_ID").toString()};
       String where = "PROJEKT_ID=? AND DATUM=? AND LSNR=? AND LIEFERANT_ID=?";
       int similar_entrys= this.find_similar(SQL_finals.TB_MATERIAL_LOG,selectionArgs,where);
       int deletedRows =0;

       if(similar_entrys == 1)
       {
           SQLiteDatabase db = this.getWritableDatabase();
           String[] del_selectionArgs = { entry_id };
           String del_where = "ID=?";

           deletedRows = db.delete(TB_MATERIAL_LOG,del_where,del_selectionArgs);
           material_log_delet_media(entry_data);
       }
       else
       {
           SQLiteDatabase db = this.getWritableDatabase();
           String[] del_selectionArgs = { entry_id };
           String del_where = "ID=?";

           deletedRows = db.delete(TB_MATERIAL_LOG,del_where,del_selectionArgs);

       }

        return deletedRows;

    }

    public long delet_all_material_log_entry(String projekt_id)
    {

            SQLiteDatabase db = this.getWritableDatabase();
            String[] del_selectionArgs = { projekt_id };
            String del_where = "PROJEKT_ID=?";

            long   deletedRows = db.delete(TB_MATERIAL_LOG,del_where,del_selectionArgs);

            //material_log_delet_media(entry_data);
        return deletedRows;

    }



    public void material_log_delet_media(ContentValues data)
    {

        String lieferant =  this.get_zulieferer_param(new String[]{data.get("LIEFERANT_ID").toString()}, "ID=?", new String[]{"NAME"});
        String lsnr = data.get("LSNR").toString();
        String datum = bsf.convert_date(data.get("DATUM").toString(),"format_database_to_readable").replace(".","");

        String filename = bsf.ls_filename_form(lieferant,lsnr,datum,"default_NO_ID");
        String search_path= this.get_projekt_root_paht()+Material.ls_media_directory_name;
        Log.d("BASI PROJ_ROOT",search_path);

        File directory = new File(search_path);
        String [] files= directory.list();
        for(String e: files)
        {
            if(e.contains(filename))
            {
                Log.d("FILE: ",e.toString());
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

            long response = dbw.update(TB_MATERIAL_LOG,data_new,where,selectionArgs);

            // Nach weitereren einträgen Suchen, die der Selben LSNR hat und abändern
            dbw.close();
            update_material_log_all_similar_entrys(data_old,data_new);



    }

    public void update_material_log_all_similar_entrys(ContentValues data_old, ContentValues data_new)
    {
            SQLiteDatabase dbw = this.getWritableDatabase();

            ContentValues update_with= new ContentValues();

            update_with.put("DATUM",data_new.get("DATUM").toString());
            update_with.put("LSNR",data_new.get("LSNR").toString());
            update_with.put("LIEFERANT_ID",data_new.get("LIEFERANT_ID").toString());

            String[] selectionArgs = { data_old.get("PROJEKT_ID").toString(),
                    data_old.get("DATUM").toString(),
                    data_old.get("LSNR").toString(),
                    data_old.get("LIEFERANT_ID").toString()};

            String where = "PROJEKT_ID=? AND DATUM=? AND LSNR=? AND LIEFERANT_ID=?";

            long response = dbw.update(TB_MATERIAL_LOG,update_with,where,selectionArgs);
            update_material_log_entry_media_filenames(data_old, data_new);
    }

    public void update_material_log_entry_media_filenames(ContentValues data_old,ContentValues data_new)
    {
        String name_zuleferer_old =     this.get_zulieferer_param(new String[]{data_old.get("LIEFERANT_ID").toString()}, "ID=?", new String[]{"NAME"});
        String name_zuleferer_new =     this.get_zulieferer_param(new String[]{data_new.get("LIEFERANT_ID").toString()}, "ID=?", new String[]{"NAME"});

        String proj_src = this.get_selectet_projekt_root_data()
                .split(",")[2]
                .replace("primary:", Environment.getExternalStorageDirectory()+"/")+"/Lieferscheine";

        String date_old=bsf.convert_date(data_old.get("DATUM").toString(),"format_database_to_readable").replace(".","");
        String date_new=bsf.convert_date(data_new.get("DATUM").toString(),"format_database_to_readable").replace(".","");

        String file_idenifer_old = name_zuleferer_old+"_LSNR_"+data_old.get("LSNR")+"@"+date_old;
        String file_idenifer_new = name_zuleferer_new+"_LSNR_"+data_new.get("LSNR")+"@"+date_new;


        File f  =new File(proj_src);
        String []filelist = f.list();

        for(String file: filelist)
        {
            File f2 = new File(proj_src+"/"+file);
            if(f2.isFile())
            {
                if(file.contains(file_idenifer_old) == true)
                {
                    Log.d("File found:",f2.getPath());
                    String filename_ID = file.substring(file.lastIndexOf("_ID_",file.length()));
                    File new_filename = new File(proj_src+"/"+file_idenifer_new+filename_ID);
                    f2.renameTo(new_filename);
                    Log.d("File Rename to:",new_filename.getPath());
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







    public String[] material_entrys_list() //Gibt alle Einträge des Projektes zurück => alte Version
    {

        //Fixme Projekt ID anpassen

        material_database_ops mdo = new material_database_ops(context);
        String proj_id =mdo.get_selectet_projekt_root_data().split(",")[1]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { proj_id };
        String where = "PROJEKT_ID=?";

        Cursor cursor = db.query(TB_MATERIAL_LOG,null,where,selectionArgs,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
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

        material_database_ops mdo = new material_database_ops(context);
        String proj_id =mdo.get_selectet_projekt_root_data().split(",")[1]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = { proj_id };
        String where = "PROJEKT_ID=?";

        Cursor cursor = db.query(TB_MATERIAL_LOG,null,where,selectionArgs,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
        String[] strings = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext())
        {
            strings[i] ="ID:"+cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            strings[i] +=",PROJEKT_ID:"+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_ID"));
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

    public String get_selectet_projekt_id()
    {

        String id = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] selectionArgs = {"1"};
            String where = "SELECT_FLAG=?";

            Cursor cursor = db.query(TB_MATERIAL_PROJEKTE,null,where,selectionArgs,null,null,null);
            id = "null";
            cursor.moveToFirst();
            id=cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            cursor.close();
            db.close();
        } catch (IllegalArgumentException e)
        {
            id="0";
            bsf.error_msg("Kein ausgewähltes Projekt gefunden! \n return=0\n"+e.getMessage().toString(),context);
            throw new RuntimeException(e);
        }

        return  id;
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
            data.put("DATE",bsf.date_refresh()+ " "+bsf.time_refresh());
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
                JsonReader reader = new JsonReader(new InputStreamReader(in2,"UTF-8"));

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
                            if (overwrite_mode == true)
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

        String proj_src = this.get_selectet_projekt_root_data()
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
                JsonReader reader = new JsonReader(new InputStreamReader(in2,"UTF-8"));

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
                        if (overwrite_mode == true)
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


        Cursor cursor = db.query(TB_MATERIAL_LOG,columns,"PROJEKT_ID=?",selectionArgs,"LSNR",null,"LSNR ASC");
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
            arg_array[c] = arg_list.next().toString();
            c++;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TB_MATERIAL_LOG,null,where_string,arg_array,null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
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

        Cursor cursor = db.query(TB_MATERIAL_LOG,null,"DATUM=?",new String[]{bsf.convert_date(date,"format_database")},null,null,"DATUM ASC,LSNR, LIEFERANT_ID");
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








}
