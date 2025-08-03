package com.example.tabnav_test.Listen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class list_items_rcv_adapter extends RecyclerView.Adapter<list_items_rcv_adapter.ViewHolder>
{
    Context context;
    private String[] localDataSet = null;
    private String list_id = null;
    list_main_db_ops db_ops;
    private String items_id = "";
    public list_items_rcv_adapter(String[] a, String list_id, Context context)
    {
        this.localDataSet = a;
        this.list_id =list_id;
        this.context = context;


    }

    @NonNull
    @Override
    public list_items_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        db_ops = new list_main_db_ops(context);
        items_id = db_ops.get_items_id(this.list_id);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_rcv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        
        holder.getItem_name().setText(localDataSet[position]);

    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
    TextView item_name;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            item_name = itemView.findViewById(R.id.textView100);
        }
        public TextView getItem_name()
        {
            return item_name;
        }
    }
}
