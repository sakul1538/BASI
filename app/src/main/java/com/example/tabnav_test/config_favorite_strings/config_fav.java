package com.example.tabnav_test.config_favorite_strings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.Import_Export.Backup;
import com.example.tabnav_test.R;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.static_finals;
import com.example.tabnav_test.material.material_database_ops;

import org.json.JSONException;

public class config_fav
{
    Context context;
    long touchStartTime = 0;
    public static Spinner  spinner_favorite_strings_settings;
    Basic_funct bsf = new Basic_funct();

    public static final String backup_dir ="/Backups&Exports/"; //



    public config_fav(Context context)
    {
        this.context = context;
    }


    public void show_dialog(ViewGroup container)
    {
        LayoutInflater li = LayoutInflater.from(this.context);
        View view_artikel = li.inflate(R.layout.favorite_strings_config_dialog,container,false);

        AlertDialog.Builder  fav_config_dialog = new AlertDialog.Builder(this.context);
        fav_config_dialog.setView(view_artikel);

        //ImageButtons
        ImageButton imagebutton_favorite_strings_update = view_artikel.findViewById(R.id.imagebutton_favorite_strings_update);
        ImageButton imageButton_favorite_strings_delet = view_artikel.findViewById(R.id.imageButton_favorite_strings_delet);
        ImageButton imageButton_favorite_strings_add = view_artikel.findViewById(R.id.imageButton_favorite_strings_add);
        Button button_favorite_strings_crate_backup = view_artikel.findViewById(R.id.button_favorite_strings_crate_backup);
        Button button_favorite_strings_restore_backup = view_artikel.findViewById(R.id.button_favorite_strings_restore_backup);
        AutoCompleteTextView autoCompleteText_favorite_strings_add = view_artikel.findViewById(R.id.autoCompleteText_favorite_strings_add);

        //Spinners
        spinner_favorite_strings_settings = view_artikel.findViewById(R.id.spinner_favorite_strings_settings);

        refresh_spinner_favorite_strings_settings(context);

        //Switch


        button_favorite_strings_crate_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Backup backup = new Backup(context);
                material_database_ops mdo = new material_database_ops(context);
                String path= mdo.get_projekt_backup_dir()+"/"+"FAVORITE_STRINGS/";
                String filename = mdo.get_projekt_name()+"["+mdo.get_projekt_nr()+"]FAVORITE_STRINGS"+"@"+bsf.get_date_for_filename()+".json";
                try {
                    String[] where_args = {mdo.get_selectet_projekt_id(),"0","FAVORITE_STRING"};
                    String  where = "(ID=? OR ID=?) AND NAME=?";
                    backup.create_backup(SQL_finals.TB_NAME_LOG_CONF,where, where_args,null,path,filename);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        button_favorite_strings_restore_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent_restore_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent_restore_backup.addCategory(Intent.CATEGORY_OPENABLE);
                intent_restore_backup.setType("application/json");
                ((Activity) context).startActivityForResult(intent_restore_backup, 1);


            }
        });

        imageButton_favorite_strings_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {  config_fav_ops cfop = new config_fav_ops(context);
                try {
                 cfop.add_favorite_string(autoCompleteText_favorite_strings_add.getText().toString());
                 refresh_spinner_favorite_strings_settings(context);
                 autoCompleteText_favorite_strings_add.setText("");

                } catch (Exception e)
                {
                    cfop.bsf.exeptiontoast(e,context);
                }
            }
        });


        imageButton_favorite_strings_delet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                config_fav_ops cfop = new config_fav_ops(context);

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        // Speichern Sie den Zeitpunkt, wenn der Finger die Ansicht berührt hat
                        touchStartTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_UP:
                        // Berechnen Sie die Dauer des Drucks
                        long pressDuration = System.currentTimeMillis() - touchStartTime;

                        // Überprüfen Sie, ob es sich um einen langen Druck handelt
                        if (pressDuration >= 1000)
                        {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Bestätigen");
                            alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);

                            alertDialogBuilder.setMessage("ALLE Einträge Löschen?").setPositiveButton("ALLE Löschen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                    cfop.delete_all();
                                    refresh_spinner_favorite_strings_settings(context);
                                    dialogInterface.cancel();
                                }
                            });
                            alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        else
                        {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Bestätigen");
                            alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                            alertDialogBuilder.setMessage("Text:  " +spinner_favorite_strings_settings.getSelectedItem().toString().replace(" (Global)","") + "  wiklich löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                    cfop.delete_element(spinner_favorite_strings_settings.getSelectedItem().toString().replace(" (Global)",""));
                                    refresh_spinner_favorite_strings_settings(context);


                                    dialogInterface.cancel();
                                }
                            });

                            alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.cancel();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                        return true;
                }
                return false;

            }
        });


        imagebutton_favorite_strings_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                LayoutInflater li = LayoutInflater.from(context);
                View view_favorite_strings_update_dialog = li.inflate(R.layout.favorite_strings_change_dialog, container, false);

                //Edit Text
                EditText editTextText2_favorite_string_update_value = view_favorite_strings_update_dialog.findViewById(R.id.editTextText2_favorite_string_update_value);

                //ImageButtons
                ImageButton imageButton_favorite_string_update_reset_value = view_favorite_strings_update_dialog.findViewById(R.id.imageButton_favorite_string_update_reset_value);

                //Radio Button
                RadioButton  radioButton_global = view_favorite_strings_update_dialog.findViewById(R.id.radioButton_global);
                RadioButton  radioButton_lokal = view_favorite_strings_update_dialog.findViewById(R.id.radioButton_lokal);

                if(spinner_favorite_strings_settings.getSelectedItem().toString().contains(" (Global)"))
                {
                    radioButton_global.setChecked(true);
                    radioButton_lokal.setChecked(false);
                }
                else
                {
                    radioButton_global.setChecked(false);
                    radioButton_lokal.setChecked(true);
                }

                String value_old= spinner_favorite_strings_settings.getSelectedItem().toString().replace(" (Global)","").trim();

                editTextText2_favorite_string_update_value.setText(value_old);

                imageButton_favorite_string_update_reset_value.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editTextText2_favorite_string_update_value.setText("");
                    }
                });

                AlertDialog.Builder favorite_strings_update_dialog = new AlertDialog.Builder(context);
                favorite_strings_update_dialog.setView(view_favorite_strings_update_dialog);
                favorite_strings_update_dialog.setTitle("String Bearbeiten");

                favorite_strings_update_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        config_fav_ops cfop = new config_fav_ops(context);
                        cfop.favorite_strings_update(value_old,editTextText2_favorite_string_update_value.getText().toString(),radioButton_global.isChecked());
                        refresh_spinner_favorite_strings_settings(context);
                        dialogInterface.cancel();
                    }
                });

                favorite_strings_update_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        dialogInterface.cancel();

                    }
                });



                favorite_strings_update_dialog.show();

            }
        });

        fav_config_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {



            }
        });






        fav_config_dialog.show();

    }

    public static void refresh_spinner_favorite_strings_settings(Context context)
    {
        //Spinner adapter
        config_fav_ops cfop = new config_fav_ops(context);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,  cfop.favorite_strings_list(true) );
        spinner_favorite_strings_settings.setAdapter(spinnerArrayAdapter);
    }



   public void log(String message)
   {
       Log.d("BASI",message);
   }
}
