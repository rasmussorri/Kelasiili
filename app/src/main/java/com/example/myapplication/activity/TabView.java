package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.fragments.MunicipalityInfoFragment;
import com.example.myapplication.utilities_plus_helpers.MunicipalityDataHelper;

public class TabView extends AppCompatActivity {

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

        // Hae kunnan nimi intentistä
        String municipalityName = getIntent().getStringExtra("municipality_name");
        if (municipalityName != null) {
            fetchMunicipalityData(municipalityName);
        }
    }

    /**
     * Fetches municipality data using the provided municipality name.
     *
     * @param municipalityName The name of the municipality to fetch data for.
     */
    private void fetchMunicipalityData(String municipalityName) {
        MunicipalityDataHelper dataHelper = new MunicipalityDataHelper();
        dataHelper.fetchPopulationAndChange(municipalityName, new MunicipalityDataHelper.Listener() {
            @Override
            public void onMunicipalityDataReady(int population, String populationChange) {
                MunicipalityInfoFragment fragment = (MunicipalityInfoFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.municipalityInfoFragment); // ID from activity_tab_view.xml

                if (fragment != null && fragment.getView() != null) {
                    String municipalityName = getIntent().getStringExtra("municipality_name");
                    fragment.updateMunicipalityInfo(municipalityName, population, populationChange, 0, 0);
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