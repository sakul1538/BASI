package com.example.tabnav_test;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
        alertDialogBuilder.setTitle("Erledigt");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_check_circle_green);
        alertDialogBuilder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void info_msg(String msg, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Info");
        alertDialogBuilder.setIcon(R.drawable.alert);
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
            Log.w("BASI", e.getMessage());
        }

        return date_new;
    }

    public String date_refresh_database() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("yyyy-MM-dd");
        String date = datumformat.format(calendar.getTime());

        return date;
    }

    public String date_refresh() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat datumformat = new SimpleDateFormat("dd.MM.yyyy");
        String date = datumformat.format(calendar.getTime());

        return date;
    }

    public String get_date_for_filename()
    {
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

            Log.d("BASI", e.getMessage());
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


        String timestamp = this.date_refresh_database() + "  " + this.time_refresh() + " " + text_stamp;

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

    public static void copyFileUsingStream(File source, File dest) throws IOException
    {
        //<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        //Permission all files

        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
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
            extension = path.substring(path.lastIndexOf("."));
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

        if (value.contains(".")) {
            try {
                String[] part = value.split("[.]");
                new_value = part[0] + ".";
                new_value += part[1].substring(0, lenght);
            } catch (Exception e) {
                new_value = value;
                Log.d(LOG_ERRORT_AG + "double_round()", e.getMessage());
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

    public String ls_filename_form(String lieferant, String ls_nr,String date, String type )
    {
        String output="";
        switch(type)
        {
            case "default":
                output = lieferant + "_LSNR_" + ls_nr +"@" + date + "_ID_" + System.currentTimeMillis(); //Name der Datei
                break;

            case "default_NO_ID":
                output = lieferant + "_LSNR_" + ls_nr +"@" + date;
                break;

                case "default_JUST_ID":
                    output =  "_ID_" + System.currentTimeMillis(); //Name der Datei
                break;

            default:
                output = lieferant + "_LSNR_" + ls_nr +"@" + date + "_ID_" + System.currentTimeMillis(); //Name der Datei
        }
        return output;
    }

    public void save_file(String path,String filename,String write_data,Context context)
    {
        Basic_funct bsf =new Basic_funct();
        File f = new File(path);
        f.mkdirs();
        try {
            f.createNewFile();

            FileWriter fw = new FileWriter(path+filename);
            fw.write(write_data);
            fw.close();

            AlertDialog.Builder create_backup_report_dialog  = new AlertDialog.Builder(context);
            create_backup_report_dialog.setTitle("Export Report");
            create_backup_report_dialog.setMessage("Backup gespeichert unter: \n\n"+path+filename);
            create_backup_report_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            create_backup_report_dialog.setNegativeButton("URL Kopieren", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    bsf.copy_to_clipboard(path+filename,context);
                }
            });

            create_backup_report_dialog.show();

        } catch (IOException e)
        {
            Toast.makeText(context, "Backup erstellen Fehlgeschlagen!:  \n"+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void log_output_arraylist(ArrayList arraylist)
    {

        for (int c = 0; c <= arraylist.size() - 1; c++) {
            Log.d("ARRAYLIST:", arraylist.get(c).toString());
        }
    }


    public String NULLtest(String s)
    {
        if(s.contains("NULL"))
        {
            return "null";
        }else
        {
            return s;
        }
}

    public void copy_to_clipboard(String text,Context context)
    {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(gen_ID(),text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Eintrag in die Zwischenablage kopiert!", Toast.LENGTH_SHORT).show();
    }

    public ArrayAdapter get_autocomplete_adapter(String [] data, Context context)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, data);
        return  adapter;
    }
    public void exeptiontoast(Exception e, Context context)
    {
        Toast.makeText(context, "Fehler:\n"+e.getMessage(), Toast.LENGTH_LONG).show();
    }



    /*public void generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        int pageHeight = 1120;
        int pagewidth = 792;
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("A portal for IT professionals.", 209, 100, title);
        canvas.drawText("Geeks for Geeks", 209, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("This is sample document which we have created.", 396, 560, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);c

        // below line is used to set the name of
        // our PDF file and its path.
        File file = new File(Environment.getExternalStorageDirectory(), "GFG.pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(getContext(), "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }*/

}



