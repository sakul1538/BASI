package com.example.tabnav_test;




import static android.app.PendingIntent.getActivity;
import static android.provider.Settings.System.getString;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.m_conf_maschine_adapter.ViewHolder;

import java.lang.reflect.Executable;

public class m_conf_maschine_adapter extends RecyclerView.Adapter<ViewHolder>
{
    public Uri imgUri=null;
    ImageView imw;
// GetContent creates an ActivityResultLauncher<String> to allow you to pass
// in the mime type you'd like to allow the user to select



    public m_conf_maschine_adapter(String[] localDataSet)
    {


        // .. Attach the interface
        this.localDataSet = localDataSet;
    }


    String text;
    private String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR = "0";
    Basic_funct bsf;

    @Override
    public m_conf_maschine_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        context = parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.m_conf_masch_list_layout, parent, false);




        // spinnerops =new log_fav(context);
        ///bsf=new Basic_funct();

        return new ViewHolder(view);

    }
    
    
    
    @Override
    public void onBindViewHolder(@NonNull m_conf_maschine_adapter.ViewHolder holder, int position)
    {
  
        
        m_database_ops mdo = new m_database_ops(context);

        int posi = position;
        Uri img_uri = null;

        String[] datasplit = localDataSet[posi].split(",");



        holder.m_name.setText("[" + datasplit[3] + "] " + datasplit[4]);
        holder.m_category.setText(datasplit[5]);
        holder.m_counter.setText(datasplit[6]);

        if(datasplit[8].contains("NULL")==true)
        {
            holder.m_view_pic.setImageResource(R.drawable.ic_baseline_agriculture_24);
        }
        else
        {
            String[] path = datasplit[8].split(":");
           // Log.d("BASI", Environment.getExternalStorageDirectory() + "/" + path[1]);


            try {
                Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + path[1]);

                Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 300, 200, true);
                holder.m_view_pic.setImageBitmap(bMapScaled);
            } catch (Exception e) {
                holder.m_view_pic.setImageResource(R.drawable.ic_baseline_agriculture_24);
            }

        }

        holder.delet_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Bestätige:");
                alertDialogBuilder.setMessage("Element aus der Datenbank entfernen?");
                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int response = mdo.delet_id(datasplit[0]);
                        Toast.makeText(context.getApplicationContext(), String.valueOf(response), Toast.LENGTH_SHORT).show();
                        localDataSet = mdo.get_maschinen(RROJ_NR);
                        notifyItemRemoved(posi);
                        notifyItemRangeChanged(posi, localDataSet.length);
                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
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

                EditText name = promptsView.findViewById(R.id.m_name);
                EditText nr = promptsView.findViewById(R.id.m_nr);
                Spinner category = promptsView.findViewById(R.id.m_category);
                EditText note = promptsView.findViewById(R.id.m_note);
                EditText counter = promptsView.findViewById(R.id.m_counter);
                ImageButton add_image = promptsView.findViewById(R.id.imageButton13);
                imw = promptsView.findViewById(R.id.imageView);



               // m_pic = promptsView.findViewById(R.id.imageView);

                m_database_ops mdo = new m_database_ops(context);

                ContentValues data = mdo.get_maschine(datasplit[0]);


                name.setText(data.get("NAME").toString());
                nr.setText((data.get("NR").toString()));


                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,R.array.m_categorys, android.R.layout.simple_spinner_item);

                int index =adapter.getPosition(data.get("CATEGORY").toString());
                category.setSelection(index);

                note.setText((data.get("NOTE").toString()));
                counter.setText((data.get("COUNTER").toString()));


                if(data.get("PIC_SRC").toString().contains("NULL")==true)
                {
                    imw.setImageResource(R.drawable.ic_baseline_agriculture_24);
                }
                else
                {
                    String[] path = data.get("PIC_SRC").toString().split(":");
                    // Log.d("BASI", Environment.getExternalStorageDirectory() + "/" + path[1]);

                    try {
                        Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + path[1]);

                        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 300, 200, true);
                        imw.setImageBitmap(bMapScaled);
                    } catch (Exception e) {
                        imw.setImageResource(R.drawable.ic_baseline_agriculture_24);
                    }

                }

                Log.d("BASI",data.get("PIC_SRC").toString());



                add_image.setOnClickListener(new View.OnClickListener()
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


                        if (imgUri == null) {
                            data.put("PIC_SRC", "NULL");
                        } else {
                            data.put("PIC_SRC", imgUri.getPath().toString());
                        }
                        data.put("ONOFF_FLAG", "TRUE");

                        Log.d("BASI", counter.getText().toString());
                        Log.d("BASI", note.getText().toString());
                        Log.d("BASIURI", data.get("PIC_SRC").toString());

                        try
                        {
                            int response= mdo.update_manschine(RROJ_NR,datasplit[0],data);
                            if(response == 1)
                            {
                                notifyItemChanged(posi);
                                Log.d("BASI_RESPONSE:",String.valueOf(response));
                            }

                        }catch (Exception e)
                        {
                            Log.d("BASI_RESPONSE:",String.valueOf(e.getMessage().toString()));

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


    }



    @Override
    public int getItemCount() {
        return localDataSet.length;

    }



    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView m_name;
        private final TextView m_category;
        private final TextView m_counter;

        private final ImageButton delet_button;
        private final ImageButton update_button;

        private final Switch switch_onoff;

        private final ImageView m_view_pic;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_name = (TextView) itemView.findViewById(R.id.textView37);
            m_category = (TextView) itemView.findViewById(R.id.textView27);
            m_counter = (TextView) itemView.findViewById(R.id.textView35);

            m_view_pic = (ImageView) itemView.findViewById(R.id.m_imageView);

            switch_onoff = (Switch) itemView.findViewById(R.id.switch1);

            delet_button = (ImageButton) itemView.findViewById(R.id.imageButton5);
            update_button = (ImageButton) itemView.findViewById(R.id.imageButton6);

        }
    }



}








