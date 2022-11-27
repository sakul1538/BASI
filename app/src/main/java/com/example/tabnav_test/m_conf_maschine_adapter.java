package com.example.tabnav_test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.m_conf_maschine_adapter.ViewHolder;



public class m_conf_maschine_adapter extends RecyclerView.Adapter<ViewHolder>
{
    public m_conf_maschine_adapter(String[] localDataSet)
    {
        this.localDataSet = localDataSet;
    }

    private String[] localDataSet;
    Context context;
    ViewGroup par;
    log_fav spinnerops;
    static final String RROJ_NR="0";
    Basic_funct bsf;

    @NonNull
    @Override
    public m_conf_maschine_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        context =parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.m_conf_masch_list_layout, parent, false);
       // spinnerops =new log_fav(context);
        ///bsf=new Basic_funct();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull m_conf_maschine_adapter.ViewHolder holder, int position)
    {


        holder.m_name.setText(localDataSet[position]);
        holder.m_view_pic.setImageResource(R.drawable.bagger);
    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView m_name;
        private final ImageView m_view_pic;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            m_name = (TextView) itemView.findViewById(R.id.textView37);
            m_view_pic = (ImageView) itemView.findViewById(R.id.m_imageView);

        }
    }
}
