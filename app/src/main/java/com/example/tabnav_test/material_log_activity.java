package com.example.tabnav_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;

public class material_log_activity extends AppCompatActivity
{

    private AppBarConfiguration appBarConfiguration;

   public static Basic_func_img bsfi = new Basic_func_img(); //Globale Instan von bsfi


    ViewPager2 viewpager;
    TabLayout tabLayout;

    material_log_slidePagerAdapter slide_adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            bsfi.photo.setImageBitmap(BitmapFactory.decodeFile(bsfi.image_path));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_material_log_activity);

        viewpager = (ViewPager2) findViewById(R.id.viewPager_log_material);

        tabLayout = (TabLayout) findViewById((R.id.material_log_tab_layout));
        slide_adapter = new material_log_slidePagerAdapter(this);

        viewpager.setAdapter(slide_adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {

            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
               viewpager.setCurrentItem(tab.getPosition());
               Log.d("TAB NR", String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });



    }
}