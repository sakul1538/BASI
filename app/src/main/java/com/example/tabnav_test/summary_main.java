package com.example.tabnav_test;

import static android.widget.Toast.*;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link summary_main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class summary_main extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public summary_main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment summary_main.
     */
    // TODO: Rename and change types and number of parameters
    public static summary_main newInstance(String param1, String param2) {
        summary_main fragment = new summary_main();
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

        View view = inflater.inflate(R.layout.fragment_summary_main, container, false);
        // Inflate the layout for this fragment

        Spinner mat_spinner = view.findViewById(R.id.material_spinner);
        TextView Wert = view.findViewById(R.id.textView31);

        String[] material ={"Splitt 4/8 [m3]","Splittbeton 4/8 [m3]","Kiessand 1 [m3]","Aushub [m3]","Rasensamen [kg]","Dünger [kg]"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, material);

        //   ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,items);
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.kamera_spinner_list);

        mat_spinner.setAdapter(spinnerArrayAdapter);

        mat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String selectet_value=mat_spinner.getSelectedItem().toString();
                String []selectet_split  =selectet_value.split("\\[");
                selectet_split[1]= selectet_split[1].replace("]",":");

                Wert.setText("Wert in "+selectet_split[1]);
                Log.d("Material:",selectet_value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });





        return view;


    }



}