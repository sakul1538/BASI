package com.example.tabnav_test.material;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.example.tabnav_test.Basic_func_img;
import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.material.galery_adaper;
import com.example.tabnav_test.material_log_slidePagerAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;

public class material_log_activity extends AppCompatActivity
{


    public static String  filePath;
    private AppBarConfiguration appBarConfiguration;
    private static final String TAG="BASI";

    public static Basic_func_img bsfi = new Basic_func_img(); //Globale Instan von bsfi


    ViewPager2 viewpager;
    TabLayout tabLayout;

    material_log_slidePagerAdapter slide_adapter;
    public static galery_adaper gad;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BASI_RQ material_log_activity",String.valueOf(requestCode));

        String source_file_path="";
        Uri uri;

        switch (requestCode)
        {
            case 1:
                gad.reload_images(getApplicationContext());
                break;
            case 2:
                //Von ls_log_view_rcv_adapter  => Intent Open Document
                uri = data.getData();
                Log.d("BASI Paht",uri.getPath());

                if((uri).getPath().contains("/document/primary:"))
                {
                    source_file_path = uri.getPath().replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
                    try
                    {
                        Basic_funct bsf =new Basic_funct();
                        File source_file = new File(source_file_path);
                        Basic_funct.copyFileUsingStream(source_file, new File(filePath)); //Kopieren von-zu
                        gad.reload_images(getApplicationContext());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Error:\n" + uri, Toast.LENGTH_SHORT).show();
                    source_file_path ="";
                }
                break;

            case 3:

                uri = data.getData();

                if((uri).getPath().contains("/document/primary:"))
                {
                    source_file_path = uri.getPath().replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
                    Log.d("source_file_path",source_file_path);
                    String file_extension = bsfi.detect_extension(source_file_path);
                    material_database_ops mdo = new material_database_ops(getApplicationContext());
                    String filename= source_file_path.substring(source_file_path.lastIndexOf("/",source_file_path.length()));
                    try
                    {
                        Basic_funct bsf =new Basic_funct();
                        File source_file = new File(source_file_path);

                        String copy_to =mdo.get_ls_images_dir()+filename;
                        Log.d("BASI", copy_to);
                        Basic_funct.copyFileUsingStream(source_file, new File(copy_to)); //Kopieren von-zu
                        gad.reload_images(getApplicationContext());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    bsfi.error_msg("Error:\n" + uri,getApplicationContext());
                    source_file_path ="";
                }

                break;
                //Todo Wird das noch verwendet?

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_material_log_activity);

        viewpager = findViewById(R.id.viewPager_log_material);

        tabLayout = findViewById((R.id.material_log_tab_layout));
        slide_adapter = new material_log_slidePagerAdapter(this,tabLayout);

        viewpager.setAdapter(slide_adapter);
        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
               viewpager.setCurrentItem(tab.getPosition());
             //  Log.d("TAB NR", String.valueOf(tab.getPosition()));
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