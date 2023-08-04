package com.example.tabnav_test.material;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;
import com.example.tabnav_test.ls_log_view_rcv_adapter;

import java.io.File;

public class galery_adaper extends RecyclerView.Adapter<galery_adaper.ViewHolder>
{

    String meine_eanderung;
    String[] localDataSet={"null"};
    String proj_nr;
    String proj_src;
    String name_idenifer;
    String ls_id;
    Context context;
    ContentValues data;

    public galery_adaper(ContentValues data, Context context)
    {
        material_database_ops mdo = new material_database_ops(context);
        this.data = data;
        this.context = context;
        this.proj_nr = data.get("PROJEKT_ID").toString();
        this.ls_id = data.get("LSNR").toString();
        this.proj_src = mdo.get_selectet_projekt_root().split(",")[2].replace("primary:",Environment.getExternalStorageDirectory()+"/")+"/Lieferscheine";

        String name_zuleferer =  mdo.get_zulieferer_param(
                new String[]{data.get("ZULIEFERER_ID").toString()},
                "ID=?",
                new String[]{"NAME"});


        Log.d("BASI_SRC",this.proj_src);
        Log.d("BASI_DATUM",data.get("DATUM").toString());


        this.name_idenifer = name_zuleferer+"_LSNR_"+data.get("LSNR")+"@"+data.get("DATUM").toString().replace(".","");

        File f  = new File(this.proj_src);
        String []filelist = f.list();
        int c = 0;
      for(String filename: filelist)
      {
          File f2 = new File(this.proj_src+"/"+filename);
          if(f2.isFile())
          {
              if(filename.contains(name_idenifer) == true)
              {
                  this.localDataSet[c]=this.proj_src+"/"+filename;
              }
          }
      }



    }

    @NonNull
    @Override
    public galery_adaper.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull galery_adaper.ViewHolder holder, int position)
    {
        if(this.localDataSet[0] !="null")
        {
            try {

                String path =this.proj_src+"/"+this.localDataSet[position];
                File f = new File(path);
                if(f.isFile())
                {
                    if(path.contains(this.name_idenifer))
                    {
                        Bitmap bMap = BitmapFactory.decodeFile(path);
                        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 200, 200, true);

                        holder.Image_view().setImageBitmap(bMapScaled);
                    }

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


    }

    @Override
    public int getItemCount()
    {
        return localDataSet.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView image;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image =(ImageView)  itemView.findViewById(R.id.imageView5);
        }
        public ImageView Image_view(){return image;}
    }



}
