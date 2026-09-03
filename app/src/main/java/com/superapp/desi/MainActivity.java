package com.superapp.desi;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = findViewById(R.id.toolsGrid);

        // 69 फीचर्स की पूरी लिस्ट
        ArrayList<String> features = new ArrayList<>();
        features.add("1. Desi Reels");
        features.add("2. Cinema Movies");
        features.add("3. AI Shayari & Video");
        features.add("4. Photo Restoration");
        features.add("5. Logo Maker");
        features.add("6. Status Saver");
        features.add("7. Video Downloader");
        features.add("8. Background Remover");
        features.add("9. Voice Dubber");
        features.add("10. Green Screen FX");

        for (int i = 11; i <= 69; i++) {
            features.add(i + ". Super Tool " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_list_item_1,
            features
        );

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = features.get(position);
            Toast.makeText(MainActivity.this, selected + " खोला जा रहा है...", Toast.LENGTH_SHORT).show();
        });
    }
}
