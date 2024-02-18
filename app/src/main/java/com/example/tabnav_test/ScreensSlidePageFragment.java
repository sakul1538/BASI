package com.example.tabnav_test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreensSlidePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreensSlidePageFragment extends Fragment
{

    // TODO: Rename and change types of parameters
    private final int seite;


    public ScreensSlidePageFragment(int seite)
    {
        this.seite = seite;// Required empty public constructor
    }

    /**
   rn A new instance of fragment ScreensSlidePageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreensSlidePageFragment newInstance(int seite)
            {
        ScreensSlidePageFragment fragment = new ScreensSlidePageFragment(seite);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflte the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screens_slide_page, container, false);
        TextView t1 = view.findViewById(R.id.tv1);
        t1.setText(String.valueOf(seite));
        return view;

    }
}