package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.SearchHistoryAdapter;
import com.example.myapplication.dataModels.MunicipalityInfo;
import com.example.myapplication.utilities_plus_helpers.SearchedMunicipalitiesManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchHistoryAdapter adapter; // ⬅ field so we can update it
    private RecyclerView searchHistoryRV;
    private EditText searchMunicipality;
    private ImageButton deleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SearchedMunicipalitiesManager.loadFromPreferences(this);
        Log.d("DEBUG", "Loaded history size: " + SearchedMunicipalitiesManager.getAll().size());

        searchHistoryRV = findViewById(R.id.searchHistoryRV);
        searchHistoryRV.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchHistoryAdapter(
                SearchedMunicipalitiesManager.getAll(),
                new SearchHistoryAdapter.OnMunicipalityClickListener() {
                    @Override
                    public void onShowClicked(MunicipalityInfo info) {
                        Log.d("DEBUG", "Navigating to info for: " + info.getName());
                        Intent intent = new Intent(MainActivity.this, TabView.class);
                        intent.putExtra("MUNICIPALITY_NAME", info.getName());
                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClicked(MunicipalityInfo info) {
                        Log.d("DEBUG", "Deleting: " + info.getName());
                        SearchedMunicipalitiesManager.removeMunicipality(info);
                        SearchedMunicipalitiesManager.saveToPreferences(MainActivity.this);
                        adapter.updateData(SearchedMunicipalitiesManager.getAll());

                        Toast.makeText(MainActivity.this, info.getName() + " poistettu historiasta", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        searchHistoryRV.setAdapter(adapter);

        deleteAll = findViewById(R.id.deleteAll);
        deleteAll.setOnClickListener(v -> {
            // Tyhjennetään muistissa ja tallennetaan
            SearchedMunicipalitiesManager.clear();
            SearchedMunicipalitiesManager.saveToPreferences(MainActivity.this);

            // Päivitetään listanäkymä ja näytetään vahvistus
            adapter.updateData(SearchedMunicipalitiesManager.getAll());
            Toast.makeText(MainActivity.this,
                    "Hakuhistoria tyhjennetty",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUG", "onResume called");

        List<MunicipalityInfo> history = SearchedMunicipalitiesManager.getAll();
        Log.d("DEBUG", "History size in onResume: " + history.size());

        if (adapter != null) {
            adapter.updateData(history);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SearchedMunicipalitiesManager.saveToPreferences(this); // Save history when app is paused
    }

    public void switchToTabView(View view) {
        searchMunicipality = findViewById(R.id.SearchMunicipalityEditText);

        if (searchMunicipality != null && !searchMunicipality.getText().toString().trim().isEmpty()) {

            String municipalityName = searchMunicipality.getText().toString().trim();

            // Tarkista löytyykö kunta ennen siirtymistä
            com.example.myapplication.utilities_plus_helpers.MunicipalityCodeHelper.fetchMunicipalityCode(
                    municipalityName,
                    new com.example.myapplication.utilities_plus_helpers.MunicipalityCodeHelper.CodeListener() {
                        @Override
                        public void onCodeReady(String code) {
                            // Kunta löytyi — siirrytään TabViewiin
                            Intent intent = new Intent(MainActivity.this, TabView.class);
                            intent.putExtra("MUNICIPALITY_NAME", municipalityName.toUpperCase());
                            startActivity(intent);
                            searchMunicipality.setText("");
                        }

                        @Override
                        public void onError(String error) {
                            // Kuntaa ei löytynyt — ei siirrytä
                            Toast.makeText(MainActivity.this, "Kuntaa ei löydy", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Et antanut kunnan nimeä tekstimuodossa!", Toast.LENGTH_SHORT).show();
        }
    }
}