package com.example.myapplication.ui.activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.ui.adapter.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tab_view);

        String municipalityName = getIntent().getStringExtra("MUNICIPALITY_NAME");
        TabPagerAdapter adapter = new TabPagerAdapter(this, municipalityName);

        ViewPager2 viewPager = findViewById(R.id.fragmentArea);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.fragmentSelector);

        try {
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Kunnan tiedot");
                                break;
                            case 1:
                                tab.setText("Liikenne/sää");
                                break;
                            case 2:
                                tab.setText("Tietovisa");
                                break;
                        }
                    }).attach();
        } catch (Exception e) {
            Log.e(TAG, "TabLayoutMediator failed", e);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void switchToMainActivity(View view) {
        finish();
    }
}