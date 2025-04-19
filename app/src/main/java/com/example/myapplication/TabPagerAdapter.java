package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.fragments.MunicipalityInfoFragment;
import com.example.myapplication.fragments.QuizFragment;
import com.example.myapplication.fragments.TrafficPlusWeatherInfoFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    // ALLA OLEVA KOODI ON COPILOTIN LUOMAA
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public TabPagerAdapter(FragmentActivity fa) {
        super(fa);
        fragmentList.add(new MunicipalityInfoFragment());
        fragmentList.add(new TrafficPlusWeatherInfoFragment());
        fragmentList.add(new QuizFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public Fragment getFragment(int position) {
        return fragmentList.get(position);
    }

    /*
    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MunicipalityInfoFragment();
            case 1:
                return new TrafficPlusWeatherInfoFragment();
            case 2:
                return new QuizFragment();
        }
        // Poikkeus, jos laajennetaan ja unohtuu muokata switch
        throw new IllegalStateException("Unexpected position: " + position);
    }

    @Override
    public int getItemCount() {
        return 3;
    } */
}
