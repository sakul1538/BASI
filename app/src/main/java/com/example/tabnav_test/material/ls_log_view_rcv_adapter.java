package com.example.tabnav_test.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.SQL_finals;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ls_log_view_rcv_adapter extends RecyclerView.Adapter<ls_log_view_rcv_adapter.ViewHolder>
{
    boolean return_colision_value =true;
    String[] localDataSet ;
    private ViewGroup parent;

    material_database_ops mdo;

    TableRow bg_lsnr;
    TableRow bg_zulieferer;
    TabLayout tab;

    Basic_funct bsf;

    public ls_log_view_rcv_adapter(String[] ls_log_view_rcv_adapter,TabLayout tab)
    {
        this.localDataSet = ls_log_view_rcv_adapter;
        this.tab  =tab;
        this.bsf = new Basic_funct();

    }



    @NonNull
    @Override
    public ls_log_view_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_log_entrys_listing_rcv_layout, parent, false);
        this.parent = parent;
         mdo = new material_database_ops(parent.getContext());

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ls_log_view_rcv_adapter.ViewHolder holder, int position)
    {
            String[] items = localDataSet[position].split(",");

            ContentValues data = new ContentValues();

            data.put("ID",items[0]);
            data.put("PROJEKT_ID",items[1]);
            data.put("DATUM",bsf.convert_date(items[2],"format_database_to_readable"));
            data.put("LSNR",bsf.NULLtest(items[3]));
            data.put("ZULIEFERER_ID",bsf.NULLtest(items[4]));
            data.put("ARTIKEL_ID",bsf.NULLtest(items[5]));
            data.put("MENGE",bsf.NULLtest(items[6]));
            data.put("EINHEIT",bsf.NULLtest(items[7]));
            data.put("SRC",bsf.NULLtest(items[8]));

        try {
            if(items[9].equals("null") || items[9].isEmpty())
            {
                data.put("NOTIZ","");
            }
            else
            {
                data.put("NOTIZ",bsf.URLdecode(items[9]));
            }
        } catch (Exception e)
        {
            data.put("NOTIZ","");

        }

        String artikel_name = mdo.get_artikel_param(
                    new String[]{data.get("ARTIKEL_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});


            String name_zuleferer =  mdo.get_zulieferer_param(
                    new String[]{data.get("ZULIEFERER_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});

           // String head =artikel_name+": "+data.get("MENGE").toString()+" " +data.get("EINHEIT").toString()+"\n"+name_zuleferer+" LSNR: "+data.get("LSNR")+" am "+ data.get("DATUM").toString();

            String head =name_zuleferer+" "
                    +"["+data.get("LSNR").toString()+"]"
                    +" am "+ data.get("DATUM").toString()

                    +"\n"+artikel_name+" : "+data.get("MENGE")+" "+ data.get("EINHEIT").toString();

            holder.artikel().setText(head);
            holder.notiz().setText(data.get("NOTIZ").toString());

            //Image Buttons

            holder.getDelet_entry().setOnClickListener(view -> delet_dialog(artikel_name,data));

            holder.getUpdate_entry().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    update_log_entry(data.get("ID").toString(), view);
                }
            });


            holder.getEntry_details().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                   if(holder.table_details().getVisibility() == View.VISIBLE)
                   {
                       holder.table_details().setVisibility(View.GONE);
                       holder.getadd_media_camera().setVisibility(View.GONE);
                       holder.getadd_media_files().setVisibility(View.GONE);
                       holder.getadd_pdf_file().setVisibility(View.GONE);
                   }
                   else
                   {
                       holder.table_details().setVisibility(View.VISIBLE);
                       holder.getadd_media_camera().setVisibility(View.VISIBLE);
                       holder.getadd_media_files().setVisibility(View.VISIBLE);
                       holder.getadd_pdf_file().setVisibility(View.VISIBLE);

                       String files_found =mdo.media_scanner(data);
                       Log.d("BASI",files_found);

                       if(files_found !="")
                       {

                           String [] media_set =files_found.split(",");
                           holder.gallery_rcv.setVisibility(View.VISIBLE);

                           material_log_activity.gad= new galery_adaper(media_set,data, parent.getContext());

                           RecyclerView.LayoutManager layoutManager = new GridLayoutManager(parent.getContext(), 3);

                           holder.getGallery_rcv().setAdapter(material_log_activity.gad);
                           holder.getGallery_rcv().setLayoutManager(layoutManager);
                       }
                       else
                       {
                           holder.gallery_rcv.setVisibility(View.GONE);
                       }
                   }
                }
            });

                holder.getadd_pdf_file().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Basic_funct bsf =new Basic_funct();
                        try {
                            String proj_root = mdo.get_ls_images_dir();

                            String document_name  =  bsf.ls_filename_form(name_zuleferer,
                                    data.get("LSNR").toString(),
                                    data.get("DATUM").toString().replace(".",""),"default"); //ohne Dateityp  Endung .jpeg

                            material_log_activity.filePath = proj_root+"/"+document_name+".pdf";

                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("application/pdf");
                            ((Activity) view.getContext()).startActivityForResult(intent, 2);

                        }catch ( Exception e)
                        {
                            exmsg("030920231259",e);
                            bsf.error_msg("Interner Fehler:\n "+ e.getMessage(),view.getContext());
                        }
                    }
                });


        holder.getadd_media_files().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Basic_funct bsf =new Basic_funct();
                try {
                    String proj_root = mdo.get_ls_temp_dir();


                    String document_name  =  bsf.ls_filename_form(name_zuleferer,
                            data.get("LSNR").toString(),
                            data.get("DATUM").toString().replace(".",""),"default"); //ohne Dateityp  Endung .jpeg

                    material_log_activity.filePath = proj_root+"/"+document_name;

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    ((Activity) view.getContext()).startActivityForResult(intent, 3);

                }catch ( Exception e)
                {
                    exmsg("030920231259",e);
                    bsf.error_msg("Interner Fehler:\n "+ e.getMessage(),view.getContext());
                }
            }
        });



        holder.getadd_media_camera().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        Basic_funct bsf = new Basic_funct();
                        try {


                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            String root = mdo.get_ls_images_dir();

                            File in_directory = new File(root);
                            in_directory.mkdirs();

                            String image_name  =  bsf.ls_filename_form(name_zuleferer,
                                    data.get("LSNR").toString(),
                                    data.get("DATUM").toString().replace(".",""),"default"); //ohne Dateityp  Endung .jpeg

                            File image_file = File.createTempFile(image_name,".jpeg",in_directory);

                            Uri photoURI;

                            if (image_file != null)
                            {
                                try {

                                    photoURI = FileProvider.getUriForFile(view.getContext(), "com.example.tabnav_test.fileprovider",image_file);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    ((Activity) view.getContext()).startActivityForResult(takePictureIntent, 1);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (IOException e)
                        {
                            bsf.error_msg("Interner Fehler:\n+"+ e.getMessage(), view.getContext());
                        }
                    }
                });

        holder.getCopy_entry().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setTitle("Eintrag Kopieren?");
                alertDialogBuilder.setMessage("Eintrag "+artikel_name +" ["+data.get("EINHEIT").toString()+"] wirklich Kopieren?");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        mdo.material_log_copy_entry(data.get("ID").toString());
                        refresh_dataset(parent.getContext());
                        dialogInterface.cancel();
                    }
                });



                alertDialogBuilder.setNeutralButton("In Zwischenablage", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        ClipboardManager clipboard = (ClipboardManager) parent.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        // Creates a new text clip to put on the clipboard

                        //LIEFERANT_ID=AxY4fNkKle PROJEKT_ID=23110022 MATERIAL_ID=IBArupsIjW ID=198edb30b2914fdb933ae1d5418282bb SRC=null LSNR=G99110 DATUM=2023-10-24 MENGE=200 a EINHEIT_ID=m3


                       // Theler: Lieferschein Nr: 12345 vom 01.01.2023 , Kies 0/22 2.3m3 =>

                        String data_copy = "";

                         data_copy += mdo.get_lieferant_name_by_id(data.get("ZULIEFERER_ID").toString())+": \n";
                         data_copy += "Lieferschein Nr:"+data.get("LSNR").toString();
                         data_copy += " vom "+data.get("DATUM").toString()+"\n";
                         data_copy += mdo.get_artikel_name_by_id(data.get("ARTIKEL_ID").toString())+" ";
                         data_copy += data.get("MENGE").toString()+" "+ data.get("EINHEIT").toString();

                         if(data.get("NOTIZ").toString() !="")
                         {
                             data_copy +=" => "+ data.get("NOTIZ").toString();
                         }

                        ClipData clip = ClipData.newPlainText("Material_log",data_copy);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(parent.getContext(), "Eintrag in die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show();





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

                alertDialogBuilder.show();
        }});
    }



    @Override
    public int getItemCount()
    {
        String a = "Einträge ("+ localDataSet.length +")";
        tab.getTabAt(0).setText(a);
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private  final TextView artikel;
        private  final TextView noitz;
        private  final ImageButton delet_entry;
        private  final ImageButton entry_details;

        private  final ImageButton update_entry;
        private  final ImageButton add_media_camera;
        private  final ImageButton add_media_files;
        private  final ImageButton add_file_pdf;
        private  final ImageButton copy_entry;

        private final TableLayout table_layout_details;
        private final RecyclerView gallery_rcv;




        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            artikel = itemView.findViewById(R.id.textView78);
            noitz = itemView.findViewById(R.id.textView75);
            delet_entry = itemView.findViewById(R.id.imageButton42);
            entry_details = itemView.findViewById(R.id.imageButton40);
            table_layout_details = itemView.findViewById(R.id.table_details);
            gallery_rcv = itemView.findViewById(R.id.gallery_rcv);
            update_entry = itemView.findViewById(R.id.imageButton41);
            add_media_camera = itemView.findViewById(R.id.imageButton44);
            add_media_files = itemView.findViewById(R.id.imageButton68);
            add_file_pdf = itemView.findViewById(R.id.imageButton38);
            copy_entry = itemView.findViewById(R.id.imageButton71);
        }

        public TextView artikel() {return artikel;}

        public TextView  notiz() {return noitz;}


        public ImageButton  getDelet_entry() {return delet_entry;}
        public ImageButton  getEntry_details() {return entry_details;}
        public ImageButton  getUpdate_entry() {return update_entry;}
        public ImageButton  getadd_media_camera() {return add_media_camera;}
        public ImageButton  getadd_media_files() {return add_media_files;}
        public ImageButton  getCopy_entry() {return copy_entry;}
        public ImageButton  getadd_pdf_file() {return add_file_pdf;}
        public TableLayout table_details() {return table_layout_details;}
        public RecyclerView getGallery_rcv() {return gallery_rcv;}


    }

    public void refresh_dataset(Context context)
    {
        localDataSet = mdo.material_entrys_list();
        notifyDataSetChanged();


    }
    public void refresh_dataset_from_array(String[] dataset,Context context)
{
    localDataSet = dataset;
    notifyDataSetChanged();
}


    public void delet_dialog(String artikel_name,ContentValues data)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setTitle("Löschbestätigung");
        alertDialogBuilder.setMessage("Eintrag "+artikel_name +" ["+data.get("EINHEIT").toString()+"] wirklich Löschen?");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                long r = mdo.delet_material_log_entry(data.get("ID").toString());
                if (r > 0)
                {
                    Toast.makeText(alertDialogBuilder.getContext(), "Eintrag wurde gelöscht!", Toast.LENGTH_SHORT).show();
                    refresh_dataset(parent.getContext());
                }
                else
                {
                    Toast.makeText(alertDialogBuilder.getContext(), "Eintrag wurde NICHT gelöscht!", Toast.LENGTH_SHORT).show();
                }
            }

        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    public void update_log_entry(String id,View view)
    {
        LayoutInflater li = LayoutInflater.from(view.getContext());
        View update_log_entry_dialog_layout = li.inflate(R.layout.update_log_entry_dialog_layout,parent, false);


        AutoCompleteTextView artikel = update_log_entry_dialog_layout.findViewById(R.id.autoCompleteTextView);

        AutoCompleteTextView ls_zulieferer = update_log_entry_dialog_layout.findViewById(R.id.autoCompleteTextView2);

        EditText menge = update_log_entry_dialog_layout.findViewById(R.id.editTextNumberDecimal3);

        Spinner artikel_einheit = update_log_entry_dialog_layout.findViewById(R.id.spinner6);

        TextView ls_date = update_log_entry_dialog_layout.findViewById(R.id.textView81);

        EditText ls_nr= update_log_entry_dialog_layout.findViewById(R.id.editTextText3);
        EditText ls_note= update_log_entry_dialog_layout.findViewById(R.id.editTextText4);

        ImageButton reset_artikel = update_log_entry_dialog_layout.findViewById(R.id.imageButton55);
        ImageButton reset_zulieferer = update_log_entry_dialog_layout.findViewById(R.id.imageButton54);
        ImageButton reset_lsnr = update_log_entry_dialog_layout.findViewById(R.id.imageButton56);

        bg_lsnr = update_log_entry_dialog_layout.findViewById(R.id.bg_lsnr);
        bg_zulieferer = update_log_entry_dialog_layout.findViewById(R.id.bg_zulieferer);

        material_database_ops mdo= new material_database_ops(view.getContext());

        ArrayAdapter<CharSequence> adapter_einheiten = ArrayAdapter.createFromResource(view.getContext(),R.array.einheiten_array, android.R.layout.simple_list_item_activated_1);

        String[] artikel_list = mdo.artikel_list_all();
        ArrayAdapter<String> artikelAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, artikel_list);


        ContentValues data= mdo.material_get_entry_id(id);
        String artikel_name ="";

        //Menge
        menge.setText(data.get("MENGE").toString());


        //Artikel
        artikel.setAdapter(artikelAdapter);

        artikel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selected =  artikel.getText().toString();
                int spos= selected.lastIndexOf("[");
                int epos= selected.lastIndexOf("]");

                String artikel_name = selected.substring(0,spos);
                String einheit = selected.substring(spos+1,epos);
                artikel.setText(artikel_name);

                int index =adapter_einheiten.getPosition(einheit);
                artikel_einheit.setSelection(index);
            }
        });

        try {

            if(data.get("MATERIAL_ID").toString().contains("null"))
            {
                artikel.setText("");


            }
            else
            {
                artikel_name = mdo.get_artikel_param(
                        new String[]{data.get("MATERIAL_ID").toString()},
                        "ID=?",
                        new String[]{"NAME"});
            }

        }catch (Exception e)
        {
            exmsg("090720231132",e);
        }

        //Einheit

        artikel_einheit.setAdapter(adapter_einheiten);
        int index =adapter_einheiten.getPosition(data.get("EINHEIT_ID").toString());
        artikel_einheit.setSelection(index);

        //Zulieferer
        String ls_zulieferere_name ="";

        String[] ls_zulieferer_liste = mdo.zulieferer_list_all("DATE DESC");
        ArrayAdapter<String> ls_zulieferer_liste_adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, ls_zulieferer_liste);
        ls_zulieferer.setAdapter(ls_zulieferer_liste_adapter);

        try {
            ls_zulieferere_name = mdo.get_zulieferer_param(
                    new String[]{data.get("LIEFERANT_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});
        }catch (Exception e)
        {
            Log.d("BASI", e.getMessage());
        }

        artikel.setText(artikel_name);

        //Datum
        ls_date.setText(bsf.convert_date(data.get("DATUM").toString(),"format_database_to_readable"));
        ls_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i,i1 , i2);


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                        String dateString = dateFormat.format(calendar.getTime());


                        ls_date.setText(dateString);
                    }

                });
                datePickerDialog.show();
            }

        });

        ls_zulieferer.setText(ls_zulieferere_name);
        String finalLs_zulieferere_name = ls_zulieferere_name;
        ls_zulieferer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b)
            {
                if (!b)
                {

                    String ls_zulieferere_id = mdo.get_zulieferer_param(
                            new String[]{ls_zulieferer.getText().toString()}, "NAME=?", new String[]{"ID"});


                    if(TextUtils.equals(ls_zulieferere_id,"null"))
                    {
                        AlertDialog.Builder create_new_zulieferer_dialog = new AlertDialog.Builder(parent.getContext());
                        create_new_zulieferer_dialog.setTitle("Aktion");
                        create_new_zulieferer_dialog.setMessage("Zulieferer Exsistiert nicht. Neu erstellen? ");

                        create_new_zulieferer_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                mdo.add_zulieferer(ls_zulieferer.getText().toString());
                            }
                        });
                        create_new_zulieferer_dialog.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        });

                        create_new_zulieferer_dialog.show();

                    }
                    else
                    {
                        String selectet_projekt_data = mdo.get_selectet_projekt();
                        String selectet_projekt_id = selectet_projekt_data.substring(selectet_projekt_data.lastIndexOf("[")+1,selectet_projekt_data.lastIndexOf("]"));
                        colision_test(selectet_projekt_id,ls_nr.getText().toString().trim(),ls_zulieferere_id);

                    }


                }
            }
        });

        ls_nr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b)
            {

                if(!b)
                {

                    String ls_zulieferere_id = mdo.get_zulieferer_param(
                            new String[]{ls_zulieferer.getText().toString()}, "NAME=?", new String[]{"ID"});

                        String selectet_projekt_data = mdo.get_selectet_projekt();
                        String selectet_projekt_id = selectet_projekt_data.substring(selectet_projekt_data.lastIndexOf("[")+1,selectet_projekt_data.lastIndexOf("]"));
                        colision_test(selectet_projekt_id,ls_nr.getText().toString().trim(),ls_zulieferere_id);
                }
                }
        });
        ls_nr.setText(data.get("LSNR").toString());

        ls_note.setText(bsf.URLdecode(data.get("NOTIZ").toString()));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setTitle("Eintrag ändern");
        alertDialogBuilder.setView(update_log_entry_dialog_layout);

        alertDialogBuilder.setIcon(R.drawable.ic_baseline_mode_24);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                ContentValues data_new = new ContentValues();

                data_new.put("ID", data.get("ID").toString());

                String selectet_projekt_data = mdo.get_selectet_projekt();
                String selectet_projekt_id = selectet_projekt_data.substring(selectet_projekt_data.lastIndexOf("[") + 1, selectet_projekt_data.lastIndexOf("]"));

                data_new.put("PROJEKT_NR", mdo.get_projekt_id());
                data_new.put("DATUM", bsf.convert_date(ls_date.getText().toString(), "format_database"));
                data_new.put("LSNR", ls_nr.getText().toString());


                String ls_zulieferere_id = mdo.get_zulieferer_param(
                        new String[]{ls_zulieferer.getText().toString()}, "NAME=?", new String[]{"ID"});
                data_new.put("LIEFERANT_ID", ls_zulieferere_id);

                mdo.add_artikel_to_list(artikel.getText().toString(), artikel_einheit.getSelectedItem().toString());

                String artikel_id = mdo.get_artikel_param(new String[]{artikel.getText().toString().trim(), artikel_einheit.getSelectedItem().toString().trim()}, "NAME=? AND EINHEIT=?", new String[]{"ID"});
                Log.d("BASI Artikel: ", artikel_id);
                data_new.put("MATERIAL_ID", artikel_id);

                data_new.put("MENGE", menge.getText().toString());
                data_new.put("EINHEIT_ID", artikel_einheit.getSelectedItem().toString());
                if (ls_note.getText().toString().isEmpty())
                {
                    data_new.put("NOTIZ","null");
                } else
                {
                    data_new.put("NOTIZ", bsf.URLencode(ls_note.getText().toString()));
                }

                    colision_test(selectet_projekt_id,ls_nr.getText().toString(),ls_zulieferere_id);

                    mdo.update_material_log_entry(data_new);


                    refresh_dataset(parent.getContext());


                //LS,Datum & Lieferant abgleichen mit anderen einträgen, sowie die Fotos abändernm, falls nötig.
                //Testen auf Duplikate
                // ID's auf Duplikate Prüfen

            }

        });
        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        alertDialogBuilder.show();



        //reset
        reset_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artikel.setText("");
            }
        });

        reset_zulieferer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ls_zulieferer.setText("");
            }
        });

        reset_lsnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ls_nr.setText("");
            }
        });

    }


    public void colision_test(String projekt_id,String LSNR,String zuliefere_id)
    {

        String[] selectionArgs= {projekt_id,LSNR,zuliefere_id};
        String where = "PROJEKT_NR=? AND LSNR=? AND LIEFERANT_ID=?";

        if(  mdo.find_similar(SQL_finals.BASI_MATERIAL,selectionArgs,where) > 1)
        {
            bg_lsnr.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.orange));
            bg_zulieferer.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.orange));

            AlertDialog.Builder  similar_warning = new AlertDialog.Builder(parent.getContext());
            similar_warning.setTitle("Warnnung");
            similar_warning.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
            similar_warning.setMessage("Duplikate Einträge55!");
            similar_warning.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();
                }
            });
            similar_warning.show();
        }
        else
        {
            bg_lsnr.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.white));
            bg_zulieferer.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.white));
        }
    }



    public void exmsg(String msg, Exception e)
    {
        Log.e("Exception: Material ->","ID: "+msg+" Message:" + e.getMessage());
        e.printStackTrace();
    }



}
