package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.TabPagerAdapter;
import com.example.myapplication.fragments.MunicipalityInfoFragment;
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TabView extends AppCompatActivity {

    private ViewPager2 fragmentArea;
    private TabLayout fragmentSelector;
    private TabPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tab_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragmentArea = findViewById(R.id.fragmentArea);
        fragmentSelector = findViewById(R.id.fragmentSelector);

        adapter = new TabPagerAdapter(this);
        fragmentArea.setAdapter(adapter);

        new TabLayoutMediator(fragmentSelector, fragmentArea, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Kunta");
                    break;
                case 1:
                    tab.setText("Liikenne/sää");
                    break;
                case 2:
                    tab.setText("Visa");
                    break;
            }
        }).attach();

        // Hae kunnan nimi intentistä
        String municipalityName = getIntent().getStringExtra("municipality_name");
        if (municipalityName != null) {
            fetchMunicipalityData(municipalityName);
        }
    }

    private void fetchMunicipalityData(String municipalityName) {
        MunicipalityDataHelper dataHelper = new MunicipalityDataHelper();
        dataHelper.fetchPopulationAndChange(municipalityName, new MunicipalityDataHelper.Listener() {
            @Override
            public void onMunicipalityDataReady(int population, String populationChange) {
                System.out.println("Väkiluku: " + population + ", Väkiluvun muutos: " + populationChange);
                Fragment fragment = adapter.getFragment(0);
                if (fragment instanceof MunicipalityInfoFragment) {
                    ((MunicipalityInfoFragment) fragment).updateMunicipalityInfo(
                            municipalityName,
                            population,
                            populationChange,
                            0,
                            0
                    );
                } else {
                    Toast.makeText(TabView.this, "Fragmentti ei ole valmis", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle errors (e.g., show a Toast)
                Toast.makeText(TabView.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void switchToMainActivity(View view) {
        // Vaihda activity_tab_view.xml layout activity_main.xml layoutiin
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}