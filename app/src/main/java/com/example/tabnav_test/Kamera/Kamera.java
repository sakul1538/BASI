package com.example.tabnav_test.Kamera;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.config_favorite_strings.config_fav;
import com.example.tabnav_test.config_favorite_strings.config_fav_ops;
import com.example.tabnav_test.db_ops;
import com.example.tabnav_test.static_finals;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class Kamera<onActivityResult> extends Fragment
{
    // FIXME: 10.01.23  Kamera stürzt ab, wenn "Tag" aktiviert aber Text feld Leer

    // ----------------------------------------------------------------- Variablen
    Uri photoURI=null;
    private Intent data;
    // ----------------------------------------------------------------- Variablen String, char
    String currentPhotoPath;
    static final String RROJ_NR="0";
    // ----------------------------------------------------------------- Variablen byte,short,int,long,float,double
    private int requestCode;
    private int resultCode;
    // ----------------------------------------------------------------- Variablen Boolean
    // ----------------------------------------------------------------- Instanzen
    // ----------------------------------------------------------------- TextView
    TextView curr_date;
    TextView tag;
    TextView media_label;
    TextView save_paht_set;

    // ----------------------------------------------------------------- AutoCompleteTextView
    AutoCompleteTextView kamera_tag_field_value;
    // ----------------------------------------------------------------- EditText
    EditText name= null;
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
    ImageView camera_photo  = null;
    ImageButton camera_reset_form  = null;
    ImageButton camera_delet_image  = null;
    ImageButton config_fav  = null;

    // ----------------------------------------------------------------- ImageView
    // ----------------------------------------------------------------- ListView
    // ----------------------------------------------------------------- RecyclerView
    // ----------------------------------------------------------------- Spinner
    Spinner spinner;
    kamera_spinner spinnerops;
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

    public Kamera()
    {
        // Required empty public constructor
    }

    public static Kamera newInstance(String param1, String param2)
    {
        Kamera fragment = new Kamera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refresh_fav_auto_complete();
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_kamera, container, false);


        // ----------------------------------------------------------------- Variablen

        // ----------------------------------------------------------------- Variablen  String, char
        // ----------------------------------------------------------------- Variablen 	byte,short,int,long,float,double
        // ----------------------------------------------------------------- Variablen 	Boolean
        // ----------------------------------------------------------------- Instanzen
        Basic_funct bsf =new  Basic_funct();
        spinnerops = new kamera_spinner(getContext());

        // ----------------------------------------------------------------- TextView
        curr_date =(TextView) view.findViewById(R.id.kamera_date);
        tag =(TextView) view.findViewById(R.id.textView32);
        media_label =(TextView) view.findViewById(R.id.textView66);
        save_paht_set =(TextView) view.findViewById(R.id.textView4);

        // ----------------------------------------------------------------- AutoCompleteTextView
        kamera_tag_field_value = (AutoCompleteTextView) view.findViewById(R.id.kamera_tag_field_value);
        // ----------------------------------------------------------------- EditText
        // ----------------------------------------------------------------- Button
        // ----------------------------------------------------------------- ImageButtons
        kamera_reset_tag = (ImageButton) view.findViewById(R.id.kamera_reset_tag_button);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        kamera_tag_add_fav = (ImageButton) view.findViewById(R.id.tag_add_to_fav);
        curr_date_refresh_button =(ImageButton) view.findViewById(R.id.kamera_date_refresh_button);
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

        kamera_tag_field_value.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b==true)
                {
                    refresh_fav_auto_complete();
                }
            }
        });

        config_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                config_fav favorites = new config_fav(getContext());
                favorites.show_dialog(container);
                kamera_tag_field_value.clearFocus();
            }
        });

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(kamera_tag_field_value.getVisibility() == View.VISIBLE)
                {
                    tag_visibility(View.GONE);
                }
                else
                {
                    tag_visibility(View.VISIBLE);
                }
            }
        });


        //---------------------------------------------------------
        adddir_modify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dir_dialog("modify",String.valueOf(spinner.getSelectedItem()),container);
            }
        });

        adddir_delet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int responde =spinnerops.deletOne(RROJ_NR,String.valueOf(spinner.getSelectedItem()));
                String message ="Es wurden "+String.valueOf(responde)+" Einträge gelöscht!";

                Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                refresh_spinner();
            }
        });

        adddir.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                dir_dialog("add",null,container);
            }
        });

        //---------------------------------------------------------


        take_picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               String[] responde = spinnerops.getOne(RROJ_NR, String.valueOf(spinner.getSelectedItem()));

               String date = curr_date.getText().toString();
                date = date.replace(".", "");
                if (kamera_switch_tag_onoff.isChecked() == true)
                {
                    dispatchTakePictureIntent(responde[1], responde[0], true, kamera_tag_field_value.getText().toString(), date);

                } else
                    dispatchTakePictureIntent(responde[1], responde[0], false, "", date); //Path
            }
        });

        camera_delet_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               // Toast.makeText(getContext(), currentPhotoPath, Toast.LENGTH_SHORT).show();
                Basic_funct bsf = new Basic_funct();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Löschen bestätigen:");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
                alertDialogBuilder.setMessage("Bild entfernen?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try {
                            File f = new File(currentPhotoPath);
                            if(f.exists())
                            {
                                f.delete();
                                bsf.succes_msg("Bild gelöscht!\n"+currentPhotoPath,getContext());
                                preview_camera_visibility(View.GONE);
                            }
                        } catch (Exception e)
                        {
                            bsf.error_msg("Löschung Fehlgeschlagen!\n"+e.getMessage(),getContext());
                            throw new RuntimeException(e);
                        }
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

        camera_reset_form.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                try {
                    curr_date.setText(bsf.date_refresh());
                    tag_background(static_finals.un_mark_color);
                    date_background(static_finals.un_mark_color);
                    spinner.setSelection(0);
                    kamera_tag_field_value.setText("");
                    camera_photo.setImageResource(0);
                    kamera_switch_tag_onoff.setChecked(false);
                    preview_camera_visibility(View.GONE);
                    tag_visibility(View.GONE);

                } catch (Exception e)
                {
                    exmsg("120220231059",e);
                }
            }
        });


        kamera_switch_tag_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                boolean m = kamera_switch_tag_onoff.isChecked();

                if (kamera_switch_tag_onoff.isChecked() == true)
                {
                    tag_background(static_finals.mark_color);
                }

                if (kamera_switch_tag_onoff.isChecked() == false)
                {
                    tag_background(static_finals.un_mark_color);
                }
            }
        });

        kamera_tag_add_fav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                config_fav_ops cfops = new config_fav_ops(getContext());
                cfops.add_favorite_string( kamera_tag_field_value.getText().toString());
                refresh_fav_auto_complete();
            }
        });

        kamera_reset_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                kamera_tag_field_value.setText("");
                kamera_switch_tag_onoff.setChecked(false);
            }
        });

        curr_date_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                curr_date.setText(bsf.date_refresh());
                date_background(static_finals.un_mark_color);
            }
        });

        curr_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog dpd = new DatePickerDialog(getContext());
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1 , i2);
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

    private void tag_background(int markColor)
    {
        tag_bg.setBackgroundColor(ContextCompat.getColor(getContext(),markColor));
    }

    private void date_background(int markColor)
    {
        date_bg.setBackgroundColor(ContextCompat.getColor(getContext(),markColor));
    }

    private void tag_visibility(int visibility)
    {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 1:

                    String message=String.valueOf(data.getData().getLastPathSegment());
                    dir.setText(message);

                break;

            case 2:
                preview_camera_visibility(View.VISIBLE);
                Basic_funct bsf  = new Basic_funct();

                String filename ="";
                String path="";
                String photostamp ="";
                String tags = "";
                String save_dir="";
                String datum="";

                //Temporäre Arrays
                String[] t_array = null;
                String[] t_array2 =null;

                path = photoURI.getPath();  //path:  /primary/DCIM/Test/Test@28122022_ID_566429213554924951.jpeg
                Log.d("URL",path);

                try {
                    path = photoURI.getPath();  //path:  /primary/DCIM/Test/Test@28122022_ID_566429213554924951.jpeg

                    path = path.replace("/primary/",""); //primary entfernen

                    //Dateinamen extrahieren.
                    t_array= path.split("/");  // DCIM/Test/Test@28122022_ID_566429213554924951.jpeg
                    filename = t_array[t_array.length-1];           //Test@28122022_ID_566429213554924951.jpeg


                    //Absoluten Pfad der datei in Path speichern.
                    path = path.replace(filename,""); //Test@28122022_ID_566429213554924951.jpeg entfernen aus den Path
                    path = Environment.getExternalStorageDirectory()+"/"+path;

                    //Photostamp erstellen : Test@28122022_ID_566429213554924951.jpeg

                    t_array = filename.split("@");

                    //Tag
                    if(t_array[0].contains("#") == true)
                    {
                        t_array2 = t_array[0].split("#");
                        save_dir=t_array2[0]; //Test
                        tags= t_array2[1]; // #Test

                    }
                    else
                    {
                        save_dir = t_array[0];
                        tags = "";

                    }

                    //Datum extrahieren  28122022_ID_566429213554924951.jpeg

                    //datum.substring(0,2)+"."+datum.substring(2,4)+"."+datum.substring(4,8);

                    String tag =t_array[1].substring(0,2);
                    String monat =t_array[1].substring(2,4);
                    String jahr =t_array[1].substring(4,8);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
                    String date = datumformat.format(calendar.getTime());

                    int kw = calendar.get(Calendar.WEEK_OF_YEAR);

                    datum = tag+"."+monat+"."+jahr;

                    if(datum.contains(date) == true)
                    {
                        datum += " "+bsf.time_refresh();
                    }
                    /*
                    Log.d("BASI_PATH:",path);
                    Log.d("BASI_FILENAME:",filename);
                    Log.d("BASI_SAVE_DIR:",save_dir);
                    Log.d("BASI_TAG:",tags);
                    Log.d("BASI_DATUM:",datum);
                    Log.d("BASI_DATUM_curr:",date);

                     */


                    //Fotostamp zusammenführen

                    if(tags =="")
                    {

                        photostamp = save_dir+ "  "+datum+" [KW"+String.valueOf(kw)+"]";
                    }
                    else
                    {
                           photostamp = save_dir+ "  "+tags+"    "+datum+" [KW"+String.valueOf(kw)+"]";
                    }


                    //Bitmap erstellen
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inScaled = false;
                    options.inMutable = true;

                    Bitmap bMapScaled = null;

                    Bitmap bMap = BitmapFactory.decodeFile(path + filename,options);

                    try {

                        ExifInterface exif = new ExifInterface(path+filename);
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,0);

                        int width= exif.getAttributeInt(ExifInterface.TAG_PIXEL_X_DIMENSION,0);
                        int height= exif.getAttributeInt(ExifInterface.TAG_PIXEL_Y_DIMENSION,0);

                        if(width==0 || height ==0)
                        {
                            width=bMap.getWidth();
                            height =bMap.getHeight();
                        }
                        Matrix matrix = new Matrix();

                        //Fixme 04.12.2023 Stndartwerte für width/height,falls keine exif daten vornhanden sind = Erzeugt sonst gelegentlich eine Exception!

                        switch (rotation)
                        {
                            case 3:

                                matrix.setRotate(180);
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0,width, height, matrix, true);

                            break;

                            case 6:

                                matrix.setRotate(90);
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0,width, height, matrix, true);
                                break;

                            default:
                                bMapScaled = Bitmap.createBitmap(bMap, 0, 0, width, height, matrix, true);
                        }

                        Rect rc = new Rect(0, bMapScaled.getHeight()-100, bMapScaled.getWidth(), bMapScaled.getHeight());

                        Paint paintrect = new Paint();
                        paintrect.setStyle(Paint.Style.FILL);
                        paintrect.setColor(Color.rgb(255, 255, 255));

                        Paint paint = new Paint();
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.rgb(225, 20, 225));
                        paint.setTextSize(50);

                        Canvas canvas = new Canvas(bMapScaled);
                        canvas.drawRect(rc,paintrect);
                        canvas.drawText(photostamp, 30, bMapScaled.getHeight()-30, paint);


                        //Speichern des Bildes
                        String url =  bsf.saveImage(bMapScaled, path,filename, getContext());

                        //Neues Bild Anzeigen im imageView
                        Bitmap bMap2 = BitmapFactory.decodeFile(url);
                        Bitmap bitmap3;

                        //Maximalgrösse der Ansicht(maximal)  je nach Orientierung
                        int bitmap_dim = 900;

                        switch (rotation)
                        {
                            case 6:
                                //hochkannt
                                bitmap3= Bitmap.createScaledBitmap(bMap2, bitmap_dim/2,bitmap_dim, true);

                                break;

                            default:
                                bitmap3= Bitmap.createScaledBitmap(bMap2, bitmap_dim,bitmap_dim/2, true);

                        }

                       camera_photo.setImageBitmap(bitmap3); // Im imageView Anzeigen

                        camera_photo.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {

                                View pic_view_UI = getLayoutInflater().inflate(R.layout.show_picture, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                                ImageView photo = (ImageView) pic_view_UI.findViewById(R.id.imageView4);
                                photo.setImageBitmap(BitmapFactory.decodeFile(url));

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(pic_view_UI);

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

                    } catch (IOException e)
                    {
                        exmsg("120220231030",e);
                    }
                } catch (Exception e)
                {
                    exmsg("120220231031A",e);
                    bsf.error_msg("Bild wurde verworfen",getContext());
                    camera_photo.setImageResource(0);
                    try
                    {
                        File f= new File(path+filename);
                        f.delete();

                    } catch (Exception ex)
                    {
                        exmsg("120220231031B",e);
                        ex.printStackTrace();
                    }

                }

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }

    }



    public void refresh_spinner()
    {
        String[] kat_nativ = spinnerops.getall(RROJ_NR);

       // Log.d("Adresse",kat_nativ[0]);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,kat_nativ);

     //   ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,items);
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.kamera_spinner_list);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public void dir_dialog(String mode,String value, ViewGroup container)
    {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.diradd, container,false);

        name = (EditText) promptsView.findViewById(R.id.editTextTextPersonName10);
        dir = (EditText) promptsView.findViewById(R.id.editTextTextPersonName11);
        final ImageButton paht = (ImageButton) promptsView.findViewById(R.id.imageButton2);
        CheckBox sub_folder = (CheckBox) promptsView.findViewById(R.id.enable_subfolder);



        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        switch(mode)
        {
            case "add":

                alertDialogBuilder.setTitle(R.string.add_title_dir_name);

                paht.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 1);
                    }
                });


                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try {

                                String foldername = name.getText().toString();

                                long resonse =  spinnerops.addOne(RROJ_NR,foldername,dir.getText().toString());

                                if(sub_folder.isChecked() ==true)
                                {
                                    String  abs_path = dir.getText().toString().replace("primary:","/storage/emulated/0/");
                                    File storageDir = new File(abs_path);

                                    File[] folders= storageDir.listFiles();
                                    for(File is :folders)
                                    {
                                        if(is.isDirectory() == true)
                                        {
                                            String[] parts =is.toString().split("/");

                                            String name =foldername+" -->"+ parts[parts.length-1];

                                            String  sub_abs_path = is.toString().replace("/storage/emulated/0/","primary:");
                                            long resonse2 =  spinnerops.addOne(RROJ_NR,name,sub_abs_path);
                                         //   Log.d(name,is.toString());
                                          //  Log.d(name,sub_abs_path);

                                        }

                                    }

                                }
                                refresh_spinner();
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                        }
                    }

                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                break;

            case "modify":

                alertDialogBuilder.setTitle(R.string.modify_title_dir_name);


                String[] items = spinnerops.getOne(RROJ_NR,value);

                name.setText(items[0]); //Name
                dir.setText(items[1]); //Dir

                paht.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        startActivityForResult(intent, 1);
                    }
                });

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        //Werte zum Update
                        String name_alt= items[0]; //Alter name , falls geändert
                        String name_new= String.valueOf(name.getText());
                        String dir_new = String.valueOf(dir.getText());

                        int  responde = spinnerops.updateOne(RROJ_NR,name_alt,name_new,dir_new);


                        Toast.makeText(getContext(), "Es wurden "+ String.valueOf(responde)+" Einträge geändert.",Toast.LENGTH_SHORT).show();
                        refresh_spinner();
                    }
                });
                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
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


    private void select_path(ViewGroup container)
    {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView_group = li.inflate(R.layout.kamera_select_dialog, container,false);
        AlertDialog.Builder alertDialogBuilder_group = new AlertDialog.Builder(getContext());
        alertDialogBuilder_group.setView(promptsView_group);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder_group.create();
        // show it
        alertDialog.show();
    }

    private void dispatchTakePictureIntent(String path,String title,boolean tag_on,String tag,String date)
    {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

            // Create the File where the photo should go
            File photoFile = null;
            try {


                photoFile= createImageFile(path,title,tag_on,tag,date);
            } catch (IOException ex)
            {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {

                try {
                    photoURI = FileProvider.getUriForFile(getContext(),"com.example.tabnav_test.fileprovider",photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent,2);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }



    private File createImageFile2() throws IOException
    {

        String  abs_path = "/storage/emulated/0/DCIM/";
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


    private File createImageFile(String path,String title,boolean tag_on,String tag,String date) throws IOException
    {

       String[] part = path.split(":");
       String abs_path="";

        switch(part[0])
       {
            case "primary":
                abs_path = "/storage/emulated/0/"+part[1]+"/";
               break;

            default:
                abs_path = "/storage/"+part[0]+"/"+part[1]+"/";

       }



        Basic_funct bsf = new Basic_funct();


        String addings= date+"_ID_";
       String imageFileName = title+"@" + addings;

        if(tag_on==true)
        {
            imageFileName = title+"#"+tag+"@" + addings;

        }

       File storageDir = new File(abs_path);
       File image = File.createTempFile(
               imageFileName,  /* prefix */
               ".jpeg",         /* suffix */
               storageDir      /* directory */
       );

      // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();


        return image;
    }


    private void refresh_fav_auto_complete()
    {
        config_fav_ops cfop = new config_fav_ops(getContext());
        ArrayAdapter<String> favArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cfop.favorite_strings_list(false));
        kamera_tag_field_value.setAdapter(favArrayAdapter);

    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: Kamera ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }
}



