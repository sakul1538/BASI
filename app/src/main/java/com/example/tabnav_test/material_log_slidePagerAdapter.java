package com.example.tabnav_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class material_log_slidePagerAdapter  extends FragmentStateAdapter
{
    public material_log_slidePagerAdapter(material_log_activity material_log_activity)
    {
        super(material_log_activity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        Fragment fragment =null;

        switch (position)
        {
            case 0:
                fragment = new material_log_entrys();
                    break;
            case 1:
                fragment = new FirstFragment();
                break;
            case 2:
                fragment = new FirstFragment();
                break;

            default:

                fragment = new FirstFragment();

        }

        return fragment;
    }

    @Override
    public int getItemCount()
    {

        return 3;
    }
}