package com.example.tabnav_test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Basic_funct
{
    public String time_refresh()
    {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm");
        String time= zeitformat.format(calendar.getTime());

        return time;
    }

    public String date_refresh()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = datumformat.format(calendar.getTime());

        return  date;
    }

    public String convert_time_for_database(int y,int m,int d)
    {

        Calendar calendar = Calendar.getInstance();
        calendar.set(y,m , d);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateString = dateFormat.format(calendar.getTime());

        return dateString;
    }

    public String gen_ID() {
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};

        Integer max_lenght= 5;

        Random rm = new Random();
        String key="";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat addTimestamp = new SimpleDateFormat("HH:mm:s");

        try {
            String random = String.valueOf(rm.nextInt());

            random = random.replace("-", "");

            String[] randomcain = random.split("");

            for(int i =0;i<max_lenght;i++)
            {
                key +=alphabet[Integer.valueOf(randomcain[i])];

            }

            //and Integer string
            random = String.valueOf(rm.nextInt());

            random = random.replace("-", "");

            randomcain = random.split("");

            for(int i =0;i<max_lenght;i++)
            {
                key +=randomcain[i];
            }

            //and Lowercase string
            random = String.valueOf(rm.nextInt());

            random = random.replace("-", "");

           randomcain = random.split("");

            for(int i =0;i<max_lenght;i++)
            {
                key +=alphabet[Integer.valueOf(randomcain[i])].toLowerCase();

            }


        } catch (Exception e) {

        }
        return key;

    }

    public String URLdecode(String v)
    {
        try {
            v = URLDecoder.decode(v, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return v;
    }

    public String URLencode(String v)
    {
        try {
            v = URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return v;
    }

    public Bitmap getBitmap(String path,String text_stamp)
    {


        String[] parts = path.split(":");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;

        Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/"+parts[1],options);

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMap.getWidth(), bMap.getHeight(), true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(225, 20, 225));
        paint.setTextSize(50);


        String timestamp = this.date_refresh()+ "  "+this.time_refresh() + " "+text_stamp;

        Canvas canvas = new Canvas(bMapScaled);

        canvas.drawText(timestamp, 20, bMapScaled.getHeight()-20, paint);


        return bMapScaled;

    }

    public String saveImage(Bitmap finalBitmap,String dir,String fname)
    {

        File myDir = new File(dir);
        myDir.mkdirs();


        //Calendar calendar = Calendar.getInstance();
      //  SimpleDateFormat zeitformat = new SimpleDateFormat("HHmmss");
       // String time= zeitformat.format(calendar.getTime());
        //String fname = this.gen_ID()+"_"+ time +".jpg";

        File file = new File(myDir, fname);
        if (file.exists())
        {
            file.delete ();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            log("Speichen erfolgreich!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir+fname;
    }


        /*Matrix matrix = new Matrix();
        matrix.set

        Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);
        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.drawBitmap(original, 5.0f, 0.0f, null);*/





    public void log(String msg)
    {

        Log.d("BASI",msg);


    }




}

