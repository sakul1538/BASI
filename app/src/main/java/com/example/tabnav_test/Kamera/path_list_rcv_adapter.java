package com.example.tabnav_test.Kamera;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class path_list_rcv_adapter extends RecyclerView.Adapter<path_list_rcv_adapter.ViewHolder>
{
    Context context;
    private String[] localDataSet = {"A","B","C","D","E","F","G"};
    public path_list_rcv_adapter(Context context)
    {
        kamera_spinner spinnerops = new kamera_spinner(context);
        localDataSet = spinnerops.getall("0");
        this.context = context;
    }

    @NonNull
    @Override
    public path_list_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        context =parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.kamera_save_path_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull path_list_rcv_adapter.ViewHolder holder, int position)
    {
        holder.get_name().setText(localDataSet[position]);
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        RadioButton select_src;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textView68);
            select_src = (RadioButton) itemView.findViewById(R.id.radioButton);

        }
        public  TextView get_name(){return name;}
        public  RadioButton getSelect_src(){return select_src;}
    }
}
