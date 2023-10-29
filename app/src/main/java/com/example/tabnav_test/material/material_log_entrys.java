package com.example.tabnav_test.material;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.google.android.material.progressindicator.BaseProgressIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class material_log_entrys extends Fragment
{
     public RecyclerView ls_log_view_rcv;

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

        switch(requestCode)
        {

            case 1: //restrore Backup

                Uri  uri = data.getData();
                String source_path = uri.getPath();//document/primary:DCIM/Baustellen /CBB E03/Lieferscheine/CBB E03[23210014]dataset_ls@20231028.json
                String backup_file_import_url = source_path.replace("/document/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");


                InputStream in2 = null;
                try {
                    in2 = new FileInputStream(new File(backup_file_import_url));
                    try {
                        JsonReader reader = new JsonReader(new InputStreamReader(in2,"UTF-8"));
                        int counter = 0;
                        reader.beginArray();
                        while(reader.hasNext())
                        {
                            reader.beginObject();
                            ContentValues output_data = new ContentValues();
                            while (reader.hasNext())
                            {
                                output_data.put(reader.nextName(),reader.nextString());
                            }
                            mdo.add_material_log_entry(output_data);
                            reader.endObject();
                            counter++;
                        }
                        reader.endArray();
                        Toast.makeText(getContext(), counter +" Einträge Importiert!", Toast.LENGTH_SHORT).show();
                        lslogrcv.refresh_dataset(getContext());

                    } catch (UnsupportedEncodingException e)
                    {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                break;

            case 2: //create backup

                Uri  uri_create_backup = data.getData();
                Log.d("BASI backup_paht", data.getData().getPath());

                String source_path_create_backup = uri_create_backup.getPath();//tree/primary:DCIM/Baustellen /CBB E03
                String backup_file_import_url_create_backup = source_path_create_backup.replace("/tree/primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");

                Log.d("BASI backup_paht",backup_file_import_url_create_backup);


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
                        temp_loop += "\""+nr[0]+"\":\""+nr[1]+"\",";
                    }
                    data_loop +=temp_loop.substring(0,temp_loop.length()-1)+"},";

                }
                data_loop =data_loop.substring(0,data_loop.length()-1)+"]";
                Log.d("BASI Json",data_loop);


                String filename= mdo.get_selectet_projekt()+"dataset_backup@"+bsf.get_date_filename()+".json";
               Log.d("BASI",backup_file_import_url_create_backup+"/"+filename);
               file_save(backup_file_import_url_create_backup+"/"+filename,data_loop);

                break;
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


        //RecyclerView
        RecyclerView ls_log_view_rcv =view.findViewById(R.id.material_log_entry_rcv);
        String[] ls_log_view_rcv_adapter=mdo.material_entrys_list();
        lslogrcv = new ls_log_view_rcv_adapter(ls_log_view_rcv_adapter);
        ls_log_view_rcv.setAdapter(lslogrcv);
        ls_log_view_rcv.setLayoutManager( new LinearLayoutManager(getContext()));


        search_in.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                LayoutInflater li = LayoutInflater.from(getContext());
                View search_in_dialog = li.inflate(R.layout.material_log_entrys_search_dialog,container,false);

                //TextView's
                TextView date = search_in_dialog.findViewById(R.id.material_log_search_datum_field);

                date.setText(bsf.date_refresh_rev2());

                AlertDialog.Builder search_dialog = new AlertDialog.Builder(getContext());
                search_dialog.setView(search_in_dialog);
                search_dialog.setTitle("Suche");


                search_dialog.setPositiveButton("Suche", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                     dialogInterface.cancel();
                    }
                });

                search_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                search_dialog.show();
            }
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

                            case R.id.export_txt:

                                    export_data("txt");

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

        String paht= mdo.get_projekt_root_paht()+Material.ls_media_directory_name+"/";
        String filename="untitled.json";

        switch (direction)
        {
            case "create":

                Intent intent_create_backup = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent_create_backup, 2);


                Toast.makeText(getContext(), direction, Toast.LENGTH_SHORT).show();
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

        String paht= mdo.get_projekt_root_paht()+Material.ls_media_directory_name+"/"+  mdo.get_selectet_projekt().split(",")[0];
        String filename="untitled.txt";

        Basic_funct bsf = new Basic_funct();

        switch (format_type)
        {
            case "json":

                String data_loop="[";
                for(String i:data )
                {
                    data_loop +="{";
                    String[] extract = i.split(",");
                    String temp_loop ="";

                    for(String e: extract)
                    {
                        String [] nr = e.split(":");
                        temp_loop += "\""+nr[0]+"\":\""+nr[1]+"\",";
                    }
                    data_loop +=temp_loop.substring(0,temp_loop.length()-1)+"},";

                }
                data_loop =data_loop.substring(0,data_loop.length()-1)+"]";
                Log.d("BASI Json",data_loop);


                filename= "dataset_ls@"+bsf.get_date_filename()+".json";
                file_save(paht+filename,data_loop);
                break;

            case "csv":

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
                                    if(nr[1].contains("null") == false)
                                    {
                                        string_loop += "\""+nr[1]+"\"\n";
                                    }
                                    else
                                    {
                                        string_loop += "\""+"-"+"\"\n";;
                                    }
                                break;
                        }
                    }
                }
                filename= "dataset_ls@"+bsf.get_date_filename()+".csv";
                file_save(paht+filename,string_loop);

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
                        long deletet_rows =mdo.delet_all_material_log_entry(mdo.get_selectet_projekt_root_data().split(",")[1]);
                        lslogrcv.refresh_dataset(getContext());
                        Toast.makeText(getContext(), String.valueOf(deletet_rows)+" Einträge gelöscht!", Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getContext(),"Export  Fehlgeschlagen\n"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
