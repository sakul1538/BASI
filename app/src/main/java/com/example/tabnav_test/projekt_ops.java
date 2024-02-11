package com.example.tabnav_test;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import static java.util.Arrays.sort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Import_Export.Backup;
import com.example.tabnav_test.Kamera.Kamera;
import com.example.tabnav_test.material.material_database_ops;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class projekt_ops extends SQLiteOpenHelper implements SQL_finals
{
    // ----------------------------------------------------------------- Variablen
    Context context;
    // ----------------------------------------------------------------- Variablen  String, char
    // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
    // ----------------------------------------------------------------- Variablen 	Boolean

    // ----------------------------------------------------------------- Instanzen
    db_ops dbo;
    projekt_browser browser;
    backup projekt_backup;
    // ----------------------------------------------------------------- TextView
    TextView current_projekt_main_title;


    // ----------------------------------------------------------------- AutoCompleteTextView
    // ----------------------------------------------------------------- EditText
    static EditText dir;
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

    public projekt_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = context;
        dbo = new db_ops(context);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }



    public void projekt_add(ContentValues data)
    {
        //in tabelle
        //TODO auf Duplikate Prüfen
        SQLiteDatabase wdb = this.getWritableDatabase();

        long newRowId = wdb.insert(TB_MATERIAL_PROJEKTE,null,data);
    }




    public String get_projekt_id(String name,String nr)
    {
        String output_id ="";

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String[] where_args = {name,nr};
            String where  = "NAME=? AND PROJEKT_NR=?";

            Cursor cursor = db.query(BASI_PROJEKTE,null,where,where_args,null,null,null);

            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
               output_id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            }

            cursor.close();
            db.close();
        } catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }

        return  output_id;
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
        String where = "STATUS_FLAG=?";

        Cursor cursor = null;
        String value = null;
        try {
            cursor = db.query(BASI_PROJEKTE,null,where,selectionArgs,null,null,null);
            value = "";
            if(cursor.getCount()>0)
            {
                while (cursor.moveToNext())
                {
                    value = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    value +="["+cursor.getString(cursor.getColumnIndexOrThrow("PROJEKT_NR"))+"]";
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        cursor.close();
        db.close();
        return  value;
    }

    public void projekt_settings(Context context)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_proj_dialog,null,false);

        // ----------------------------------------------------------------- Variablen
        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        browser = new projekt_browser(context);
        projekt_backup= new backup(context);

        // ----------------------------------------------------------------- TextView
        browser.current_selectet= promptsView.findViewById(R.id.current_selectet);
        // ----------------------------------------------------------------- AutoCompleteTextView
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        ImageButton menu = promptsView.findViewById(R.id.imageButton46);
        // ----------------------------------------------------------------- ImageView
        // ----------------------------------------------------------------- ListView

        // ----------------------------------------------------------------- RecyclerView
        // ----------------------------------------------------------------- Spinner
        browser.projlist = promptsView.findViewById(R.id.spinner2);
        // ----------------------------------------------------------------- CheckBox
        // ----------------------------------------------------------------- RadioButton
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- SeekBar
        // ----------------------------------------------------------------- ProgressBar
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- ScrollView
        // ----------------------------------------------------------------- Layouts
        // ----------------------------------------------------------------- END


        //Init

        try {
            browser.current_selectet.setText(this.get_selectet_projekt());
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
        browser.projekt_spinner_reload();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PopupMenu popup = new PopupMenu(context,view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.settings_projekt_menu_modifys, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        switch (menuItem.getItemId())
                        {
                            case R.id.projekt_create:
                                browser.create_projekt();
                                break;

                            case R.id.projekt_delet:
                                browser.delet_projekt_dialog();
                                break;


                            case R.id.projekt_info:
                                try {
                                    ContentValues output = browser.projekt_spinner_get_selectet_item();
                                    browser.projekt_info_dialog(output);
                                } catch (Exception e)
                                {
                                    Toast.makeText(context, "Error \n"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                                    throw new RuntimeException(e);
                                }
                                break;

                            case R.id.projekt_update:
                                ContentValues output = browser.projekt_info(browser.projekt_spinner_get_selectet_item().get("ID").toString());
                                browser.update_projekt_dialog(output);
                                break;

                            case R.id.projekt_select:
                                browser.projekt_select();
                                break;

                            case R.id.projekt_create_backup:

                                try {
                                    projekt_backup.create_backup();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                Toast.makeText(context, "Backup erstellt ", Toast.LENGTH_SHORT).show();

                                break;

                                case R.id.projekt_restore_backup:


                                    Intent intent_restore_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    intent_restore_backup.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent_restore_backup.setType("application/json");

                                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                                    alertdialog.setTitle("Aktion bei vorhandenen Einträgen?");

                                    alertdialog.setPositiveButton("Überschreiben", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            projekt_backup.import_backup_overwrite= true;



                                            dialogInterface.cancel();
                                            ((Activity) context).startActivityForResult(intent_restore_backup, 3);
                                        }
                                    });

                                    alertdialog.setNegativeButton("Behalten", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            projekt_backup.import_backup_overwrite= false;

                                            ((Activity) context).startActivityForResult(intent_restore_backup, 3);

                                        }
                                    });

                                    alertdialog.show();


                                break;


                            default:
                                Toast.makeText(context, "Nicht Implementiert", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Projektbowser");
        Basic_funct bsf = new Basic_funct();
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                current_projekt_main_title.setText(get_selectet_projekt());
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                current_projekt_main_title.setText(get_selectet_projekt());
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();

    }


    public static class backup extends projekt_ops
    {

        private  Basic_funct bsf = new Basic_funct();
        private  material_database_ops mdo= new material_database_ops(context);
        public String backup_filename = "BACKUP_BASI_PROJEKTE@"+bsf.get_date_for_filename()+".json";
        public String backup_path=Environment.getExternalStorageDirectory()+static_finals.backup_and_export_dir;
        public Boolean import_backup_overwrite =false;
        public backup(@Nullable Context context)
        {
            super(context);
        }
        public Boolean restore_backup(String file, Boolean overwrite_mode)
        {
            SQLiteDatabase dbw = this.getWritableDatabase();
            SQLiteDatabase dbr = this.getReadableDatabase();
            int imported=0;
            int updated =0;
            int skipt = 0;

            InputStream in2 = null;
            try {
                in2 = new FileInputStream(new File(file));
                try {
                    JsonReader reader = new JsonReader(new InputStreamReader(in2,"UTF-8"));
                    reader.beginArray();
                    while(reader.hasNext())
                    {
                        reader.beginObject();
                        ContentValues output_data = new ContentValues();
                        while (reader.hasNext())
                        {
                            String name =reader.nextName();
                            String value =reader.nextString();
                            if(name.equals("ID") ==false)
                            {
                                value =bsf.URLdecode(value);
                            }
                            output_data.put(name,value);
                        }

                        int entry_couter= dbr.query(BASI_PROJEKTE,null,"ID=?",new String[]{output_data.get("ID").toString()},null,null,null).getCount();

                        switch (entry_couter)
                        {
                            case 0:
                                dbw.insert(BASI_PROJEKTE,null,output_data);
                                imported++;
                                break;

                            case 1:

                                if(overwrite_mode ==true)
                                {
                                    dbw.update(BASI_PROJEKTE,output_data,"ID=?",new String[]{output_data.get("ID").toString()});
                                    updated++;
                                }
                                else
                                {
                                    skipt++;
                                }
                                break;
                            default:
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                    reader.close();
                    String rapport_message = "Importiert: "+String.valueOf(imported)+"\n";
                    rapport_message += "Aktuallisiert: "+String.valueOf(updated)+"\n";
                    rapport_message += "Übersprungen: "+String.valueOf(skipt)+"\n";
                    Toast.makeText(context, rapport_message, Toast.LENGTH_LONG).show();
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException(e);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

            } catch (FileNotFoundException ex)
            {
                throw new RuntimeException(ex);
            }

            return true;
        }

        public void create_backup() throws JSONException
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(BASI_PROJEKTE,null,null,null,null,null,null);
            String[] colums = cursor.getColumnNames();

            JSONArray data = new JSONArray();

            while (cursor.moveToNext())
            {
                JSONObject t = new JSONObject();
                for(String c: colums)
                {
                    try {

                        switch(c)
                        {
                            case "ID":
                                t.put(c, cursor.getString(cursor.getColumnIndexOrThrow(c.toString())));
                                break;
                            case "STATUS_FLAG":
                                t.put(c,"0");
                                break;
                            default:
                                t.put(c, bsf.URLencode(cursor.getString(cursor.getColumnIndexOrThrow(c.toString()))));
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    }
                }
                data.put(t);
            }

            File f = new File(backup_path);
            f.mkdirs();
            try {
                f.createNewFile();

                FileWriter fw = new FileWriter(backup_path+backup_filename);
                fw.write(data.toString());
                fw.close();

                AlertDialog.Builder create_backup_report_dialog  = new AlertDialog.Builder(context);
                create_backup_report_dialog.setTitle("Export Report");
                create_backup_report_dialog.setMessage("Backup gespeichert unter: \n\n"+backup_path+backup_filename);
                create_backup_report_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                create_backup_report_dialog.setNegativeButton("URL Kopieren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        bsf.copy_to_clipboard(backup_path+backup_filename,context);
                    }
                });

                create_backup_report_dialog.show();

            } catch (IOException e)
            {
                Toast.makeText(context, "Backup erstellen Fehlgeschlagen!:  \n"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public class projekt_browser extends projekt_ops
    {
        EditText dir_src;
        Spinner projlist;
        TextView current_selectet;

        public projekt_browser(@Nullable Context context)
        {
            super(context);

        }
        public void projekt_spinner_reload()
        {
            projlist.setAdapter(get_projekt_array_adapter_for_spinner());
        }

        public ContentValues  projekt_spinner_get_selectet_item()
        {
        String i= projlist.getSelectedItem().toString();

        String name= i.substring(0,i.lastIndexOf("["));

        String nr =  i.substring(i.lastIndexOf("["),i.length());
        nr =  nr.replace("[","");
        nr = nr.replace("]","");

        String id = get_projekt_id(name.trim(),nr.trim());

        Log.d("BASI",name);
        Log.d("BASI",nr);
        Log.d("BASI",id);

        ContentValues output = new ContentValues();

        output.put("ID",id);
        output.put("PROJEKT_NR",nr);
        output.put("NAME",name);

        return  output;
    }

        private ArrayAdapter get_projekt_array_adapter_for_spinner()
        {
            final String name ="NAME";
            final String projekt_nr ="PROJEKT_NR";

            ArrayList list = new ArrayList();
            try {
                SQLiteDatabase db = this.getReadableDatabase();
                String[] columns = {name,projekt_nr };

                Cursor cursor = db.query(BASI_PROJEKTE,columns,null,null,null,null,"NAME");

                if(cursor.getCount() > 0)
                {
                    while (cursor.moveToNext())
                    {
                        String temp ="";
                        temp = cursor.getString(cursor.getColumnIndexOrThrow(name));
                        temp += "["+cursor.getString(cursor.getColumnIndexOrThrow(projekt_nr))+"]";
                        list.add(temp);
                    }
                }
                else
                {
                    list.add("");
                }
                cursor.close();
                db.close();
            }
            catch (Exception e)
            {
                list.add("");
                throw new RuntimeException(e);
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, list);
            return  spinnerArrayAdapter;
        }
        public void setDir_src_in_dialog(String path)
        {
            try {
                dir_src.setText(path);
            }
            catch ( Exception e)
            {

            }
        }



        public void create_projekt()
        {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.settings_projekt_create_new,null,false);
            // ----------------------------------------------------------------- Variablen
            // ----------------------------------------------------------------- Variablen  String, char
            // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
            // ----------------------------------------------------------------- Variablen 	Boolean
            // ----------------------------------------------------------------- Instanzen
            Basic_funct bsf = new Basic_funct();
            // ----------------------------------------------------------------- TextView
            // ----------------------------------------------------------------- AutoCompleteTextView
            // ----------------------------------------------------------------- EditText
            EditText name = promptsView.findViewById(R.id.create_projekt_name);
            EditText nr = promptsView.findViewById(R.id.create_projekt_nr);
            dir_src  = promptsView.findViewById(R.id.create_projekt_dir); ///Global
            // ----------------------------------------------------------------- Button
            // ----------------------------------------------------------------- ImageButtons
            ImageButton reset_name= promptsView.findViewById(R.id.create_projekt_name_reset_button);
            ImageButton reset_nr= promptsView.findViewById(R.id.create_projekt_nr_reset_button);
            ImageButton select_dir= promptsView.findViewById(R.id.create_projekt_dir_select_button);
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

            reset_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    name.setText("");
                }
            });
            reset_nr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nr.setText("");
                }
            });



            select_dir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    ((Activity) context).startActivityForResult(intent, 2);

                }
            });

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    String value_nr = nr.getText().toString().trim();
                    String value_name = name.getText().toString().trim();
                    String value_root_dir = dir_src.getText().toString().trim();

                    //Input Fallback
                    if(value_name.isEmpty())
                    {
                        value_name = "Projekt@"+bsf.get_date_for_filename();
                    }
                    if(value_nr.isEmpty())
                    {
                        value_nr = bsf.gen_ID();
                    }
                    if(value_root_dir.isEmpty())
                    {
                        value_root_dir = Environment.getExternalStorageDirectory().toString();
                    }

                    if(do_projekt_exist(value_nr,value_name) == true)
                    {
                        Toast.makeText(context, "ERROR: \n Projekt nicht angelegt, da es schon existert!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {

                        try {

                            ContentValues insert_data = new ContentValues();
                            insert_data.put("ID",bsf.gen_UUID());
                            insert_data.put("DATE",bsf.date_refresh_database());
                            insert_data.put("PROJEKT_NR",value_nr);
                            insert_data.put("NAME",value_name);
                            insert_data.put("DIR_ROOT",value_root_dir);
                            insert_data.put("DIR_SUB","[{\"NAME\":\"DEFAULT\",\"DIR\":\"\"}]");
                            insert_data.put("STATUS_FLAG","0");
                            dbo.insert(SQL_finals.BASI_PROJEKTE,insert_data);
                            Toast.makeText(context, "Projekt angelegt!", Toast.LENGTH_LONG).show();
                            projekt_spinner_reload();
                            projekt_info_dialog(insert_data);

                        } catch (Exception e)
                        {
                            Toast.makeText(context, "ERROR: \n Projekt konte nicht angelegt werden\n"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                            throw new RuntimeException(e);
                        }

                    }
                    dialogInterface.cancel();
                }

            });
            alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.show();

        }

        public void delet_projekt_dialog()
        {
            ContentValues output = projekt_spinner_get_selectet_item();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setIcon(R.drawable.alert);

            alertDialogBuilder.setTitle("Projekt entfernen");

            String message = "\n" +projlist.getSelectedItem().toString() +" löschen? \n -> Es werden alle Projektspezifischen Daten enfernt! <-";
            alertDialogBuilder.setMessage(message);

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    String message="";
                    try
                    {
                        boolean del_check = delet_projekt(output.get("ID").toString());

                        if(del_check == true)
                        {
                            message  ="Projekt wurde entfernt! \n Response: "+String.valueOf(del_check);
                            projekt_spinner_reload();

                        }else
                        {
                            message  ="Projekt konnte NICHT entfernt werden! \n Response:"+String.valueOf(del_check);

                        }
                    }catch (Exception e)
                    {
                        message  ="Projekt konnte NICHT entfernt werden! \n Error:"+e.getMessage().toString();

                    }
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show();

                    dialogInterface.cancel();

                }
            });
            alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.show();
        }
        private boolean delet_projekt(String id)
        {
            Boolean  del_check = false;
            try {
                SQLiteDatabase dbw = this.getWritableDatabase();
                long response = dbw.delete(BASI_PROJEKTE,"ID=?",new String[]{id});

                if(response >0)
                {
                   del_check =true;

                }
                dbw.close();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }


            return  del_check;
        }
        public void projekt_info_dialog(ContentValues output)
        {
            Basic_funct bsf = new Basic_funct();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);

            alertDialogBuilder.setTitle("Projekt Infos");

            ContentValues info = projekt_info(output.get("ID").toString());

            String message ="";

            String message_sub_dirs ="";

            for(String key: info.keySet())
            {
                switch(key.toString())
                {
                    case "DIR_SUB":
                        message_sub_dirs += key.toString()+":\n";
                        try {
                            JSONArray a = new JSONArray(info.get(key).toString());
                            for(int c= 0;c < a.length();c++)
                            {
                                JSONObject o = new JSONObject(a.get(c).toString());
                                message_sub_dirs += "+ "+bsf.URLdecode(o.getString("NAME"))+"\n";
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    default:

                            message += key.toString()+": "+ info.get(key)+"\n";

                }
            }
            message+=message_sub_dirs;

            alertDialogBuilder.setMessage(message);

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();

                }
            });
            String finalMessage = message;
            alertDialogBuilder.setNeutralButton("Kopieren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                   Basic_funct bsf  =new Basic_funct();
                   bsf.copy_to_clipboard(finalMessage,context);
                }
            });
            alertDialogBuilder.show();
        }
        private ContentValues projekt_info(String id)
        {
            ContentValues output_data = new  ContentValues();

            SQLiteDatabase dbr = this.getReadableDatabase();

            Cursor cursor = dbr.query(BASI_PROJEKTE,null,"ID=?",new String[]{id},null,null,null);
            cursor.moveToFirst();

            String[] colums = cursor.getColumnNames();
            for(String c: colums)
            {
                output_data.put(c,cursor.getString(cursor.getColumnIndexOrThrow(c)));
            }
            return output_data;
        }

        public void update_projekt_dialog(ContentValues projekt_data)
        {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.settings_projekt_create_new,null,false);
                // ----------------------------------------------------------------- Variablen
                // ----------------------------------------------------------------- Variablen  String, char
                // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
                // ----------------------------------------------------------------- Variablen 	Boolean
                // ----------------------------------------------------------------- Instanzen
                Basic_funct bsf = new Basic_funct();
                // ----------------------------------------------------------------- TextView
                TextView title = promptsView.findViewById(R.id.create_dialog_title);
                // ----------------------------------------------------------------- AutoCompleteTextView
                // ----------------------------------------------------------------- EditText
                EditText name = promptsView.findViewById(R.id.create_projekt_name);
                EditText nr = promptsView.findViewById(R.id.create_projekt_nr);
                dir_src  = promptsView.findViewById(R.id.create_projekt_dir); ///Global
                // ----------------------------------------------------------------- Button
                // ----------------------------------------------------------------- ImageButtons
                ImageButton reset_name= promptsView.findViewById(R.id.create_projekt_name_reset_button);
                ImageButton reset_nr= promptsView.findViewById(R.id.create_projekt_nr_reset_button);
                ImageButton select_dir= promptsView.findViewById(R.id.create_projekt_dir_select_button);
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
                nr.setText(projekt_data.get("PROJEKT_NR").toString());
                name.setText(projekt_data.get("NAME").toString());
                setDir_src_in_dialog(projekt_data.get("DIR_ROOT").toString());
                title.setText((projekt_data.get("NAME").toString() +" Bearbeiten"));
                reset_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        name.setText("");
                    }
                });
                reset_nr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nr.setText("");
                    }
                });

                select_dir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        ((Activity) context).startActivityForResult(intent, 2);

                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        String value_nr = nr.getText().toString().trim();
                        String value_name = name.getText().toString().trim();
                        String value_root_dir = dir_src.getText().toString().trim();

                        //Input Fallback
                        if(value_name.isEmpty() || value_nr.isEmpty() || value_root_dir.isEmpty())
                        {
                            bsf.error_msg("ERROR: \n Projekt nicht geändert werden: \n -> Leere oder Ungültige Datenfelder",context);
                        }
                        else
                        {
                            if(value_name.equals(projekt_data.get("NAME").toString()) && value_nr.equals(projekt_data.get("PROJEKT_NR").toString()))
                            {
                                projekt_data.put("PROJEKT_NR",value_nr);
                                projekt_data.put("NAME",value_name);
                                projekt_data.put("DIR_ROOT",value_root_dir);
                                projekt_update(projekt_data);
                            }
                            else
                            {
                                if(do_projekt_exist(value_nr,value_name) == true)
                                {
                                    bsf.error_msg("ERROR: \n Projekt konnte nicht geändert werden: \n -> Datenkollision",context);
                                }
                                else
                                {
                                    projekt_data.put("PROJEKT_NR",value_nr);
                                    projekt_data.put("NAME",value_name);
                                    projekt_data.put("DIR_ROOT",value_root_dir);
                                    projekt_update(projekt_data);
                                }
                            }

                        }
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.show();
        }
        public void projekt_update(ContentValues projekt_data)
        {
            Basic_funct bsf = new Basic_funct();
            try {

                boolean resonse = dbo.update(SQL_finals.BASI_PROJEKTE,projekt_data);
                if(resonse == true)
                {
                    projekt_spinner_reload();
                    bsf.succes_msg( "Projektdaten wurden abgeändert!\n Response :"+resonse,context);
                }
                else
                {
                    bsf.error_msg("Projektdaten konnten nicht abgeändert!\n Response:"+resonse,context);
                }
            } catch (Exception e)
            {
                bsf.error_msg("ERROR: \n ->"+e.getMessage().toString(),context);
                throw new RuntimeException(e);
            }
        }

        public void projekt_select()
        {
            Basic_funct bsf = new Basic_funct();
            try {
                String selected_id = projekt_get_selected_id();
                String new_selected_id = projekt_spinner_get_selectet_item().get("ID").toString();

                if(selected_id != "0")
                {
                   if(projekt_set_unselect(selected_id) == true)
                   {
                      if(projekt_set_select(new_selected_id) == true)
                      {
                          current_selectet.setText(get_selectet_projekt());

                      }
                      else
                      {
                          bsf.error_msg("Error: projekt_set_select=false",context);
                      }
                   }
                   else
                   {
                       bsf.error_msg("Error: projekt_set_unselect=false",context);
                   }
                }
                else
                {
                    if(projekt_set_select(new_selected_id) == true)
                    {
                        current_selectet.setText(get_selectet_projekt());
                    }
                    else
                    {
                        bsf.error_msg("Error: projekt_set_select =false",context);
                    }

                }
            }
            catch (Exception e)
            {
                bsf.error_msg("Error (Exception \n"+e.getMessage().toString(),context);
                throw new RuntimeException(e);
            }
        }
    }

    public String projekt_get_selected_id()
    {
        String default_id = "0";

        try {
            SQLiteDatabase dbr = this.getReadableDatabase();
            Cursor cursor = dbr.query(BASI_PROJEKTE,null,"STATUS_FLAG=?",new String[]{"1"},null,null,null);

            if(cursor.getCount() >0)
            {
                while(cursor.moveToNext())
                {
                    default_id =cursor.getString(cursor.getColumnIndexOrThrow("ID"));
                }
            }
            cursor.close();
            dbr.close();
        } catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        return   default_id;
    }

    public String projekt_get_selected_root_dir()
    {
        String default_root = "0";

        try {
            SQLiteDatabase dbr = this.getReadableDatabase();
            Cursor cursor = dbr.query(BASI_PROJEKTE,null,"STATUS_FLAG=?",new String[]{"1"},null,null,null);

            if(cursor.getCount() >0)
            {
                while(cursor.moveToNext())
                {
                    default_root =cursor.getString(cursor.getColumnIndexOrThrow("DIR_ROOT"));
                }
            }
            cursor.close();
            dbr.close();
        } catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        return   default_root;
    }

    public boolean projekt_set_unselect(String id)
    {
        boolean run_check = false;
        try {
            SQLiteDatabase dbw = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("STATUS_FLAG","0");

            long response = dbw.update(BASI_PROJEKTE,values,"ID=?",new String[]{id});

            if(response != -1)
            {
                run_check =true;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  run_check;
    }

    public boolean projekt_set_select(String id)
    {
        boolean run_check = false;
        try {
            SQLiteDatabase dbw = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("STATUS_FLAG","1");

            long response = dbw.update(BASI_PROJEKTE,values,"ID=?",new String[]{id});

            if(response != -1)
            {
                run_check =true;

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  run_check;
    }


    public String projekt_get_current_root_dir()
    {
        String root_dir = "";

        try {
            SQLiteDatabase dbr = this.getReadableDatabase();

            Cursor cursor = dbr.query(BASI_PROJEKTE,null,"STATUS_FLAG=?",new String[]{"1"},null,null,null);
            cursor.moveToFirst();
            root_dir =cursor.getString(cursor.getColumnIndexOrThrow("DIR_ROOT"));
        } catch (IllegalArgumentException e)
        {

            throw new RuntimeException(e);
        }

        return root_dir;

    }

    private boolean do_projekt_exist(String projekt_nr, String name)
    {
        ContentValues args = new ContentValues();
        args.put("PROJEKT_NR",projekt_nr);
        args.put("NAME",name);

        return   dbo.entry_exist(SQL_finals.BASI_PROJEKTE,args);
    }
    public static class kamera_dir extends projekt_ops

    {
        public kamera_dir(@Nullable Context context)
        {
            super(context);

        }

        public String get_dir(String projekt_id)
        {
            String dir_list ="";

            SQLiteDatabase db = this.getReadableDatabase();
            String[] selectionArgs = {projekt_id};
            String where = "ID=?";

            Cursor cursor = null;
            try {
                cursor = db.query(BASI_PROJEKTE,null,where,selectionArgs,null,null,null);
                if(cursor.getCount() !=0)
                {
                    cursor.moveToFirst();
                    dir_list = (cursor.getString(cursor.getColumnIndexOrThrow("DIR_SUB")));
                }
            }
            catch (IllegalArgumentException e)
            {
                throw new RuntimeException(e);
            }

            cursor.close();
            db.close();


         return dir_list;

        }
        public  void set_dir(String projekt_id,String input)
        {
            //reset von allen
            long response =0;
            SQLiteDatabase db = this.getWritableDatabase();

            String[] selectionArgs = {projekt_id};
            String where= "ID=?";

            ContentValues  data = new ContentValues();
            data.put("DIR_SUB",input);
            response = db.update(BASI_PROJEKTE,data,where,selectionArgs);
            Log.d("BASI", String.valueOf(response));

            db.close();
        }

    }

}









