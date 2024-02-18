package com.example.tabnav_test;




import static android.app.PendingIntent.getActivity;
import static android.provider.Settings.System.getString;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.m_conf_maschine_adapter.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.text.BreakIterator;
import java.util.Locale;

public class m_conf_maschine_adapter extends RecyclerView.Adapter<ViewHolder>
{
    public Uri imgUri=null;
    ImageView imw;
// GetContent creates an ActivityResultLauncher<String> to allow you to pass
// in the mime type you'd like to allow the user to select
    String text;
    private String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR = "0";
    Basic_funct bsf =new Basic_funct();

    m_database_ops mdo;
    String currentPhotoPath = null;


    public m_conf_maschine_adapter(String[] localDataSet)
    {

        // .. Attach the interface
        this.localDataSet = localDataSet;
        bsf =new Basic_funct();

        imgUri=null;
    }

    @Override
    public m_conf_maschine_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context = parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.m_conf_masch_list_layout, parent, false);


        // spinnerops =new log_fav(context);


        return new ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull m_conf_maschine_adapter.ViewHolder holder, int position)
    {


        mdo = new m_database_ops(context);

        int posi = position;
        Uri img_uri = null;
        String[] datasplit = localDataSet[posi].split(",");

        holder.m_name.setText("[" + datasplit[3] + "] " + datasplit[4]);
        holder.m_category.setText(datasplit[5]);
        holder.m_counter.setText(mdo.get_counter(datasplit[0]));

        if(datasplit[8].contains("NULL"))
        {
            holder.m_view_pic.setImageResource(R.drawable.ic_baseline_agriculture_24);
        }
        else
        {
            try
            {
            String path = Basic_funct.APP_DCIM_MASCHINEN +"/"+datasplit[8];
           // Log.d("BASI", Environment.getExternalStorageDirectory() +  "/" + path[1]);
            Basic_func_img bsfimg =new  Basic_func_img();
            Bitmap bMapScaled = bsfimg.Bitmap_adjust(path,500);

            holder.m_view_pic.setImageBitmap(bMapScaled);

            } catch (Exception e) {
                holder.m_view_pic.setImageResource(R.drawable.ic_baseline_agriculture_24);
            }
        }


        holder.m_view_pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater li = LayoutInflater.from(context);
                View  image_viewer = li.inflate(R.layout.show_picture, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                ImageView photo = image_viewer.findViewById(R.id.imageView4);

                String path = Basic_funct.APP_DCIM_MASCHINEN +"/"+datasplit[8];

                Basic_func_img bsfimg =new  Basic_func_img();

                Bitmap bMapScaled = bsfimg.Bitmap_adjust(path,0);
                photo.setImageBitmap(bMapScaled);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(image_viewer);

                alertDialogBuilder.setTitle("Viewer");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        holder.delet_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Bestätige");
                alertDialogBuilder.setMessage("Element aus der Datenbank entfernen?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) 
                    {
                        // FIXME: 15.01.23 Bei löschen einer Maschine auch die einträge in der Datenbank löschen!
                        int response = mdo.delet_masch_id(datasplit[0]);
                        if(response ==-1)
                        {
                            bsf.error_msg("Löschen Fehlgeschlagen!",context);
                        }
                        else
                        {
                            bsf.succes_msg("Maschine wurde aus der Datenbank entfernt!",context);
                         //   Toast.makeText(context.getApplicationContext(), String.valueOf(response), Toast.LENGTH_SHORT).show();
                            localDataSet = mdo.get_maschinen(RROJ_NR);
                            notifyItemRemoved(posi);
                            notifyItemRangeChanged(posi, localDataSet.length);
                        }

                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Löschen abgebrochen!", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.show();
            }
        });

        holder.update_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.m_add_maschine_dialog, null);

                TextView m_message_text_view = promptsView.findViewById(R.id.m_message_text_view);
                TextView  counter_label= promptsView.findViewById(R.id.textView39);

                EditText name = promptsView.findViewById(R.id.m_name);
                EditText nr = promptsView.findViewById(R.id.m_nr);
                EditText note = promptsView.findViewById(R.id.m_note);
                EditText counter = promptsView.findViewById(R.id.m_counter);

                Spinner category = promptsView.findViewById(R.id.m_category);

                ImageButton update_image = promptsView.findViewById(R.id.imageButton13);
                ImageButton camera_image = promptsView.findViewById(R.id.imageButton14);
                imw = promptsView.findViewById(R.id.imageView);
                ImageView m_error_text_view_image =promptsView.findViewById(R.id.m_error_text_view_image);

                LinearLayout m_name_nr_layoutcontainer = promptsView.findViewById(R.id.m_name_nr_layoutcontainer);

               // m_pic = promptsView.findViewById(R.id.imageView);

                m_database_ops mdo = new m_database_ops(context);

                ContentValues data = mdo.get_maschine(datasplit[0]);


                // FIXME: 17.01.23 datasplit[0] = ID als validierung nutzen.Bei gelegenheit Abändern.

                name.setText(data.get("NAME").toString());

                name.setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {
                            if(mdo.check_maschine(name.getText().toString(),nr.getText().toString()) != "null") //"null" = keine Maschine existiert
                            {
                                if(name.getText().toString().equals(data.get("NAME").toString()) && nr.getText().toString().equals(data.get("NR").toString()))
                                {
                                    m_message_text_view.setVisibility(View.GONE);
                                    m_error_text_view_image.setVisibility(View.GONE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.white)); //Hintergrund grün
                                    m_message_text_view.setText("Validierung erfolgreich!");
                                    m_message_text_view.setTextColor(context.getResources().getColor(R.color.grün));
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_check_circle_green); //Setze icon

                                }
                                else
                                {
                                    //Wenn Maschine existiert
                                    String error_txt = "Maschine existiert bereits!";
                                    m_message_text_view.setVisibility(View.VISIBLE);
                                    m_error_text_view_image.setVisibility(View.VISIBLE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.rot)); //Hintergrund rot
                                    m_message_text_view.setText(error_txt); //Zeige Error Text an
                                    m_message_text_view.setTextColor(context.getResources().getColor(R.color.rot));
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_error_outline_24); //Setze icon

                                }
                            }
                            else
                            {
                                //Wenn nicht existiert
                                m_message_text_view.setVisibility(View.VISIBLE);
                                m_error_text_view_image.setVisibility(View.VISIBLE);
                                m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.grün)); //Hintergrund grün
                                m_message_text_view.setText("Validierung erfolgreich!");
                                m_message_text_view.setTextColor(context.getResources().getColor(R.color.grün));
                                m_error_text_view_image.setImageResource(R.drawable.ic_baseline_check_circle_green); //Setze icon

                            }
                    }
                });

                nr.setText((data.get("NR").toString()));

                nr.setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {
                        if(mdo.check_maschine(name.getText().toString(),nr.getText().toString()) != "null") //"null" = keine Maschine existiert
                        {
                            if(name.getText().toString().equals(data.get("NAME").toString()) && nr.getText().toString().equals(data.get("NR").toString()))
                            {

                                //Validierung der akutellen Maschine
                                m_message_text_view.setVisibility(View.GONE);
                                m_error_text_view_image.setVisibility(View.GONE);
                                m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.white)); //Hintergrund weiss

                            }
                            else
                            {

                                //Wenn Maschine schon existiert
                                String error_txt = "Maschine existiert bereits!";
                                m_message_text_view.setVisibility(View.VISIBLE);
                                m_error_text_view_image.setVisibility(View.VISIBLE);
                                m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.rot)); //Hintergrund rot
                                m_message_text_view.setText(error_txt); //Zeige Error Text an
                                m_message_text_view.setTextColor(context.getResources().getColor(R.color.rot));
                                m_error_text_view_image.setImageResource(R.drawable.ic_baseline_error_outline_24); //Setze icon

                            }
                        }
                        else
                        {
                            //Wenn nicht existiert, kann abgeändert werden!
                            m_message_text_view.setVisibility(View.VISIBLE);
                            m_error_text_view_image.setVisibility(View.VISIBLE);
                            m_name_nr_layoutcontainer.setBackgroundColor(context.getResources().getColor(R.color.grün)); //Hintergrund grün
                            m_message_text_view.setText("Validierung erfolgreich!");
                            m_message_text_view.setTextColor(context.getResources().getColor(R.color.grün));
                            m_error_text_view_image.setImageResource(R.drawable.ic_baseline_check_circle_green); //Setze icon
                        }
                    }
                });

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.m_categorys, android.R.layout.simple_spinner_item);

                int index =adapter.getPosition(data.get("CATEGORY").toString());
                category.setSelection(index);

                note.setText((data.get("NOTE").toString()));
                counter.setText((data.get("COUNTER").toString()));
                counter.setVisibility(View.GONE);
                counter_label.setVisibility(View.GONE);

                if(data.get("PIC_SRC").toString().contains("NULL"))
                {
                    imw.setImageResource(R.drawable.ic_baseline_agriculture_24);
                }
                else
                {
                    String path = Basic_funct.APP_DCIM_MASCHINEN + "/" + data.get("PIC_SRC").toString();

                    try {
                        Basic_func_img  bsfimg= new Basic_func_img();
                        Bitmap bMapScaled = bsfimg.Bitmap_adjust(path,600);
                        imw.setImageBitmap(bMapScaled);
                    } catch (Exception e) {
                        imw.setImageResource(R.drawable.ic_baseline_agriculture_24);
                    }
                }


                update_image.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");


                        ((Activity) context).startActivityForResult(intent, 2);
                    }
                });

                camera_image.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        dispatchTakePictureIntent();
                       // Bitmap bMap = BitmapFactory.decodeFile(currentPhotoPath);

                       // Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 300, 200, true);
                      //  imw.setImageBitmap(bMapScaled);
                       // Log.d("BASI_currentPhotoPath1:",imgUri.getPath());

                    }
                });

               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Maschine ändern");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        ContentValues data = new ContentValues();
                        data.put("PROJ_NR", RROJ_NR);
                        data.put("NR", nr.getText().toString());
                        data.put("NAME", name.getText().toString());
                        data.put("CATEGORY", category.getSelectedItem().toString());
                        data.put("COUNTER", counter.getText().toString());
                        data.put("NOTE", note.getText().toString());

                        String filename ="NULL";

                        if (imgUri == null)
                        {
                            data.put("PIC_SRC", filename);
                        } else
                        {
                            String tpaht = imgUri.getPath();
                            if(!tpaht.contains(":"))
                            {
                                Log.d("BASI", "Kamera:"+tpaht);
                                String[] t = tpaht.split("/");
                                filename= t[t.length-1];
                            }
                            else
                            {
                                String path = bsf.getabsPath(tpaht);
                                String[] t = path.split("/");
                                filename= t[t.length-1];

                                bsf.copyBitmap_to(path, Basic_funct.APP_DCIM_MASCHINEN,filename);

                            }

                            data.put("PIC_SRC", filename);
                        }
                        data.put("ONOFF_FLAG", "TRUE");
                        try {
                            String masch_id = mdo.check_maschine(data.get("NAME").toString(), data.get("NR").toString());
                            bsf.log_div();
                            bsf.log(masch_id);
                            bsf.log(datasplit[0]);
                            bsf.log_div();


                            if (TextUtils.equals(masch_id, datasplit[0]) || masch_id.equals("null"))
                            {
                                int response= mdo.update_manschine(RROJ_NR,datasplit[0],data);
                                if(response == 1)
                                {
                                    add_data(mdo.get_maschinen(RROJ_NR));
                                    notifyDataSetChanged();
                                    Log.d("BASI_RESPONSE:",String.valueOf(response));
                                    bsf.succes_msg("Machine wurde Geändert!",context);
                                }
                            }
                            else
                            {
                                bsf.error_msg("Machine wurde NICHT Geändert! \n Maschine Existiert schon!",context);
                               // Toast.makeText(context,"Machine wurde NICHT Geändert! \n Maschine Existiert schon!" ,Toast.LENGTH_SHORT).show();
                            }


                        }catch (Exception e)
                        {
                            Log.d("BASI_RESPONSE:",String.valueOf(e.getMessage()));

                        }


                        //(ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");

                        dialogInterface.cancel();


                        //(ID TEXT,PROJ_NR TEXT,DATE TEXT,TIME TEXT,NR TEXT,NAME TEXT,CATEGORY TEXT,COUNTER TEXT,NOTE TEXT,PIC_SRC TEXT,ONOFF_FLAG TEXT)");



                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // create alert dialog
               AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();



            }

        });

        holder.info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                Basic_func_img bsfimg = new Basic_func_img();

                LayoutInflater li = LayoutInflater.from(context);
                View  info_promptsView = li.inflate(R.layout.m_info_dialog, null);


                TextView m_info_name_value = info_promptsView.findViewById(R.id.m_info_name_value);
                TextView m_info_nr_value = info_promptsView.findViewById(R.id.m_info_nr_value);
                TextView m_info_created_date_value = info_promptsView.findViewById(R.id.m_info_created_date);
                TextView m_info_created_time_value = info_promptsView.findViewById(R.id.m_info_created_time);
                TextView m_info_calegory_value = info_promptsView.findViewById(R.id.m_info_category_value);
                TextView m_info_note_value = info_promptsView.findViewById(R.id.m_info_note_value);
                TextView m_info_counter_value = info_promptsView.findViewById(R.id.m_info_counter_value);
                TextView m_info_entry_counter_value = info_promptsView.findViewById(R.id.m_info_entry_counter_value);
                TextView m_info_last_entry_value = info_promptsView.findViewById(R.id.m_info_entry_last_value);

                ImageView m_info_imageview = info_promptsView.findViewById(R.id.m_info_imageview);

                ContentValues cv = mdo.get_maschine(datasplit[0]);

                m_info_name_value.setText(cv.get("NAME").toString());
                m_info_nr_value.setText(cv.get("NR").toString());
                m_info_created_date_value.setText(bsf.convert_date(cv.get("DATE").toString(),"format_database_to_readable"));
                m_info_created_time_value.setText(cv.get("TIME").toString());
                m_info_calegory_value.setText(cv.get("CATEGORY").toString());
                m_info_note_value.setText(cv.get("NOTE").toString());
                m_info_counter_value.setText(mdo.get_maschine_counter(cv.get("ID").toString()));


                m_info_entry_counter_value.setText(mdo.log_get(cv.get("ID").toString(),"COUNT_ENTRYS"));

                String last_entry = bsf.convert_date(mdo.log_get(cv.get("ID").toString(),"LAST_ENTRY"),"format_database_to_readable");

                m_info_last_entry_value.setText(last_entry);

                Bitmap m_info_pic = bsfimg.Bitmap_adjust(Basic_funct.APP_DCIM_MASCHINEN +"/"+cv.get("PIC_SRC").toString(),0);

                m_info_imageview.setImageBitmap(m_info_pic);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(info_promptsView);
                alertDialogBuilder.setTitle("Info");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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


        holder.switch_onoff.setChecked(Boolean.valueOf(datasplit[9]));

        holder.switch_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                mdo.maschine_on_off(datasplit[0],"SET",b);
            }
        });

        holder.show_log_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                Intent m_log_main= new Intent(context, m_log_main.class);
                m_log_main.putExtra("MASCH_ID",datasplit[0]);
                context.startActivity(m_log_main);

            }
        });

    }



    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }

    public void add_data(String[] localDataSet)
    {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }

    public void refresh_dataset()
    {
        notifyDataSetChanged();
    }

    public void setimgURI(Uri imgURI)
    {
        this.imgUri = imgURI;
    }
    public String getCurrentPhotoPath()
    {
       return  this.currentPhotoPath;
    }


    private void dispatchTakePictureIntent()
    {

        Basic_funct bsf = new Basic_funct();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;

        try {

            File storageDir = new File(Basic_funct.APP_DCIM_MASCHINEN);
            File image=null;

            if(!storageDir.exists())
            {
                storageDir.mkdirs();
            }

            image = File.createTempFile(
                    "maschine_",  /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            //  String  currentPhotoPath = image.getAbsolutePath();

            if (image != null)
            {
                currentPhotoPath= image.getAbsolutePath();
                Log.d("BASI_currentPhotoPath:",currentPhotoPath);
                imgUri = FileProvider.getUriForFile(context,"com.example.tabnav_test.fileprovider",image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

                ((Activity) context).startActivityForResult(takePictureIntent, 4);

            }
        } catch (IOException ex)
        {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created

    }







    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView m_name;
        private final TextView m_category;
        private final TextView m_counter;

        private final ImageButton delet_button;
        private final ImageButton update_button;
        private final ImageButton info_button;
        private final ImageButton show_log_button;

        private final Switch switch_onoff;

        private final ImageView m_view_pic;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_name = itemView.findViewById(R.id.textView37);
            m_category = itemView.findViewById(R.id.textView27);
            m_counter = itemView.findViewById(R.id.textView35);

            m_view_pic = itemView.findViewById(R.id.m_imageView);

            switch_onoff = itemView.findViewById(R.id.switch1);

            delet_button = itemView.findViewById(R.id.imageButton5);
            update_button = itemView.findViewById(R.id.imageButton6);
            info_button = itemView.findViewById(R.id.imageButton4);
            show_log_button = itemView.findViewById(R.id.imageButton19);
        }
    }




}








