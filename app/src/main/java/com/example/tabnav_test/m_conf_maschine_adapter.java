package com.example.tabnav_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
        m_database_ops mdo =new m_database_ops(context);

        int posi = position;
        Uri img_uri=null;
;
        String[] datasplit = localDataSet[posi].split(",");
        /*
        Index:

        ID=0
        DATE=1
        TIME=2
        NR=3
        NAME=4
        CATEOGRY=5
        COUNTER=6
        NOTE=7
        PIC_SRC=8
        ONOFF_FLAG=9


        */

        holder.m_name.setText("["+datasplit[3]+"] "+datasplit[4] );
        holder.m_category.setText(datasplit[5]);
        holder.m_counter.setText(datasplit[6]);

        Uri u = Uri.parse(datasplit[8]);
        Log.d("BASI USI",datasplit[8]);
        holder.m_view_pic.setImageURI(u);


        holder.delet_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Bestätige:");
                alertDialogBuilder.setMessage("Element aus der Datenbank entfernen?");
                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        int response =mdo.delet_id(datasplit[0]);
                        Toast.makeText(context.getApplicationContext(),String.valueOf(response),Toast.LENGTH_SHORT).show();
                        localDataSet = mdo.get_maschinen(RROJ_NR);
                        notifyItemRemoved(posi);
                        notifyItemRangeChanged(posi,localDataSet.length);
                    }
                });

                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Aktion abgebrochen!", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.show();
            }});
    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;

    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView m_name;
        private final TextView m_category;
        private final TextView m_counter;

        private final ImageButton delet_button;

        private final Switch switch_onoff;

        private final ImageView m_view_pic;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            m_name = (TextView) itemView.findViewById(R.id.textView37);
            m_category = (TextView) itemView.findViewById(R.id.textView27);
            m_counter = (TextView) itemView.findViewById(R.id.textView35);

            m_view_pic = (ImageView) itemView.findViewById(R.id.m_imageView);

            switch_onoff = (Switch) itemView.findViewById(R.id.switch1);

            delet_button = (ImageButton) itemView.findViewById(R.id.imageButton5);

        }
    }
}
