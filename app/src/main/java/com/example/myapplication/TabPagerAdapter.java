package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Fragments.MunicipalityInfoFragment;
import com.example.myapplication.Fragments.QuizFragment;
import com.example.myapplication.Fragments.TrafficPlusWeatherInfoFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    private String municipalityName;
    private static final int NUM_TABS = 3;

    public TabPagerAdapter (FragmentActivity fa, String municipalityName) {
        super(fa);
        this.municipalityName = municipalityName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            switch (position) {
                case 0: return MunicipalityInfoFragment.newInstance(municipalityName);
                case 1: return TrafficPlusWeatherInfoFragment.newInstance(municipalityName);
                case 2: return new QuizFragment();
                default:
                    Log.e("TabPagerAdapter", "createFragment: unexpected position " + position);
                    return MunicipalityInfoFragment.newInstance(municipalityName);
            }
        } catch (Exception e) {
            Log.e("TabPagerAdapter", "Error creating fragment for position " + position, e);
            // Palauta varmuuden vuoksi info-fragment
            return MunicipalityInfoFragment.newInstance(municipalityName);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}
