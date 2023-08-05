package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.Basic_func_img;
import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;

import java.io.File;
import java.io.FileOutputStream;

public class galery_adaper extends RecyclerView.Adapter<galery_adaper.ViewHolder>
{
    private static final String TAG = "BASI"; // höhe
    private static final int foto_preview_w = 800; //breite
    private static final int foto_preview_h = 800; // höhe
   
    String[] localDataSet={"null"};
    String proj_nr;
    String proj_src;
    String name_idenifer;
    String ls_id;
    Context context;
    ContentValues data;
    Basic_funct bsf  =new Basic_funct();


    public galery_adaper(String[] localDataSet, ContentValues data, Context context)
    {
        this.localDataSet = localDataSet;
        this.context = context;
        this.data  =data;
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
        String path = this.localDataSet[position];
        if(this.localDataSet[position] != "")
        {
            if (bsf.detect_extension(path).contains(".pdf")) //Pdf öffnen
            {
                Bitmap ls_picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdflogo);
                Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
                holder.Image_view().setImageBitmap(ls_picture_scaled);

            } else
            {
                Bitmap bMap = BitmapFactory.decodeFile(path);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 200, 200, true);
                holder.Image_view().setImageBitmap(bMapScaled);
            }
        }





        holder.Image_view().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ls_image_viewer(path,view.getContext());
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if(localDataSet[0]=="")
        {
            return  0;
        }
        else
        {
            return localDataSet.length;

        }
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


    public void ls_image_viewer(String file_url,Context context)
    {
        ImageView photo_viewer;


        try {

            Basic_func_img bsf = new Basic_func_img();

            if (file_url != "null")
            {
                LayoutInflater myLayout = LayoutInflater.from(context);
                View pic_view_UI = myLayout.inflate(R.layout.show_picture, null);

                TextView path_value = pic_view_UI.findViewById(R.id.textView65);

                photo_viewer = (ImageView) pic_view_UI.findViewById(R.id.imageView4);

                ImageButton refresh_image = (ImageButton) pic_view_UI.findViewById(R.id.imageButton60);
                ImageButton rotate_right = (ImageButton) pic_view_UI.findViewById(R.id.imageButton62);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(pic_view_UI);
                alertDialogBuilder.setTitle("Viewer");

                path_value.setText(file_url.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), ""));

                if (bsf.detect_extension(file_url).contains(".pdf")) //Pdf öffnen
                {
                    Bitmap ls_picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.pdflogo);
                    Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
                    photo_viewer.setImageBitmap(ls_picture_scaled);

                } else
                {

                   Bitmap  ls_picture = BitmapFactory.decodeFile(file_url);
                   Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);
                    try {
                        photo_viewer.setImageBitmap(ls_picture_scaled);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    rotate_right.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Matrix matrix = new Matrix();
                            Bitmap bMap = BitmapFactory.decodeFile(file_url);

                            matrix.setRotate(90);
                            Bitmap bMapRotation = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);

                            File file = new File(file_url);
                            if (file.exists())
                            {
                                file.delete();
                            }
                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bMapRotation.compress(Bitmap.CompressFormat.JPEG, 50, out);
                                out.flush();
                                out.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Bitmap ls_picture = BitmapFactory.decodeFile(file_url);
                            Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);

                            photo_viewer.setImageBitmap(ls_picture_scaled);

                        }
                    });

                    refresh_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Todo fallbacks wenn kein Bild existiert, damit man noch eines Hinzufügen kann.

                           // take_picture(file_url, TAKE_IMAGE_REFRESH_MODE, view.getContext());
                        }
                    });

                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            Bitmap ls_picture = BitmapFactory.decodeFile(file_url);
                            Bitmap ls_picture_scaled = Bitmap.createScaledBitmap(ls_picture, foto_preview_w, foto_preview_h, true);

                            photo_viewer.setImageBitmap(ls_picture_scaled);
                            reload_images();
                            dialogInterface.cancel();
                        }
                    });

                    alertDialogBuilder.setNeutralButton("Bild entfernen", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                    // set prompts.xml to alertdialog builder

                                    alertDialogBuilder.setTitle("Artikelverwaltung");
                                    alertDialogBuilder.setMessage("Bild löschen?\n" + file_url);

                                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i)
                                        {
                                            File f = new File(file_url);
                                            if(f.exists())
                                            {
                                                f.delete();
                                            }
                                            reload_images();
                                            dialogInterface.cancel();
                                        }
                                    });

                                    alertDialogBuilder.setNegativeButton("Abbrecen", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();


                                    dialogInterface.cancel();

                                }
                            }
                    );
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }


            } //else?
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reload_images()
    {
        try {
            material_database_ops mdo = new material_database_ops(this.context);
            String t = mdo.media_scanner(this.data);
            if(t !="")
            {
                this.localDataSet =mdo.media_scanner(this.data).split(",");

            }
            else
            {
                this.localDataSet = new String[]{""};
            }

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        notifyDataSetChanged();
    }


}
