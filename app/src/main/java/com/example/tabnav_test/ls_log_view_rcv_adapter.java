package com.example.tabnav_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.material.material_database_ops;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ls_log_view_rcv_adapter extends RecyclerView.Adapter<ls_log_view_rcv_adapter.ViewHolder>
{
    String[] localDataSet ;
    private ViewGroup parent;
    material_database_ops mdo;


    public ls_log_view_rcv_adapter(String[] ls_log_view_rcv_adapter)
    {
        this.localDataSet = ls_log_view_rcv_adapter;

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
            data.put("DATUM",items[2]);
            data.put("LSNR",items[3]);
            data.put("ZULIEFERER_ID",items[4]);
            data.put("ARTIKEL_ID",items[5]);
            data.put("MENGE",items[6]);
            data.put("EINHEIT",items [7]);
            data.put("SRC",items[8]);

           if(items[9].equals("null"))
           {
               data.put("NOTIZ","");
           }
           else
           {
               data.put("NOTIZ",items[9]);
           }

            String artikel_name = mdo.get_artikel_param(
                    new String[]{data.get("ARTIKEL_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});

            holder.artikel().setText(artikel_name +" ["+data.get("EINHEIT").toString()+"]");

            String name_zuleferer =  mdo.get_zulieferer_param(
                    new String[]{data.get("ZULIEFERER_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});

            holder.zulieferer().setText(name_zuleferer);
            holder.datum().setText(data.get("DATUM").toString());
            holder.lsnr().setText(data.get("LSNR").toString());
            holder.notiz().setText(data.get("NOTIZ").toString());

            //Image Buttons

            holder.getDelet_entry().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                   // delet_dialog(artikel_name,data);
                     update_log_entry(data.get("ID").toString(), view);
                }
            });

            holder.getMedia_entry().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                  //  material_log_activity.bsfi.ls_image_viewer(material_log_activity.bsfi.URLdecode(data.get("SRC").toString()), parent.getContext());
                }
            });

    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private  final TextView artikel;
        private  final TextView zulieferer;
        private  final TextView datum;
        private  final TextView lsnr;
        private  final TextView noitz;
        private final ImageButton media_entry;

        private  final ImageButton delet_entry;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            artikel = (TextView) itemView.findViewById(R.id.textView78);
            zulieferer = (TextView) itemView.findViewById(R.id.textView67);
            datum = (TextView) itemView.findViewById(R.id.textView77);
            lsnr = (TextView) itemView.findViewById(R.id.textView69);
            noitz = (TextView) itemView.findViewById(R.id.textView75);
            delet_entry = (ImageButton) itemView.findViewById(R.id.imageButton42);
            media_entry = (ImageButton) itemView.findViewById(R.id.imageButton44);
        }

        public TextView artikel() {return artikel;}
        public TextView zulieferer() {return zulieferer;}
        public TextView  datum() {return datum;}
        public TextView  lsnr() {return lsnr;}
        public TextView  notiz() {return noitz;}
        public ImageButton  getMedia_entry() {return media_entry;}

        public ImageButton  getDelet_entry() {return delet_entry;}
    }

    public void refresh_dataset(Context context)
    {

        localDataSet = mdo.material_entrys_list();
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

        Spinner artikel_einheit = update_log_entry_dialog_layout.findViewById(R.id.spinner6);

        TextView ls_date = update_log_entry_dialog_layout.findViewById(R.id.textView81);

        EditText ls_nr= update_log_entry_dialog_layout.findViewById(R.id.editTextText3);
        EditText ls_note= update_log_entry_dialog_layout.findViewById(R.id.editTextText4);

        ImageButton ls_media = update_log_entry_dialog_layout.findViewById(R.id.imageButton61);
        ImageButton reset_artikel = update_log_entry_dialog_layout.findViewById(R.id.imageButton55);
        ImageButton reset_zulieferer = update_log_entry_dialog_layout.findViewById(R.id.imageButton54);
        ImageButton reset_lsnr = update_log_entry_dialog_layout.findViewById(R.id.imageButton56);

        material_database_ops mdo= new material_database_ops(view.getContext());

        ArrayAdapter<CharSequence> adapter_einheiten = ArrayAdapter.createFromResource(view.getContext(),R.array.einheiten_array, android.R.layout.simple_list_item_activated_1);

        String[] artikel_list = mdo.artikel_list_all();
        ArrayAdapter<String> artikelAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, artikel_list);


        ContentValues data= mdo.material_get_entry_id(id);
        String artikel_name ="";

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
            artikel_name = mdo.get_artikel_param(
                    new String[]{data.get("MATERIAL_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});

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

        String[] ls_zulieferer_liste = mdo.zulieferer_list_all();
        ArrayAdapter<String> ls_zulieferer_liste_adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_dropdown_item_1line, ls_zulieferer_liste);
        ls_zulieferer.setAdapter(ls_zulieferer_liste_adapter);

        try {
            ls_zulieferere_name = mdo.get_zulieferer_param(
                    new String[]{data.get("LIEFERANT_ID").toString()},
                    "ID=?",
                    new String[]{"NAME"});
        }catch (Exception e)
        {
            Log.d("BasI",e.getMessage().toString());
        }

        artikel.setText(artikel_name);

        //Datum
        ls_date.setText(data.get("DATUM").toString());
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
        ls_nr.setText(data.get("LSNR").toString());
        ls_note.setText(data.get("NOTIZ").toString());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setTitle("Eintrag ändern");
        alertDialogBuilder.setView(update_log_entry_dialog_layout);

        alertDialogBuilder.setIcon(R.drawable.ic_baseline_mode_24);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
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

    public void exmsg(String msg, Exception e)
    {
        Log.e("Exception: Material ->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }



}
