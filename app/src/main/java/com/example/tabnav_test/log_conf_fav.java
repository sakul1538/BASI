package com.example.tabnav_test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.databinding.ActivityLogConfFavBinding;

public class log_conf_fav extends AppCompatActivity
{

    private AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setTitle("Kategorien");
        setContentView(R.layout.activity_log_conf_fav);

        RecyclerView rcv1= findViewById(R.id.fav_view);

        log_fav getfav = new log_fav(getApplicationContext());

        String[] t =getfav.getalllogfav("0");

        log_favorite_settings_adapter lca = new log_favorite_settings_adapter(t);
        rcv1.setAdapter(lca);

        rcv1.setLayoutManager( new LinearLayoutManager(log_conf_fav.this));
    }

}