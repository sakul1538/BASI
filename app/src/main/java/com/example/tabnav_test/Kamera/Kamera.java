package com.example.tabnav_test.Kamera;

import static java.util.Arrays.sort;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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
import java.util.zip.Inflater;


public class Kamera<onActivityResult> extends Fragment {
    // FIXME: 10.01.23  Kamera stürzt ab, wenn "Tag" aktiviert aber Text feld Leer

    // ----------------------------------------------------------------- Variablen
    Uri photoURI = null;
    private Intent data;
    // ----------------------------------------------------------------- Variablen String, char
    String currentPhotoPath;
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
        refresh_spinner();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
        curr_date = (TextView) view.findViewById(R.id.kamera_date);
        tag = (TextView) view.findViewById(R.id.textView32);
        media_label = (TextView) view.findViewById(R.id.textView66);
        save_paht_set = (TextView) view.findViewById(R.id.textView4);

        // ----------------------------------------------------------------- AutoCompleteTextView
        kamera_tag_field_value = (AutoCompleteTextView) view.findViewById(R.id.kamera_tag_field_value);
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        kamera_reset_tag = (ImageButton) view.findViewById(R.id.kamera_reset_tag_button);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        curr_date_refresh_button = (ImageButton) view.findViewById(R.id.kamera_date_refresh_button);
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
        kamera_switch_tag_onoff = (Switch) view.findViewById(R.id.kamera_switch_tag_onoff);
        // ----------------------------------------------------------------- SeekBar
        // ----------------------------------------------------------------- ProgressBar
        // ----------------------------------------------------------------- Switch
        // ----------------------------------------------------------------- ScrollView
        // ----------------------------------------------------------------- Layouts
        tag_bg = (LinearLayout) view.findViewById(R.id.tag_background);
        date_bg = (LinearLayout) view.findViewById(R.id.date_background);
        // ----------------------------------------------------------------- END

        //Init
        refresh_spinner();

        tag_visibility(View.GONE);
        preview_camera_visibility(View.GONE);


        kamera_tag_field_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b == true) {
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
                                            kamera_dirs.delet(selectet_item);
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

       /* adddir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //dir_dialog("add",null,container);
            }
        });*/

        //---------------------------------------------------------


        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                String selected_item = spinner.getSelectedItem().toString();
                String date = curr_date.getText().toString();
                date = date.replace(".", "");
                String paht=projekt.projekt_get_current_root_dir();
                try {
                    paht =kamera_dirs.get_dir_from_name(selected_item);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (kamera_switch_tag_onoff.isChecked() == true)
                {
                    dispatchTakePictureIntent(paht, selected_item, true, kamera_tag_field_value.getText().toString(), date);

                } else
                    dispatchTakePictureIntent(paht, selected_item, false, "", date); //Path
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
                    curr_date.setText(bsf.date_refresh());
                    tag_background(static_finals.un_mark_color);
                    date_background(static_finals.un_mark_color);
                    kamera_dirs.spinner.setSelection(0);
                    kamera_tag_field_value.setText("");
                    camera_photo.setImageResource(0);
                    kamera_switch_tag_onoff.setChecked(false);
                    preview_camera_visibility(View.GONE);
                    tag_visibility(View.GONE);

                } catch (Exception e) {
                    exmsg("120220231059", e);
                }
            }
        });


        kamera_switch_tag_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean m = kamera_switch_tag_onoff.isChecked();

                if (kamera_switch_tag_onoff.isChecked() == true) {
                    tag_background(static_finals.mark_color);
                }

                if (kamera_switch_tag_onoff.isChecked() == false) {
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

    private void preview_camera_visibility(int visibility) {
        camera_photo.setVisibility(visibility);
        camera_delet_image.setVisibility(visibility);
        media_label.setVisibility(visibility);
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
                String path_src  =Environment.getExternalStorageDirectory()+"/"+cut[1];
                dir.setText(path_src);

                break;

            case 2:
                preview_camera_visibility(View.VISIBLE);
                Basic_funct bsf = new Basic_funct();

                String filename = "";
                String path = "";
                String photostamp = "";
                String tags = "";
                String save_dir = "";
                String datum = "";

                //Temporäre Arrays
                String[] t_array = null;
                String[] t_array2 = null;

                path = photoURI.getPath();  //path:  /primary/DCIM/Test/Test@28122022_ID_566429213554924951.jpeg
                Log.d("URL", path);

                try {
                    path = photoURI.getPath();  //path:  /primary/DCIM/Test/Test@28122022_ID_566429213554924951.jpeg

                    path = path.replace("/primary/", ""); //primary entfernen

                    //Dateinamen extrahieren.
                    t_array = path.split("/");  // DCIM/Test/Test@28122022_ID_566429213554924951.jpeg
                    filename = t_array[t_array.length - 1];           //Test@28122022_ID_566429213554924951.jpeg


                    //Absoluten Pfad der datei in Path speichern.
                    path = path.replace(filename, ""); //Test@28122022_ID_566429213554924951.jpeg entfernen aus den Path
                    path = Environment.getExternalStorageDirectory() + "/" + path;

                    //Photostamp erstellen : Test@28122022_ID_566429213554924951.jpeg

                    t_array = filename.split("@");

                    //Tag
                    if (t_array[0].contains("#") == true) {
                        t_array2 = t_array[0].split("#");
                        save_dir = t_array2[0]; //Test
                        tags = t_array2[1]; // #Test

                    } else {
                        save_dir = t_array[0];
                        tags = "";
                    }

                    //Datum extrahieren  28122022_ID_566429213554924951.jpeg

                    //datum.substring(0,2)+"."+datum.substring(2,4)+"."+datum.substring(4,8);

                    String tag = t_array[1].substring(0, 2);
                    String monat = t_array[1].substring(2, 4);
                    String jahr = t_array[1].substring(4, 8);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
                    String date = datumformat.format(calendar.getTime());

                    int kw = calendar.get(Calendar.WEEK_OF_YEAR);

                    datum = tag + "." + monat + "." + jahr;

                    if (datum.contains(date) == true) {
                        datum += " " + bsf.time_refresh();
                    }

                    String projekt_name= projekt.get_selectet_projekt();

                    if (tags == "")
                    {

                        photostamp = projekt_name+": "+ save_dir + "  " + datum + " [KW" + String.valueOf(kw) + "]";
                    } else {
                        photostamp =projekt_name+": "+ save_dir + "  #" + tags + "    " + datum + " [KW" + String.valueOf(kw) + "]";
                    }


                    //Bitmap erstellen
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    options.inMutable = true;

                    Bitmap bMapScaled = null;

                    Bitmap bMap = BitmapFactory.decodeFile(path + filename, options);

                    try {

                        ExifInterface exif = new ExifInterface(path + filename);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                        int width = exif.getAttributeInt(ExifInterface.TAG_PIXEL_X_DIMENSION, 0);
                        int height = exif.getAttributeInt(ExifInterface.TAG_PIXEL_Y_DIMENSION, 0);

                        if (width == 0 || height == 0) {
                            width = bMap.getWidth();
                            height = bMap.getHeight();
                        }
                        Matrix matrix = new Matrix();

                        //Fixme 04.12.2023 Stndartwerte für width/height,falls keine exif daten vornhanden sind = Erzeugt sonst gelegentlich eine Exception!

                        switch (rotation) {
                            case 3:

                                matrix.setRotate(180);
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0, width, height, matrix, true);

                                break;

                            case 6:

                                matrix.setRotate(90);
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0, width, height, matrix, true);
                                break;

                            default:
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0, width, height, matrix, true);
                        }

                        Rect rc = new Rect(0, bMapScaled.getHeight() - 100, bMapScaled.getWidth(), bMapScaled.getHeight());

                        Paint paintrect = new Paint();
                        paintrect.setStyle(Paint.Style.FILL);
                        paintrect.setColor(Color.rgb(255, 255, 255));

                        Paint paint = new Paint();
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.rgb(225, 20, 225));
                        paint.setTextSize(50);

                        Canvas canvas = new Canvas(bMapScaled);
                        canvas.drawRect(rc, paintrect);
                        canvas.drawText(photostamp, 30, bMapScaled.getHeight() - 30, paint);


                        //Speichern des Bildes
                        String url = bsf.saveImage(bMapScaled, path, filename, getContext());

                        //Neues Bild Anzeigen im imageView
                        Bitmap bMap2 = BitmapFactory.decodeFile(url);
                        Bitmap bitmap3;

                        //Maximalgrösse der Ansicht(maximal)  je nach Orientierung
                        int bitmap_dim = 900;

                        switch (rotation) {
                            case 6:
                                //hochkannt
                                bitmap3 = Bitmap.createScaledBitmap(bMap2, bitmap_dim / 2, bitmap_dim, true);

                                break;

                            default:
                                bitmap3 = Bitmap.createScaledBitmap(bMap2, bitmap_dim, bitmap_dim / 2, true);

                        }

                        camera_photo.setImageBitmap(bitmap3); // Im imageView Anzeigen

                        camera_photo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                View pic_view_UI = getLayoutInflater().inflate(R.layout.show_picture, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                                ImageView photo = (ImageView) pic_view_UI.findViewById(R.id.imageView4);
                                photo.setImageBitmap(BitmapFactory.decodeFile(url));

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(pic_view_UI);

                                alertDialogBuilder.setTitle("Viewer");


                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();


                            }
                        });

                    } catch (IOException e) {
                        exmsg("120220231030", e);
                    }
                } catch (Exception e) {
                    exmsg("120220231031A", e);
                    bsf.error_msg("Bild wurde verworfen", getContext());
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
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }

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

        name = (EditText) promptsView.findViewById(R.id.editTextTextPersonName10);
        dir = (EditText) promptsView.findViewById(R.id.editTextTextPersonName11);
        final ImageButton paht = (ImageButton) promptsView.findViewById(R.id.imageButton2);
        ;


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
                            Toast.makeText(getContext(), "A:" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
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
                            Basic_funct bsf = new Basic_funct();
                            String name_value = bsf.URLencode(name.getText().toString());
                            String paht_value = bsf.URLencode(dir.getText().toString());

                            kamera_dirs.add(name_value, paht_value);
                            refresh_spinner();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "A:" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    private void dispatchTakePictureIntent(String path, String title, boolean tag_on, String tag, String date) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;
        try {

            photoFile = createImageFile(path, title, tag_on, tag, date);
        } catch (IOException ex) {
            // Error occurred while creating the File

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


    private File createImageFile2() throws IOException {

        String abs_path = "/storage/emulated/0/DCIM/";
        Basic_funct bsf = new Basic_funct();

        File storageDir = new File(abs_path);
        File image = File.createTempFile(
                "BASI_temp",  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }


    private File createImageFile(String path, String title, boolean tag_on, String tag, String date) throws IOException {

        Basic_funct bsf = new Basic_funct();


        String addings = date + "_ID_";
        String imageFileName = title + "@" + addings;

        if (tag_on == true) {
            imageFileName = title + "#" + tag + "@" + addings;

        }
        File storageDir = new File(path);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void refresh_spinner() {

        try {
            String[] dir_name = kamera_dirs.get_dir_names_as_array(projekt.projekt_get_selected_id());
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, dir_name);
            spinner.setAdapter(spinnerArrayAdapter);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void refresh_fav_auto_complete() {
        config_fav_ops cfop = new config_fav_ops(getContext());
        ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cfop.favorite_strings_list(false));
        kamera_tag_field_value.setAdapter(favArrayAdapter);

    }

    private void exmsg(String msg, Exception e) {
        Log.e("Exception: Kamera ->", "ID: " + msg + " Message:" + e.getMessage().toString());
        e.printStackTrace();
    }

    public static class kamera_directorys extends Kamera {

        projekt_ops.kamera_dir directory;
        Context context;

        public kamera_directorys(Context context) {
            this.context = context;
            directory = new projekt_ops.kamera_dir(this.context);

        }

        public void add(String name, String dir) throws JSONException {

            String json = directory.get_dir(directory.projekt_get_selected_id());
            JSONArray data_array;
            if (json.isEmpty()) {
                data_array = new JSONArray();
                JSONObject obj = new JSONObject();

                try {
                    obj.put("DIR", dir);
                    obj.put("NAME", name);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                data_array.put(obj);
            } else {
                data_array = new JSONArray(json);
                JSONObject obj = new JSONObject();
                obj.put("NAME", name);
                obj.put("DIR", dir);
                data_array.put(obj);
            }

            directory.set_dir(directory.projekt_get_selected_id(), data_array.toString());
        }


        public void update(String name_old, String name_new, String path_new) throws JSONException {
            Basic_funct bsf = new Basic_funct();
            String json = directory.get_dir(directory.projekt_get_selected_id());

            JSONArray dirlist_new = new JSONArray();

            if (json.equals("") == false) {
                JSONArray dirlist = new JSONArray(json);
                if (dirlist.length() != 0) {
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

        public void delet(String name) throws JSONException {
            Basic_funct bsf = new Basic_funct();
            String json = directory.get_dir(directory.projekt_get_selected_id());

            JSONArray dirlist_new = new JSONArray();

            if (json.equals("") == false) {
                JSONArray dirlist = new JSONArray(json);
                if (dirlist.length() != 0) {
                    for (int c = 0; c < dirlist.length(); c++) {
                        JSONObject obj = new JSONObject(dirlist.get(c).toString());
                        String item = obj.getString("NAME");

                        if (item.equals(bsf.URLencode(name)) == false) {
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

        public String get_dir_from_name(String name) throws JSONException {
            Basic_funct bsf = new Basic_funct();
            String json = directory.get_dir(directory.projekt_get_selected_id());
            String paht = "";

            if (json.equals("") == false) {
                JSONArray dirlist = new JSONArray(json);

                if (dirlist.length() != 0) {
                    for (int c = 0; c < dirlist.length(); c++) {
                        JSONObject obj = new JSONObject(dirlist.get(c).toString());
                        String item = obj.getString("NAME");

                        if (item.equals(bsf.URLencode(name))) {
                            paht = bsf.URLdecode(obj.getString("DIR").toString());
                        }
                    }
                }
            }

            return paht;
        }

        public String[] get_dir_names_as_array(String projekt_id) throws JSONException {
            String json = directory.get_dir(projekt_id);
            Basic_funct bsf = new Basic_funct();

          if(json.equals("")==false)
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



