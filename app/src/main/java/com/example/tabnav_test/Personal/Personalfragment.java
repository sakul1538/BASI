package com.example.tabnav_test.Personal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tabnav_test.Basic_funct;
import com.example.tabnav_test.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Personalfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Personalfragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //ImageButtons
    ImageButton personal_new_entry;
    ImageButton personal_open_log;
    ImageButton personal_add_employ;

    //RecyclerViews
    RecyclerView personalstamm_recycler;



    public Personalfragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Personalfragment newInstance(String param1, String param2) {
        Personalfragment fragment = new Personalfragment();
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
        View view = inflater.inflate(R.layout.fragment_personalfragment, container, false);
        //ImageButtons
        personal_new_entry = view.findViewById(R.id.personal_new_entry);
        personal_open_log = view.findViewById(R.id.personal_open_log);
        personal_add_employ = view.findViewById(R.id.personal_add_employ);

        //RecyclerViews
        personalstamm_recycler = view.findViewById(R.id.personalstamm_recycler);
        personalstamm_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        personalstamm_recycler.setAdapter(new personalstamm_adapter());


        personal_new_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                toast_fallback();
            }
        });

        personal_open_log.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toast_fallback();
            }
        });

        personal_add_employ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialog
                View alert_view_add_employ = getLayoutInflater().inflate(R.layout.add_employ, null);
                android.app.AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(alert_view_add_employ);

                alertDialogBuilder.setTitle("Mitarbeiter hinzufügen");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            /// Speichern in datenbank


                        } catch (Exception e) {
                            Toast.makeText(getContext(), "A:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alertDialogBuilder.show();
            }
        });
        return view;
    }
    private void toast_fallback()
    {
        Toast.makeText(getContext(),"Nicht implementiert",Toast.LENGTH_SHORT).show();

    }
}