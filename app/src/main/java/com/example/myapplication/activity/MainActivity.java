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

import com.example.myapplication.R;

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
    }


    public void switchToTabView(View view) {
        // Hae kunnan nimi EditTextistä
        EditText searchMunicipality = findViewById(R.id.searchMunicipality);

        // Virheenkäsittely: tarkista, onko EditTextissä sisältöä
        if (searchMunicipality != null && !searchMunicipality.getText().toString().trim().isEmpty()) {
            // Vaihda activity_main.xml layout activity_tab_view.xml layoutiin
            Intent intent = new Intent(this, TabView.class);
            startActivity(intent);
        } else {
            // Lähetä huomautus käyttäjälle
            Toast.makeText(this, "Et antanut kunnan nimeä tekstimuodossa!", Toast.LENGTH_SHORT).show();
        }

    }
}