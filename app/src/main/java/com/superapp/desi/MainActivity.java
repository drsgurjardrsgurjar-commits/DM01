package com.superapp.desi;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static class CustomAppTool {
        String title;
        String symbol;
        int badgeColor;
        String desc;

        CustomAppTool(String title, String symbol, int badgeColor, String desc) {
            this.title = title;
            this.symbol = symbol;
            this.badgeColor = badgeColor;
            this.desc = desc;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = findViewById(R.id.toolsGrid);

        List<CustomAppTool> list = new ArrayList<>();
        // टॉप 10 ओरिजिनल टूल्स के बैज और कलर्स
        list.add(new CustomAppTool("Desi Reels", "▶", Color.parseColor("#FF2D55"), "शॉर्ट और वायरल रील्स इंजन"));
        list.add(new CustomAppTool("Cinema Talkies", "🎬", Color.parseColor("#AF52DE"), "फुल स्क्रीन मूवीज़ और शोज़"));
        list.add(new CustomAppTool("AI Shayari FX", "✨", Color.parseColor("#5856D6"), "AI शायरी, आवाज़ और वीडियो मेकर"));
        list.add(new CustomAppTool("Photo Fixer", "🖼", Color.parseColor("#007AFF"), "डैमेज व पुरानी फ़ोटो रिस्टोरर"));
        list.add(new CustomAppTool("Brand Creator", "🎨", Color.parseColor("#34C759"), "चैनल, बिज़नेस व 3D लोगो डिज़ाइनर"));
        list.add(new CustomAppTool("Status Vault", "📥", Color.parseColor("#30B0C7"), "मीडिया व स्टेटस सेवर"));
        list.add(new CustomAppTool("Super Fetcher", "⚡", Color.parseColor("#FF9500"), "ऑल-इन-वन मीडिया डाउनलोडर"));
        list.add(new CustomAppTool("Cutout Magic", "✂", Color.parseColor("#FF3B30"), "ऑटो बैकग्राउंड रिमूवर"));
        list.add(new CustomAppTool("Desi Dubber", "🎙", Color.parseColor("#FFCC00"), "वॉइस चेंज और डबिंग स्टूडियो"));
        list.add(new CustomAppTool("Chroma Studio", "🟩", Color.parseColor("#34C759"), "ग्रीन स्क्रीन और वीडियो इफ़ेक्ट्स"));

        // बाकी टूल्स के लिए डायनामिक पैलेट
        int[] palette = {
            Color.parseColor("#5E5CE6"),
            Color.parseColor("#64D2FF"),
            Color.parseColor("#30D158"),
            Color.parseColor("#FFD60A"),
            Color.parseColor("#FF9F0A")
        };

        for (int i = 11; i <= 69; i++) {
            list.add(new CustomAppTool("Super Tool " + i, "⚙", palette[i % palette.length], "टूल नंबर " + i + " का मॉड्यूल"));
        }

        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() { return list.size(); }

            @Override
            public Object getItem(int pos) { return list.get(pos); }

            @Override
            public long getItemId(int pos) { return pos; }

            @Override
            public View getView(int pos, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_tool_card, parent, false);
                }

                TextView badge = convertView.findViewById(R.id.tvToolBadge);
                TextView title = convertView.findViewById(R.id.tvToolName);

                CustomAppTool tool = list.get(pos);
                title.setText(tool.title);
                badge.setText(tool.symbol);

                // कार्ड का बैकग्राउंड (प्रीमियम राउंडेड डार्क ग्रे)
                GradientDrawable cardShape = new GradientDrawable();
                cardShape.setColor(Color.parseColor("#242426"));
                cardShape.setCornerRadius(24f);
                convertView.setBackground(cardShape);

                // आइकन बैज का राउंडेड बैकग्राउंड
                GradientDrawable badgeShape = new GradientDrawable();
                badgeShape.setColor(tool.badgeColor);
                badgeShape.setCornerRadius(18f);
                badge.setBackground(badgeShape);

                return convertView;
            }
        };

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            CustomAppTool tool = list.get(position);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(tool.title)
                    .setMessage(tool.desc + "\n\nफ़ीचर तैयार है!")
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
