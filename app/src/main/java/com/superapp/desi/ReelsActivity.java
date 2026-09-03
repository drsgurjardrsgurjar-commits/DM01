package com.superapp.desi;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReelsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("Desi Reels Player - Loading Videos...");
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(20);
        tv.setPadding(40, 80, 40, 40);
        tv.setBackgroundColor(0xFF000000);
        
        setContentView(tv);
    }
}
