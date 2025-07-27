package com.example.tabnav_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tabnav_test.Kamera.Kamera;
import com.example.tabnav_test.Listen.list_main;
import com.example.tabnav_test.Personal.Personalfragment;
import com.example.tabnav_test.material.Material;

public class ScreenSlidePagerAdapter  extends FragmentStateAdapter
{
    public ScreenSlidePagerAdapter(@NonNull MainActivity fragmentActivity)
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
                fragment = new list_main();
                break;

            case 3:
                fragment = new fragment_maschinen();
                break;
            case 4:
                fragment = new Material();
                break;
            case 5:
                fragment = new Personalfragment();
                break;

            default:
                fragment = new  ScreensSlidePageFragment(0);

        }
       return fragment;
    }

    @Override
    public int getItemCount() {
        return   6;
    }
}
