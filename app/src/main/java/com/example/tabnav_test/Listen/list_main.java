package com.example.tabnav_test.Listen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tabnav_test.R;
import com.example.tabnav_test.ScreenSlidePagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class list_main extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    TabLayout tabLayout;
    ViewPager2 viewpager4;
    ScreenSlidePagerAdapter_list_main slide_list_adapter;

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
        View view = inflater.inflate(R.layout.fragment_list_main, container, false);
        tabLayout = view.findViewById(R.id.tablayout4);
        viewpager4 = view.findViewById(R.id.viewPager4);
        slide_list_adapter = new ScreenSlidePagerAdapter_list_main(this,tabLayout);
        viewpager4.setAdapter(slide_list_adapter);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewpager4.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Inflate the layout for this fragment
        return view;

    }

}
