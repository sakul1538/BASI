package com.example.tabnav_test.material;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.media.AudioAttributesImplBaseParcelizer;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.SQL_finals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    public static final String ls_media_directory_name = "/Lieferscheine"; // höhe
    private static final String ls_media_directory_name_temp = "/Lieferscheine/temp"; // höhe
    public static final String backup_dir ="/Backups&Exports/"; // höhe
    private static final String backup_artikel = "BASI_Backupfile_artikel"; // höhe
    private static final String backup_lieferanten = "BASI_Backupfile_lieferanten"; // höhe
    private static final String TAG = "BASI"; // höhe
    private static final String TAKE_IMAGE_REFRESH_MODE = "refresh"; // höhe
    private static final String TAKE_IMAGE_NEW_MODE = "new"; // höhe

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String currentPhotoPath = "null";
    Uri photoURI = null;
    String photo_path = "null";
    String in_directory = "/";

    long touchStartTime = 0;

    Boolean lieferant_lock_status = false;
    ImageButton settings_projekt_button;
    ImageButton settings_zulieferer;

    ImageButton set_locksatus_zulieferer;
    ImageButton settings_artikel;
    ImageButton add_artikel;
    ImageButton reset_artikel;
    ImageButton reset_zulieferer;
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
    AutoCompleteTextView edit_zulieferer_name;


    //Spinner
    Spinner projlist;
    Spinner zulieferer_liste;

    Spinner spinner_artikel_settings ;



    Spinner spinner_einheiten;

    //Globale Instanzen
    Basic_funct bsf = new Basic_funct();
    material_database_ops mdo;

    //TextView
    TextView projekt_label;
    TextView date_label;
    TextView imagecounter;

    ImageView ls_photo_view;
    ImageView photo_viewer;


    LinearLayout date_background;
    LinearLayout ls_nr_background;
    LinearLayout zulieferer_backgound;


    String[] imageset;
    Integer imageset_array_pointer = 0;


    Bitmap ls_picture;
    Bitmap ls_picture_scaled;



    routines r = new routines();



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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri uri = null;
        switch (requestCode) {
            case 0:
                dirname.setText(resultData.getData().getLastPathSegment());
                break;

            case 1://Foto mit Kamera neu

                String path = photoURI.getPath().replace("/primary", Environment.getExternalStorageDirectory().getAbsolutePath());

                corr_pic_orientaion(path);
                compress_bitmap(path);


                //Log.d(TAG, path); //storage/emulated/0/DCIM/Baustellen/TST/Lieferscheine/temp/Kiener+Wittlin_LSNR_#@01012022_ID_16910464164706228446865913171345.jpeg
                //String datapath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/BASI/";

                create_imageset();
                try {
                    //liest alle Dateien im Verzeichniss "in_directory" ein und Speichert sie im Array "imageset"

                    if (imageset.length > 0) // Wenn das imageset einträge enthält...
                    {
                        media_visibilitiy(View.VISIBLE);
                        media_shift("r");
                    }

                } catch (Exception e) {
                    exmsg("18062023110", e);
                }

                //Toast.makeText(getContext(),path, Toast.LENGTH_SHORT).show();
                break;

            case 2:

                refresh_viewer_photo();
                update_photo_view();
                break;

            case 3: //PDF öffnen

                if (resultData != null) {
                    uri = resultData.getData(); //Url von der Datei, die Kopiert werden soll
                    String source_uri = uri.getPath();
                    Log.d("BASI",source_uri);
                    source_uri = source_uri.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                    String destinationPath = "";
                    String filename = create_media_filename(".pdf");

                    if (filename != "null") {
                        try {
                            destinationPath = in_directory + "/" + filename; //Verzeichniss und Dateiname verbinden
                            File source = new File(source_uri);
                            File destination = new File(destinationPath);

                            try {
                                bsf.copyFileUsingStream(source, destination); //Kopieren von-zu
                                create_imageset();
                                media_visibilitiy(View.VISIBLE);
                                media_shift("r");

                            } catch (IOException e) {
                                exmsg("250720231849", e);
                            }

                        } catch (Exception e) {
                            exmsg("270720231227", e);
                            Toast.makeText(getContext(), "PDF import Fehlgeschlagen! \n" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case 4: //bild von dateien öffnen

                if (resultData != null) {
                    uri = resultData.getData();
                    String source_path = uri.getPath();///document/primary:DCIM/Baustellen /testprojekt/Lieferscheine/Volken_NR_1223@25072023_ID_6547592956172734206.jpeg
                    source_path = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                    String destinationPath = null;

                    String filename = create_media_filename(bsf.detect_extension(source_path));
                    if (filename != "null") ;
                    {
                        try {
                            destinationPath = in_directory + "/" + filename; //Verzeichniss und Dateiname verbinden
                            File source = new File(source_path);
                            File destination = new File(destinationPath);

                            try {
                                bsf.copyFileUsingStream(source, destination); //Kopieren von-zu
                                media_visibilitiy(View.VISIBLE);
                                create_imageset();
                                media_shift("r");
                            } catch (IOException e) {
                                exmsg("250720231849", e);
                            }

                        } catch (Exception e) {
                            exmsg("270720231230", e);
                            Toast.makeText(getContext(), "Bild import Fehlgeschlagen! \n" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;

            case 5:
                if (resultData != null) {
                    uri = resultData.getData();
                    String source_path = uri.getPath();///document/primary:DCIM/Baustellen /testprojekt/Lieferscheine/Volken_NR_1223@25072023_ID_6547592956172734206.jpeg
                    source_path = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                    String destinationPath = in_directory + "/" + imageset[imageset_array_pointer]; //Pfad des Mediums

                        try {
                            File source = new File(source_path);
                            File destination = new File(destinationPath);

                            try {
                                bsf.copyFileUsingStream(source, destination); //Kopieren von-zu
                                refresh_viewer_photo();
                            } catch (IOException e) {
                                exmsg("250720231849", e);
                            }

                        } catch (Exception e) {
                            exmsg("270720231230", e);
                            Toast.makeText(getContext(), "Bild import Fehlgeschlagen! \n" + e, Toast.LENGTH_SHORT).show();
                        }
                }

                break;

            case 6: //Backup Zulieferer Restore

                if (resultData != null)
                {
                    uri = resultData.getData();
                    String source_path = uri.getPath();///document/primary:DCIM/Baustellen /testprojekt/Lieferscheine/Volken_NR_1223@25072023_ID_6547592956172734206.jpeg
                    source_path = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                    Log.d("BADO SOURCE PAHT BACKUP ",source_path);


                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
                    alertdialog.setTitle("Aktion bei vorhandenen Einträgen?");
                    String finalSource_path = source_path;
                    alertdialog.setPositiveButton("Überschreiben", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                            mdo.restore_zulieferer(finalSource_path,getContext(),true);
                            refresh_spinner_zulieferer_settings();
                            dialogInterface.cancel();
                        }
                    });

                    alertdialog.setNegativeButton("Behalten", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            mdo.restore_zulieferer(finalSource_path,getContext(),false);
                            refresh_spinner_zulieferer_settings();
                            dialogInterface.cancel();
                        }
                    });

                    alertdialog.show();
                }
                break;

            case 7: //Backup Artikel Restore


                if (resultData != null)
                {
                    uri = resultData.getData();
                    String source_path = uri.getPath();///document/primary:DCIM/Baustellen /testprojekt/Lieferscheine/Volken_NR_1223@25072023_ID_6547592956172734206.jpeg
                    source_path = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                    Log.d("BADO SOURCE PAHT BACKUP ",source_path);


                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(getContext());
                    alertdialog.setTitle("Aktion bei vorhandenen Einträgen?");
                    String finalSource_path = source_path;
                    alertdialog.setPositiveButton("Überschreiben", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {

                           mdo.restore_artikel(finalSource_path,getContext(),true);
                            refresh_spinner_artikel_settings();
                            dialogInterface.cancel();
                        }
                    });

                    alertdialog.setNegativeButton("Behalten", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                          mdo.restore_artikel(finalSource_path,getContext(),false);
                            refresh_spinner_artikel_settings();
                            dialogInterface.cancel();
                        }
                    });

                    alertdialog.show();
                }

                break;



        }
    }

    private void corr_pic_orientaion(String file_url) {
        try {
            ExifInterface exif = new ExifInterface(file_url);
            int orientaion = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            Bitmap bMap = BitmapFactory.decodeFile(file_url);

            Log.d(TAG, String.valueOf(orientaion));
            Matrix matrix = new Matrix();


            switch (orientaion) {
                case 6:
                    matrix.setRotate(90);
                    Bitmap bMapRotation = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
                    save_bitmap(bMapRotation,file_url);
                    break;

            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
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
    public void onDestroyView() {
        super.onDestroyView();
        try {
            set_media_directory(ls_media_directory_name_temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        clean_temp_dir();

    }

    @Override
    public void onDestroy() {
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
                             Bundle savedInstanceState) {
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
        edit_zulieferer_name = view.findViewById(R.id.autocomplete_zulieferer_name);


        //ImageButtons
        settings_zulieferer = view.findViewById(R.id.imageButton37);
        settings_projekt_button = view.findViewById(R.id.imageButton34);
        settings_artikel = view.findViewById(R.id.imageButton39);

        reset_artikel = view.findViewById(R.id.reset_artikel);
        reset_zulieferer = view.findViewById(R.id.reset_zulieferer);
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
        set_locksatus_zulieferer = view.findViewById(R.id.zulieferer_reset_lock);



        //EditText
        lsnr_field = view.findViewById(R.id.edittext_LS_ID);

        //Spinner
        spinner_einheiten = view.findViewById(R.id.spinner6_einheiten);

        //Layouts
        date_background = view.findViewById(R.id.date_background);
        ls_nr_background = view.findViewById(R.id.ls_nr_bg);
        zulieferer_backgound = view.findViewById(R.id.zulieferer_background);


        try {
            refresh_artikel_autocomplete();
            refresh_projekt_label();
            refresh_autocomplete_liste_zulieferer();
            set_media_directory(ls_media_directory_name_temp);
            clean_temp_dir();
            create_imageset();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        reset_complete();


        lsnr_field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if(b==false)
                {
                    r.lsnr_test_alert(r.lsnr_test());
                }
            }
        });

        set_locksatus_zulieferer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(lieferant_lock_status==false)
                {
                    zulieferer_backgound.setBackgroundColor(getResources().getColor(R.color.orange));
                    lieferant_lock_status =true;
                }
                else
                {
                    zulieferer_backgound.setBackgroundColor(getResources().getColor(R.color.hellgrün));
                    lieferant_lock_status = false;
                }
            }
        });

        reset_zulieferer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_autocomplete_liste_zulieferer();
            }
        });




        ls_photo_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ls_image_viewer();
            }
        });

        ls_img_collection_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media_shift("f");
            }
        });


        ls_img_collection_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                media_shift("b");
            }
        });

        open_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, 3);
            }
        });
        open_filesystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 4);
            }
        });

        start_material_log_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent log_activity = new Intent(getContext(), material_log_activity.class);
                    startActivity(log_activity);
                } catch (Exception e) {
                    exmsg("250620231224", e);
                }

            }
        });

        ls_delet_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageset.length > 0)
                {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    // set prompts.xml to alertdialog builder

                    alertDialogBuilder.setTitle("Artikelverwaltung");
                    alertDialogBuilder.setMessage("Bild löschen?\n" + in_directory + "/" + imageset[imageset_array_pointer]);

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File f = new File(in_directory + "/" + imageset[imageset_array_pointer]);
                            f.delete();
                            create_imageset();
                            if (imageset.length > 0) {
                                media_shift("r");
                                dialogInterface.cancel();
                            } else {
                                media_visibilitiy(View.GONE);
                            }


                        }
                    });

                    alertDialogBuilder.setNegativeButton("Abbrecen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
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
            public void onClick(View view) {
                material_database_ops mdo = new material_database_ops(getContext());

                String proj = String.valueOf(projekt_label.getText());
                int spos = proj.lastIndexOf("[");
                int epos = proj.lastIndexOf("]");

                ContentValues data = new ContentValues();

                try {
                    data.put("ID", bsf.gen_UUID());
                    data.put("PROJEKT_ID", proj.substring(spos + 1, epos));
                    String date_db_format = bsf.convert_date(String.valueOf(date_label.getText()),"format_database");
                    data.put("DATUM",date_db_format);

                    if(TextUtils.isEmpty(lsnr_field.getText()))
                    {
                        String ms_time_string =  String.valueOf(System.currentTimeMillis());

                        String ls_nr_alternativ ="G"+ms_time_string.substring(ms_time_string.length()-5,ms_time_string.length());
                        data.put("LSNR", ls_nr_alternativ);

                    }
                    else
                    {
                        data.put("LSNR", String.valueOf(lsnr_field.getText()));
                    }
                    //String id_zulieferer =  mdo.get_id_zulieferer(Zulieferer_liste_main.getSelectedItem().toString());
                    String id_zulieferer = r.get_id_zulieferer();

                    data.put("LIEFERANT_ID", String.valueOf(id_zulieferer));

                    data.put("MENGE", String.valueOf(menge.getText()).trim());

                    data.put("EINHEIT_ID", spinner_einheiten.getSelectedItem().toString());

                    String notiz = note_field.getText().toString();
                    if (notiz == "" || notiz.isEmpty()) {
                        data.put("NOTIZ", "null");
                    } else {
                        data.put("NOTIZ", notiz);
                    }

                    data.put("SRC", "null"); //Fixme anpassen !!!

                    String artikel_value = String.valueOf(edit_artikel_name.getText()).trim();

                    String id_artikel = mdo.get_artikel_param(new String[]{artikel_value}, "NAME    =?", new String[]{"ID"});

                    if (id_artikel == "null") {
                        mdo.add_artikel_to_list(artikel_value, String.valueOf(data.get("EINHEIT_ID"))); //Erstellt Speichert den Artikel in Datenbank, da er nicht existert
                        data.put("MATERIAL_ID", mdo.get_artikel_param(new String[]{artikel_value}, "NAME=?", new String[]{"ID"}));
                    } else {
                        data.put("MATERIAL_ID", id_artikel);
                    }
                } catch (Exception e) {
                    exmsg("200720232123", e);
                }

                try {



                    long response = mdo.add_material_log_entry(data);
                    if (response > 0)
                    {
                        String copy_temp_msg = "";
                        if (copy_media_files_from_temp(r.get_zulieferer_name(),data.get("LSNR").toString(),r.get_date().replace(".","")) == true)
                        {
                            copy_temp_msg = "+ Medien wurden ins Verzeichniss übernommen!";
                        } else {
                            copy_temp_msg = "+ Medien wurden NICHT ins Verzeichnis übernommen!";

                        }

                        Toast.makeText(getContext(), "+Es  wurde  eine Eintrag erstellt!\n" + copy_temp_msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "+ Es konnte KEIN Eintrag erstellt werden!", Toast.LENGTH_SHORT).show();

                    }

                    clean_temp_dir();
                    reset_complete();
                } catch (Exception e) {
                    exmsg("200620232152", e);
                    Toast.makeText(getContext(), "Kein Eintrag erstellt, interner Feher \n " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    clean_temp_dir();
                    reset_complete();
                }
            }
        });

        reset_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reset_complete();

            }
        });

        refresh_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_date();
            }
        });

        date_label.setText(bsf.date_refresh_rev2());

        date_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2);


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());


                        date_label.setText(dateString);
                        date_background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));

                    }
                });
                datePickerDialog.show();
            }
        });




        ls_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                material_database_ops mdo = new material_database_ops(getContext());
                try {
                    set_media_directory(ls_media_directory_name_temp);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                take_picture(in_directory, TAKE_IMAGE_NEW_MODE, getContext());
            }
        });
        ls_note_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_notiz();
            }
        });

        reset_LS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_ls_nr();
            }
        });
        //Artikel

        reset_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_artikel();
                reset_menge();
            }
        });

        edit_artikel_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = edit_artikel_name.getText().toString();
                int spos = selected.lastIndexOf("[");
                int epos = selected.lastIndexOf("]");

                String artikel = selected.substring(0, spos);
                String einheit = selected.substring(spos + 1, epos);
                edit_artikel_name.setText(artikel);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.einheiten_array, android.R.layout.simple_spinner_item);
                int index = adapter.getPosition(einheit);
                spinner_einheiten.setSelection(index);

            }
        });

        settings_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View view_artikel = li.inflate(R.layout.artikel_config_dialog, container, false);



                //ImageButtons
                ImageButton imagebutton_artikel_update = view_artikel.findViewById(R.id.imagebutton_artikel_update);
                ImageButton imagebutton_artikel_delet = view_artikel.findViewById(R.id.imageButton_artikel_delet);
                ImageButton imageButton_artikel_add = view_artikel.findViewById(R.id.imageButton_artikel_add);
                Button button_artikel_crate_backup = view_artikel.findViewById(R.id.button_artikel_crate_backup);
                Button button_artikel_restore_backup = view_artikel.findViewById(R.id.button_artikel_restore_backup);

                //Spinners
                Spinner spinner_artikel_unit  = view_artikel.findViewById(R.id.spinner_artikel_unit);
                spinner_artikel_settings = view_artikel.findViewById(R.id.spinner_artikel_settings);

                //Autocomplete
                AutoCompleteTextView autoCompleteText_artikel_add = view_artikel.findViewById(R.id.autoCompleteText_artikel_add);
                material_database_ops mdo = new material_database_ops(getContext());
                String[] artikel = mdo.artikel_list_all_no_unit();
                ArrayAdapter<String> artikelAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, artikel);
                autoCompleteText_artikel_add.setAdapter(artikelAdapter);

                refresh_spinner_artikel_settings();

                button_artikel_restore_backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent_restore_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent_restore_backup.addCategory(Intent.CATEGORY_OPENABLE);
                        intent_restore_backup.setType("application/json");
                        startActivityForResult(intent_restore_backup, 7);

                    }
                });


              button_artikel_crate_backup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        r.create_backup(mdo.create_backup_artikel(),backup_artikel);
                    }
                });

                imageButton_artikel_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        try {
                            String new_artikel = autoCompleteText_artikel_add.getText().toString().trim();
                            String new_einheit = spinner_artikel_unit.getSelectedItem().toString();
                            mdo.add_artikel_to_list(new_artikel,new_einheit);
                            refresh_spinner_artikel_settings();
                            autoCompleteText_artikel_add.setText("");
                            Toast.makeText(getContext(), "Artikel\n "+new_artikel +" "+new_einheit +" hinzugefügt!",Toast.LENGTH_SHORT).show();

                        }catch (Exception e )
                        {
                            Toast.makeText(getContext(), "Fehler:\n"+e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });



                imagebutton_artikel_delet.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)
                    {
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

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    alertDialogBuilder.setTitle("Bestätigen");
                                    alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);

                                    alertDialogBuilder.setMessage("ALLE Einträge Löschen?").setPositiveButton("ALLE Löschen", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {

                                            mdo.artikel_delet_all();
                                            refresh_spinner_artikel_settings();
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
                                    String selected = spinner_artikel_settings.getSelectedItem().toString();
                                    int spos = selected.lastIndexOf("[");
                                    int epos = selected.lastIndexOf("]");

                                   String artikel = selected.substring(0, spos).trim();
                                   String einheit = selected.substring(spos + 1, epos).trim();


                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    alertDialogBuilder.setTitle("Bestätigen");
                                    alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                                    alertDialogBuilder.setMessage("Artikel " +spinner_artikel_settings.getSelectedItem().toString() + "  wiklich löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            mdo.artikel_delet(artikel,einheit);
                                            refresh_spinner_artikel_settings();
                                            //TODO Ordner im Verzeichnis auch löschen!*/


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

                imagebutton_artikel_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View update_artikel = li.inflate(R.layout.material_artikel_change_dialog, container, false);

                        EditText editTextText2_artikel = update_artikel.findViewById(R.id.editTextText2_artikel);
                        Spinner spinner_artikel_einheit = update_artikel.findViewById(R.id.spinner_artikel_einheit);

                        String artikel = "";
                        String  einheit ="";
                        try {

                            String selected = spinner_artikel_settings.getSelectedItem().toString();
                            int spos = selected.lastIndexOf("[");
                            int epos = selected.lastIndexOf("]");

                            artikel = selected.substring(0, spos).trim();
                            einheit = selected.substring(spos + 1, epos).trim();

                            editTextText2_artikel.setText(artikel);

                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.einheiten_array, android.R.layout.simple_spinner_item);
                            int index = adapter.getPosition(einheit);
                            spinner_artikel_einheit.setSelection(index);

                        } catch (Exception e) {

                            throw new RuntimeException(e);
                        }

                        AlertDialog.Builder update_artikel_dialog = new AlertDialog.Builder(getContext());
                        // set prompts.xml to alertdialog builder
                        update_artikel_dialog.setView(update_artikel);
                        update_artikel_dialog.setTitle("Artikel ändern");

                        String finalArtikel = artikel;
                        String finalEinheit = einheit;
                        update_artikel_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                String artikel_new =editTextText2_artikel.getText().toString().trim();
                                String eiheit_new = spinner_artikel_einheit.getSelectedItem().toString();
                                mdo.update_artikel(finalArtikel, finalEinheit,artikel_new,eiheit_new);
                                refresh_spinner_artikel_settings();
                                dialogInterface.cancel();
                            }
                        });

                        update_artikel_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });
                        update_artikel_dialog.show();
                    }
                });

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(view_artikel);
                alertDialogBuilder.setTitle("Artikelverwaltung");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //refresh_artikel_autocomplete();
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



        //Alte version
        /*settings_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View view_artikel = li.inflate(R.layout.material_type_conf, container, false);

                RecyclerView matieral_artikel = view_artikel.findViewById(R.id.material_artikel_liste);

                material_database_ops mdo = new material_database_ops(getContext());

                String[] material_artikel_adapter = mdo.artikel_list_all();
                com.example.tabnav_test.material_artikel_adapter lcar = new material_artikel_adapter(material_artikel_adapter);
                matieral_artikel.setAdapter(lcar);
                matieral_artikel.setLayoutManager(new LinearLayoutManager(getContext()));

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(view_artikel);
                alertDialogBuilder.setTitle("Artikelverwaltung");

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refresh_artikel_autocomplete();
                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });*/

        //Zulieferer
        settings_zulieferer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.lieferanten_config_dialog, container, false);

                ImageButton add_zulieferer = promptsView.findViewById(R.id.imageButton_artikel_add);
                ImageButton update_zulieferer = promptsView.findViewById(R.id.imageButton50);
                ImageButton del_zuliefere = promptsView.findViewById(R.id.imageButton_artikel_delet);

                Button create_backup = promptsView.findViewById(R.id.button_artikel_crate_backup);
                Button restore_backup = promptsView.findViewById(R.id.button_artikel_restore_backup);
                zulieferer_liste = promptsView.findViewById(R.id.spinner_artikel_settings);
                AutoCompleteTextView add_new_liferant_value = promptsView.findViewById(R.id.editText_add_lieferant);
                add_new_liferant_value.setAdapter(get_zulieferer_adapter());

                refresh_spinner_zulieferer_settings();

                create_backup.setOnClickListener(new View.OnClickListener()
                {
                    @Override

                    public void onClick(View view)
                    {
                            r.create_backup(mdo.create_backup_json_zulieferer(),backup_lieferanten);
                        }
                });

                restore_backup.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        Intent intent_restore_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent_restore_backup.addCategory(Intent.CATEGORY_OPENABLE);
                        intent_restore_backup.setType("application/json");
                        startActivityForResult(intent_restore_backup, 6);

                    }
                });

                add_zulieferer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // set prompts.xml to alertdialog builder

                        alertDialogBuilder.setTitle("Aktion Zulieferer");
                        alertDialogBuilder.setMessage("Zulieferer \""+add_new_liferant_value.getText().toString()+"\" hinzufügen?");

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                material_database_ops mdo = new material_database_ops(getContext());
                                mdo.add_zulieferer(add_new_liferant_value.getText().toString());
                               refresh_spinner_zulieferer_settings();

                               String lieferant_ID= mdo.get_id_zulieferer(add_new_liferant_value.getText().toString());

                                Toast.makeText(getContext(),   "Lieferant NAME:" +mdo.get_lieferant_name_by_id(lieferant_ID)+" ID:"+lieferant_ID, Toast.LENGTH_SHORT).show();
                                add_new_liferant_value.setText("");
                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.setNeutralButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                add_new_liferant_value.setText("");
                                Toast.makeText(getContext(), "Abgebrochen durch Benutzer!", Toast.LENGTH_SHORT).show();
                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.show();
                    }
                });

                update_zulieferer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater li = LayoutInflater.from(getActivity());
                        View input_view = li.inflate(R.layout.add_lieferanten_dialog, container, false);

                        EditText name_zulieferer = input_view.findViewById(R.id.name_zulieferer);
                        String zulieferer_name = zulieferer_liste.getSelectedItem().toString();
                        name_zulieferer.setText(zulieferer_name);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(input_view);
                        alertDialogBuilder.setTitle("Zulieferer hinzufügen");

                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //update_zulieferer(alte Bezeichnung ,neuer Name)
                                material_database_ops mdo = new material_database_ops(getContext());
                                mdo.update_zulieferer(zulieferer_name, name_zulieferer.getText().toString());
                                refresh_spinner_zulieferer_settings();

                                dialogInterface.cancel();
                            }
                        });
                        alertDialogBuilder.show();

                    }
                });

                del_zuliefere.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)

                    {
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

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    alertDialogBuilder.setTitle("Bestätigen");
                                    alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);

                                    alertDialogBuilder.setMessage("ALLE Einträge Löschen?").setPositiveButton("ALLE Löschen", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            mdo.zulieferer_delet_all();
                                            refresh_spinner_zulieferer_settings();
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
                                    String zulieferer_name = zulieferer_liste.getSelectedItem().toString();

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    alertDialogBuilder.setTitle("Bestätigen");
                                    alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                                    alertDialogBuilder.setMessage("Zulieferer " + zulieferer_name + "  wiklich löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            material_database_ops mdo = new material_database_ops(getContext());
                                            mdo.zulieferer_delet(zulieferer_name);
                                            refresh_spinner_zulieferer_settings();
                                            //TODO Ordner im Verzeichnis auch löschen!

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

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setTitle("Zulieferer verwalten");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refresh_autocomplete_liste_zulieferer();
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

    public void ls_image_viewer()
    {
        String file_url = in_directory + "/" + imageset[imageset_array_pointer];

        try {
            if (file_url != "null") {
                if (bsf.detect_extension(file_url).contains(".pdf")) //Pdf öffnen
                {
                    Log.d(TAG, file_url);
                    open_pdf(file_url, getContext());

                } else
                {

                    LayoutInflater myLayout = LayoutInflater.from(getContext());
                    View pic_view_UI = myLayout.inflate(R.layout.show_picture, null);

                    TextView path_value = pic_view_UI.findViewById(R.id.textView65);

                    photo_viewer = (ImageView) pic_view_UI.findViewById(R.id.imageView4);

                    ImageButton refresh_image = (ImageButton) pic_view_UI.findViewById(R.id.imageButton60);
                    ImageButton refresh_image_file = (ImageButton) pic_view_UI.findViewById(R.id.imageButton63);
                    ImageButton rotate_right = (ImageButton) pic_view_UI.findViewById(R.id.imageButton62);

                    path_value.setText(file_url.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), ""));

                    try {
                        photo_viewer.setImageBitmap(ls_picture_scaled);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    rotate_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Matrix matrix = new Matrix();
                            Bitmap bMap = BitmapFactory.decodeFile(file_url);

                           matrix.setRotate(90);
                           Bitmap bMapRotation = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);

                            File file = new File(file_url);
                            if (file.exists()) {
                                file.delete();
                            }
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bMapRotation.compress(Bitmap.CompressFormat.JPEG, 50, out);
                                out.flush();
                                out.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            photo_viewer.setImageBitmap(update_photo_view());

                        }
                    });

                    refresh_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Todo fallbacks wenn kein Bild existiert, damit man noch eines Hinzufügen kann.
                            take_picture(file_url, TAKE_IMAGE_REFRESH_MODE, view.getContext());
                        }
                    });


                    refresh_image_file.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("json/*");
                            startActivityForResult(intent, 5);
                        }
                    });

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(pic_view_UI);
                    alertDialogBuilder.setTitle("Viewer");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ls_photo_view.setImageBitmap(update_photo_view());
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            } //else?
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void reset_complete() {
        reset_date();
        reset_ls_nr();
        reset_artikel();
        reset_autocomplete_liste_zulieferer();
        reset_menge();
        reset_notiz();
        reset_camera();
        media_visibilitiy(View.GONE);
        clean_temp_dir();
        create_imageset();
        r.lsnr_test_alert(r.lsnr_test());

    }

    private void reset_autocomplete_liste_zulieferer()
    {
        if(lieferant_lock_status ==false)
        {
            edit_zulieferer_name.setText("");
        }
    }

    void refresh_artikel_autocomplete() {

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

    public void refresh_autocomplete_liste_zulieferer()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.zulieferer_list_all("DATE DESC");
        if (zulieferer_liste_items.length == 0) {
            zulieferer_liste_items = new String[]{"Keine Zulieferer"};
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);
        edit_zulieferer_name.setAdapter(get_zulieferer_adapter());
    }



    public ArrayAdapter get_zulieferer_adapter()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.zulieferer_list_all("NAME DESC");
        if (zulieferer_liste_items.length == 0) {
            zulieferer_liste_items = new String[]{"Keine Zulieferer"};
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);

        return spinnerArrayAdapter;
    }



    public void refresh_spinner_zulieferer_settings()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.zulieferer_list_all("NAME");
        if (zulieferer_liste_items.length == 0) {
            zulieferer_liste_items = new String[]{"Keine Zulieferer"};
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);
        zulieferer_liste.setAdapter(spinnerArrayAdapter);

    }

    public void refresh_spinner_artikel_settings()
    {
        material_database_ops mdo = new material_database_ops(getContext());
        //Spinner adapter
        String[] zulieferer_liste_items = mdo.artikel_list_all();
        if (zulieferer_liste_items.length == 0) {
            zulieferer_liste_items = new String[]{"Keine Artikel"};
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, zulieferer_liste_items);
        spinner_artikel_settings.setAdapter(spinnerArrayAdapter);
    }


    public String[] divider(String value) {
        String[] item_parts = value.split("\\[");
        item_parts[1] = item_parts[1].replace("]", "");

        return item_parts;

    }

    public void refresh_projekt_label() {
        material_database_ops mdo = new material_database_ops(getContext());
        projekt_label.setText(mdo.get_selectet_projekt());
    }

    //Bild aufnehmen

    public void take_picture(String path, String mode, Context context)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        int request_code = 1;

        switch (mode) {

            case "new":

                Log.d(TAG, "take_picture_new");
                String imageFileName = create_media_filename(""); //Extension für in image.creatTempfile hinzugefügt (".jepg"
                if (imageFileName != "null") {
                    try {
                        File storageDir = new File(path);

                        storageDir.mkdirs();  //erstellt fehlende Ordner...
                        File image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpeg",         /* suffix */
                                storageDir      /* directory */
                        );

                        currentPhotoPath = image.getAbsolutePath();
                        photoFile = image;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case "refresh":
                Log.d(TAG, "take_picture_refresh");
                photoFile = new File(path);
                request_code = 2;
                break;
            default:

        }


        if (photoFile != null) {
            try {
                photoURI = FileProvider.getUriForFile(context, "com.example.tabnav_test.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, request_code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void reset_date() {
        date_label.setText(bsf.date_refresh_rev2());
        date_background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.hellgrün));
    }

    private void reset_ls_nr() {
        lsnr_field.setText("");
        ls_nr_background.setBackgroundColor(getResources().getColor(R.color.hellgrün));
    }




    private void reset_artikel() {
        edit_artikel_name.setText("");
    }

    private void reset_menge() {
        menge.setText("");
    }

    private void reset_notiz() {
        note_field.setText("");
    }

    private void reset_camera() {
        ls_photo_view.setImageResource(R.drawable.cam);
    }

    private void media_shift(String direction) {
        Bitmap ls_picture_scaled = null;
        int max = imageset.length;
        String arraypos = "";

        if (max > 0) {
            switch (direction) {
                case "f": //Vorwärts
                    if (imageset_array_pointer <= max - 2) {
                        imageset_array_pointer++;
                    }

                    ls_picture_scaled = update_photo_view();
                    ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/

                    break;
                case "b": //Rückwärts
                    if (imageset_array_pointer >= 1) {
                        imageset_array_pointer--;
                    }

                    ls_picture_scaled = update_photo_view();
                    ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/

                    break;

                case "r"://aktuallisieren "relaod"

                    ls_picture_scaled = update_photo_view();
                    ls_photo_view.setImageBitmap(ls_picture_scaled); //Setzt das Bild im ImageView*/

                    break;
                default:
                    Toast.makeText(getContext(), "Fehler: Keine gültiger Parameter", Toast.LENGTH_SHORT).show();
            }

            arraypos = "  (" + imageset_array_pointer + ")";
            imagecounter.setText("Bild " + String.valueOf(imageset_array_pointer + 1) + " von " + max + arraypos);

        } else {
            ///media_visibilitiy(View.GONE);
            Toast.makeText(getContext(), "Keine Bilder ", Toast.LENGTH_SHORT).show();
        }
    }

    private void set_media_directory(String sub_dir) throws Exception {
        material_database_ops mdo = new material_database_ops(getContext());

        try {
            String r = mdo.get_selectet_projekt_root_data().split(",")[2]; //Martinheim Süd,23110022,primary:DCIM/Baustellen /Martinsheim Süd;
            r = r.replace("primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            r += sub_dir;
            in_directory = r;
            Log.d("in_directory", r);
            File f = new File(r);
            f.mkdirs(); //erstellt die fehlenden verzeichnisse.
        } catch (Exception e) {
            exmsg("250720231142", e);
        }

    }

    private void media_visibilitiy(int visible_mode) {
        try {
            ls_photo_view.setVisibility(visible_mode);
            ls_img_collection_backward.setVisibility(visible_mode);
            ls_img_collection_forward.setVisibility(visible_mode);
            imagecounter.setVisibility(visible_mode);
        } catch (Exception e) {
            exmsg("250720231006", e);
        }
    }

    private String create_media_filename(String file_extension)
    {
        String filename = "null";

        try {
            String lieferant = edit_zulieferer_name.getText().toString();
            String ls_nr = String.valueOf(lsnr_field.getText());
            String date = (String) date_label.getText();
            date = date.replace(".", "");

            filename = bsf.ls_filename_form(lieferant,ls_nr,date,"default")+file_extension;

        } catch (Exception e) {
            exmsg("270720231219", e);
            filename = "null";
        }
        return filename;
    }

    private void create_imageset() {
        File f = new File(in_directory);
        imageset = f.list();
        if (imageset.length > 0) {
            imageset_array_pointer = 0;
        }
    }

    private Bitmap update_photo_view()  //Akuallisiert das imageView mit dem Akueller Position des imagset pointers
    {
        String path = in_directory + "/" + imageset[imageset_array_pointer]; //Pfad des Mediums

        try {
            switch (bsf.detect_extension(path))  // fixme try block?
            {
                case ".jpeg":
                    ls_picture = BitmapFactory.decodeFile(path);
                    ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);

                    break;

                case ".jpg":
                    ls_picture = BitmapFactory.decodeFile(path);
                    ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
                    break;

                case ".png":
                    ls_picture = BitmapFactory.decodeFile(path);
                    ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);

                    break;

                case ".pdf":


                    // Öffnen Sie die PDF-Datei mit PdfRenderer
                    ParcelFileDescriptor fileDescriptor = null;
                    try {
                        fileDescriptor = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
                        PdfRenderer.Page page = pdfRenderer.openPage(0);

                        ls_picture_scaled = Bitmap.createBitmap(foto_preview_w,  foto_preview_h, Bitmap.Config.ARGB_8888);
                        Bitmap.Config[] formates = Bitmap.Config.values();

                        Log.d(TAG, String.valueOf(Bitmap.Config.values()[0]));
                        page.render(ls_picture_scaled, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    } catch (IOException e)
                    {

                        ls_picture = BitmapFactory.decodeResource(getResources(), R.drawable.pdflogo);
                        ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
                        throw new RuntimeException(e);

                    }


                    break;

                default:
                    ls_picture = BitmapFactory.decodeResource(getResources(), R.drawable.some_file);
                    ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);

            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return ls_picture_scaled;
    }

    private boolean copy_media_files_from_temp(String lieferant, String lsnr,String date)
    {
        String source_dir = "";
        String destination_dir = "";
        Boolean check = false;
        try {
            set_media_directory(ls_media_directory_name);
            destination_dir = in_directory;
            set_media_directory(ls_media_directory_name_temp);
            source_dir = in_directory;

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        File f = new File(in_directory);
        if (f.exists()) {
            String[] files = f.list();

            for (String d : files)
            {
                String source = source_dir + "/" + d;

                String destination = destination_dir + "/" + bsf.ls_filename_form(lieferant,lsnr,date,"default") +bsf.detect_extension(d);
                check = true;

                try {
                    File source_file = new File(source);
                    bsf.copyFileUsingStream(source_file, new File(destination)); //Kopieren von-zu
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        return check;
    }

    public void clean_temp_dir() {
        String in_directory_backup = in_directory; //Sichern
        try {
            set_media_directory(ls_media_directory_name_temp);
            File f = new File(in_directory);

            String[] files = f.list();
            for (String d : files) {
                File t = new File(in_directory + "/" + d);
                t.delete();
            }
            bsf.log("Verzeichnis 'temp' bereinigt!");

        } catch (Exception e) {
            bsf.error_msg("Verzeichnis 'temp' bereinigung Fehlgeschlagen!\n" + e, getContext());
        }
        in_directory = in_directory_backup; //Wiederherstellen
    }

    public void open_pdf(String file_url, Context context) {

        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, "com.example.tabnav_test.fileprovider", new File(file_url));
        Log.d(TAG, String.valueOf(uri));
       pdfIntent.setDataAndType(uri, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            // Adobe Reader oder ein geeigneter PDF-Viewer ist nicht installiert
            Toast.makeText(context, "Es wurde kein PDF-Viewer gefunden", Toast.LENGTH_SHORT).show();
        }

    }

    private void refresh_viewer_photo() {
        Bitmap ls_picture = BitmapFactory.decodeFile(in_directory + "/" + imageset[imageset_array_pointer]);
        Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
        photo_viewer.setImageBitmap(ls_picture_scaled);
    }

    private void compress_bitmap(String bitmap_paht) {
        Bitmap bMap = BitmapFactory.decodeFile(bitmap_paht);

        File file = new File(bitmap_paht);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bMap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void save_bitmap(Bitmap bMap,String file_url)
    {
        File file = new File(file_url);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bMap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class routines implements SQL_finals
    {
        public String get_lsnr()
        {
          return   lsnr_field.getText().toString();
        }
        public String get_date()
        {
          return   date_label.getText().toString();
        }
        public String get_zulieferer_name()
        {
          return    edit_zulieferer_name.getText().toString();
        }

        public String get_selectet_projekt_id()
        {

            String name_id =   mdo.get_selectet_projekt();

            return  name_id.substring(name_id.lastIndexOf("[")+1,name_id.length()-1);
        }



        public Boolean lsnr_test()
        {

           String lsnr =   lsnr_field.getText().toString();

           String[] select_args ={
                   lsnr,
                   this.get_id_zulieferer(),
                   get_selectet_projekt_id()
                };

           if(mdo.find_similar(TB_MATERIAL_LOG,select_args,"LSNR=? AND LIEFERANT_ID=? AND PROJEKT_ID=?")>0)
           {
               return true;
           }
           else
           {
               return false;
           }
        }

        public void lsnr_test_alert(Boolean b)
        {
            if (b==true)
            {
                bsf.info_msg( this.get_lsnr() + " von "+this.get_zulieferer_name()+" \n =>  In Datenbank gefunden!", getContext());
                ls_nr_background.setBackgroundColor(getResources().getColor(R.color.camera_button));
            } else
            {
                ls_nr_background.setBackgroundColor(getResources().getColor(R.color.hellgrün));
            }

        }

        public String get_id_zulieferer()
        {
            if (edit_zulieferer_name.getText().toString().isEmpty())
            {
                return "null";
            }
            else
            {
                String id = mdo.get_id_zulieferer(edit_zulieferer_name.getText().toString());
                if(id =="null")
                {
                    mdo.add_zulieferer(edit_zulieferer_name.getText().toString());
                    id = mdo.get_id_zulieferer(edit_zulieferer_name.getText().toString());
                }
                return id ;

            }

        }

        public void create_backup(String data,String type)
        {
            String filename =type+"@"+bsf.get_date_filename()+"_ID_"+String.valueOf(System.currentTimeMillis())+".json";

            String backup_root = mdo.get_projekt_root_paht()+backup_dir+"Artikel&Lieferanten/";
            File f = new File(backup_root);
            f.mkdirs();
            try {
                f.createNewFile();

                FileWriter fw = new FileWriter(backup_root+filename);
                fw.write(data);
                fw.close();

                AlertDialog.Builder create_backup_report_dialog  = new AlertDialog.Builder(getContext());
                create_backup_report_dialog.setTitle("Export Report");
                create_backup_report_dialog.setMessage("Backup gespeichert unter: \n\n"+backup_root+filename);
                create_backup_report_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                create_backup_report_dialog.setNegativeButton("URL Kopieren", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        bsf.copy_to_clipboard(backup_root+filename,getContext());
                    }
                });

                create_backup_report_dialog.show();

            } catch (IOException e)
            {
                Toast.makeText(getContext(), "Backup erstellen Fehlgeschlagen!:  \n"+e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    void exmsg(String msg, Exception e)
    {
        Log.e("Exception: Material ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }

}