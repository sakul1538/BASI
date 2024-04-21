package com.example.tabnav_test.Kamera;

import static java.util.Arrays.sort;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Basic_func_img;
import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.config_favorite_strings.config_fav;
import com.example.tabnav_test.config_favorite_strings.config_fav_ops;
import com.example.tabnav_test.projekt_ops;
import com.example.tabnav_test.static_finals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Kamera<onActivityResult> extends Fragment {
    // FIXME: 10.01.23  Kamera stürzt ab, wenn "Tag" aktiviert aber Text feld Leer
    //TODO: 07.04.2024 Imagedatei erst nach den definitiven Speichern richtig benennen, KW entsprechend anpassen

    // ----------------------------------------------------------------- Variablen
    Uri photoURI = null;
    private Intent data;
    // ----------------------------------------------------------------- Variablen String, char
    String currentPhotoPath;


    Bitmap final_image;
    String image_stamp_text="";
    int  stamp_bg_color = Color.WHITE;
    int stamp_txt_color =Color.MAGENTA;

    String date;


    static final String RROJ_NR = "0";
    // ----------------------------------------------------------------- Variablen byte,short,int,long,float,double
    private int requestCode;
    private int resultCode;
    // ----------------------------------------------------------------- Variablen Boolean
    // ----------------------------------------------------------------- Instanzen
    projekt_ops projekt;
    kamera_directorys kamera_dirs;
    // ----------------------------------------------------------------- TextView
    TextView curr_date;

    TextView tag;
    TextView media_label;
    TextView save_paht_set;

    // ----------------------------------------------------------------- AutoCompleteTextView
    AutoCompleteTextView kamera_tag_field_value;
    // ----------------------------------------------------------------- EditText
    EditText name = null;
    EditText dir = null;
    // ----------------------------------------------------------------- Button
    // ----------------------------------------------------------------- ImageButtons
    ImageButton kamera_reset_tag;
    ImageButton kamera_open_image;
    ImageButton save_image;

    ImageButton curr_date_refresh_button;
    ImageButton take_picture = null;
    ImageButton adddir_delet = null;
    ImageButton adddir_modify = null;
    ImageButton adddir = null;
    ImageButton kamera_tag_add_fav = null;
    ImageView camera_photo = null;
    ImageButton camera_reset_form = null;
    ImageButton camera_delet_image = null;
    ImageButton config_fav = null;

    // ----------------------------------------------------------------- ImageView
    // ----------------------------------------------------------------- ListView
    // ----------------------------------------------------------------- RecyclerView
    // ----------------------------------------------------------------- Spinner
    kamera_spinner spinnerops;

    Spinner spinner;
    // ----------------------------------------------------------------- CheckBox
// ----------------------------------------------------------------- RadioButton
// ----------------------------------------------------------------- Switch
    Switch kamera_switch_tag_onoff;
    // ----------------------------------------------------------------- SeekBar
// ----------------------------------------------------------------- ProgressBar
// ----------------------------------------------------------------- Switch
// ----------------------------------------------------------------- ScrollView
// ----------------------------------------------------------------- Layouts
    LinearLayout date_bg;
    LinearLayout tag_bg;
// ----------------------------------------------------------------- END

    public Kamera() {
        // Required empty public constructor
    }

    public static Kamera newInstance(String param1, String param2) {
        Kamera fragment = new Kamera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh_fav_auto_complete();
        ///refresh_spinner();

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        clean_temp_dir();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kamera, container, false);


        // ----------------------------------------------------------------- Variablen

        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        Basic_funct bsf = new Basic_funct();
        spinnerops = new kamera_spinner(getContext());
        projekt = new projekt_ops(getContext());
        kamera_dirs = new kamera_directorys(getContext());


        // ----------------------------------------------------------------- TextView
        curr_date = view.findViewById(R.id.kamera_date);
        tag = view.findViewById(R.id.textView32);
        media_label = view.findViewById(R.id.textView66);
        save_paht_set = view.findViewById(R.id.textView4);

        // ----------------------------------------------------------------- AutoCompleteTextView
        kamera_tag_field_value = view.findViewById(R.id.kamera_tag_field_value);
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        kamera_reset_tag = view.findViewById(R.id.kamera_reset_tag_button);
        kamera_open_image = view.findViewById(R.id.kamera_open_image);
        save_image= view .findViewById(R.id.imageButton34);
        kamera_tag_add_fav = view.findViewById(R.id.tag_add_to_fav);
        curr_date_refresh_button = view.findViewById(R.id.kamera_date_refresh_button);
        take_picture = view.findViewById(R.id.imageButton11);
        adddir = view.findViewById(R.id.imageButton7);
        adddir_delet = view.findViewById(R.id.imageButton10);
        adddir_modify = view.findViewById(R.id.imageButton9);
        camera_photo = view.findViewById(R.id.imageView3);
        camera_reset_form = view.findViewById(R.id.imageButton32);
        camera_delet_image = view.findViewById(R.id.delet_image);
        config_fav = view.findViewById(R.id.config_tag);
        // ----------------------------------------------------------------- ImageView
        // ----------------------------------------------------------------- ListView
        // ----------------------------------------------------------------- RecyclerView
        // ----------------------------------------------------------------- Spinner
        spinner = view.findViewById(R.id.spinner4);
        // ----------------------------------------------------------------- CheckBox
        // ----------------------------------------------------------------- RadioButton
        // ----------------------------------------------------------------- Switch
        kamera_switch_tag_onoff = view.findViewById(R.id.kamera_switch_tag_onoff);
        // ----------------------------------------------------------------- SeekBar
        // ----------------------------------------------------------------- ProgressBar
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- ScrollView
        // ----------------------------------------------------------------- Layouts
        tag_bg = view.findViewById(R.id.tag_background);
        date_bg = view.findViewById(R.id.date_background);

        // ----------------------------------------------------------------- END


        //Init
        refresh_spinner();

        tag_visibility(View.GONE);
        preview_camera_visibility(View.GONE);

        /*
        // Prüfen Sie, ob die Berechtigung bereits erteilt wurde
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Wenn nicht, fordern Sie die Berechtigung an
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                    1);
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Wenn nicht, fordern Sie die Berechtigung an
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }

        if (ContextCompat.checkSelfPermission(gANTED)
        {
            // Wenn nichtetContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            //                != PackageManager.PERMISSION_GR, fordern Sie die Berechtigung an
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    3);
        }   if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.MA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Wenn nicht, fordern Sie die Berechtigung an
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    4);
        }

        */

        save_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    String paht = kamera_dirs.get_dir_from_name(spinner.getSelectedItem().toString());
                    paht+="/KW"+String.valueOf(get_kw(get_date()));

                    int kw = get_kw(get_date());

                    image_stamp_text = projekt.projekt_get_selected_name()+" ["+ projekt.projekt_get_selected_nr()+"]"+ "  " + get_date() + " [KW " + kw + "]";
                    String filename= projekt.projekt_get_selected_name()+"["+ projekt.projekt_get_selected_nr()+"]";

                    if (kamera_switch_tag_onoff.isChecked())
                    {
                        if(kamera_tag_field_value.getVisibility() != View.GONE)
                        {
                            image_stamp_text = projekt.projekt_get_selected_name()+" ["+ projekt.projekt_get_selected_nr()+"]"+ " #"+kamera_tag_field_value.getText().toString()+"  "+ get_date() + " [KW " + kw + "]";
                           filename+= "#"+kamera_tag_field_value.getText().toString();
                        }
                    }
                    filename+="@"+get_date().replace(".","")+"_ID_"+String.valueOf(System.currentTimeMillis());
                    filename+=bsf.detect_extension(currentPhotoPath);
                    create_image();

                    bsf.saveImage(final_image,paht,filename,getContext());

                    camera_photo.setImageResource(0);
                    preview_camera_visibility(View.GONE);
                    spinner.setEnabled(true);
                    camera_and_import_visibility(View.VISIBLE);
                    clean_temp_dir();



                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        kamera_open_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent open_image = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                open_image.addCategory(Intent.CATEGORY_OPENABLE);
                open_image.setType("image/*");
                startActivityForResult(open_image, 3);
            }
        });


        kamera_tag_field_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    refresh_fav_auto_complete();
                }
            }
        });

        config_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config_fav favorites = new config_fav(getContext());
                favorites.show_dialog(container);
                kamera_tag_field_value.clearFocus();
            }
        });

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kamera_tag_field_value.getVisibility() == View.VISIBLE) {
                    tag_visibility(View.GONE);
                } else {
                    tag_visibility(View.VISIBLE);
                }
            }
        });


        //---------------------------------------------------------
        adddir_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_spinner();
            }
        });

        adddir_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        adddir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.kamera_subdir_options_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.kamera_sub_dir_add_new:
                                sub_dir_dialog("add", null, container);
                                break;

                            case R.id.kamera_sub_dir_add_clone:
                                sub_dir_dialog("clone", null, container);
                                break;

                            case R.id.kamera_sub_dir_update:
                                sub_dir_dialog("update", null, container);
                                break;

                            case R.id.kamera_sub_sir_delet:

                                String selectet_item = spinner.getSelectedItem().toString();

                                AlertDialog.Builder confirm_delet = new AlertDialog.Builder(getContext());
                                confirm_delet.setTitle("Bestätigen");

                                confirm_delet.setIcon(R.drawable.alert);
                                confirm_delet.setMessage(selectet_item + " wirklich löschen?");
                                confirm_delet.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            if (kamera_dirs.delet(selectet_item)) {
                                                bsf.succes_msg("Löschen erfolgt!", getContext());
                                            } else {
                                                bsf.error_msg("Löschen fehlgeschlagen!", getContext());
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        refresh_spinner();
                                        dialogInterface.cancel();
                                    }
                                });
                                confirm_delet.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                confirm_delet.show();


                                break;

                            default:
                                Toast.makeText(getContext(), "Nicht Implementiert", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        //---------------------------------------------------------
        take_picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
            }
            /*    // Prüfen Sie, ob die Berechtigung bereits erteilt wurde
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    // Wenn nicht, fordern Sie die Berechtigung an
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                            1);
                } else
                {
                    dispatchTakePictureIntent();
                }

            }*/
        });


        camera_photo.setOnClickListener(new View.OnClickListener()
        {
            int rotate_value=90;
            @Override
            public void onClick(View view)
            {
                View pic_view_UI = getLayoutInflater().inflate(R.layout.show_picture, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                ImageView photo = pic_view_UI.findViewById(R.id.imageView4);
                TextView image_path = pic_view_UI.findViewById(R.id.textView65);
                ImageButton image_roate = pic_view_UI.findViewById(R.id.imageButton62);


                image_roate.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inScaled = false;
                        options.inMutable = true;

                        Bitmap image = BitmapFactory.decodeFile(currentPhotoPath,options);
                        Matrix matrix= new Matrix();
                        matrix.setRotate(rotate_value);
                        Bitmap bmap_rotated = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                        bsf.saveImage_silence(bmap_rotated,projekt.projekt_get_current_root_dir_images_temp(),get_filename(),getContext());
                        create_image();
                        photo.setImageBitmap(final_image);

                    }
                });

                try {

                    photo.setImageBitmap(final_image);
                    image_path.setText(currentPhotoPath.toString());

                } catch (Exception e) {
                    photo.setImageResource(R.drawable.ic_baseline_error_outline_24);
                   // image_path.setText(e.getMessage().toString());
                    image_path.setText(String.valueOf(rotate_value));
                }
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(pic_view_UI);

                alertDialogBuilder.setTitle("Viewer");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String filename= currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/")+1,currentPhotoPath.length());
                        bsf.saveImage_silence(final_image,projekt.projekt_get_current_root_dir_images_temp(),filename,getContext());
                        create_image();

                        dialogInterface.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }


        });

        camera_delet_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getContext(), currentPhotoPath, Toast.LENGTH_SHORT).show();
                Basic_funct bsf = new Basic_funct();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Löschen bestätigen:");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
                alertDialogBuilder.setMessage("Bild entfernen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            File f = new File(currentPhotoPath);
                            if (f.exists()) {
                                f.delete();
                                bsf.succes_msg("Bild gelöscht!\n" + currentPhotoPath, getContext());
                                preview_camera_visibility(View.GONE);
                                camera_photo.setImageResource(0);
                                preview_camera_visibility(View.GONE);
                                camera_and_import_visibility(View.VISIBLE);
                                clean_temp_dir();

                            }
                        } catch (Exception e) {
                            bsf.error_msg("Löschung Fehlgeschlagen!\n" + e.getMessage(), getContext());
                            throw new RuntimeException(e);
                        }
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

        camera_reset_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    reset_complete();



                } catch (Exception e) {
                    exmsg("120220231059", e);
                }
            }
        });


        kamera_switch_tag_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean m = kamera_switch_tag_onoff.isChecked();

                if (kamera_switch_tag_onoff.isChecked()) {
                    tag_background(static_finals.mark_color);
                }

                if (!kamera_switch_tag_onoff.isChecked()) {
                    tag_background(static_finals.un_mark_color);
                }
            }
        });

        kamera_tag_add_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config_fav_ops cfops = new config_fav_ops(getContext());
                cfops.add_favorite_string(kamera_tag_field_value.getText().toString());
                refresh_fav_auto_complete();
            }
        });

        kamera_reset_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kamera_tag_field_value.setText("");
                kamera_switch_tag_onoff.setChecked(false);
            }
        });

        curr_date_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curr_date.setText(bsf.date_refresh());
                date_background(static_finals.un_mark_color);
            }
        });

        curr_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getContext());
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());
                        curr_date.setText(dateString);
                        date_background(static_finals.mark_color);

                    }
                });

                dpd.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
        String date = datumformat.format(calendar.getTime()); // Inflate the layout for this fragment
        curr_date.setText(bsf.date_refresh());
        date_background(static_finals.un_mark_color);

        return view;
    }

    private void reset_complete()
    {
            Basic_funct bsf =new Basic_funct();
            curr_date.setText(bsf.date_refresh());
            tag_background(static_finals.un_mark_color);
            date_background(static_finals.un_mark_color);
            kamera_tag_field_value.setText("");
            camera_photo.setImageResource(0);
            kamera_switch_tag_onoff.setChecked(false);
            preview_camera_visibility(View.GONE);
            tag_visibility(View.GONE);
            spinner.setEnabled(true);
            camera_and_import_visibility(View.VISIBLE);
            clean_temp_dir();
    }

    public void clean_temp_dir()
    {
        Basic_funct bsf =new Basic_funct();
        projekt= new projekt_ops(getContext());
        String paht = projekt.projekt_get_current_root_dir_images_temp();

        try {
            File f = new File(paht);
            if(f.exists())
            {
                String[] files = f.list();
                if(files.length>0)
                {
                    for (String d : files) {
                        File t = new File(paht + "/" + d);
                        t.delete();
                    }
                }
            }
            else
            {
                f.mkdirs();
            }

        } catch (Exception e)
        {
            bsf.error_msg("Verzeichnis 'temp' bereinigung Fehlgeschlagen!\n" + e, getContext());
        }
    }


    private void tag_background(int markColor) {
        tag_bg.setBackgroundColor(ContextCompat.getColor(getContext(), markColor));
    }

    private void date_background(int markColor) {
        date_bg.setBackgroundColor(ContextCompat.getColor(getContext(), markColor));
    }

    private void tag_visibility(int visibility) {
        kamera_tag_field_value.setVisibility(visibility);
        kamera_tag_field_value.setText("");
        kamera_tag_add_fav.setVisibility(visibility);
        kamera_reset_tag.setVisibility(visibility);
        kamera_switch_tag_onoff.setVisibility(visibility);
        kamera_switch_tag_onoff.setChecked(false);
        config_fav.setVisibility(visibility);
    }

    private void preview_camera_visibility(int visibility)
    {
        camera_photo.setVisibility(visibility);
        camera_delet_image.setVisibility(visibility);
        media_label.setVisibility(visibility);
        save_image_visibility(visibility);
    }
    private void camera_and_import_visibility(int visibility)
    {
        take_picture.setVisibility(visibility);
        kamera_open_image.setVisibility(visibility);
    }
    private void save_image_visibility(int visibility)
    {
        save_image.setVisibility(visibility);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == 1) {
            Log.d("BASI bere", String.valueOf(grantResults[0]));
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("BASI bere", String.valueOf(grantResults[0]));

            }

            if (requestCode == 2)
            {
                Log.d("BASI bere", String.valueOf(grantResults[0]));
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("BASI bere", String.valueOf(grantResults[0]));

                }
            }
            if (requestCode == 3)
            {
                Log.d("BASI bere", String.valueOf(grantResults[0]));
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("BASI bere", String.valueOf(grantResults[0]));


                }
            }
            if (requestCode == 4)
            {
                Log.d("BASI bere", String.valueOf(grantResults[0]));
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("BASI bere", String.valueOf(grantResults[0]));

                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:

                String message = String.valueOf(data.getData().getLastPathSegment());
                String[] cut = message.split(":");
                String path_src = Environment.getExternalStorageDirectory() + "/" + cut[1];
                dir.setText(path_src);

                break;

            case 2:

                spinner.setEnabled(false);
                camera_and_import_visibility(View.GONE);
                Basic_funct bsf = new Basic_funct();

                String filename = "";
                String path = "";
                String tags = "";
                try {

                path = photoURI.getPath();  ///BASI/DEFAULT[1]/Bilder/temp/14042024393346163544505652.jpeg
                filename = path.substring(path.lastIndexOf("/")+1,path.length());
                currentPhotoPath = projekt.projekt_get_current_root_dir_images_temp()+"/"+filename;
                Log.d("URL", currentPhotoPath);

                    String projekt_name = projekt.get_selectet_projekt();
                    int kw = get_kw(get_date());
                    if (tags == "") {

                        image_stamp_text = projekt_name + "  " + get_date() + " [KW " + kw + "]";
                    } else {
                        image_stamp_text = projekt_name + "   #" + tags + "    " + get_date() + " [KW " + kw + "]";
                    }

                    //Bitmap erstellen
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    options.inMutable = true;

                    Bitmap bMapScaled = null;

                    Bitmap bMap = BitmapFactory.decodeFile(currentPhotoPath, options);


                    //String url = bsf.saveImage(bMapScaled, path, filename, getContext());


                    //bsf.saveImage(imported_Bitmap_with_stamp,path,filename,getContext());
                    //Vor dem Definitiven speichern drehen und in temp verzeichiss speichern
                    preview_camera_visibility(View.VISIBLE);
                    image_stamp_camera();
                    create_image();

                    //Speichern des Bildes


                    //Neues Bild Anzeigen im imageView
                       /* Bitmap bMap2 = BitmapFactory.decodeFile(url);
                        Bitmap bitmap3;

                        //Maximalgrösse der Ansicht(maximal)  je nach Orientierung
                        int bitmap_dim = 900;

                        if (rotation == 6) {//hochkannt
                            bitmap3 = Bitmap.createScaledBitmap(bMap2, bitmap_dim / 2, bitmap_dim, true);
                        } else {
                            bitmap3 = Bitmap.createScaledBitmap(bMap2, bitmap_dim, bitmap_dim / 2, true);
                        }

                        camera_photo.setImageBitmap(bitmap3); // Im imageView Anzeigen*/

                } catch (Exception e)
        {
                    exmsg("120220231031A", e);
                    bsf.error_msg("Bild wurde verworfen\n" + e.getMessage(), getContext());
                    camera_photo.setImageResource(0);
                    try {
                        File f = new File(path + filename);
                        f.delete();

                    } catch (Exception ex) {
                        exmsg("120220231031B", e);
                        ex.printStackTrace();
                    }

                }
                break;

            case 3:
                spinner.setEnabled(false);
                camera_and_import_visibility(View.GONE);
                bsf = new Basic_funct();
                String source_path= bsf.get_absolute_paht(data.getData().getLastPathSegment());

                Log.d("BASI", source_path);

                filename =String.valueOf(System.currentTimeMillis())+bsf.detect_extension(source_path);

                image_stamp_text ="";
                image_stamp_text += projekt.projekt_get_selected_name();
                image_stamp_text += " ["+projekt.projekt_get_selected_nr()+"]";
                image_stamp_text+=" #"+get_tag()+" ";
                image_stamp_text+= get_date() ;
                image_stamp_text +=" [KW "+String.valueOf(get_kw(get_date())+"]");

                Log.d("BASI", filename);
                Basic_func_img bsfi = new Basic_func_img();
                bsf.copyBitmap_to(source_path,projekt.projekt_get_current_root_dir_images_temp(),filename);
                currentPhotoPath = projekt.projekt_get_current_root_dir_images_temp()+"/"+filename;
                Log.d("BASI", currentPhotoPath);
                preview_camera_visibility(View.VISIBLE);
                image_stamp_import();
                create_image();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }

    }

    private int get_kw(String date)
    {
        Calendar calendar = Calendar.getInstance();

        String []date_parts = date.split("[.]");

        int day = Integer.parseInt(date_parts[0]);
        int month = Integer.parseInt(date_parts[1]) - 1;
        int year = Integer.parseInt(date_parts[2]);

        calendar.set(year, month, day);

        int kw = calendar.get(Calendar.WEEK_OF_YEAR);
        return  kw;

    }



   /* public void refresh_spinner()
    {
        String []dir_names = kamera_dirs.get_dir_names_as_array(projekt.projekt_get_selected_id());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,dir_names);
        kamera_dirs.spinner.setAdapter(spinnerArrayAdapter);
    }*/

    public void sub_dir_dialog(String mode, String value, ViewGroup container) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.diradd, container, false);

        name = promptsView.findViewById(R.id.editTextTextPersonName10);
        dir = promptsView.findViewById(R.id.editTextTextPersonName11);
        final ImageButton paht = promptsView.findViewById(R.id.imageButton2);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        switch (mode) {
            case "add":

                alertDialogBuilder.setTitle(R.string.add_title_dir_name);

                paht.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 1);
                    }
                });


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Basic_funct bsf = new Basic_funct();
                            String name_value = bsf.URLencode(name.getText().toString());
                            String paht_value = bsf.URLencode(dir.getText().toString());

                            kamera_dirs.add(name_value, paht_value);
                            refresh_spinner();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "A:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                break;


            case "clone":

                alertDialogBuilder.setTitle(R.string.add_sub_dir_title_dir_name);

                name.setText(spinner.getSelectedItem().toString() + "> ");

                paht.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 1);
                    }
                });


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String paht_value =dir.getText().toString();
                            String name_value =name.getText().toString();

                            Basic_funct bsf = new Basic_funct();

                            if(paht_value.isEmpty())
                            {
                                paht_value =kamera_dirs.get_dir_from_name(spinner.getSelectedItem().toString())+"/"+name_value.substring(name_value.lastIndexOf(">")+1,name_value.length());
                            }

                           kamera_dirs.add(bsf.URLencode(name_value), bsf.URLencode(paht_value));

                            refresh_spinner();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "A:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                break;


            case "update":

                alertDialogBuilder.setTitle(R.string.modify_title_dir_name);

                String name_old = spinner.getSelectedItem().toString();
                //Leere DIR einträge überbrücken !
                name.setText(name_old);
                try {
                    dir.setText(kamera_dirs.get_dir_from_name(spinner.getSelectedItem().toString()));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                paht.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 1);
                    }
                });

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Werte zum Update
                        ///String name_alt= items[0]; //Alter name , falls geändert
                        String name_new = String.valueOf(name.getText());
                        String dir_new = String.valueOf(dir.getText());

                        try {
                            kamera_dirs.update(name_old, name_new, dir_new);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        // Toast.makeText(getContext(), "Es wurden "+ String.valueOf(responde)+" Einträge geändert.",Toast.LENGTH_SHORT).show();
                        refresh_spinner();

                    }
                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });

                break;

            default:
                break;
        }
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }


    private void select_path(ViewGroup container) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView_group = li.inflate(R.layout.kamera_select_dialog, container, false);
        AlertDialog.Builder alertDialogBuilder_group = new AlertDialog.Builder(getContext());
        alertDialogBuilder_group.setView(promptsView_group);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder_group.create();
        // show it
        alertDialog.show();
    }

    private void dispatchTakePictureIntent()
    {
        String selected_item = spinner.getSelectedItem().toString();

       // date = get_date();
        String date_filename = get_date().replace(".", "");
        String paht = projekt.projekt_get_current_root_dir();
        try {
            paht = kamera_dirs.get_dir_from_name(selected_item);

            if (!paht.equals(""))
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                // Create the File where the photo should go
                File photoFile = null;

                String projekt_name = projekt.get_selectet_projekt();

                if (kamera_switch_tag_onoff.isChecked())
                {
                    photoFile = createImageFile(projekt.projekt_get_current_root_dir_images_temp(), projekt_name,true, kamera_tag_field_value.getText().toString(), date_filename);

                } else
                {
                    photoFile = createImageFile(projekt.projekt_get_current_root_dir_images_temp(), projekt_name, false, "", date_filename);
                }


                // Continue only if the File was successfully created
                if (photoFile != null) {


                    try {
                        photoURI = FileProvider.getUriForFile(getContext(), "com.example.tabnav_test.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            else
            {
                AlertDialog.Builder alert_empty_path = new AlertDialog.Builder(getContext());
                alert_empty_path.setTitle("Error");
                alert_empty_path.setIcon(R.drawable.alert);

                alert_empty_path.setMessage("Kein Speicherpfad vorhanden!");
                alert_empty_path.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert_empty_path.show();
            }

        } catch (JSONException e)
        {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File createImageFile(String path, String title, boolean tag_on, String tag, String date) throws IOException
    {
        Basic_funct bsf = new Basic_funct();

        String addings = date + "_ID_";

        title = title.replace(">","_").replace(" ","");

        String imageFileName = get_date().replace(".","");

        if (tag_on) {
            imageFileName = title + "#" + tag + "@" + addings;

        }
        File storageDir = new File(path);
        if(storageDir.exists()==false)
        {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String get_date()
    {
      return curr_date.getText().toString();
    }
    private String get_tag()
    {
        if(kamera_switch_tag_onoff.isChecked())
        {
            return "#"+kamera_tag_field_value.getText().toString();
        }
        else
        {
            return "";
        }
    }
    public void refresh_spinner() {
        try {
            String[] dir_name = kamera_dirs.get_dir_names_as_array(projekt.projekt_get_selected_id());
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, dir_name);
            spinner.setAdapter(spinnerArrayAdapter);

        } catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void create_image()
    {


        Basic_func_img bsfi = new Basic_func_img();
        Bitmap image_whit_stamp = null;
        try {
            image_whit_stamp = bsfi.makeBitmap_textstamp(currentPhotoPath,image_stamp_text,stamp_bg_color,stamp_txt_color);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final_image = image_whit_stamp;
        camera_photo.setImageBitmap(bsfi.Bitmap_setScaling(final_image,900));

    }
    private void image_stamp_camera()
    {
        stamp_bg_color =getResources().getColor(R.color.yellow);
        stamp_txt_color= Color.BLACK;

    }
    private void image_stamp_import()
    {
        stamp_bg_color =getResources().getColor(R.color.orange);
        stamp_txt_color= Color.BLACK;
    }

    private void style_stamp_reset()
    {
        set_stamp_bgcolor(-1);
        set_stamp_txt_color(-1);
    }
    private void set_stamp_bgcolor(int color)
    {
        if(color==-1)
        {
            stamp_bg_color =Color.WHITE;
        }
        else
        {
            stamp_bg_color  =color;
        }

    }
    private void set_stamp_txt_color(int color)
    {
        if(color ==-1)
        {
            stamp_txt_color = Color.MAGENTA;
        }
        else
        {
            stamp_txt_color  =color;
        }
    }

    private String get_filename()
    {

        String filename= "noname_"+System.currentTimeMillis()+".jpeg";
        try {
            filename = currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/")+1,currentPhotoPath.length());
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return filename;
    }

    private void refresh_fav_auto_complete()
    {
        try {
            config_fav_ops cfop = new config_fav_ops(getContext());
            ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cfop.favorite_strings_list(false));
            kamera_tag_field_value.setAdapter(favArrayAdapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void exmsg(String msg, Exception e) {
        Log.e("Exception: Kamera ->", "ID: " + msg + " Message:" + e.getMessage());
        e.printStackTrace();
    }

    public static class kamera_directorys extends Kamera {

        projekt_ops.kamera_dir directory;
        Context context;

        public kamera_directorys(Context context) {
            this.context = context;
            directory = new projekt_ops.kamera_dir(this.context);
        }

        public void add(String name, String dir) throws JSONException
        {

            String json = directory.get_dir(directory.projekt_get_selected_id());
            JSONArray data_array;
            if (json.isEmpty())
            {
                data_array = new JSONArray();
                JSONObject obj = new JSONObject();

                try {
                    obj.put("DIR", dir);
                    obj.put("NAME", name);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                data_array.put(obj);
            } else
            {
                Basic_funct bsf  = new Basic_funct();
                if(!kamera_subdir_exist(name))
                {
                    data_array = new JSONArray(json);
                    JSONObject obj = new JSONObject();
                    obj.put("NAME", name);
                    obj.put("DIR", dir);
                    data_array.put(obj);
                    if(directory.set_dir(directory.projekt_get_selected_id(), data_array.toString()))
                    {
                        bsf.succes_msg("Verzeichnis "+name+" angelegt!",context);
                    }
                }
                else
                {

                    bsf.error_msg("Vezeichns "+name+" existiert schon!",context);
                }
            }
        }

        public boolean kamera_subdir_exist(String name)
        {
            Boolean output = false;
            try {
                String []  existed_dir_name = get_dir_names_as_array(directory.projekt_get_selected_id());
                for(String item:existed_dir_name)
                {
                    if (item.contains(name)) {
                        output = true;
                        break;
                    }
                }

            } catch (JSONException e)
            {

                throw new RuntimeException(e);
            }
            return  output;
        }


        public void update(String name_old, String name_new, String path_new) throws JSONException
        {
            Basic_funct bsf = new Basic_funct();

            if(!kamera_subdir_exist(name_new) || name_new.equals(name_old))
            {
                String json = directory.get_dir(directory.projekt_get_selected_id());

                JSONArray dirlist_new = new JSONArray();

                if (!json.equals(""))
                {
                    JSONArray dirlist = new JSONArray(json);
                    if (dirlist.length() != 0)
                    {
                        for (int c = 0; c < dirlist.length(); c++) {
                            JSONObject obj = new JSONObject(dirlist.get(c).toString());
                            String item = obj.getString("NAME");

                            if (item.equals(bsf.URLencode(name_old))) {
                                JSONObject obj_new = new JSONObject();
                                obj_new.put("NAME", bsf.URLencode(name_new));
                                obj_new.put("DIR", bsf.URLencode(path_new));
                                dirlist_new.put(obj_new);
                            } else {
                                JSONObject obj_new = new JSONObject();
                                obj_new.put("NAME", obj.getString("NAME"));
                                obj_new.put("DIR", obj.getString("DIR"));
                                dirlist_new.put(obj_new);
                            }
                        }
                    }
                }
                directory.set_dir(directory.projekt_get_selected_id(), dirlist_new.toString());
            }
            else
            {
                Toast.makeText(context, "Error: Überschneidungen mit bestehenden Einträgen!", Toast.LENGTH_SHORT).show();
            }
        }

        public boolean delet(String name) throws JSONException
        {
            Boolean resonpnse= false;
            Basic_funct bsf = new Basic_funct();
            String json = directory.get_dir(directory.projekt_get_selected_id());


            JSONArray dirlist_new = new JSONArray();

            if (!json.equals(""))
            {
                JSONArray dirlist = new JSONArray(json);
                if (dirlist.length() != 0)
                {
                    for (int c = 0; c < dirlist.length(); c++)
                    {
                        JSONObject obj = new JSONObject(dirlist.get(c).toString());
                        String item = obj.getString("NAME");

                        if (!item.equals(bsf.URLencode(name)))
                        {
                            JSONObject obj_new = new JSONObject();
                            obj_new.put("NAME", obj.getString("NAME"));
                            obj_new.put("DIR", obj.getString("DIR"));
                            dirlist_new.put(obj_new);
                        }
                        else
                        {
                            resonpnse =true;
                        }
                    }
                }
            }

            directory.set_dir(directory.projekt_get_selected_id(), dirlist_new.toString());
            return resonpnse;

        }

        public String get_dir_from_name(String name) throws JSONException
        {
            Basic_funct bsf = new Basic_funct();
            String json = directory.get_dir(directory.projekt_get_selected_id());
            String paht = "";

            if (!json.equals("")) {
                JSONArray dirlist = new JSONArray(json);

                if (dirlist.length() != 0) {
                    for (int c = 0; c < dirlist.length(); c++) {
                        JSONObject obj = new JSONObject(dirlist.get(c).toString());
                        String item = obj.getString("NAME");

                        if (item.equals(bsf.URLencode(name))) {
                            paht = bsf.URLdecode(obj.getString("DIR"));
                        }
                    }
                }
            }

            return paht;
        }

        public String[] get_dir_names_as_array(String projekt_id) throws JSONException {
            String json = directory.get_dir(projekt_id);
            Basic_funct bsf = new Basic_funct();

          if(!json.equals(""))
           {
               JSONArray dirlist =new JSONArray(json);
               String [] output = new String[dirlist.length()];

               if(dirlist.length() !=0)
               {
                   for(int c= 0; c< dirlist.length();c++)
                   {
                       JSONObject obj = new JSONObject(dirlist.get(c).toString());
                       output[c] = bsf.URLdecode(obj.getString("NAME"));
                   }
                   sort(output);
                   return  output;
               }
               else
               {
                  return  new String[]{"DEFAULT"};
               }
           }
           else
           {
               return  new String[]{"DEFAULT"};
           }



        }

    }
}



