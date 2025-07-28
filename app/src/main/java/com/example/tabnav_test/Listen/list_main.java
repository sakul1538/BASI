package com.example.tabnav_test.Listen;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.example.tabnav_test.SQL_finals;
import com.example.tabnav_test.ScreenSlidePagerAdapter;
import com.example.tabnav_test.db_ops;
import com.example.tabnav_test.projekt_ops;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Type;
import java.util.UUID;

public class list_main extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView list_main_rcv;


    ImageButton add_new_List;

    private list_main_rcv_adapter list_main_rcv_adapter;


    public list_main()
    {


    }
    public static list_main newInstance(String param1, String param2)
    {

        list_main fragment = new list_main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Context context = this.getContext();

        projekt_ops po = new projekt_ops(context);
        db_ops dbo = new db_ops(context);
        Basic_funct bsf = new Basic_funct();
        list_main_db_ops list_main_db_ops = new list_main_db_ops(context);


        View view = inflater.inflate(R.layout.fragment_list_main, container, false);

        String[] dataset = list_main_db_ops.get_lists_form_projekt(po.projekt_get_selected_id());

        list_main_rcv = view.findViewById(R.id.RecyclerView_list);
        Log.d("BASI",po.projekt_get_selected_id());
        add_new_List = view.findViewById(R.id.imageButton87);

        list_main_rcv_adapter = new list_main_rcv_adapter(context,dataset);
        list_main_rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        list_main_rcv.setAdapter(list_main_rcv_adapter);


        add_new_List.setOnClickListener(new View.OnClickListener()
        {
                                            @Override
                                            public void onClick(View view) {
                                                //Make a Dialog to add a new List
                                                View alert_view_add_list = getLayoutInflater().inflate(R.layout.add_list, null);
                                                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                                alertDialogBuilder.setView(alert_view_add_list);
                                                alertDialogBuilder.setTitle("Neue Liste erstellen");
                                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        try {
                                                            EditText list_name = alert_view_add_list.findViewById(R.id.editTextText6);
                                                            String name = list_name.getText().toString();

                                                            EditText list_details = alert_view_add_list.findViewById(R.id.editTextText7);
                                                            String details = list_details.getText().toString();
                                                            ContentValues values = new ContentValues();
                                                            values.put("ID", UUID.randomUUID().toString());
                                                            values.put("NAME", name);
                                                            values.put("DATE", bsf.date_refresh_database());
                                                            values.put("TYP", "1");
                                                            values.put("NOTE", details);
                                                            values.put("ITEMS_ID", UUID.randomUUID().toString());
                                                            values.put("PROJ_ID",po.projekt_get_selected_id());
                                                            values.put("POSITION","0");
                                                            //output in console
                                                            bsf.log(values.toString());

                                                            try {
                                                                dbo.insert(SQL_finals.TB_LISTS_MAIN, values);
                                                                Toast.makeText(getContext(), "Liste wurde erstellt", Toast.LENGTH_LONG).show();
                                                               String[] dataset = list_main_db_ops.get_lists_form_projekt(po.projekt_get_selected_id());
                                                                list_main_rcv_adapter.reload(dataset);
                                                                dialogInterface.dismiss();
                                                            }catch (Exception e)
                                                            {
                                                                Toast.makeText(getContext(), "A:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                            }

                                                            /// Speichern in datenbank
                                                        } catch (Exception e) {
                                                            Toast.makeText(getContext(), "A:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                                alertDialogBuilder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i)
                                                    {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                alertDialogBuilder.show();
                                            }
                                        });


            // Inflate the layout for this fragment
        return view;
    }

}

