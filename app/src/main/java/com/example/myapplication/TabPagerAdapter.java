package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Fragments.MunicipalityInfoFragment;
import com.example.myapplication.Fragments.QuizFragment;
import com.example.myapplication.Fragments.TrafficPlusWeatherInfoFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    private String municipalityName;

    public TabPagerAdapter (FragmentActivity fa, String municipalityName) {
        super(fa);
        this.municipalityName = municipalityName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return MunicipalityInfoFragment.newInstance(municipalityName);
            case 1:
                return TrafficPlusWeatherInfoFragment.newInstance(municipalityName);
            case 2:
                return new QuizFragment();
        }
        // Poikkeus, jos laaajennetaan ja unohtuu muokata switch
        throw new IllegalStateException("Unexpected position: " + position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
