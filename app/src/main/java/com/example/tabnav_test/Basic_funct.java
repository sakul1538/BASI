package com.example.tabnav_test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

public class Basic_funct {

    public static final String APP_DCIM = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/BASI/APP/BILDER";
    public static final String APP_DCIM_MASCHINEN = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/BASI/APP/BILDER/MASCHINEN";
    public static final String PROJ_NR = "0";

    public static final String LOG_ERRORT_AG = "Error -> Basic_funct :";
    public static Boolean confirm_value = false;


    public String getabsPath(String uri)   //   /document/primary:DCIM/Test@05122022_ID_2735252314015920324.jpeg
    {

        String[] parts = uri.split(":");

        String path = Environment.getExternalStorageDirectory() + "/" + parts[1];
        Log.d("BASIgetabsPath", path);

        return path;
    }

    public void error_msg(String msg, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Fehler:");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_report_gmailerrorred_24);
        alertDialogBuilder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void succes_msg(String msg, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Info");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
        alertDialogBuilder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public Boolean confirm(String msg, Context context) {
        // f//inal Boolean confirm_value = false;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Bestätigen");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_info_24_blue);
        alertDialogBuilder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                confirm_value = true;
                dialogInterface.cancel();
            }
        });

        alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                confirm_value = false;
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return confirm_value;
    }


    public void promt(String msg, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Message:");
        alertDialogBuilder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String time_refresh() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat zeitformat = new SimpleDateFormat("HH:mm");
        String time = zeitformat.format(calendar.getTime());

        return time;
    }

    public String time_day_shift(String date, String mode, int shift_value) //Format 01.01.2023
    {
        String[] s = null;
        int day = 0;
        int month = 0;
        int year = 0;
        String date_new = "";

        switch (mode) {
            default:
                try {
                    s = date.split("[.]");
                    day = Integer.valueOf(s[0]);
                    month = Integer.valueOf(s[1]) - 1; //Monate Fangen bei 0 An zu zählen!
                    year = Integer.valueOf(s[2]);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    calendar.add(Calendar.DATE, shift_value);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                    date_new = dateFormat.format(calendar.getTime());

                } catch (Exception e) {
                    date_new = date;
                    Log.w("BASI", e.getMessage().toString());
                }
        }

        return date_new;
    }

    public String date_refresh() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = datumformat.format(calendar.getTime());

        return date;
    }

    public String date_refresh_rev2() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
        String date = datumformat.format(calendar.getTime());

        return date;
    }


    public String get_date_filename() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("yyyyMMdd");
        String date = datumformat.format(calendar.getTime());

        return date;
    }

    public String convert_date(String datestring, String format) {

        String[] date_parts;
        String new_date;
        int day = 0;
        int month = 0;
        int year = 0;

        try {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat;
            new_date = "null";

            switch (format) {
                case "format_database":

                    date_parts = datestring.split("[.]");

                    day = Integer.parseInt(date_parts[0]);
                    month = Integer.parseInt(date_parts[1]) - 1;
                    year = Integer.parseInt(date_parts[2]);

                    calendar.set(year, month, day);

                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    new_date = dateFormat.format(calendar.getTime());

                    break;

                case "format_database_to_readable":


                    date_parts = datestring.split("-");

                    day = Integer.parseInt(date_parts[2]);
                    month = Integer.parseInt(date_parts[1]) - 1;
                    year = Integer.parseInt(date_parts[0]);

                    calendar.set(year, month, day);

                    dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                    new_date = dateFormat.format(calendar.getTime());


                    break;


            }
        } catch (Exception e) {

            new_date = "null";

            Log.d("BASI", e.getMessage().toString());
        }

        return new_date;
    }


    public String convert_time_for_database(int y, int m, int d) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String dateString = dateFormat.format(calendar.getTime());

        return dateString;
    }

    public String gen_ID() {
        String[] alphabet = "ABCDEFGHIJKLMNOPQRTSUVWXYZ".split("");
        String key = "";
        Random rm = new Random();
        String[] randomcain = new String[10]; //Länge der ID


        //Generiere Zufallsarray
        for (int c = 0; c < randomcain.length; c++) {
            randomcain[c] = String.valueOf(rm.nextInt(9)).replace("-", ""); //Zahlen 0-10
        }

        try {

            for (String e : randomcain) {
                int v = Integer.parseInt(e);
                if (v <= 3) {
                    key += alphabet[rm.nextInt(alphabet.length - 1)];
                }
                if (v >= 4 && v <= 7) {
                    key += alphabet[rm.nextInt(alphabet.length - 1)].toLowerCase();
                }
                if (v >= 8) {
                    key += rm.nextInt(9);
                }

            }
        } catch (Exception e) {
            //TODO exmsg einbinden
        }

        return key;
    }


    public String gen_UUID() {
        String key = UUID.randomUUID().toString();

        key = key.replace("-", "");
        return key;

    }

    public String URLdecode(String v) {
        try {
            v = URLDecoder.decode(v, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return v;
    }

    public String URLencode(String v) {
        try {
            v = URLEncoder.encode(v, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return v;
    }

    public Bitmap getBitmap(String path, String text_stamp) {

        String[] parts = path.split(":");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;

        Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/" + parts[1], options);

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


    public void copyBitmap_to(String path, String path_new, String filename) {

        Bitmap bMap = BitmapFactory.decodeFile(path);

        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, bMap.getWidth(), bMap.getHeight(), true);


        File myDir = new File(path_new);
        myDir.mkdirs();


        File file = new File(myDir, filename);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bMapScaled.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public String saveImage(Bitmap finalBitmap, String dir, String fname, Context context) {

        File myDir = new File(dir);
        myDir.mkdirs();

        //Calendar calendar = Calendar.getInstance();
        //  SimpleDateFormat zeitformat = new SimpleDateFormat("HHmmss");
        // String time= zeitformat.format(calendar.getTime());
        //String fname = this.gen_ID()+"_"+ time +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(context, "Bild erfolgreich geschpeichert!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Fehler: Bild  NICHT geschpeichert!", Toast.LENGTH_SHORT).show();
        }
        return dir + fname;
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        //<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        //Permission all files

        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public String detect_extension(String path) {
        Log.d("pre_extension", path);
        String extension;
        try {
            extension = path.substring(path.lastIndexOf("."), path.length());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.d("after_exstension", extension);
        return extension;
    }


    public void log(String msg) {

        Log.d("BASI", msg);
    }

    public void log_div() {
        Log.d("BASI", "::::::::::::::::::::::::::::::::::::::::::::::");
    }

    public String double_round(String value, int lenght) {
        String new_value = "";

        if (value.contains(".") == true) {
            try {
                String[] part = value.split("[.]");
                new_value = part[0] + ".";
                new_value += part[1].substring(0, lenght);
            } catch (Exception e) {
                new_value = value;
                Log.d(LOG_ERRORT_AG + "double_round()", e.getMessage().toString());
            }
        } else {
            new_value = value;
        }

        return new_value;

    }

    public ArrayList<String> to_ArrayList(String[] array) {
        ArrayList<String> arraylist = new ArrayList<>(Arrays.asList(array));
        return arraylist;
    }


    public void log_output_arraylist(ArrayList arraylist) {

        for (int c = 0; c <= arraylist.size() - 1; c++) {
            Log.d("ARRAYLIST:", arraylist.get(c).toString());
        }
    }
}


