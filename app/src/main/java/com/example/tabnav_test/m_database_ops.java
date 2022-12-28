package com.example.tabnav_test;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class m_database_ops  extends SQLiteOpenHelper implements SQL_finals
{
 Context context2;

    public m_database_ops(@Nullable Context context)
    {
        super(context, DB_NAME, null, 1);
        context2 = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {

    }

    public long add_manschine(ContentValues data)
    {
        Basic_funct bsf = new Basic_funct();


            long newRowId = -1;
            try
            {
                SQLiteDatabase db = this.getWritableDatabase();

                data.put("ID", bsf.gen_ID());
                data.put("DATE", bsf.date_refresh());
                data.put("TIME", bsf.time_refresh());

// Insert the new row, returning the primary key value of the new row
                newRowId = db.insert(TB_NAME_MASCHINEN_ITEMS,null,data);

            }catch (Exception e)
            {
                Log.d("BASI","add_manschine:"+e.toString());

            }

            return newRowId;

        }


    public String[] get_maschinen(String proj_id)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {proj_id};
        String where = "PROJ_NR=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        String[] strings = new String[cursor.getCount()];

        while (cursor.moveToNext())
        {

            String value = cursor.getString(cursor.getColumnIndexOrThrow("ID"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NR"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("COUNTER"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("NOTE"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("PIC_SRC"));
            value +=","+cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG"));

            strings[i]=value;
            i++;
            Log.d("Wert",value);
        }
        cursor.close();
        db.close();

        return  strings;

    }

    public int delet_id(String proj_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = {proj_id};
        String where = "ID=?";


        int response = db.delete(TB_NAME_MASCHINEN_ITEMS,where,selectionArgs);

        return response;
    }


    public ContentValues get_maschine(String id)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] selectionArgs = {id};
        String where = "ID=?";
        int i=0;

        Cursor cursor = db.query(TB_NAME_MASCHINEN_ITEMS,null,where,selectionArgs,null,null,null);
        cursor.moveToFirst();

        ContentValues data = new ContentValues();

        data.put("ID",cursor.getString(cursor.getColumnIndexOrThrow("ID")));
        data.put("DATE",cursor.getString(cursor.getColumnIndexOrThrow("DATE")));
        data.put("TIME",cursor.getString(cursor.getColumnIndexOrThrow("TIME")));
        data.put("NR",cursor.getString(cursor.getColumnIndexOrThrow("NR")));
        data.put("NAME",cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
        data.put("CATEGORY",cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY")));
        data.put("COUNTER",cursor.getString(cursor.getColumnIndexOrThrow("COUNTER")));
        data.put("NOTE",cursor.getString(cursor.getColumnIndexOrThrow("NOTE")));
        data.put("PIC_SRC",cursor.getString(cursor.getColumnIndexOrThrow("PIC_SRC")));
        data.put("ONOFF_FLAG",cursor.getString(cursor.getColumnIndexOrThrow("ONOFF_FLAG")));

        cursor.close();

        return  data;

    }

    public int get_cateogry_index(String value)
    {
         ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context2, android.R.layout.simple_dropdown_item_1line,context2.getResources().getStringArray(R.array.m_categorys));

          int index_pos= spinnerArrayAdapter.getPosition(value);

    return index_pos;

    }

    public Bitmap getBitmap(String path)
    {
        Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + path);

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 300, 200, true);

        return bMapScaled;
    }

    public void dialog(Context context)
    {

        Uri imgUri = null;

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.m_add_maschine_dialog, null);

        EditText name = promptsView.findViewById(R.id.m_name);
        EditText nr = promptsView.findViewById(R.id.m_nr);
        Spinner category = promptsView.findViewById(R.id.m_category);
        EditText note = promptsView.findViewById(R.id.m_note);
        EditText counter = promptsView.findViewById(R.id.m_counter);
        ImageButton add_image = promptsView.findViewById(R.id.imageButton13);
        ImageView m_pic = promptsView.findViewById(R.id.imageView);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context.getApplicationContext());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Maschine hinzufügen");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {





             //   m_conf_maschine_adapter mcma = new m_conf_maschine_adapter(mdo.get_maschinen(RROJ_NR));
                //m_rcv.setAdapter(mcma);


                //(ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");

                dialogInterface.cancel();

            }
        });

        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();


    }


    public int update_manschine(String proj_id,String id,ContentValues data)
    {

        int response =0;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] selectionArgs = { proj_id,id };
        String where = "PROJ_NR=? AND ID=?";


        response = db.update(TB_NAME_MASCHINEN_ITEMS,data,where,selectionArgs);

        return response;

    }
}



