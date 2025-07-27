package com.example.tabnav_test.Personal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class personalstamm_adapter extends RecyclerView.Adapter<personalstamm_adapter.ViewHolder>
{
    private String[] localDataSet = {"Lukas Grossen","Varli Hasan","Erni Meret"};

    public personalstamm_adapter()
    {

    }
    @NonNull
    @Override
    public personalstamm_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personalstamm_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull personalstamm_adapter.ViewHolder holder, int position)
    {
        holder.get_names().setText(localDataSet[position]);
    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.textView96);

        }
        public  TextView get_names()
        {
            return name;
        }
    }
}
