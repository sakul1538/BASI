package com.example.tabnav_test.Kamera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;
import com.example.tabnav_test.projekt_ops;

import java.io.File;
import java.util.ArrayList;

public class kamera_imageset_RecyclerViewAdapter  extends RecyclerView.Adapter<kamera_imageset_RecyclerViewAdapter.ViewHolder>
{
    Context context;
    String dir = "";
    ArrayList<String> image_set = new ArrayList<>();

    public kamera_imageset_RecyclerViewAdapter(Context context)
    {
        this.context = context;
        projekt_ops projekt = new projekt_ops(context);
        dir  = projekt.projekt_get_current_root_dir_images_temp();
        scan();


    }
    public void scan()
    {
        File scan_dir = new File(this.dir);
        if(scan_dir.exists())
        {
            String[]  files_found= scan_dir.list();
            if(files_found.length >0)
            {
                for(String file:files_found)
                {
                    String path= this.dir+"/"+file;
                    this.image_set.add(path);
                    Log.d("BASI", path);
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kamera_image_view_pager_layout, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if(image_set.size() >0)
    {
        try {

            Bitmap image_scaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(image_set.get(position)),900,900,false);
            holder.getImageView().setImageBitmap(image_scaled);
            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {


                    LayoutInflater inflater = LayoutInflater.from(context);
                    View pic_view_UI = inflater.inflate(R.layout.show_picture, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    ImageView photo = pic_view_UI.findViewById(R.id.imageView4);
                    TextView image_path = pic_view_UI.findViewById(R.id.textView65);
                    ImageButton delet_image= pic_view_UI.findViewById(R.id.imageButton63);
                    ImageButton rotate_image= pic_view_UI.findViewById(R.id.imageButton62);

                    try {
                        photo.setImageBitmap(BitmapFactory.decodeFile(image_set.get(position)));
                        image_path.setText(image_set.get(position).toString());

                    } catch (Exception e) {
                        photo.setImageResource(R.drawable.ic_baseline_error_outline_24);
                        image_path.setText(e.getMessage().toString());
                    }

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(pic_view_UI);

                    alertDialogBuilder.setTitle("Viewer");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });
        } catch (Exception e)
        {
            Log.d("BASI", e.getMessage().toString());

        }
    }
    }

    @Override
    public int getItemCount()
    {

        return image_set.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final  ImageView imageView;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView6);

        }
        public ImageView getImageView()
        {
            return imageView;
        }
    }
}
