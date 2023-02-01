package com.example.tabnav_test;



import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Calendar;


// TODO: 11.01.23 Bei neue Maschine Registieren, prüfen ob in datenbank schon vorhanden (NAME, NUMMER)
// TODO: 17.01.23 selectlist bei name der Maschine implementieren
// TODO: 17.01.23 on/off switch
// TODO: 17.01.23 info Button  einbinden
// TODO: 17.01.23 log button einbinden
// FIXME: 24.01.23 Bei Rückkehr von Log RecView Aktuallisieren


public class m_conf_maschine extends AppCompatActivity {
    static final String RROJ_NR = "0";
    static final String PATH_PICTURES = Environment.getExternalStorageDirectory() + "/BASI/Pictures/";

    Uri imgUri = null;
    int errorflag =0;

    ImageButton m_conf_add_maschine_button;
    ImageButton m_conf_find_maschine_button;
    ImageButton m_conf_filter_maschine_button;
    ImageButton m_conf_date_maschine_button;
    LinearLayout m_conf_date_maschine_dialog;

    TextView m_conf_date_maschine_curr_date;
    ImageButton m_conf_date_maschine_curr_date_forward;
    ImageButton m_conf_date_maschine_curr_date_backward;

    ImageView m_pic;
    RecyclerView m_rcv;

    m_conf_maschine_adapter mcma;
    String currentPhotoPath = "null";

    @Override
    protected void onResume()
    {
        super.onResume();
        mcma.refresh_dataset();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Basic_funct bsf = new Basic_funct();
        Basic_func_img bsfimg = new Basic_func_img();

        final int imgscaling = 600;

        switch (requestCode) {
            case 1:
                //Neue Maschine, Bild aus Dateien

                imgUri = data.getData();
                currentPhotoPath = bsf.getabsPath(imgUri.getPath());///document/primary:DCIM/maschinenbilder/radlader.jpg

                Bitmap newbMap1 = bsfimg.Bitmap_adjust(currentPhotoPath, imgscaling);
                m_pic.setImageBitmap(newbMap1);

                break;

            case 2:
                //Maschine ändern, Bild aus DAteien

                imgUri = data.getData();

                currentPhotoPath = bsf.getabsPath(imgUri.getPath());///document/primary:DCIM/maschinenbilder/radlader.jpg
                Bitmap newbMap2 = bsfimg.Bitmap_adjust(currentPhotoPath, imgscaling);

                mcma.setimgURI(imgUri);
                mcma.imw.setImageBitmap(newbMap2);

                break;

            case 3:
                //Neue Maschine, Foto mit Kamera

                Bitmap newbMap3 = bsfimg.Bitmap_adjust(currentPhotoPath, imgscaling);

                m_pic.setImageBitmap(newbMap3);

                break;


            case 4:

                //Maschine ändernm Bild mit Kamera


                currentPhotoPath = mcma.getCurrentPhotoPath();

                Bitmap newbMap4 = bsfimg.Bitmap_adjust(currentPhotoPath, imgscaling);
                mcma.imw.setImageBitmap(newbMap4);

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mconf_maschine);

        Context context = getApplicationContext();
        m_database_ops mdo = new m_database_ops(context);
        Basic_funct bsf = new Basic_funct();

        m_conf_add_maschine_button = (ImageButton) findViewById(R.id.m_conf_add_maschine_button);
        m_conf_find_maschine_button = (ImageButton) findViewById(R.id.m_conf_find_maschine_button);

        m_conf_filter_maschine_button = (ImageButton) findViewById(R.id.m_conf_filter_maschine_button);

        m_conf_date_maschine_button = (ImageButton) findViewById(R.id.m_conf_date_maschine_button);
        m_conf_date_maschine_curr_date_forward = (ImageButton) findViewById(R.id.m_conf_date_maschine_curr_date_forward);
        m_conf_date_maschine_curr_date_backward = (ImageButton) findViewById(R.id.m_conf_date_maschine_curr_date_backward);


        m_conf_date_maschine_dialog = (LinearLayout) findViewById(R.id.m_conf_date_maschine_dialog);

        m_conf_date_maschine_curr_date = (TextView) findViewById(R.id.m_conf_date_maschine_curr_date);


        m_conf_date_maschine_curr_date.setText(bsf.date_refresh_rev2());// FIXME: 08.01.23

        m_rcv = (RecyclerView) findViewById(R.id.m_rcv);



        String[] data = mdo.get_maschinen(RROJ_NR);

        mcma = new m_conf_maschine_adapter(data);
        m_rcv.setAdapter(mcma);


        m_rcv.setLayoutManager(new LinearLayoutManager(m_conf_maschine.this));

        m_conf_add_maschine_button.setOnClickListener(new View.OnClickListener()
                                                      {
            @Override
                public void onClick(View view) {


                  imgUri = null;
                  errorflag=0;
                  m_database_ops mdo = new m_database_ops(getApplicationContext());

                  View promptsView = getLayoutInflater().inflate(R.layout.m_add_maschine_dialog, null);

                  TextView m_message_text_view = promptsView.findViewById(R.id.m_message_text_view);

                  EditText name = promptsView.findViewById(R.id.m_name);
                  EditText nr = promptsView.findViewById(R.id.m_nr);
                  EditText counter = promptsView.findViewById(R.id.m_counter);
                  EditText note = promptsView.findViewById(R.id.m_note);

                  Spinner category = promptsView.findViewById(R.id.m_category);

                  ImageButton pick_image = promptsView.findViewById(R.id.imageButton13);
                  ImageButton camera_image = promptsView.findViewById(R.id.imageButton14);

                  ImageView m_error_text_view_image =promptsView.findViewById(R.id.m_error_text_view_image);

                  LinearLayout m_name_nr_layoutcontainer = promptsView.findViewById(R.id.m_name_nr_layoutcontainer);
                 // LinearLayout m_status_message_layoutcontainer = promptsView.findViewById(R.id.m_status_message_layoutcontainer);

                  m_pic = promptsView.findViewById(R.id.imageView);


                name.setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {
                        String error_txt = "Maschine existiert bereits!";
                        if(b == false) //Beim Verlassen
                        {
                            if(TextUtils.isEmpty(nr.getText().toString())== false) // Wenn NR nicht leer
                            {
                                if(mdo.check_maschine(name.getText().toString(),nr.getText().toString()) != "null") //"null" = keine Maschine existiert
                                {

                                    //Wenn Maschine existiert
                                    m_message_text_view.setVisibility(View.VISIBLE);
                                    m_error_text_view_image.setVisibility(View.VISIBLE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(getResources().getColor(R.color.rot)); //Hintergrund rot
                                    m_message_text_view.setText(error_txt); //Zeige Error Text an
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_error_outline_24); //Setze icon
                                }
                                else
                                {
                                    //Wenn nicht existiert
                                    //Wenn Maschine existiert
                                    m_message_text_view.setVisibility(View.VISIBLE);
                                    m_error_text_view_image.setVisibility(View.VISIBLE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(getResources().getColor(R.color.grün)); //Hintergrund Grün
                                    m_message_text_view.setText("Validierung erfolgreich!");
                                    m_message_text_view.setTextColor(getResources().getColor(R.color.grün));
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_check_circle_green); //Setze icon
                                }
                            }
                        }
                    }
                });


                nr.setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View view, boolean b)
                    {
                        String error_txt = "Maschine existiert bereits!";
                        if(b == false) //Beim Verlassen
                        {
                            if(TextUtils.isEmpty(name.getText().toString())== false) // Wenn NR nicht leer
                            {
                                if(mdo.check_maschine(name.getText().toString(),nr.getText().toString()) != "null") //"null" = keine Maschine existiert
                                {

                                    //Wenn Maschine existiert
                                    m_message_text_view.setVisibility(View.VISIBLE);
                                    m_error_text_view_image.setVisibility(View.VISIBLE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(getResources().getColor(R.color.rot)); //Hintergrund rot
                                    m_message_text_view.setTextColor(getResources().getColor(R.color.rot));
                                    m_message_text_view.setText(error_txt); //Zeige Error Text an
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_error_outline_24); //Setze icon
                                }
                                else
                                {
                                    //Wenn nicht existiert

                                    m_message_text_view.setVisibility(View.VISIBLE);
                                    m_error_text_view_image.setVisibility(View.VISIBLE);
                                    m_name_nr_layoutcontainer.setBackgroundColor(getResources().getColor(R.color.grün)); //Hintergrund Grün
                                    m_message_text_view.setText("Validierung erfolgreich!");

                                    m_message_text_view.setTextColor(getResources().getColor(R.color.grün));
                                    m_error_text_view_image.setImageResource(R.drawable.ic_baseline_check_circle_green); //Setze icon
                                }
                            }
                        }
                    }
                });

                  pick_image.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {

                          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                          intent.addCategory(Intent.CATEGORY_OPENABLE);
                          intent.setType("image/*");

                          startActivityForResult(intent, 1);
                      }
                  });

                  camera_image.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          dispatchTakePictureIntent();
                      }
                  });

                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_conf_maschine.this);
                  alertDialogBuilder.setView(promptsView);
                  alertDialogBuilder.setTitle("Maschine hinzufügen");

                  alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i)
                      {
                          ContentValues data = new ContentValues();
                          data.put("PROJ_NR", RROJ_NR);

                          if(TextUtils.isEmpty(nr.getText().toString()) == true)
                          {
                              data.put("NR","");
                          }
                          else
                          {
                              data.put("NR", nr.getText().toString());
                          }


                          if(TextUtils.isEmpty(name.getText().toString()) == true)
                              {
                                  data.put("NAME","");
                              }
                              else
                              {
                                  data.put("NAME", name.getText().toString());
                              }


                              if(TextUtils.isEmpty(counter.getText().toString()) == true)
                              {

                                  data.put("COUNTER",0);
                              }
                              else
                              {
                                  data.put("COUNTER", counter.getText().toString());
                              }

                              data.put("CATEGORY", category.getSelectedItem().toString());;
                              data.put("NOTE", note.getText().toString());

                              String filename = "NULL";

                              if (imgUri == null) {
                                  data.put("PIC_SRC", filename);
                              } else {
                                  String tpaht = imgUri.getPath();
                                  if (tpaht.contains(":") == false) {
                                      Log.d("BASI", "Kamera:" + tpaht);
                                      String[] t = tpaht.split("/");
                                      filename = t[t.length - 1];
                                  } else {
                                      String path = bsf.getabsPath(tpaht);
                                      String[] t = path.split("/");
                                      filename = t[t.length - 1];

                                      bsf.copyBitmap_to(path, bsf.APP_DCIM_MASCHINEN, filename);

                                  }

                                  //Bitmap m_pic = Bitmap.createScaledBitmap()
                                  data.put("PIC_SRC", filename);
                              }

                              data.put("ONOFF_FLAG", "true");

                              if(data.get("NAME").toString() =="" || data.get("NR").toString() =="")
                              {

                                  Toast.makeText(context,"Machine wurde NICHT Registriert! \n Fehlende Angaben!",Toast.LENGTH_SHORT).show();
                              }
                              else
                              {
                                  String check_response =mdo.check_maschine(data.get("NAME").toString(),data.get("NR").toString());
                                  if (check_response == "null")
                                  {
                                      String response = mdo.add_manschine(data);
                                      if(response.equals("null") == true)
                                      {
                                          bsf.error_msg("Machine wurde NICHT Gespeichert! \n Fehler beim Speichern!",m_conf_maschine.this);
                                         // Toast.makeText(context,"Machine wurde NICHT Gespeichert!",Toast.LENGTH_SHORT).show();
                                      }
                                      else
                                      {
                                          String masch_id = response;
                                          String msg_text = "";
                                          Boolean init_done =mdo.add_log_entry_init(masch_id,data.get("NR").toString(),data.get("NAME").toString(),data.get("COUNTER").toString());
                                          if(init_done == true)
                                          {
                                              msg_text ="->Maschine Erfolgreich Geschpeichert!("+"ID: "+response+")\n ";
                                              msg_text +="->Init Eintrag Erfolgreich Geschpeichert!\n ";
                                              mcma.add_data(mdo.get_maschinen(RROJ_NR));
                                              bsf.succes_msg(msg_text,m_conf_maschine.this);
                                          }
                                          else
                                          {
                                              msg_text ="Maschine Erfolgreich Geschpeichert!("+"ID: "+response+")\n ";
                                              bsf.succes_msg(msg_text,m_conf_maschine.this);
                                              bsf.error_msg("Init Eintrag in der Log wurde nicht erstellt!",m_conf_maschine.this);
                                              mcma.add_data(mdo.get_maschinen(RROJ_NR));
                                          }

                                         // Toast.makeText(context,"Geschpeichert! ",Toast.LENGTH_SHORT).show();
                                      }

                                  }
                                  else
                                  {
                                     bsf.error_msg("Machine wurde NICHT Gespeichert! \n Maschine Existiert schon!",m_conf_maschine.this);
                                     //Toast.makeText(context,"Machine wurde NICHT Registriert! \n Maschine Existiert schon!",Toast.LENGTH_SHORT).show();
                                  }
                              }




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
          }
        );

        m_conf_find_maschine_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                View find_promptsView = getLayoutInflater().inflate(R.layout.m_search_dialog, null);

                AutoCompleteTextView find_value = find_promptsView.findViewById(R.id.search_field);
                ;
                ImageButton reset_search = find_promptsView.findViewById(R.id.m_search_reset_button);
                m_database_ops mdops = new m_database_ops(context);

                String[] list = mdops.get_list_for_autocomplete(RROJ_NR, "NAME");
                ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, list);
                find_value.setThreshold(1);
                find_value.setAdapter(favArrayAdapter);

                reset_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        find_value.setText("");
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_conf_maschine.this);
                alertDialogBuilder.setView(find_promptsView);
                alertDialogBuilder.setTitle("Maschine Suchen");


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {


                        dialogInterface.cancel();
                    }
                });


                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
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


        m_conf_filter_maschine_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                m_database_ops mdops = new m_database_ops(context);
                View find_promptsView = getLayoutInflater().inflate(R.layout.m_category_filter, null);


                RecyclerView mrcv = find_promptsView.findViewById(R.id.m_filter_rcview);
                m_filter_rcv_adapter mfra= new m_filter_rcv_adapter();
                mrcv.setAdapter(mfra);
                mrcv.setLayoutManager(new LinearLayoutManager(context));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_conf_maschine.this);
                alertDialogBuilder.setView(find_promptsView);
                alertDialogBuilder.setTitle("Maschinen Filtern");


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ArrayList<String> categorys = mfra.filter_getselectet_categorys();
                        Log.d("BASI", String.valueOf(categorys));
                        dialogInterface.cancel();

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

        m_conf_date_maschine_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View view)
            {
                m_database_ops mdops = new m_database_ops(context);

                if(m_conf_date_maschine_dialog.getVisibility() == View.VISIBLE)
                {
                    m_conf_date_maschine_dialog.setVisibility(View.GONE);
                }
                else
                {
                    m_conf_date_maschine_dialog.setVisibility(View.VISIBLE);

                    m_conf_date_maschine_curr_date.setText(bsf.date_refresh_rev2());
                }


            }
        });

        m_conf_date_maschine_curr_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog dpd = new DatePickerDialog(m_conf_maschine.this);

                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {

                        Log.d("BASI",String.valueOf(i2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1 , i2);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());

                        m_conf_date_maschine_curr_date.setText(dateString);

                    }
                });
                dpd.show();

            }});

        m_conf_date_maschine_curr_date_forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String new_date = bsf.time_day_shift( m_conf_date_maschine_curr_date.getText().toString(),"",1);

                m_conf_date_maschine_curr_date.setText(new_date);
            }
        });
        m_conf_date_maschine_curr_date_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String new_date = bsf.time_day_shift( m_conf_date_maschine_curr_date.getText().toString(),"",-1);

                m_conf_date_maschine_curr_date.setText(new_date);
            }
        });


    }


    private void dispatchTakePictureIntent()
    {

        Basic_funct bsf = new Basic_funct();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;
        try {

            File storageDir = new File(bsf.APP_DCIM_MASCHINEN);
            File image=null;

            if(storageDir.exists() == false)
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
                    imgUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.tabnav_test.fileprovider",image);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(takePictureIntent,3);

                }
        } catch (IOException ex)
        {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created

    }




}