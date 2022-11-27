package com.example.tabnav_test;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class m_conf_maschine extends AppCompatActivity
{

    static final String RROJ_NR = "0";

    ImageButton m_conf_add_maschine_button;
    ImageButton m_conf_find_maschine_button;
    ImageButton m_conf_filter_maschine_button;
    RecyclerView m_rcv;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mconf_maschine);

        m_conf_add_maschine_button = (ImageButton) findViewById(R.id.m_conf_add_maschine_button);
        m_conf_find_maschine_button = (ImageButton) findViewById(R.id.m_conf_find_maschine_button);
        m_conf_filter_maschine_button = (ImageButton) findViewById(R.id.m_conf_filter_maschine_button);

        m_rcv = (RecyclerView) findViewById(R.id.m_rcv);


        String[] t = {"U20 [2323]","KX80 [2325]","Dumper [2024]"};
        m_conf_maschine_adapter mcma = new m_conf_maschine_adapter(t);
        m_rcv.setAdapter(mcma);

        m_rcv.setLayoutManager( new LinearLayoutManager(m_conf_maschine.this));

        m_conf_add_maschine_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_conf_maschine.this);
                View promptsView = getLayoutInflater().inflate(R.layout.m_add_maschine_dialog, null);
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setTitle("Maschine hinzufügen");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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