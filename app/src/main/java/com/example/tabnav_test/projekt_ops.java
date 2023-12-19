package com.example.tabnav_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.Import_Export.Backup;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.material.material_database_ops;
import com.example.tabnav_test.static_finals;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

public class projekt_ops extends SQLiteOpenHelper implements SQL_finals
{
    Context context;
    db_ops dbo;
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

    public String get_selectet_projekt_id()
    {
        Basic_funct bsf = new Basic_funct();
        this.test_exist_projects();
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

        String root =cursor.getString(cursor.getColumnIndexOrThrow("SRC")).replace("primary:", Environment.getExternalStorageDirectory().toString()+"/");

        cursor.close();
        db.close();

        return  root;
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

    public void projekt_settings(Context context)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.settings_proj_dialog,null,false);

        // ----------------------------------------------------------------- Variablen
        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        projekt_browser browser = new projekt_browser(context);

        // ----------------------------------------------------------------- TextView
        TextView current_selectet= promptsView.findViewById(R.id.current_selectet);
        // ----------------------------------------------------------------- AutoCompleteTextView
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        ImageButton menu = promptsView.findViewById(R.id.imageButton46);
        // ----------------------------------------------------------------- ImageView
        // ----------------------------------------------------------------- ListView

        // ----------------------------------------------------------------- RecyclerView
        // ----------------------------------------------------------------- Spinner
        Spinner projlist = promptsView.findViewById(R.id.spinner2);
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
            current_selectet.setText(this.get_selectet_projekt());
        } catch (Exception e) {

            throw new RuntimeException(e);
        }

        projlist.setAdapter(browser.list_for_spinner());

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

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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

    private class projekt_browser extends projekt_ops
    {

        public projekt_browser(@Nullable Context context)
        {
            super(context);
        }

        private ArrayAdapter list_for_spinner()
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

        private void create_projekt()
        {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.settings_projekt_create_new,null,false);



            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    ContentValues data = new ContentValues();
                    data.put("KEY1","1");
                    data.put("KEY2","2");
                    data.put("KEY3","3");
                    dbo.entry_exist("tet",data);
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
    }

    public class sub_dir_list extends BaseAdapter
    {
        String[] data;
        Context context;
        LayoutInflater layoutInflater;

        public sub_dir_list(String[] data, Context context)
        {

            super();
            this.data = data;
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup paren)
        {
            convertView= layoutInflater.inflate(R.layout.sub_dir_projekt_list, null);
            TextView txt=(TextView)convertView.findViewById(R.id.textView98);
            txt.setText(data[position]);
            return convertView;

        }
    }







}
