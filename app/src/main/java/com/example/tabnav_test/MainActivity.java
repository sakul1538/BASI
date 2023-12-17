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
import android.text.style.ClickableSpan;
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

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity
{
    // ----------------------------------------------------------------- Variablen
    // ----------------------------------------------------------------- Variablen  String, char
    // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
    // ----------------------------------------------------------------- Variablen 	Boolean
    // ----------------------------------------------------------------- Instanzen
    ScreenSlidePagerAdapter adapter;

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
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        db_ops dbo= new db_ops(getApplicationContext());
        dbo.init();

        // ----------------------------------------------------------------- Variablen
        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        projekt_ops projekt = new projekt_ops(getApplicationContext());
        adapter = new ScreenSlidePagerAdapter(this);

        // ----------------------------------------------------------------- TextView
        TextView current_projekt = findViewById(R.id.textView91);
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
        CoordinatorLayout clay = (CoordinatorLayout) findViewById(R.id.colay_main);
        tabLayout = (TabLayout) findViewById((R.id.tabLayout3));

        viewPager2 =(ViewPager2) findViewById(R.id.viewPager1);
        viewPager2.setAdapter(adapter);
        // ----------------------------------------------------------------- END

       //init
        try {
            current_projekt.setText(projekt.get_selectet_projekt());
        } catch (Exception e)
        {
            current_projekt.setText("");
            Toast.makeText(getApplicationContext(),"Error\n"+e.getMessage().toString(),Toast.LENGTH_LONG).show();
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
        popup.show();

    }
}





















