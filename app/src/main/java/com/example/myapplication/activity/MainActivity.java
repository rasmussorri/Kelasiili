package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.example.myapplication.utilities_plus_helpers.SearchedMunicipalitiesManager;

public class MainActivity extends AppCompatActivity {

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
        RecyclerView searchHistoryRV = findViewById(R.id.searchHistoryRV);
        searchHistoryRV.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryRV.setAdapter(new SearchHistoryAdapter(SearchedMunicipalitiesManager.getAll()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView searchHistoryRV = findViewById(R.id.searchHistoryRV);
        SearchHistoryAdapter adapter = new SearchHistoryAdapter(SearchedMunicipalitiesManager.getAll());
        searchHistoryRV.setAdapter(adapter);
    }


    public void switchToTabView(View view) {
        // Hae kunnan nimi EditTextistä
        EditText searchMunicipality = findViewById(R.id.SearchMunicipalityEditText);

        // Virheenkäsittely: tarkista, onko EditTextissä sisältöä
        if (searchMunicipality != null && !searchMunicipality.getText().toString().trim().isEmpty()) {

            String municipalityName = searchMunicipality.getText().toString().trim();

            // Vaihda activity_main.xml layout activity_tab_view.xml layoutiin
            Intent intent = new Intent(this, TabView.class);
            intent.putExtra("MUNICIPALITY_NAME", municipalityName); // Lähetä kunnan nimi seuraavaan activityyn
            startActivity(intent);
            // Tyhjennä EditText
            searchMunicipality.setText("");
        } else {
            // Lähetä huomautus käyttäjälle
            Toast.makeText(this, "Et antanut kunnan nimeä tekstimuodossa!", Toast.LENGTH_SHORT).show();
        }

    }
}