package com.example.tabnav_test;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class m_conf_maschine extends AppCompatActivity
{

    static final String RROJ_NR = "0";

    Uri imgUri= null;

    ImageButton m_conf_add_maschine_button;
    ImageButton m_conf_find_maschine_button;
    ImageButton m_conf_filter_maschine_button;

    ImageView m_pic;

    RecyclerView m_rcv;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 1:
                        imgUri = data.getData();
                        Log.i("BASI", "Uri: " +  data.getData());
                        m_pic.setImageURI(imgUri);

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mconf_maschine);

        Context context =getApplicationContext();
        m_database_ops mdo = new m_database_ops(context);

        m_conf_add_maschine_button = (ImageButton) findViewById(R.id.m_conf_add_maschine_button);
        m_conf_find_maschine_button = (ImageButton) findViewById(R.id.m_conf_find_maschine_button);
        m_conf_filter_maschine_button = (ImageButton) findViewById(R.id.m_conf_filter_maschine_button);

        m_rcv = (RecyclerView) findViewById(R.id.m_rcv);


        String[] data = mdo.get_maschinen(RROJ_NR);




        m_conf_maschine_adapter mcma = new m_conf_maschine_adapter(data);
        m_rcv.setAdapter(mcma);

        m_rcv.setLayoutManager( new LinearLayoutManager(m_conf_maschine.this));

        m_conf_add_maschine_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                View promptsView = getLayoutInflater().inflate(R.layout.m_add_maschine_dialog, null);

                EditText name = promptsView.findViewById(R.id.m_name);
                EditText nr = promptsView.findViewById(R.id.m_nr);
                Spinner category = promptsView.findViewById(R.id.m_category);
                EditText note  = promptsView.findViewById(R.id.m_note);
                EditText counter = promptsView.findViewById(R.id.m_counter);
                ImageButton add_image= promptsView.findViewById(R.id.imageButton13);

                m_pic = promptsView.findViewById(R.id.imageView);



                add_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");

                        startActivityForResult(intent, 1);

                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_conf_maschine.this);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Maschine hinzufügen");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        ContentValues data = new ContentValues();
                        data.put("PROJ_NR", RROJ_NR);

                        data.put("NR", nr.getText().toString());
                        data.put("NAME", name.getText().toString());
                        data.put("CATEGORY", category.getSelectedItem().toString());
                        data.put("COUNTER",counter.getText().toString());
                        data.put("NOTE",note.getText().toString());
                        data.put("PIC_SRC",imgUri.toString());
                        data.put("ONOFF_FLAG","TRUE");

                        Log.d("BASI",counter.getText().toString());
                        Log.d("BASI",note.getText().toString());


                        mdo.add_manschine(data);

                        m_conf_maschine_adapter mcma = new m_conf_maschine_adapter(mdo.get_maschinen(RROJ_NR));
                        m_rcv.setAdapter(mcma);

                        //(ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");

                     dialogInterface.cancel();

                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        });






    }




}