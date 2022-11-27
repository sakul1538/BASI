package com.example.tabnav_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Kamera#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Kamera<onActivityResult> extends Fragment
{


    String currentPhotoPath;
    Uri currentPhotoPaht_uri;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final String RROJ_NR="0";

    ImageButton kamera_reset_tag;
    Switch kamera_switch_tag_onoff;
    TextView kamera_tag_field_value;


    TextView curr_date;
    ImageButton curr_date_refresh_button;

    ImageButton take_picture = null;
    ImageButton adddir_delet = null;
    ImageButton adddir_modify = null;
    ImageButton adddir = null;
    ImageButton dir_open = null;
    ImageView camera_photo  = null;

    EditText name= null;
    EditText dir = null;
    TextView pfadtemp = null;
    TextView nametemp = null;
    ImageView previewImageView = null;

    private int requestCode;
    private int resultCode;
    private Intent data;
    Spinner spinner;
    kamera_spinner spinnerops;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters


    public Kamera()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Kamera.
     */
    // TODO: Rename and change types and number of parameters
    public static Kamera newInstance(String param1, String param2)
    {
        Kamera fragment = new Kamera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

        kamera_reset_tag = (ImageButton) view.findViewById(R.id.kamera_reset_tag_button);
        kamera_switch_tag_onoff = (Switch) view.findViewById(R.id.kamera_switch_tag_onoff);
        kamera_tag_field_value = (TextView) view.findViewById(R.id.kamera_tag_field_value);
        LinearLayout tag_bg = (LinearLayout) view.findViewById(R.id.tag_background);
        LinearLayout date_bg = (LinearLayout) view.findViewById(R.id.date_background);




        curr_date_refresh_button =(ImageButton) view.findViewById(R.id.kamera_date_refresh_button);
        curr_date =(TextView) view.findViewById(R.id.kamera_date);


        take_picture = view.findViewById(R.id.imageButton11);
        adddir = view.findViewById(R.id.imageButton7);
        adddir_delet = view.findViewById(R.id.imageButton10);
        adddir_modify = view.findViewById(R.id.imageButton9);



        spinner = view.findViewById(R.id.spinner4);
        spinnerops = new kamera_spinner(getContext());
        refresh_spinner();


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

        ImageView finalPreviewImageView = previewImageView;

        take_picture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String[] responde = spinnerops.getOne(RROJ_NR,String.valueOf(spinner.getSelectedItem()));

                String date = curr_date.getText().toString();
                date = date.replace(".","");
                if(kamera_switch_tag_onoff.isChecked() == true)
                {
                    dispatchTakePictureIntent(responde[1],responde[0],true,kamera_tag_field_value.getText().toString(),date);

                }
                else
                {
                    dispatchTakePictureIntent(responde[1],responde[0],false,"",date); //Path ,Name
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
                    tag_bg.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
                }

                if (kamera_switch_tag_onoff.isChecked() == false)
                {
                    tag_bg.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.hellgrün));

                }
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
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
                String date = datumformat.format(calendar.getTime());

                curr_date.setText(date);

                date_bg.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.hellgrün));

              /*  String paht = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Baustellen/Martinsheim, Visp/Lieferscheine/");


                File f = new File(paht);
                String[] fil =f.list();
                for (String pathname : fil) {
                    // Print the names of files and directories
                    Log.d("BASI:",pathname);
                }
                */




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

                        date_bg.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange));
                    }
                });

                dpd.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
        String date = datumformat.format(calendar.getTime()); // Inflate the layout for this fragment
        curr_date.setText(date);

        return view;
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

                    Toast.makeText(getContext(),String.valueOf(requestCode),Toast.LENGTH_LONG).show();
                    String message=String.valueOf(data.getData().getLastPathSegment());

                    dir.setText(message);

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
                                long resonse =  spinnerops.addOne(RROJ_NR,name.getText().toString(),dir.getText().toString());

                                if(resonse == -1)
                                {

                                    Toast.makeText(getContext(),"Eintrag konnte nicht erstellt werden!",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(),"Neuer Eintrag wurde erstellt!"+resonse,Toast.LENGTH_LONG).show();
                                    refresh_spinner();
                                }

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
                Uri photoURI = FileProvider.getUriForFile(getContext(),"com.example.tabnav_test.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivity(takePictureIntent);
                Log.d("Foto:",photoURI.toString());


            }
        }

    private void startActivityForResult()

    {
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

}



