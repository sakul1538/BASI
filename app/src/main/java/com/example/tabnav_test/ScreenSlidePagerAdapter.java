package com.example.tabnav_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tabnav_test.material.Material;

public class ScreenSlidePagerAdapter  extends FragmentStateAdapter
{
    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        Fragment fragment = null; 
        switch(position)
        {
            case 0:
                fragment = new Kamera();
            break;

            case 1:
                fragment = new Log_main();
                break;

            case 2:
                fragment = new fragment_maschinen();
                break;

            case 3:
                fragment = new Material();
                break;


            default:
                fragment = new  ScreensSlidePageFragment(0);

        }
       return fragment;
    }

    @Override
    public int getItemCount() {
        return   4;
    }
}
