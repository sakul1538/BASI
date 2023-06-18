package com.example.tabnav_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
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

import java.io.File;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String currentPhotoPath;
    Uri photoURI=null;

    String photo_path ="null";

    //ImageButton
    ImageButton settings_projekt_button;
    ImageButton settings_zulieferer;
    ImageButton settings_artikel;
    ImageButton add_artikel;
    ImageButton reset_artikel;
    ImageButton ls_note_reset;
    ImageButton ls_take_photo;
    ImageButton reset_form;


    ImageButton reset_LS;
    ImageButton refresh_date;

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

    //TextView
    TextView projekt_label;
    TextView date_label;

    ImageView  ls_photo_view;

   LinearLayout date_background;

    public Material() {
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
        switch (requestCode)
        {
            case 0:
                dirname.setText(resultData.getData().getLastPathSegment());
                break;
            case 1:

                String filename ="";
                String path="";
                String photostamp ="";
                String tags = "";
                String save_dir="";
                String datum="";

                path = photoURI.getPath(); //path:  /primary/DCIM/Test/Test@28122022_ID_566429213554924951.jpeg

                Log.d("URL:",path);

                try {
                    path = path.replace("/primary/",""); //primary entfernen


                    path = Environment.getExternalStorageDirectory()+"/"+path;
                    set_foto_path(path);
                    Log.d("URL_2: ",path);

                    Bitmap ls_picture= BitmapFactory.decodeFile(path);
                    Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture,400,400,true);

                    ls_photo_view.setImageBitmap(ls_picture_scaled);

                    ls_photo_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)

                        {
                            View pic_view_UI = getLayoutInflater().inflate(R.layout.show_picture, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                            ImageView photo = (ImageView) pic_view_UI.findViewById(R.id.imageView4);
                            photo.setImageBitmap(BitmapFactory.decodeFile(get_foto_path()));

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




                }catch (Exception e)
                {
                    exmsg("18062023110",e);
                }


                Toast.makeText(getContext(),path, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        try {
            refresh_artikel_autocomplete();
            refresh_spinner_zulieferer();
        }catch (Exception e)
        {
            exmsg("110620231104",e);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_material, container, false);


        //TextView
        projekt_label = view.findViewById(R.id.textView58);
        date_label = view.findViewById(R.id.textView59_date);

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

        //EditText
        lsnr_field =view.findViewById(R.id.edittext_LS_ID);

        //Spinner
        spinner_einheiten = view.findViewById(R.id.spinner6_einheiten);

        //Layouts
        date_background = view.findViewById(R.id.date_background);


        material_database_ops mdo = new material_database_ops(getContext());

        refresh_artikel_autocomplete();
        refresh_projekt_label();
        refresh_spinner_zulieferer();
        //Liferschein Nr.

        reset_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                reset_date();
                reset_ls_nr();
                reset_artikel();
                reset_menge();
                reset_notiz();
                reset_camera();
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

                //Todo Ordner automisch erstellen 14.06.2023

                material_database_ops mdo = new material_database_ops(getContext());
                String data= mdo.get_selectet_projekt_root();
                String []p = data.split(",");
                String zulieferer = Zulieferer_liste_main.getSelectedItem().toString();

                String date = String.valueOf(date_label.getText());
                date = date.replace(".","");
               dispatchTakePictureIntent(p[2]+"/Lieferscheine/"+zulieferer.trim(),p[0]+"#"+zulieferer.trim(),false,"", date);
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
                material_artikel_adapter lcar = new material_artikel_adapter(material_artikel_adapter);
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
        settings_zulieferer.setOnClickListener(new View.OnClickListener() {
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

                del_zuliefere.setOnClickListener(new View.OnClickListener() {
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
                startActivityForResult(takePictureIntent,1);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }
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






    void exmsg(String msg, Exception e)
    {
        Log.e("Exception: Material ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }

}