package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Fragments.MunicipalityInfoFragment;
import com.example.myapplication.R;
import com.example.myapplication.TabPagerAdapter;

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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void switchToMainActivity(View view) {
        // Vaihda activity_tab_view.xml layout activity_main.xml layoutiin
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}