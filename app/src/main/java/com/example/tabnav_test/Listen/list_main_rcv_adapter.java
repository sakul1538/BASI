package com.example.tabnav_test.Listen;

import static java.security.AccessController.getContext;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;
import com.example.tabnav_test.db_ops;
import com.example.tabnav_test.projekt_ops;

import java.security.AccessControlContext;

public  class list_main_rcv_adapter extends RecyclerView.Adapter<list_main_rcv_adapter.ViewHolder>
{
    Context context;
    list_main_db_ops list_db_ops;
    projekt_ops projekt_ops;
    private String[] localDataSet = null;

    public list_main_rcv_adapter(Context context,String[] dataset)
    {
        this.context = context.getApplicationContext();
        this.localDataSet=dataset;

    }

    @NonNull
    @Override

    public list_main_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_main_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull list_main_rcv_adapter.ViewHolder holder, int position)
    {
        holder.getTabel_name().setText(localDataSet[position]);
        holder.getTable_open().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        holder.getTable_transfer_items().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        holder.getTable_copy_items().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        holder.getTable_send().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {}
        });

        holder.getTable_settings().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {}
        });

        holder.getTable_delete().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {}
        });



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
    public void reload(String [] data)
    {
        this.localDataSet = data;
        notifyDataSetChanged();
    }

}
