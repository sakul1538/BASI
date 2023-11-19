package com.example.tabnav_test;

import static com.example.tabnav_test.R.id.categorys_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class log_conf_categorys extends AppCompatActivity
{
    static final String RROJ_NR="0";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_conf_categorys);

        this.setTitle("Kategorien");

        RecyclerView rcv1= findViewById(categorys_view);

        log_fav log_categorys = new log_fav(log_conf_categorys.this);

        String[] t =log_categorys.getallcategorys();

        log_categorys_settings_adapter lca = new log_categorys_settings_adapter(t);
        rcv1.setAdapter(lca);

        rcv1.setLayoutManager( new LinearLayoutManager(log_conf_categorys.this));

    }


}
