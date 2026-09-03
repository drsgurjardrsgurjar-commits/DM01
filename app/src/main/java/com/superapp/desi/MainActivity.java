package com.superapp.desi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String[] toolNames = {
        "1. Desi Reels 🔥", "2. Movies Hub 🎬", "3. Video Downloader 📥",
        "4. Photo Restore 🖼️", "5. Status Saver 💬", "6. BG Remover ✂️"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView grid = findViewById(R.id.toolsGrid);
        if (grid != null) {
            grid.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() { return toolNames.length; }

                @Override
                public Object getItem(int pos) { return toolNames[pos]; }

                @Override
                public long getItemId(int pos) { return pos; }

                @Override
                public View getView(int pos, View convert, ViewGroup parent) {
                    TextView tv = new TextView(MainActivity.this);
                    tv.setText(toolNames[pos]);
                    tv.setTextColor(0xFFFFFFFF);
                    tv.setBackgroundColor(0xFF1F1F1F);
                    tv.setPadding(24, 40, 24, 40);
                    tv.setTextSize(16);
                    tv.setGravity(android.view.Gravity.CENTER);
                    return tv;
                }
            });

            grid.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    Intent intent = new Intent(MainActivity.this, ReelsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, toolNames[position] + " जल्द आ रहा है!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    }
