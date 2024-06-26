package com.example.tabnav_test;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tabnav_test.material.construction_fragment;
import com.example.tabnav_test.material.import_export_fragment;
import com.example.tabnav_test.material.material_log_activity;
import com.example.tabnav_test.material.material_log_entrys;
import com.example.tabnav_test.material.material_summary;
import com.google.android.material.tabs.TabLayout;

public class material_log_slidePagerAdapter  extends FragmentStateAdapter
{
    public TabLayout tab;


    public material_log_slidePagerAdapter(material_log_activity material_log_activity)
    {
        super(material_log_activity);


    }

    public material_log_slidePagerAdapter(material_log_activity materialLogActivity, TabLayout tabLayout)
    {
        super(materialLogActivity);
        this.tab = tabLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
       Fragment fragment =null;
        switch (position)
        {
            case 0:
                fragment = new material_log_entrys(this.tab) ;

                    break;
            case 1:
                fragment = new material_summary();
                break;
            case 2:
                fragment = new import_export_fragment();
                break;

            default:

                fragment = new construction_fragment();
        }

        return fragment;
    }

    @Override
    public int getItemCount()
    {

        return 3;
    }
}
