package com.example.tabnav_test;


import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class m_filter_rcv_adapter extends RecyclerView.Adapter<m_filter_rcv_adapter.ViewHolder>
{
    Context context;
    ViewGroup par;

    String[] categorys;

    m_database_ops mdo;

    ContentValues cv;

    static final String RROJ_NR = "0";

    public m_filter_rcv_adapter()
    {
        this.categorys = Resources.getSystem().getStringArray(R.array.m_categorys);
        for(int i=0;i<this.categorys.length;i++)
        {
            this.categorys[i] = this.categorys[i]+",0";
            //  Log.d("Array",categorys[i].toString());

        }

    }

    @NonNull
    @Override
    public m_filter_rcv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        context = parent.getContext();
        par = parent;
        View view = LayoutInflater.from(context).inflate(R.layout.m_category_filter_rcview_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull m_filter_rcv_adapter.ViewHolder holder, int position)
    {
        int posi = position;
      holder.m_switch_cat_name.setText(categorys[posi].substring(0,categorys[posi].length()-2));

      holder.m_switch_cat_name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b)
          {
              if(b == true)
              {
                  categorys[posi] = categorys[posi].substring(0,categorys[posi].length()-1);
                  categorys[posi]=categorys[posi]+"1";
                  holder.m_switch_cat_name.setChecked(true);

              }
              if(b == false)
              {

                  categorys[posi] = categorys[posi].substring(0,categorys[posi].length()-1);
                  categorys[posi]=categorys[posi]+"0";
                  holder.m_switch_cat_name.setChecked(false);

              }
             /* for(String val : categorys)
              {
                  Log.d("BASI",val);


              }*/

          }

      });
    }

    @Override
    public int getItemCount()
    {
        return categorys.length;
    }


    public ArrayList<String> filter_getselectet_categorys()
    {
        ArrayList<String> selectet = new ArrayList<String>();
        try {
            for(String i : categorys)
            {
               String[] t = i.split(",");

               if(t[1].contains("1") ==true) //Kategoirenamen drüfen KEINE 1 oder 0 enthalten
               {
                   selectet.add(t[0]);
               }
            }
        } catch (Exception e)
        {
            exmsg("020220231049",e);
        }
        return selectet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {



        private final Switch m_switch_cat_name;


        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            m_switch_cat_name = (Switch) itemView.findViewById(R.id.m_switch_cat_name);

        }
    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: m_filter_rcv_adapter->","ID: "+msg+" Message:" +e.getMessage().toString());
        e.printStackTrace();
    }

}
