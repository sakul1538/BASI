package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class material_log_entrys extends Fragment
{
     public RecyclerView ls_log_view_rcv;
    TextView date_select;

    private ls_log_view_rcv_adapter lslogrcv;
    public material_log_entrys()
    {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("BASI material_log_entrys", String.valueOf(requestCode));
        material_database_ops mdo = new material_database_ops(getContext());
        Basic_funct bsf = new Basic_funct();

        if (requestCode == 1) { //restrore Backup

            Uri uri = data.getData();
            String source_path = uri.getPath();//document/primary:DCIM/Baustellen /CBB E03/Lieferscheine/CBB E03[23210014]dataset_ls@20231028.json
            String backup_file_import_url = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");


            InputStream in2 = null;
            try {
                in2 = new FileInputStream(new File(backup_file_import_url));
                try {
                    JsonReader reader = new JsonReader(new InputStreamReader(in2, StandardCharsets.UTF_8));
                    int counter = 0;
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        ContentValues output_data = new ContentValues();
                        while (reader.hasNext()) {
                            output_data.put(reader.nextName(), reader.nextString());
                        }
                        mdo.add_material_log_entry(output_data);
                        reader.endObject();
                        counter++;
                    }
                    reader.endArray();
                    Toast.makeText(getContext(), counter + " Einträge Importiert!", Toast.LENGTH_SHORT).show();
                    lslogrcv.refresh_dataset(getContext());

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static material_log_entrys newInstance(String param1, String param2)
    {

        material_log_entrys fragment = new material_log_entrys();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Basic_funct bsf = new Basic_funct();


        material_database_ops mdo = new material_database_ops(getContext());

        View view = inflater.inflate(R.layout.material_log_entrys_listing, container, false);

        //ImageButton
        ImageButton search_in = view.findViewById(R.id.imageButton74);
        ImageButton action_menu = view.findViewById(R.id.imageButton36);
        ImageButton reload_dataset = view.findViewById(R.id.imageButton53);
        ImageButton date_calender_visibility = view.findViewById(R.id.imageButton35);
        ImageButton date_calender_shift_back = view.findViewById(R.id.imageButton72);
        ImageButton date_calender_shift_forward = view.findViewById(R.id.imageButton73);


        //RecyclerView
        RecyclerView ls_log_view_rcv =view.findViewById(R.id.material_log_entry_rcv);
        String[] ls_log_view_rcv_adapter=mdo.material_entrys_list();
        lslogrcv = new ls_log_view_rcv_adapter(ls_log_view_rcv_adapter);
        ls_log_view_rcv.setAdapter(lslogrcv);
        ls_log_view_rcv.setLayoutManager( new LinearLayoutManager(getContext()));

        //TextView

        date_select = view.findViewById(R.id.textView53);


        //Layouts
        LinearLayout select_date_layout = view.findViewById(R.id.select_date_layout);
        select_date_layout.setVisibility(View.GONE);
        date_calender_shift_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                date_select.setText(bsf.time_day_shift(date_select.getText().toString(),"",-1));
                date_search_refresh_dataset();
            }
        });

        date_calender_shift_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                date_select.setText(bsf.time_day_shift(date_select.getText().toString(),"",1));
                date_search_refresh_dataset();
            }
        });

        date_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2);


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());


                        date_select.setText(dateString);
                        date_search_refresh_dataset();


                    }
                });
                datePickerDialog.show();
            }
        });

        date_calender_visibility.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(select_date_layout.getVisibility() == View.VISIBLE)
                {
                    select_date_layout.setVisibility(View.GONE);
                    try {
                        lslogrcv.refresh_dataset(getContext());
                    } catch (Exception e) {
                      bsf.exeptiontoast(e,getContext());
                    }
                }
                else
                {
                    select_date_layout.setVisibility(View.VISIBLE);
                    date_select.setText(bsf.date_refresh());
                    date_search_refresh_dataset();
                }

            }
        });
        
        reload_dataset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                try {
                    lslogrcv.refresh_dataset(getContext());

                    Toast.makeText(getContext(), "Alle Einträge neu geladen!", Toast.LENGTH_SHORT).show();
                    select_date_layout.setVisibility(View.GONE);
                } catch (Exception e) {
                   bsf.exeptiontoast(e,getContext());
                }
            }
        });


        search_in.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LayoutInflater li = LayoutInflater.from(getContext());
                View search_in_dialog = li.inflate(R.layout.material_log_entrys_search_dialog,container,false);

                //TextView's
                TextView date = search_in_dialog.findViewById(R.id.material_log_search_datum_field);

                //Checkboxen
                CheckBox material_log_search_lsnr_field_check = search_in_dialog.findViewById(R.id.material_log_search_lsnr_field_check);
                CheckBox material_log_search_lieferant_field_check = search_in_dialog.findViewById(R.id.material_log_search_lieferant_field_check);
                CheckBox material_log_search_datum_field_check = search_in_dialog.findViewById(R.id.material_log_search_datum_field_check);
                CheckBox material_log_search_artikel_field_check = search_in_dialog.findViewById(R.id.material_log_search_artikel_field_check);

                AutoCompleteTextView material_log_search_lsnr_field = search_in_dialog.findViewById(R.id.material_log_search_lsnr_field) ;
                AutoCompleteTextView material_log_search_lieferant_field = search_in_dialog.findViewById(R.id.material_log_search_lieferant_field) ;
                AutoCompleteTextView material_log_search_artikel_field = search_in_dialog.findViewById(R.id.material_log_search_artikel_field) ;

               material_log_search_lsnr_field.setAdapter(bsf.get_autocomplete_adapter(mdo.get_entry_lsnr_list_all(),getContext()));
               material_log_search_lieferant_field.setAdapter(bsf.get_autocomplete_adapter(mdo.zulieferer_list_all("NAME DESC"),getContext()));
               material_log_search_artikel_field.setAdapter(bsf.get_autocomplete_adapter(mdo.artikel_list_all(),getContext()));


                date.setText(bsf.date_refresh());
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                            {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(i, i1, i2);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                                String dateString = dateFormat.format(calendar.getTime());
                                date.setText(dateString);
                            }
                        });
                        datePickerDialog.show();
                    }
                });
                AlertDialog.Builder search_dialog = new AlertDialog.Builder(getContext());
                search_dialog.setView(search_in_dialog);
                search_dialog.setTitle("Suche");


                search_dialog.setPositiveButton("Suche", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ArrayList select_args= new ArrayList();
                        ArrayList where= new ArrayList();


                        try {
                            if(material_log_search_lsnr_field_check.isChecked())
                            {
                                select_args.add(material_log_search_lsnr_field.getText().toString());
                                where.add("LSNR=? ");
                            }

                            if(material_log_search_lieferant_field_check.isChecked())
                            {

                                select_args.add(mdo.get_id_zulieferer(material_log_search_lieferant_field.getText().toString()));
                                where.add("LIEFERANT_ID=?");
                            }

                            if(material_log_search_datum_field_check.isChecked())
                            {
                                select_args.add(bsf.convert_date(date.getText().toString(),"format_database"));
                                where.add("DATUM=?");
                            }
                            if(material_log_search_artikel_field_check.isChecked())
                              {
                                 String[] raw_input =  material_log_search_artikel_field.getText().toString().split("\\[");
                                 String artikel = raw_input[0].trim();
                                 String einheit = raw_input[1].substring(0,raw_input[1].length()-1);
                                 String []selectinArgs = new String[]{artikel,einheit};
                                 select_args.add(mdo.get_artikel_param(selectinArgs,"NAME=? AND EINHEIT=?",new String[]{"ID"}));
                                 where.add("MATERIAL_ID=?");

                              }

                            select_args.add(mdo.get_selectet_projekt_id() );
                            where.add("PROJEKT_ID=?");
                        } catch (Exception e)
                        {
                            bsf.exeptiontoast(e,getContext());
                        }

                        try {
                            String []  matches = mdo.material_log_search(where,select_args);
                            lslogrcv.refresh_dataset_from_array(matches,getContext());
                        } catch (Exception e)
                        {
                            bsf.exeptiontoast(e,getContext());
                        }

                        dialogInterface.cancel();
                    }
                });


                search_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        try {
                            lslogrcv.refresh_dataset(getContext());
                        } catch (Exception e)
                        {
                           bsf.exeptiontoast(e,getContext());
                        }
                        dialogInterface.cancel();
                    }
                });
                search_dialog.show();             }
        });


        action_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PopupMenu popup = new PopupMenu(getContext(),view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.material_log_entrys_action_menu,popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem)
                    {
                        switch(menuItem.getItemId())
                        {
                            case R.id.entry_show_all:

                                    entrys_show_all();

                                break;
                            case R.id.entry_del_all:

                                    entrys_delet_all();

                                break;

                            case R.id.export_json:

                                    export_data("json");

                                break;

                            case R.id.export_csv:

                                    export_data("csv");

                                break;

                            case R.id.export_pdf:

                                    export_data("pdf");

                                break;

                            case R.id.create_backup:

                                    backup("create");
                                break;
                            case R.id.restore_backup:

                                    backup("restore");

                                break;
                        }
                        return false;
                    }
                });
                popup.show();



            }
        });

        return view;
    }

    private void backup(String direction)
    {
        material_database_ops mdo = new material_database_ops(getContext());
        Basic_funct bsf  = new Basic_funct();

        String paht= mdo.get_projekt_root_paht()+Material.backup_dir+"DATASETS/";
        String filename="untitled.json";

        switch (direction)
        {
            case "create":
                File dir = new File(paht);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
                String[] entrys = mdo.get_current_projekt_entrys();

                String data_loop="[";
                for(String i:entrys )
                {


                    data_loop +="{";
                    String[] extract = i.split(",");
                    String temp_loop ="";

                    for(String e: extract)
                    {
                        String [] nr = e.split(":");
                        Log.d("BASI",e);

                            temp_loop += "\""+nr[0]+"\":\""+nr[1]+"\",";


                    }
                    data_loop +=temp_loop.substring(0,temp_loop.length()-1)+"},";
                }
               data_loop =data_loop.substring(0,data_loop.length()-1)+"]";

               String file_name= mdo.get_selectet_projekt()+"dataset_backup@"+bsf.get_date_for_filename()+"_ID_"+bsf.gen_UUID()+".json";
                file_save(paht+file_name,data_loop);

                break;
            case "restore":

                Intent intent_restore_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent_restore_backup.addCategory(Intent.CATEGORY_OPENABLE);
                intent_restore_backup.setType("application/json");
                startActivityForResult(intent_restore_backup, 1);

                Toast.makeText(getContext(), direction, Toast.LENGTH_SHORT).show();
                break;
            default:

        }
    }

    private void export_data(String format_type)
    {
        material_database_ops mdo = new material_database_ops(getContext());

        String[] data = mdo.get_current_projekt_entrys();
        String paht="";

        String filename="untitled.txt";

        Basic_funct bsf = new Basic_funct();

        switch (format_type)
        {
            case "json":

                paht= mdo.get_projekt_export_dir_json();
                File dir = new File(paht);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }

                String data_loop="[";
                for(String i:data )
                {
                    data_loop +="{";
                    String[] extract = i.split(",");
                    String temp_loop ="";

                    for(String e: extract)
                    {
                        String [] nr = e.split(":");
                        if(nr[0].contains("NOTIZ"))
                        {
                            temp_loop += "\""+nr[0]+"\":\""+bsf.URLdecode(nr[1])+"\",";
                        }
                        else
                        {
                            temp_loop += "\""+nr[0]+"\":\""+nr[1]+"\",";
                        }

                    }
                    data_loop +=temp_loop.substring(0,temp_loop.length()-1)+"},";

                }
                data_loop =data_loop.substring(0,data_loop.length()-1)+"]";
                Log.d("BASI Json",data_loop);



                filename= mdo.get_projekt_name()+"_dataset_ls@"+bsf.get_date_for_filename()+".json";

                file_save(paht+filename,data_loop);
                break;

            case "csv":

                paht= mdo.get_projekt_export_dir_csv();
                dir = new File(paht);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }

                String string_loop ="ID,DATUM,LIEFERSCHEIN NR,LIEFERANT, ARTIKEL,MENGE,EINHEIT,NOTIZ\n"; //Colum names

                for(String i:data )
                {
                    String[] extract = i.split(",");

                    for(String e: extract)
                    {
                        String [] nr = e.split(":");
                        switch (nr[0])
                        {
                            case "ID":
                                    string_loop +="\""+ nr[1]+"\",";
                                break;

                                case "DATUM":
                                    string_loop += "\""+bsf.convert_date(nr[1],"format_database_to_readable")+"\",";
                                break;

                                case "LSNR":
                                    string_loop += "\""+nr[1]+"\",";
                                break;
                                case "LIEFERANT_ID":
                                    string_loop +=  "\""+ mdo.get_lieferant_name_by_id(nr[1])+"\",";
                                break;
                                case "MATERIAL_ID":
                                    string_loop +=  "\""+ mdo.get_artikel_name_by_id(nr[1])+"\",";
                                break;
                                case "MENGE":
                                    try {
                                        string_loop +=  "\""+nr[1]+"\",";
                                    }catch (Exception s)
                                    {
                                        string_loop +=  "\"\",";
                                    }
                                break;
                                case "EINHEIT_ID":
                                    string_loop +=  "\""+nr[1]+"\",";
                                break;
                                case "NOTIZ":
                                    if(!nr[1].contains("null"))
                                    {
                                        string_loop += "\""+bsf.URLdecode(nr[1])+"\"\n";
                                    }
                                    else
                                    {
                                        string_loop += "\""+"-"+"\"\n";
                                    }
                                break;
                        }
                    }
                }
                filename= mdo.get_projekt_name()+"_dataset_ls@"+bsf.get_date_for_filename()+".csv";
                file_save(paht+filename,string_loop);
                break;

            case "pdf":

                break;

            default:
                Toast.makeText(getContext(), "Nicht Impementiert", Toast.LENGTH_SHORT).show();
        }



    }

    private void entrys_delet_all()
    {

        AlertDialog.Builder del_confirm_dialog = new AlertDialog.Builder(getContext());

        del_confirm_dialog.setMessage("Alle Einträge Löschen?");
        del_confirm_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        material_database_ops mdo = new material_database_ops(getContext());
                        long deletet_rows =mdo.delet_all_material_log_entry();
                        lslogrcv.refresh_dataset(getContext());
                        Toast.makeText(getContext(), deletet_rows +" Einträge gelöscht!", Toast.LENGTH_SHORT).show();
                    }
                });
        del_confirm_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        del_confirm_dialog.show();

    }

    private void entrys_show_all()
    {
        lslogrcv.refresh_dataset(getContext());
        Toast.makeText(getContext(), "entry_show_all", Toast.LENGTH_SHORT).show();
    }


    private String emptystest(String element)
    {
        if(element.isEmpty())
        {

            return "0";
        }
        else
        {
            return element;
        }
    }

    private  void file_save(String filename,String text)
    {
        File f = new File(filename);
        try {
            if(f.exists())
            {
                f.delete();
            }
            f.createNewFile();
            FileWriter fw = new FileWriter(filename);
            fw.write(text);
            fw.close();
            Toast.makeText(getContext(),"Export  Erfolgreich als Datei "+filename, Toast.LENGTH_SHORT).show();
        } catch (IOException e)
        {
            Toast.makeText(getContext(),"Export  Fehlgeschlagen\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void date_search_refresh_dataset()
    {
        Basic_funct bsf =new Basic_funct();
        material_database_ops mdo = new material_database_ops(getContext());
        try {
            lslogrcv.refresh_dataset_from_array(mdo.material_log_search_date(date_select.getText().toString()),getContext());
        } catch (Exception e) {
            bsf.exeptiontoast(e,getContext());
        }

    }




}
