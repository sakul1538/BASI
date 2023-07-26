package com.example.tabnav_test;



import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Basic_func_img extends Basic_funct
{

   public ImageView photo = null;
   public String image_path = null;




    public Bitmap makeBitmap_textstamp(String path, String text_stamp) {
        //path =  /storage/emulated/0/DCIM/...

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;

        Bitmap bMap = BitmapFactory.decodeFile(path);

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMap.getWidth(), bMap.getHeight(), true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(225, 20, 225));
        paint.setTextSize(50);


        String timestamp = this.date_refresh() + "  " + this.time_refresh() + " " + text_stamp;

        Canvas canvas = new Canvas(bMapScaled);

        canvas.drawText(timestamp, 20, bMapScaled.getHeight() - 20, paint);


        return bMapScaled;

    }


    public Bitmap Bitmap_adjust(String paht, int bitmap_dim)
    {

        Bitmap bitmap_final = null;
        Bitmap bMapRotation = null;

        int rotation = 0;
        int width = 0;
        int height = 0;

        try {

            Bitmap bMap = BitmapFactory.decodeFile(paht);

            ExifInterface exif = null;

            exif = new ExifInterface(paht);
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);


            Matrix matrix = new Matrix();

            switch (rotation)
            {
                case 0:

                    log("Keine Information zur Ausrichtung gefunden!");
                    log(paht);
                    log_div();

                    bitmap_final  = Bitmap_setScaling(bMap,bitmap_dim);

                    break;
                    case 3:

                        log("Ausrichtung:3");
                        log(paht);
                        log_div();

                        matrix.setRotate(180);
                        bMapRotation = Bitmap.createBitmap(bMap, 0, 0,width, height, matrix, true);
                        bitmap_final  = Bitmap_setScaling(bMapRotation,bitmap_dim);

                    break;

                case 6:

                        log("Ausrichtung:6");
                        log(paht);
                        log_div();

                        matrix.setRotate(90);
                        bMapRotation = Bitmap.createBitmap(bMap, 0, 0,width, height, matrix, true);
                        bitmap_final  = Bitmap_setScaling(bMapRotation,bitmap_dim);
                    break;

                default:

                        bitmap_final  = Bitmap_setScaling(bMap,bitmap_dim);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return bitmap_final;
    } // Funktion ende

    
    
    public Bitmap Bitmap_setScaling(Bitmap bMap,int bitmap_dim)
    {
        Bitmap bitmap_final=null;
        int width = bMap.getWidth();
        int height = bMap.getHeight();
        
        if (bitmap_dim == 0) 
        {
            if (width > height) 
            {
                bitmap_dim = width;
                
            } else 
            {
                bitmap_dim = height;
            }
            log("Keine Skalierung");
            log("Bitmap dimension_max:" + bitmap_dim);
            log_div();
        }

        if (width > height) //Wenn Querformat
        {
            log("Bild im Querformat"+"[WIDTH:"+width+" /"+"HEIGHT:"+height);
            log_div();
            bitmap_final = Bitmap.createScaledBitmap(bMap, bitmap_dim, bitmap_dim / 2, true);

        } else // Wenn Hochformat
        {
            log("Bild im Hochformat"+"[WIDTH:"+width+" /"+"HEIGHT:"+height+"]");
            log_div();
            bitmap_final = Bitmap.createScaledBitmap(bMap, bitmap_dim / 2, bitmap_dim, true);
        }

        return bitmap_final;

    }

    public void ls_image_viewer(String path,String filename_contains,Context context)
    {

        //path Bsp: /storage/emulated/0/DCIM/Baustellen /Martinsheim Süd/Lieferscheine/Hydro Nico_NR_1212@22072023_ID_1408876708414326862.jpeg
        //filename_contains Bsp: Hydro Nico_NR_1212@22072023
        /*
        LayoutInflater myLayout = LayoutInflater.from(context);
        View pic_view_UI = myLayout.inflate(R.layout.show_picture, null);

        TextView path_value= pic_view_UI.findViewById(R.id.textView65);

        ImageButton refresh_image =(ImageButton) pic_view_UI.findViewById(R.id.imageButton60);
        photo = (ImageView) pic_view_UI.findViewById(R.id.imageView4);



        try
        {
            image_path= this.URLdecode(foto_path);
            path_value.setText(image_path);
        }
        catch (Exception e)
        {
            path_value.setText("");

            exmsg("220720231435",e);
        }



        refresh_image.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //Todo fallbacks wenn kein Bild existiert, damit man noch eines Hinzufügen kann.
                    dispatchTakePictureIntent(view.getContext(),image_path);
                }
            });


        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            if(foto_path != "null")
            {
                photo.setImageBitmap(BitmapFactory.decodeFile(foto_path));

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(pic_view_UI);
                alertDialogBuilder.setTitle("Viewer");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

        } catch (Exception e)
        {

            Toast.makeText(context, "Kein Bild gefunden", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);

        */


    }

    private void dispatchTakePictureIntent(Context context, String path)
    {
        Uri photoURI =null;

        File storageDir = new File(path);

        if(storageDir.exists())
        {
            storageDir.delete();
        }
        else
        {
            storageDir.mkdirs();
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go


        // Continue only if the File was successfully created
        if (storageDir != null)
        {
            try {
                photoURI = FileProvider.getUriForFile(context,"com.example.tabnav_test.fileprovider",storageDir);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                ((Activity) context).startActivityForResult(takePictureIntent, 1); //startActivityForResult in material_log_activity
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    private void exmsg(String msg,Exception e)
    {
        Log.e("Exception: Basic_func_img->","ID: "+msg+" Message:" +e.getMessage().toString());

    }



}

