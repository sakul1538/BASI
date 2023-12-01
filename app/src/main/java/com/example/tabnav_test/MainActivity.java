package com.example.tabnav_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MicrophoneInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.tabnav_test.Import_Export.Backup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
{

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    ScreenSlidePagerAdapter adapter;
    ScrollView scroll_main;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BASI","MainActivity:"+ String.valueOf(requestCode));
        switch(requestCode)
        {
            case 1: //button_favorite_strings_restore_backup
                Log.d("BASI",data.getData().getPath());
                Backup backup = new Backup(getApplicationContext());
                try {
                    backup.restore_backup(SQL_finals.TB_NAME_LOG_CONF,data.getData().getPath(),false);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;


        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();


        // assigning ID of the toolbar to a variable
        // using toolbar as ActionBar

        viewPager2 =(ViewPager2) findViewById(R.id.viewPager1);
        //scroll_main =(ScrollView) findViewById(R.id.scroll_view_main);



        CoordinatorLayout clay = (CoordinatorLayout) findViewById(R.id.colay_main);
        tabLayout = (TabLayout) findViewById((R.id.tabLayout3));
        adapter = new ScreenSlidePagerAdapter(this);
        viewPager2.setAdapter(adapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position)
                    {
                        tabLayout.selectTab(tabLayout.getTabAt(position));

                super.onPageSelected(position);
            }
        });



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager2.setCurrentItem(tab.getPosition());
               //scroll_main.scrollTo(0,0);


                Log.d("POSI", String.valueOf(tab.getPosition()));
              /*  switch (tab.getPosition())
                {
                    case 0:
                       tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_5));
                        //viewPager2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_5));
                      //  clay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_5));

                        break;

                    case 1:
                        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_2));
                      //  viewPager2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_2));
                        //clay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_2));
                        break;

                    case 2:
                        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_3));
                      //  viewPager2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_3));
                      //  clay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Cornfield_3));
                         break;

                    case 3:
                        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Greenjungle_2));
                        break;
                    case 4:
                        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Greenjungle_1));
                        break;



                }

               */








            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



    }
}





















