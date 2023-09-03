package com.example.tabnav_test.material;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabnav_test.R;

public class material_log_entrys extends Fragment
{
     public RecyclerView ls_log_view_rcv;

    private ls_log_view_rcv_adapter lslogrcv;
    public material_log_entrys()
    {

    }

    public static material_log_entrys newInstance(String param1, String param2)
    {
        material_log_entrys fragment = new material_log_entrys();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        material_database_ops mdo = new material_database_ops(getContext());

        View view = inflater.inflate(R.layout.material_log_entrys_listing, container, false);

        RecyclerView ls_log_view_rcv =view.findViewById(R.id.material_log_entry_rcv);


        String[] ls_log_view_rcv_adapter=mdo.material_entrys_list();
        lslogrcv = new ls_log_view_rcv_adapter(ls_log_view_rcv_adapter);


        ls_log_view_rcv.setAdapter(lslogrcv);
        ls_log_view_rcv.setLayoutManager( new LinearLayoutManager(getContext()));


        return view;
    }


}
