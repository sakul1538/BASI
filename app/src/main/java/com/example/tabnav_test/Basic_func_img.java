package com.example.tabnav_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class Basic_func_img extends Basic_funct {

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


}

