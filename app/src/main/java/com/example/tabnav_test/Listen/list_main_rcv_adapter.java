package com.example.tabnav_test.Listen;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GnssAntennaInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.TorchState;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.projekt_ops;

public  class list_main_rcv_adapter extends RecyclerView.Adapter<list_main_rcv_adapter.ViewHolder>
{
    Context context;
    ViewGroup parent;
    Basic_funct bsf;
    list_main_db_ops list_db_ops;
    projekt_ops projekt_ops;
    private String[] localDataSet = null;

    public list_main_rcv_adapter(Context context,String[] dataset)
    {
        this.context = context.getApplicationContext();
        this.localDataSet=dataset;

        list_db_ops = new list_main_db_ops(context);
        projekt_ops = new projekt_ops(context);
        bsf = new Basic_funct();


    }

    @NonNull
    @Override

    public list_main_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        list_db_ops = new list_main_db_ops(context);
        projekt_ops = new projekt_ops(context);
        this.parent = parent;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_main_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull list_main_rcv_adapter.ViewHolder holder, int position)
    {
        String[] colum = localDataSet[position].split(",");
        String ID = colum[0];
        String NAME = colum[1];
        String NOTE = colum[2];


        holder.getTabel_name().setText(NAME);
        holder.getTabel_details().setText(NOTE);
        holder.getTable_open().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //Start a new Activity
               Intent List_items_activity = new Intent(context ,list_items.class);

                List_items_activity.putExtra("list_id",ID);
                List_items_activity.putExtra("list_name",NAME);
                List_items_activity.putExtra("list_details",NOTE);
                context.startActivity(List_items_activity);
            }
        });

        holder.getTable_transfer_items().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Not implemented
                Toast.makeText(context, "Nicht implementiert", Toast.LENGTH_SHORT).show();
            }
        });

        holder.getTable_copy_items().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Not implemented
                Toast.makeText(context, "Nicht implementiert", Toast.LENGTH_SHORT).show();
            }
        });

        holder.getTable_send().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Not implemented
                Toast.makeText(context, "Nicht implementiert", Toast.LENGTH_SHORT).show();
            }
        });

        holder.getTable_settings().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                edit_list(ID,NAME,NOTE);
            }
        });

        holder.getTable_delete().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               delete_list(ID);
            }
        });



    }

    private void edit_list(String id, String name, String note)
    {
        AlertDialog.Builder alertDialogBuilder;
        //Implements view Laoyut for the dialog
        View view_edit_list = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_list ,null);

        TextView list_name = view_edit_list.findViewById(R.id.editTextText6);
        TextView list_details = view_edit_list.findViewById(R.id.editTextText7);
        //Add the values to the dialog
        list_name.setText(name);
        list_details.setText(note);

        alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        alertDialogBuilder.setTitle("Bearbeiten");
        alertDialogBuilder.setIcon(R.drawable.baseline_mode_edit_24);
        alertDialogBuilder.setView(view_edit_list);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //Save the changes
                try {
                    String name = list_name.getText().toString();
                    String details = list_details.getText().toString();
                    int c = list_db_ops.update_list(id, name, details);
                    if(c>0)
                    {
                        Toast.makeText(context, "Änderungen gespeichert", Toast.LENGTH_SHORT).show();
                        reload_dataset(list_db_ops.get_lists_form_projekt(projekt_ops.projekt_get_selected_id()));
                        dialogInterface.cancel();

                    }
                } catch (Exception e)
                {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        alertDialogBuilder.show();

    }


    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tabel_name;
        TextView tabel_details;
        ImageButton table_open;
        ImageButton table_transfer_items;
        ImageButton table_copy_items;
        ImageButton table_send;
        ImageButton table_settings;
        ImageButton table_delete;



        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            context= itemView.getContext();
            tabel_name = itemView.findViewById(R.id.textView58);
            tabel_details = itemView.findViewById(R.id.textView97);
            table_open = itemView.findViewById(R.id.imageButton88);
            table_transfer_items = itemView.findViewById(R.id.imageButton81);
            table_copy_items = itemView.findViewById(R.id.imageButton84);
            table_send = itemView.findViewById(R.id.imageButton85);
            table_settings = itemView.findViewById(R.id.imageButton77);
            table_delete = itemView.findViewById(R.id.imageButton78);
        }
        public TextView getTabel_name()
            {
            return tabel_name;
        }
        public TextView getTabel_details()
        {
            return tabel_details;
        }
        public ImageButton getTable_open()
        {
            return table_open;
        }
        public ImageButton getTable_transfer_items()
            {
            return table_transfer_items;
        }
        public ImageButton getTable_copy_items()
        {
            return table_copy_items;
        }
        public ImageButton getTable_send()
            {
            return table_send;
        }
        public ImageButton getTable_settings()
        {
            return table_settings;
        }
        public ImageButton getTable_delete()
            {
            return table_delete;
        }
    }

    public void reload_dataset(String [] data)
    {

        this.localDataSet = data;
        notifyDataSetChanged();
    }


    public void delete_list(String list_id)
    {

        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(parent.getContext());
        alertDialogBuilder.setTitle("Bestätigen");
        alertDialogBuilder.setIcon(R.drawable.baseline_delete_24);
        alertDialogBuilder.setMessage("Liste löschen?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                try {
                    int c = list_db_ops.delete_list(list_id);
                    if (c > 0)
                    {
                        Toast.makeText(context, "Liste gelöscht", Toast.LENGTH_SHORT).show();

                        reload_dataset(list_db_ops.get_lists_form_projekt(projekt_ops.projekt_get_selected_id()));
                        dialogInterface.cancel();
                    }
                } catch (Exception e)
                {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        alertDialogBuilder.show();


    }
}
