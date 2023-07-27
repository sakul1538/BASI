package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import com.example.tabnav_test.Basic_func_img;
import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.material_artikel_adapter;
import com.example.tabnav_test.material_log_activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Material#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Material extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int foto_preview_w = 800; //breite
    private static final int foto_preview_h = 800; // höhe
    private static final String ls_media_directory_name = "/Lieferscheine"; // höhe
    private static final String ls_media_directory_name_temp = "/Lieferscheine/temp"; // höhe

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String currentPhotoPath ="null";
    Uri photoURI=null;
    String photo_path ="null";
    String in_directory ="/";

    //ImageButton
    ImageButton settings_projekt_button;
    ImageButton settings_zulieferer;
    ImageButton settings_artikel;
    ImageButton add_artikel;
    ImageButton reset_artikel;
    ImageButton ls_note_reset;
    ImageButton ls_take_photo;
    ImageButton ls_delet_foto;
    ImageButton reset_form;
    ImageButton save_ls_entry;
    ImageButton start_material_log_activity;

    ImageButton open_pdf;
    ImageButton open_filesystem;

    ImageButton reset_LS;
    ImageButton refresh_date;

    ImageButton ls_img_collection_forward;
    ImageButton ls_img_collection_backward;

    // EditText
    EditText dirname;
    EditText menge;

    EditText note_field;

    EditText lsnr_field;

    //AutoCompleteText
    AutoCompleteTextView edit_artikel_name;


    //Spinner
    Spinner projlist;
    Spinner zulieferer_liste;
    Spinner Zulieferer_liste_main;


    Spinner spinner_einheiten;

    //Globale Instanzen
    Basic_funct bsf = new Basic_funct();
    material_database_ops mdo;

    //TextView
    TextView projekt_label;
    TextView date_label;
    TextView imagecounter;

    ImageView  ls_photo_view;

   LinearLayout date_background;

   String []imageset;
   Integer imageset_array_pointer=0;



    public Material()
    {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Material.
     */
    // TODO: Rename and change types and number of parameters
    public static Material newInstance(String param1, String param2) {
        Material fragment = new Material();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        Uri uri = null;
        switch (requestCode)
        {
            case 0:
                dirname.setText(resultData.getData().getLastPathSegment());
                break;

            case 1://Foto mit Kamera

                if (resultData != null)
                {
                    try {

                        create_imageset(); //liest alle Dateien im Verzeichniss "in_directory" ein und Speichert sie im Array "imageset"

                        if(imageset.length>0) // Wenn das imageset einträge enthält...
                        {
                            media_visibilitiy(View.VISIBLE);
                            media_shift("r");

                            ls_photo_view.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)

                                {
                             /*  Basic_func_img bsfi = new Basic_func_img();
                                String filename_contains = foto_path.substring(foto_path.lastIndexOf("/")+1,foto_path.lastIndexOf("ID")-1);
                                String in_directory = foto_path.substring(0,foto_path.lastIndexOf("/")+1);
                                bsfi.ls_image_viewer(in_directory,filename_contains,getContext());*/
                                }
                            });
                        }

                    }catch (Exception e)
                    {
                        exmsg("18062023110",e);
                    }
                }

                //Toast.makeText(getContext(),path, Toast.LENGTH_SHORT).show();
                break;

            case 3: //PDF öffnen

                    if (resultData != null)
                    {

                        uri = resultData.getData(); //Url von der Datei, die Kopiert werden soll
                        String source_uri = uri.getPath();
                        source_uri = source_uri.replace("/document/primary:",Environment.getExternalStorageDirectory().getAbsolutePath()+"/");

                        String destinationPath ="";
                        try {

                            String lieferant = Zulieferer_liste_main.getSelectedItem().toString();
                            String ls_nr = String.valueOf(lsnr_field.getText());
                            String date  = (String) date_label.getText();
                            date = date.replace(".","");

                            long time= System.currentTimeMillis(); //"Zufallszahl" generieren, damit dateien Verschiedene Namen haben.

                            String filename= lieferant+"_NR_"+ls_nr+"@"+date+"_ID_"+String.valueOf(time)+".pdf"; //Name der Kopierten Datei
                            destinationPath = in_directory+"/"+filename; //Verzeichniss und Dateiname verbinden
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        File source = new File(source_uri);
                        File destination = new File(destinationPath);

                      try {
                          bsf.copyFileUsingStream(source,destination); //Kopieren von-zu

                          create_imageset();
                          media_visibilitiy(View.VISIBLE);
                          media_shift("r");

                          Bitmap default_pdf_logo = BitmapFactory.decodeResource(getResources(), R.drawable.pdflogo);
                          Bitmap default_pdf_logo_scaled =  Bitmap.createScaledBitmap(default_pdf_logo,foto_preview_w,foto_preview_h,true);
                          ls_photo_view.setImageBitmap(default_pdf_logo_scaled);

                        } catch (IOException e)
                      {
                            exmsg("250720231849",e);
                        }

                    }
                break;

            case 4: //bild von dateien öffnen

                if (resultData != null)
                {
                    uri = resultData.getData();
                    String source_path = uri.getPath();///document/primary:DCIM/Baustellen /testprojekt/Lieferscheine/Volken_NR_1223@25072023_ID_6547592956172734206.jpeg
                    source_path = source_path.replace("/document/primary:",Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
                    String file_extension = bsf.detect_extension(source_path); //Gibt den Dateityp (.jpeg .png ect) zurück in file_extension
                    String destinationPath =null;
                    try {
                        String lieferant = Zulieferer_liste_main.getSelectedItem().toString();
                        String ls_nr = String.valueOf(lsnr_field.getText());
                        String date  = (String) date_label.getText();
                        date = date.replace(".","");

                        long time= System.currentTimeMillis(); //"Zufallszahl" generieren, damit dateien Verschiedene Namen haben.

                        String filename= lieferant+"_NR_"+ls_nr+"@"+date+"_ID_"+String.valueOf(time)+file_extension; //Name der Kopierten Datei
                        destinationPath= in_directory+"/"+filename; //Verzeichniss und Dateiname verbinden
                    } catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                    File source = new File(source_path);
                    File destination = new File(destinationPath);

                    try {
                        bsf.copyFileUsingStream(source,destination); //Kopieren von-zu
                        media_visibilitiy(View.VISIBLE);
                        create_imageset();
                        media_shift("r");

                        Bitmap ls_picture= BitmapFactory.decodeFile(destinationPath);
                        Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(
                                ls_picture,
                                foto_preview_w,
                                foto_preview_h,
                                true);

                        ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView

                    } catch (IOException e)
                    {
                        exmsg("250720231849",e);
                    }
                }
                break;
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        try {
            set_media_directory(ls_media_directory_name_temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clean_temp_dir(); 
      
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try {
            set_media_directory(ls_media_directory_name_temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clean_temp_dir(); //temp Verzeichnis löschen 23423442
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_material, container, false);

        //Instanzen

        mdo = new material_database_ops(getContext());

        //TextView
        projekt_label = view.findViewById(R.id.textView58);
        date_label = view.findViewById(R.id.textView59_date);
        imagecounter = view.findViewById(R.id.textView73);

        //ImageView
        ls_photo_view = view.findViewById(R.id.imageView2);

        //AutoCompleteText
        edit_artikel_name = view.findViewById(R.id.autoCompleteTextView_artikel);


        //ImageButtons
        settings_zulieferer = view.findViewById(R.id.imageButton37);
        settings_projekt_button = view.findViewById(R.id.imageButton34);
        settings_artikel = view.findViewById(R.id.imageButton39);
        Zulieferer_liste_main = view.findViewById(R.id.spinner3);
        add_artikel = view.findViewById(R.id.imageButton53);
        reset_artikel = view.findViewById(R.id.reset_artikel);
        reset_LS = view.findViewById(R.id.reset_LS);
        ls_note_reset = view.findViewById(R.id.ls_note_reset);
        menge = view.findViewById(R.id.editTextNumberDecimal4_menge);
        note_field = view.findViewById(R.id.ls_note_field);
        ls_take_photo = view.findViewById(R.id.ls_shot_foto);
        refresh_date = view.findViewById(R.id.imageButton35_refresh_date);
        reset_form = view.findViewById(R.id.imageButton44_reset_form);
        save_ls_entry = view.findViewById(R.id.imageButton41_save_entry);
        ls_delet_foto = view.findViewById(R.id.ls_delet_foto);
        start_material_log_activity = view.findViewById(R.id.material_log_show);
        ls_img_collection_forward = view.findViewById(R.id.imageButton57);
        ls_img_collection_backward = view.findViewById(R.id.imageButton64);
        open_pdf = view.findViewById(R.id.imageButton67);
        open_filesystem = view.findViewById(R.id.imageButton43);



        //EditText
        lsnr_field =view.findViewById(R.id.edittext_LS_ID);

        //Spinner
        spinner_einheiten = view.findViewById(R.id.spinner6_einheiten);

        //Layouts
        date_background = view.findViewById(R.id.date_background);
        date_background = view.findViewById(R.id.date_background);


        try {
            refresh_artikel_autocomplete();
            refresh_projekt_label();
            refresh_spinner_zulieferer();
            set_media_directory(ls_media_directory_name_temp);
            clean_temp_dir();
            create_imageset();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Liferschein Nr.

        ls_img_collection_forward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                media_shift("f");
            }
        });


        ls_img_collection_backward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                media_shift("b");
            }
        });

        open_pdf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

               Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, 3);
            }
        });
        open_filesystem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });

                start_material_log_activity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try {
                    Intent log_activity = new Intent(getContext(), material_log_activity.class);
                    startActivity(log_activity);
                }catch (Exception e)
                {
                    exmsg("250620231224",e);
                }

            }
        });

        ls_delet_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(imageset.length >0)
                {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    // set prompts.xml to alertdialog builder

                    alertDialogBuilder.setTitle("Artikelverwaltung");
                    alertDialogBuilder.setMessage("Bild löschen?\n"+in_directory+"/"+imageset[imageset_array_pointer]);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            File f = new File(in_directory+"/"+imageset[imageset_array_pointer]);
                            f.delete();
                            create_imageset();
                            media_shift("r");
                            dialogInterface.cancel();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("Abbrecen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }

            }
        });

        save_ls_entry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                material_database_ops mdo = new material_database_ops(getContext());

                String proj= String.valueOf(projekt_label.getText());
                int spos= proj.lastIndexOf("[");
                int epos= proj.lastIndexOf("]");

                ContentValues data = new ContentValues();

                try {
                    data.put("ID",bsf.gen_UUID());
                    data.put("PROJEKT_ID",proj.substring(spos+1,epos));
                    data.put("DATUM", String.valueOf(date_label.getText()));
                    data.put("LSNR", String.valueOf(lsnr_field.getText()));
                    //String id_zulieferer =  mdo.get_id_zulieferer(Zulieferer_liste_main.getSelectedItem().toString());
                    String id_zulieferer =  mdo.get_zulieferer_param(
                            new String[]{Zulieferer_liste_main.getSelectedItem().toString()},
                            "NAME =?",
                            new String[]{"ID"});

                    data.put("LIEFERANT_ID", String.valueOf(id_zulieferer));

                    data.put("MENGE", String.valueOf(menge.getText()).trim());

                    data.put("EINHEIT_ID",spinner_einheiten.getSelectedItem().toString());

                    String notiz= note_field.getText().toString();
                    if(notiz == "" || notiz.isEmpty())
                    {
                        data.put("NOTIZ","null");
                    }
                    else
                    {
                        data.put("NOTIZ",notiz);
                    }

                    data.put("SRC","null"); //Fixme anpassen !!!

                    String artikel_value =String.valueOf(edit_artikel_name.getText()).trim();

                    String id_artikel =  mdo.get_artikel_param(new String[]{artikel_value},"NAME    =?",new String[]{"ID"});

                    if(id_artikel == "null")
                    {
                        mdo.add_artikel_to_list(artikel_value, String.valueOf(data.get("EINHEIT_ID"))); //Erstellt Speichert den Artikel in Datenbank, da er nicht existert
                        data.put("MATERIAL_ID", mdo.get_artikel_param(new String[]{artikel_value},"NAME=?",new String[]{"ID"}));
                    }
                    else
                    {
                        data.put("MATERIAL_ID",id_artikel);
                    }
                }catch (Exception e)
                {
                     exmsg("200720232123",e);
                }

                try
                {
                    long response=   mdo.add_material_log_entry(data);
                    if(response >0)
                    {
                        String copy_temp_msg  = "";
                        if( copy_media_files_from_temp()== true)
                        {
                            copy_temp_msg="+ Medien wurden ins Verzeichniss übernommen!";


                        }else
                        {
                            copy_temp_msg ="+ Medien wurden NICHT ins Verzeichnis übernommen!";

                        }

                        Toast.makeText(getContext(), "+Es  wurde  eine Eintrag erstellt!\n"+copy_temp_msg, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "+ Es konnte KEIN Eintrag erstellt werden!" , Toast.LENGTH_SHORT).show();

                    }

                    clean_temp_dir();
                    reset_complete();
                }
                catch (Exception e)
                {
                    exmsg("200620232152",e);
                    Toast.makeText(getContext(), "Kein Eintrag erstellt, interner Feher \n "+e.getMessage().toString() , Toast.LENGTH_SHORT).show();
                    clean_temp_dir();
                    reset_complete();
                }
            }
        });

        reset_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                reset_complete();

            }
        });

        refresh_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_date();
            }
        });

        date_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1 , i2);


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());


                        date_label.setText(dateString);
                        date_background.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.orange));

                    }
                });
                datePickerDialog.show();
            }
        });


        ls_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                material_database_ops mdo = new material_database_ops(getContext());


                String zulieferer = Zulieferer_liste_main.getSelectedItem().toString();



                String date = String.valueOf(date_label.getText());
                date = date.replace(".","");


                dispatchTakePictureIntent(in_directory,zulieferer.trim()+"_NR_"+lsnr_field.getText().toString(),false,"", date);
                //dispatchTakePictureIntent(p[2]+"/Lieferscheine/"+zulieferer.trim(),p[0]+"#"+zulieferer.trim(),false,"", date);
            }
        });
        ls_note_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_notiz();
            }
        });

        reset_LS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_ls_nr();
            }
        });
        //Artikel

        reset_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_artikel();
                reset_menge();
            }
        });

        add_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                    try {
                        material_database_ops mdo = new material_database_ops(getContext());
                        long response = mdo.add_artikel_to_list( edit_artikel_name.getText().toString(),spinner_einheiten.getSelectedItem().toString());
                        if(response>0)
                        {
                            bsf.succes_msg(edit_artikel_name.getText().toString()+" wurde geschpeichert!", view.getContext());
                        }
                        else
                        {
                            bsf.error_msg(edit_artikel_name.getText().toString()+" konnte nicht hinzügefügt werden!", view.getContext());
                        }

                    }catch (Exception e)
                    {
                        exmsg("110620231140",e);
                    }

                try {
                    refresh_artikel_autocomplete();
                }catch (Exception e)
                {
                    exmsg("110620231105A",e);
                }
            }
        });

        edit_artikel_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
               String selected =  edit_artikel_name.getText().toString();
                int spos= selected.lastIndexOf("[");
                int epos= selected.lastIndexOf("]");

                String artikel = selected.substring(0,spos);
                String einheit = selected.substring(spos+1,epos);
                edit_artikel_name.setText(artikel);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.einheiten_array, android.R.layout.simple_spinner_item);
                int index =adapter.getPosition(einheit);
                spinner_einheiten.setSelection(index);

            }
        });

        settings_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View view_artikel = li.inflate(R.layout.material_type_conf, container, false);

                RecyclerView matieral_artikel = view_artikel.findViewById(R.id.material_artikel_liste);

                material_database_ops mdo = new material_database_ops(getContext());

                String[] material_artikel_adapter = mdo.artikel_list_all();
                com.example.tabnav_test.material_artikel_adapter lcar = new material_artikel_adapter(material_artikel_adapter);
                matieral_artikel.setAdapter(lcar);
                matieral_artikel.setLayoutManager( new LinearLayoutManager(getContext()));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(view_artikel);
                alertDialogBuilder.setTitle("Artikelverwaltung");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        refresh_artikel_autocomplete();
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //Zulieferer
        settings_zulieferer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.lieferanten_config_dialog, container, false);

                ImageButton add_zulieferer = promptsView.findViewById(R.id.imageButton49);
                ImageButton update_zulieferer = promptsView.findViewById(R.id.imageButton50);
                ImageButton del_zuliefere = promptsView.findViewById(R.id.imageButton51);
                zulieferer_liste = promptsView.findViewById(R.id.spinner5);

                refresh_spinner_zulieferer_settings();

                add_zulieferer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View input_view = li.inflate(R.layout.add_lieferanten_dialog, container, false);

                        //Fixme Leeres feld nicht in Datenbank eintragen
                        EditText name_zulieferer = input_view.findViewById(R.id.name_zulieferer);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(input_view);
                        alertDialogBuilder.setTitle("Zulieferer hinzufügen");

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                material_database_ops mdo = new material_database_ops(getContext());
                                mdo.add_zulieferer(name_zulieferer.getText().toString());
                                refresh_spinner_zulieferer_settings();


                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.setNeutralButton("Abbrechen",new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.show();
                    }
                });

                update_zulieferer.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View input_view = li.inflate(R.layout.add_lieferanten_dialog, container, false);

                        EditText name_zulieferer = input_view.findViewById(R.id.name_zulieferer);
                        String zulieferer_name = zulieferer_liste.getSelectedItem().toString();
                        name_zulieferer.setText(zulieferer_name);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(input_view);
                        alertDialogBuilder.setTitle("Zulieferer hinzufügen");

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                //update_zulieferer(alte Bezeichnung ,neuer Name)
                                material_database_ops mdo = new material_database_ops(getContext());
                                mdo.update_zulieferer(zulieferer_name,name_zulieferer.getText().toString());
                                refresh_spinner_zulieferer_settings();

                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.show();

                    }
                });

                del_zuliefere.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)

                    {
                            // f//inal Boolean confirm_value = false;

                        String zulieferer_name = zulieferer_liste.getSelectedItem().toString();

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                            alertDialogBuilder.setTitle("Bestätigen");
                            alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                            alertDialogBuilder.setMessage("Zulieferer "+zulieferer_name+ "  wiklich löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    material_database_ops mdo = new material_database_ops(getContext());
                                    mdo.zulieferer_delet(zulieferer_name);
                                    refresh_spinner_zulieferer_settings();
                                    //TODO Ordner im Verzeichnis auch löschen!

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

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Zulieferer verwalten");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refresh_spinner_zulieferer();
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.show();

            }
        });


        settings_projekt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.material_choose_proj_dialog, container, false);

                projlist = promptsView.findViewById(R.id.spinner2);
                ImageButton add = promptsView.findViewById(R.id.imageButton48);
                ImageButton delet_item = promptsView.findViewById(R.id.imageButton47);
                ImageButton update_item = promptsView.findViewById(R.id.imageButton46);

                update_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upate_projekt(container, String.valueOf(projlist.getSelectedItem()));
                    }
                });

                delet_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String selected_item = String.valueOf(projlist.getSelectedItem());
                        String[] item_part = divider(selected_item);
                        Log.d("BASI", item_part[0]);
                        Log.d("BASI", item_part[1]);

                        String item_name = item_part[0];
                        String item_id = item_part[1];
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle("Löschen Bestätigen:");
                        alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);

                        alertDialogBuilder.setMessage("Projekt:\n" + item_name + " \n\nwirklich Löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                material_database_ops mdo = new material_database_ops(getContext());
                                int r = mdo.projekt_delet(item_id);

                                Toast.makeText(getContext(), String.valueOf(r), Toast.LENGTH_SHORT).show();
                                refresh_spinner();
                                refresh_artikel_autocomplete();


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
                });


                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add_projekt(container);
                        refresh_spinner();
                    }
                });

                //Spinner

                refresh_spinner();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Projektbowser");
                alertDialogBuilder.setPositiveButton("Projekt auswählen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected_item = String.valueOf(projlist.getSelectedItem());
                        String[] item_part = divider(selected_item);
                        Log.d("BASI", item_part[0]);
                        Log.d("BASI", item_part[1]);
                        material_database_ops mdo = new material_database_ops(getContext());
                        mdo.select_projekt(item_part[1]);
                        refresh_projekt_label();
                        dialogInterface.cancel();
                        try {
                            set_media_directory(ls_media_directory_name_temp);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialogBuilder.show();

            }
        });
        return view;
    }

    private void reset_complete()
    {
        reset_date();
        reset_ls_nr();
        reset_artikel();
        reset_menge();
        reset_notiz();
        reset_camera();
        media_visibilitiy(View.GONE);
        clean_temp_dir();
        create_imageset();
    }

    void refresh_artikel_autocomplete()
    {

        material_database_ops mdo = new material_database_ops(getContext());
        String[] artikel = mdo.artikel_list_all();
        ArrayAdapter<String> artikelAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, artikel);
        edit_artikel_name.setAdapter(artikelAdapter);
    }


    public void add_projekt(ViewGroup container) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.material_neues_projekt_anlegen_dialog, container, false);

        ImageButton dir = promptsView.findViewById(R.id.imageButton45);

        EditText proj_name = promptsView.findViewById(R.id.proj_name);
        EditText proj_nr = promptsView.findViewById(R.id.proj_nr);

        dirname = promptsView.findViewById(R.id.proj_path);

        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 0);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Neues Projekt anlegen");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = proj_name.getText().toString();
                String id = proj_nr.getText().toString();
                if (id.isEmpty() == true) {
                    id = bsf.gen_ID();
                }
                String dir_value = dirname.getText().toString();
                //Toast.makeText(getContext(), name+":"+dir_value, Toast.LENGTH_SHORT).show();
                material_database_ops mdo = new material_database_ops(getContext());

                ContentValues cv = new ContentValues();
                cv.put("ID", id);
                cv.put("NAME", name);
                cv.put("SRC", dir_value);
                cv.put("SELECT_FLAG", "0");
                mdo.projekt_add(cv);
                refresh_spinner();
                dialogInterface.cancel();

            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();

    }


    public void upate_projekt(ViewGroup container, String selected_item) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.material_neues_projekt_anlegen_dialog, container, false);

        material_database_ops mdo = new material_database_ops(getContext());

        String[] parts = divider(selected_item);

        String cv = mdo.get_projekt_data(parts[1]); //ID
        String[] data = cv.split(",");
        Log.d("BASI", cv);


        ImageButton dir = promptsView.findViewById(R.id.imageButton45);
        EditText proj_name = promptsView.findViewById(R.id.proj_name);
        EditText proj_nr = promptsView.findViewById(R.id.proj_nr);
        EditText proj_paht = promptsView.findViewById(R.id.proj_path);

        try {
            proj_nr.setText(data[0]);
            proj_name.setText(data[1]);
            proj_paht.setText(data[2]);
        } catch (Exception e) {

        }

        dirname = promptsView.findViewById(R.id.proj_path);

        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, 0);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle(" Projekt Bearbeiten");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = proj_name.getText().toString();
                String dir_value = dirname.getText().toString();
                //Toast.makeText(getContext(), name+":"+dir_value, Toast.LENGTH_SHORT).show();

                ContentValues cv = new ContentValues();
                cv.put("NAME", name);
                cv.put("SRC", dir_value);
                mdo.update_projekt(data[0], cv);
                refresh_spinner();
                dialogInterface.cancel();

            }
        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();

    }


    public void refresh_spinner() {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] projekt_liste = mdo.projekt_list_all();
        if (projekt_liste.length == 0) {
            projekt_liste = new String[]{"Keine Projekte"};
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, projekt_liste);
        projlist.setAdapter(spinnerArrayAdapter);

    }
    public void refresh_spinner_zulieferer()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.zulieferer_list_all();
        if (zulieferer_liste_items.length == 0)
        {
            zulieferer_liste_items = new String[]{"Keine Zulieferer"};
        }

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);
            Zulieferer_liste_main.setAdapter(spinnerArrayAdapter);
    }
    public void refresh_spinner_zulieferer_settings()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.zulieferer_list_all();
        if (zulieferer_liste_items.length == 0)
        {
            zulieferer_liste_items = new String[]{"Keine Zulieferer"};
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);
        zulieferer_liste.setAdapter(spinnerArrayAdapter);
    }

    public String[] divider(String value) {
        String[] item_parts = value.split("\\[");
        item_parts[1] = item_parts[1].replace("]", "");

        return item_parts;

    }

    public void refresh_projekt_label()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        projekt_label.setText(mdo.get_selectet_projekt());
    }

    //Bild aufnehmen

    private void dispatchTakePictureIntent(String path,String title,boolean tag_on,String tag,String date)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;
        try {

            String addings= date+"_ID_";
            String imageFileName = title+"@" + addings;

            File storageDir = new File(path);
            storageDir.mkdirs();  //erstellt fehlende Ordner...
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpeg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
            photoFile = image;
            //photoFile= createImageFile(path,title,tag_on,tag,date);
        } catch (IOException e)
        {
            exmsg("250720231154",e);
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null)
        {
            try {
                photoURI = FileProvider.getUriForFile(getContext(),"com.example.tabnav_test.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    public void refrehshPicture(String path)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile  = new File(path);



        // Continue only if the File was successfully created
        if (photoFile != null)
        {
            try {
                photoURI = FileProvider.getUriForFile(getContext(),"com.example.tabnav_test.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }



    private File createImageFile(String path,String title,boolean tag_on,String tag,String date) throws IOException
    {

        String addings= date+"_ID_";
        String imageFileName = title+"@" + addings;



        File storageDir = new File(path);
        storageDir.mkdirs();  //erstellt fehlende Ordner...
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private String get_foto_path()
    {
        return  photo_path;
    }

    private void set_foto_path(String path)
    {
          photo_path =path;
    }

    private void reset_date()
    {
        date_label.setText(bsf.date_refresh_rev2());
        date_background.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.hellgrün));
    }

    private void reset_ls_nr()
    {
        lsnr_field.setText("");
    }
    private void reset_artikel()
    {
        edit_artikel_name.setText("");
    }
    private void reset_menge()
    {
        menge.setText("");
    }
    private void reset_notiz()
    {
        note_field.setText("");
    }

    private void reset_camera()
    {
        ls_photo_view.setImageResource(R.drawable.cam);
    }

    private void media_shift(String direction)
    {
        int max = imageset.length;
        String arraypos = "";

        if (max > 0)
        {
            switch (direction)
            {
                case "f": //Vorwärts
                    if (imageset_array_pointer <= max - 2)
                    {
                        imageset_array_pointer++;
                    }
                    update_photo_view();

                    break;
                case "b": //Rückwärts
                    if (imageset_array_pointer >= 1)
                    {
                        imageset_array_pointer--;
                    }
                    update_photo_view();

                    break;

                case "r"://aktuallisieren "relaod"
                     update_photo_view();
                    break;
                default:
                    Toast.makeText(getContext(), "Fehler: Keine gültiger Parameter", Toast.LENGTH_SHORT).show();
            }

            arraypos = "  (" + imageset_array_pointer + ")";
            imagecounter.setText("Bild "+String.valueOf(imageset_array_pointer+1)+ " von "+max+arraypos);

        } else
        {
            media_visibilitiy(View.GONE);
            Toast.makeText(getContext(), "Keine Bilder ", Toast.LENGTH_SHORT).show();
        }
    }

    private void set_media_directory(String sub_dir) throws Exception
    {
        material_database_ops mdo = new material_database_ops(getContext());

        try {
            String  r = mdo.get_selectet_projekt_root().split(",")[2]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
            r = r.replace("primary:",Environment.getExternalStorageDirectory().getAbsolutePath()+"/");
            r +=sub_dir;
            in_directory = r;
            Log.d("in_directory",r);
            File f = new File(r);
            f.mkdirs(); //erstellt die fehlenden verzeichnisse.
        } catch (Exception e)
        {
            exmsg("250720231142",e);
        }

    }

    private void media_visibilitiy(int visible_mode)
    {
        try {
            ls_photo_view.setVisibility(visible_mode);
            ls_img_collection_backward.setVisibility(visible_mode);
            ls_img_collection_forward.setVisibility(visible_mode);
            imagecounter.setVisibility(visible_mode);
        } catch (Exception e)
        {
           exmsg("250720231006",e);
        }
    }

    private void  create_imageset()
    {
        File f = new File(in_directory);
        imageset =f.list();
        if(imageset.length >0)
        {
            imageset_array_pointer=0;
        }

    }

    private void  update_photo_view()  //Akuallisiert das imageView mit dem Akueller Position des imagset pointers
    {
        String path = in_directory+"/"+imageset[imageset_array_pointer]; //Pfad des Mediums

        Bitmap ls_picture=null;
        Bitmap ls_picture_scaled =null;


        switch(bsf.detect_extension(path))  // fixme try block?
        {
            case ".jpeg":
                ls_picture= BitmapFactory.decodeFile(path);
                ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture,foto_preview_w,foto_preview_h,true);
                ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/
                break;

            case ".jpg":
                ls_picture= BitmapFactory.decodeFile(path);
                ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture,foto_preview_w,foto_preview_h,true);
                ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/
                break;

            case ".png":

                ls_picture= BitmapFactory.decodeFile(path);
                ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture,foto_preview_w,foto_preview_h,true);
                ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/
                break;

            case ".pdf":
                Bitmap pdf_logo = BitmapFactory.decodeResource(getResources(), R.drawable.pdflogo);
                Bitmap pdf_logo_scaled =  Bitmap.createScaledBitmap(pdf_logo,foto_preview_w,foto_preview_h,true);
                ls_photo_view.setImageBitmap(pdf_logo_scaled);
                break;

            default:
                Bitmap sometype = BitmapFactory.decodeResource(getResources(), R.drawable.some_file);
                Bitmap sometype_scaled =  Bitmap.createScaledBitmap(sometype,foto_preview_w,foto_preview_h,true);
                ls_photo_view.setImageBitmap(sometype_scaled);
        }

    }

    private boolean copy_media_files_from_temp()
    {
        String source_dir ="";
        String destination_dir ="";
        Boolean check = false;
        try {
            set_media_directory(ls_media_directory_name);
            destination_dir = in_directory;
            set_media_directory(ls_media_directory_name_temp);
            source_dir = in_directory;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        File f = new File (in_directory);
        if(f.exists())

        {
            String[] files = f.list();

            for(String d: files)
            {
                String source = source_dir+"/"+d;
                String destination =destination_dir+"/"+d;
                check = true;

                try {
                    File source_file = new File(source);
                    bsf.copyFileUsingStream(source_file,new File(destination)); //Kopieren von-zu
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return  check;
    }

    public void clean_temp_dir()
    {
        String in_directory_backup = in_directory; //Sichern
        try
            {
            set_media_directory(ls_media_directory_name_temp);
            File f= new File(in_directory);

            String [] files =f.list();
            for (String d: files)
            {
                File t = new File(in_directory+"/"+d);
                t.delete();
            }
                bsf.log("Verzeichnis 'temp' bereinigt!");

        } catch (Exception e)
        {
            bsf.error_msg("Verzeichnis 'temp' bereinigung Fehlgeschlagen!\n"+e,getContext());
        }
            in_directory = in_directory_backup; //Wiederherstellen
    }

    void exmsg(String msg, Exception e)
    {
        Log.e("Exception: Material ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }

}