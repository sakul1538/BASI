package com.example.tabnav_test.share;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.example.tabnav_test.static_finals;
public class Share
{

    public void file_import(ArrayList  entrys)
    {

    }

    public String file_export(ArrayList<String> data,String filename,String file_type)
    {

        final String PATH_EXPORTS = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOCUMENTS+"/BASI/exports/";
        String file_path ="";

        switch (file_type)
        {
            case "csv":
                File file_csv = new File(PATH_EXPORTS+"csv/",filename+"."+file_type);
                file_csv.mkdirs();

                if(file_csv.exists()==true)
                {
                    file_csv.delete();
                }
                try
                {
                    FileWriter fput = new FileWriter(file_csv);

                    for(String v: data)
                    {
                        fput.write(v+"\n");
                    }
                    fput.close();

                    file_path =file_csv.getAbsolutePath();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;

            case "txt":
                File file_txt = new File(PATH_EXPORTS+"txt/",filename+"."+file_type);
                file_txt.mkdirs();

                if(file_txt.exists()==true)
                {
                    file_txt.delete();
                }
                try
                {
                    FileWriter fput = new FileWriter(file_txt);

                    for(String v: data)
                    {
                        fput.write(v+"\n");
                    }
                    fput.close();

                    file_path =file_txt.getAbsolutePath();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;

            case "backup":

                File file_backup = new File(PATH_EXPORTS+"backups/",filename+".txt");
                file_backup.mkdirs();

                if(file_backup.exists()==true)
                {
                    file_backup.delete();
                }
                try
                {
                    FileWriter fput = new FileWriter(file_backup);

                    for(String v: data)
                    {
                        fput.write(v+"\n");
                    }
                    fput.close();

                    file_path =file_backup.getAbsolutePath();

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
        return file_path;
    }

}
