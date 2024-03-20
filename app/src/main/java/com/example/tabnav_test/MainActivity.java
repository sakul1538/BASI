package com.example.tabnav_test;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Import_Export.Backup;

import com.example.tabnav_test.Kamera.Kamera;
import com.google.android.material.tabs.TabLayout;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
{
    // ----------------------------------------------------------------- Variablen

    // ----------------------------------------------------------------- Variablen  String, char

    String source_path ="";
    // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
    // ----------------------------------------------------------------- Variablen 	Boolean
    // ----------------------------------------------------------------- Instanzen
    ScreenSlidePagerAdapter adapter;
    projekt_ops projekt;

    // ----------------------------------------------------------------- TextView
    // ----------------------------------------------------------------- AutoCompleteTextView
    // ----------------------------------------------------------------- EditText

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
    ScrollView scroll_main;
    // ----------------------------------------------------------------- Layouts
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    // ----------------------------------------------------------------- END


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 1: //button_favorite_strings_restore_backup
                Log.d("BASI",data.getData().getPath());
                Backup backup = new Backup(getApplicationContext());
                try {
                    backup.restore_backup(SQL_finals.TB_NAME_LOG_CONF,data.getData().getPath());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;

            case 2:

                projekt.browser.setDir_src_in_dialog(data.getData().getPath().replace("/tree/primary:",Environment.getExternalStorageDirectory().toString()+"/")+"/");
                break;
            case 3:

                    String path_to_restore_file =data.getData().getPath(); //primary:BASI/Backup&Exports/BASI_PROJEKTE@20240109.json
                    String backup_restore_file_src = path_to_restore_file.replace("/document/primary:", Environment.getExternalStorageDirectory().toString()+"/");
                    if(projekt.projekt_backup.restore_backup(backup_restore_file_src,projekt.projekt_backup.import_backup_overwrite))
                    {
                        projekt.browser.projekt_spinner_reload();
                    }



                break;
            default:
                Log.d("BASI","MainActivity:"+ requestCode);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db_ops dbo= new db_ops(getApplicationContext());
        dbo.init();

        // ----------------------------------------------------------------- Variablen
        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        projekt = new projekt_ops(getApplicationContext());
        adapter = new ScreenSlidePagerAdapter(this);

        // ----------------------------------------------------------------- TextView
        projekt.current_projekt_main_title= findViewById(R.id.textView91);

        // ----------------------------------------------------------------- AutoCompleteTextView
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        ImageButton app_settings = findViewById(R.id.imageButton49);
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
        CoordinatorLayout clay = findViewById(R.id.colay_main);
        tabLayout = findViewById((R.id.tabLayout3));
        viewPager2 = findViewById(R.id.viewPager1);
        viewPager2.setAdapter(adapter);
        // ----------------------------------------------------------------- END
       //init
        projekt.current_projekt_main_title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                projekt.projekt_settings(MainActivity.this);


            }
        });
        try {
            projekt.current_projekt_main_title.setText(projekt.get_selectet_projekt());
        } catch (Exception e)
        {
            projekt.current_projekt_main_title.setText("");
            Toast.makeText(getApplicationContext(),"Error\n"+ e.getMessage(),Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }

        app_settings.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view)
            {
                app_settings_menu(view);

            }
        });

       // scroll_main =(ScrollView) findViewById(R.id.scroll_view_main);

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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void app_settings_menu(View view)
    {
        PopupMenu popup = new PopupMenu(getApplicationContext(),view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.settings_app_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                if (menuItem.getItemId() == R.id.settings_app_projektverwaltung) {
                    projekt.projekt_settings(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this, "Nicht Implementiert", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        popup.show();

    }

}





















