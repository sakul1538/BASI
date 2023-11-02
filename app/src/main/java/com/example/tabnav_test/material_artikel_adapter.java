package com.example.tabnav_test;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.material.material_database_ops;

public class material_artikel_adapter extends RecyclerView.Adapter<material_artikel_adapter.ViewHolder>
{

    String[] localDataSet ;
    ViewGroup parent;

    public material_artikel_adapter(String[] material_artikel_adapter)
    {
       this.localDataSet = material_artikel_adapter;

    }

    @NonNull
    @Override
    public material_artikel_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_artikel_liste_layout, parent, false);
        this.parent = parent;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull material_artikel_adapter.ViewHolder holder, int position)
    {

        // Replace the contents of a view (invoked by the layout manager)
        int spos= localDataSet[position].lastIndexOf("[");
        int epos= localDataSet[position].lastIndexOf("]");

         String artikel = localDataSet[position].substring(0,spos).trim();
        String einheit = localDataSet[position].substring(spos+1,epos).trim();



        holder.getTextView().setText(artikel +" ("+einheit+")");


        holder.update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {  //FIXME Feedback, Fehler ausbügeln

                LayoutInflater li = LayoutInflater.from(view.getContext());
                View view_artikel = li.inflate(R.layout.material_artikel_change_dialog,parent, false);

                EditText edit_artikel = view_artikel.findViewById(R.id.editTextText2_artikel);
                Spinner spinner_einheit = view_artikel.findViewById(R.id.spinner_artikel_einheit);

                edit_artikel.setText(artikel);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),R.array.einheiten_array, android.R.layout.simple_spinner_item);
                int index =adapter.getPosition(einheit);
                spinner_einheit.setSelection(index);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setView(view_artikel);
                alertDialogBuilder.setTitle("Eintrag ändern");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        material_database_ops mdo = new material_database_ops(view.getContext());

                        String artikel_to = edit_artikel.getText().toString().trim();
                        String einheit_to  = spinner_einheit.getSelectedItem().toString().trim();

                        long r = mdo.update_artikel(artikel,einheit,artikel_to,einheit_to);
                        Toast.makeText(view.getContext(), String.valueOf(r), Toast.LENGTH_SHORT).show();
                        refresh_dataset(view.getContext());

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

        holder.del_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Löschen bestätigen");
                alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
                alertDialogBuilder.setMessage("Artikel "+artikel+" wirklich löschen?").setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        material_database_ops mdo = new material_database_ops(view.getContext());
                        try {
                            mdo.artikel_delet(artikel,einheit);
                            refresh_dataset(view.getContext());
                        }catch (Exception e)
                        {
                            Log.d("BASI", e.getMessage().toString());
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

    }
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public void refresh_dataset(Context context)
    {

            material_database_ops mdo = new material_database_ops(context);
            localDataSet = mdo.artikel_list_all();
            notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView artikel_name;
        private final  ImageButton del_button;
        private final  ImageButton update_button;
        public ViewHolder(@NonNull View itemView)
        {
            // Define click listener for the ViewHolder's View
            super(itemView);
            artikel_name = (TextView) itemView.findViewById(R.id.textView70);
            del_button = (ImageButton) itemView.findViewById(R.id.imageButton59);
            update_button = (ImageButton) itemView.findViewById(R.id.imageButton58);
        }
        public TextView getTextView()
        {
            return artikel_name;
        }
        public ImageButton del_button()
        {
            return del_button;
        }
        public ImageButton update_button()
        {
            return update_button;
        }
    }


}



