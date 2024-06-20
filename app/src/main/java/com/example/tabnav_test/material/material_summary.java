package com.example.tabnav_test.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.projekt_ops;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link material_summary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class material_summary extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView sumary_details_list;
    String[] dataset;

    public material_summary()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment material_summary.
     */
    // TODO: Rename and change types and number of parameters
    public static material_summary newInstance(String param1, String param2) {
        material_summary fragment = new material_summary();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_material_summary, container, false);
        // Inflate the layout for this fragment
        sumary_details_list= view.findViewById(R.id.sumary_details_list);
        ImageButton share_button = view.findViewById(R.id.imageButton51);
        ImageButton reload_summary= view.findViewById(R.id.reload_summary);

        Spinner summpary_type = view.findViewById(R.id.spinner_summary_type);

        material_database_ops mdo = new material_database_ops(getContext());
        dataset = mdo.get_artikel_summary();
        load_data(dataset);




        share_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder sharing_type = new AlertDialog.Builder(getContext());
                sharing_type.setPositiveButton("Text", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,prepare_for_sharing(dataset,"text"));
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        getContext().startActivity(shareIntent,null);
                    }
                });

                sharing_type.setNegativeButton("CSV", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,prepare_for_sharing(dataset,"csv"));
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        getContext().startActivity(shareIntent,null);
                    }
                });

                sharing_type.show();




            }
        });

        reload_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dataset = mdo.get_artikel_summary();
                switch(summpary_type.getSelectedItem().toString())
                {
                    case "Teilprojekt":
                        load_data(dataset);
                        break;

                      case "Gesamtprojekt":
                          dataset = mdo.get_projekt_ids_by_nr();
                          load_data(dataset);
                        break;
                    default:
                        load_data(dataset);
                }
            }
        });
        return view;


    }

    private class MyAdapter extends BaseAdapter
    {
        String [] dataset;
        public MyAdapter(String[] dataset)
        {
            this.dataset  = dataset;
        }
// override other abstract methods here

        @Override
        public int getCount()
        {
            return this.dataset.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container)
        {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }
            String [] part = this.dataset[position].split(",");

            TextView data = convertView.findViewById(R.id.textView19);

            data.setText(part[0]);

            TextView dat2 = convertView.findViewById(R.id.textView21);
            dat2.setText(part[1]);

            return convertView;
        }
    }

    private String prepare_for_sharing(String[] dataset,String type)
    {
        Basic_funct bsf = new Basic_funct();
        projekt_ops projekt  = new projekt_ops(getContext());
        String output_string="";
        switch(type)
        {
            case  "csv":
                output_string="ARTIKEL,TOTAL";
                for(String i: dataset)
                {
                    String[] part = i.split(",");
                    output_string += "\""+part[0]+"\",\""+part[1]+"\"\n";
                }
                break;
            default:

                for(String i: dataset)
                {
                    String[] part = i.split(",");
                    output_string += part[0]+":"+part[1]+"\n";
                }

                output_string += "\n Stand: "+bsf.date_refresh()+ " "+bsf.time_refresh();
        }


        return output_string;

    }

    public void load_data(String []dataset)
    {
        MyAdapter a = new MyAdapter(dataset);
        sumary_details_list.setAdapter(a);

    }


}



