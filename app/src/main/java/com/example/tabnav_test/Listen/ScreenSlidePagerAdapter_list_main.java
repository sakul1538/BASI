package com.example.tabnav_test.Listen;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tabnav_test.Kamera.Kamera;
import com.example.tabnav_test.Log_main;
import com.example.tabnav_test.ScreenSlidePagerAdapter;
import com.example.tabnav_test.fragment_maschinen;
import com.example.tabnav_test.material.construction_fragment;
import com.google.android.material.tabs.TabLayout;

public class ScreenSlidePagerAdapter_list_main extends FragmentStateAdapter
{
    private static final int NUM_PAGES = 2;
    private ViewPager viewPager;

    private ViewPager2 viewPager2;
    private FragmentStateAdapter pagerAdapter;


    public ScreenSlidePagerAdapter_list_main(list_main listMain, TabLayout tabLayout)
    {
        super(listMain);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        Fragment fragment=null;

        switch (position)
            {
                case 0:
                    fragment =  new fragment_maschinen();
                    break;

                case 1:
                    fragment= new Log_main();
                    break;

            }
        return fragment;
    }

    @Override
    public int getItemCount()
    {
        return NUM_PAGES;
    }
}


