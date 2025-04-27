package com.example.myapplication.ui.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.core.util.MunicipalityCodeHelper;
import com.example.myapplication.search.ui.SearchHistoryAdapter;
import com.example.myapplication.municipality.model.MunicipalityInfo;
import com.example.myapplication.core.util.SearchedMunicipalitiesManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchHistoryAdapter adapter;
    private RecyclerView searchHistoryRV;
    private EditText searchMunicipality;
    private Button searchButton;
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
        try {
            SearchedMunicipalitiesManager.loadFromPreferences(this);
        } catch (Exception e) {
            Log.e(TAG, "Error loading history, clearing it", e);
            SearchedMunicipalitiesManager.clear();
            Toast.makeText(this,
                    "Hakuhistoria vioittunut ja tyhjennetty",
                    Toast.LENGTH_LONG).show();
        }

        searchHistoryRV = findViewById(R.id.searchHistoryRV);
        searchHistoryRV.setLayoutManager(new LinearLayoutManager(this));

        searchMunicipality = findViewById(R.id.SearchMunicipalityEditText);
        deleteAll = findViewById(R.id.deleteAll);
        searchButton = findViewById(R.id.SearchMunicipalityButton);

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

        deleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Tyhjennä historia?")
                    .setMessage("Haluatko varmasti poistaa kaikki hakutiedot?")
                    .setPositiveButton("Kyllä", (dialog, which) -> {
                        SearchedMunicipalitiesManager.clear();
                        try {
                            SearchedMunicipalitiesManager.saveToPreferences(this);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to save after clear", e);
                            Toast.makeText(this, "Historian tyhjennys epäonnistui", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter.updateData(SearchedMunicipalitiesManager.getAll());
                        Toast.makeText(this, "Hakuhistoria tyhjennetty", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Ei", null)
                    .show();
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
        try {
            SearchedMunicipalitiesManager.saveToPreferences(this); // Save history when app is paused
        } catch (Exception e) {
            Log.e(TAG, "Error saving history", e);
            Toast.makeText(this,
                    "Hakuhistorian tallennus epäonnistui",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void switchToTabView(View view) {
        if (searchMunicipality != null && !searchMunicipality.getText().toString().trim().isEmpty()) {

            String municipalityName = searchMunicipality.getText().toString().trim();

            // Tarkista löytyykö kunta ennen siirtymistä
            MunicipalityCodeHelper.fetchMunicipalityCode(
                    municipalityName,
                    new MunicipalityCodeHelper.CodeListener() {
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