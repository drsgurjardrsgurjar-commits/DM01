package com.superapp.desi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPagerReels);
        bottomNav = findViewById(R.id.bottomNavigationView);

        List<String> videoIds = new ArrayList<>();
        videoIds.add("dQw4w9WgXcQ");
        videoIds.add("kJQP7kiw5Fk");
        videoIds.add("fJ9rUzIMcZQ");

        ReelsAdapter adapter = new ReelsAdapter(this, videoIds);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager.setAdapter(adapter);

        bottomNav.setOnItemSelectedListener(item -> true);
    }
                     }
                                            
