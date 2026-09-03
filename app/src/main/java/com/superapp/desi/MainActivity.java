package com.superapp.desi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<SuperTool> fullToolList = new ArrayList<>();
    private final List<SuperTool> displayedList = new ArrayList<>();
    private ToolAdapter adapter;
    private String selectedCategory = "All";

    private final String[] CATEGORIES = {
            "All", "AI Studio", "Offline Hardware", "Social Saver", "Desi Daily", "Office & PDF"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAllTools();

        setupCategoryChips();

        RecyclerView rv = findViewById(R.id.rvToolsGrid);
        rv.setLayoutManager(new GridLayoutManager(this, 2)); // 2-Column Responsive Grid
        adapter = new ToolAdapter(displayedList, this::onToolClicked);
        rv.setAdapter(adapter);

        // लाइव सर्च फ़िल्टर
        EditText etSearch = findViewById(R.id.etSearchTools);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTools(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterTools("");
    }

    private void setupCategoryChips() {
        LinearLayout chipGroup = findViewById(R.id.chipGroupCategories);
        chipGroup.removeAllViews();

        for (String cat : CATEGORIES) {
            TextView chip = new TextView(this);
            chip.setText(cat);
            chip.setTextSize(13);
            chip.setPadding(30, 14, 30, 14);
            chip.setTextColor(cat.equals(selectedCategory) ? Color.WHITE : Color.parseColor("#88889D"));
            chip.setBackgroundColor(cat.equals(selectedCategory) ? Color.parseColor("#FF0055") : Color.parseColor("#1B1C26"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                selectedCategory = cat;
                setupCategoryChips(); // रिफ्रेश स्टाइल
                EditText etSearch = findViewById(R.id.etSearchTools);
                filterTools(etSearch.getText().toString());
            });

            chipGroup.addView(chip);
        }
    }

    private void filterTools(String query) {
        displayedList.clear();
        String q = query.toLowerCase().trim();

        for (SuperTool tool : fullToolList) {
            boolean matchesCat = selectedCategory.equals("All") || tool.category.equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = q.isEmpty() || tool.name.toLowerCase().contains(q) || tool.desc.toLowerCase().contains(q);

            if (matchesCat && matchesQuery) {
                displayedList.add(tool);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void onToolClicked(SuperTool tool) {
        Toast.makeText(this, "Launching: " + tool.name, Toast.LENGTH_SHORT).show();
        // यहाँ से Action Type के आधार पर Native Hardware या AI Studio खुलेगा
    }

    private void initAllTools() {
        fullToolList.clear();

        // 1. AI STUDIO
        fullToolList.add(new SuperTool("AI BG Remover", "✂️", "AI Studio", "1-Tap Background Cut", "AI", "ACTION_AI_BG"));
        fullToolList.add(new SuperTool("AI Vocal Separator", "🎤", "AI Studio", "Remove vocals from songs", "AI", "ACTION_AI_VOCAL"));
        fullToolList.add(new SuperTool("AI Photo Restorer", "🖼️", "AI Studio", "Fix old damaged photos", "AI", "ACTION_AI_RESTORE"));
        fullToolList.add(new SuperTool("AI Voice Clone", "🗣️", "AI Studio", "Transform voices instantly", "AI", "ACTION_AI_VOICE"));
        fullToolList.add(new SuperTool("AI Resume Builder", "📄", "AI Studio", "Create CV in 2 minutes", "AI", "ACTION_AI_RESUME"));
        fullToolList.add(new SuperTool("AI Video Dubber", "🌐", "AI Studio", "Translate video languages", "AI", "ACTION_AI_DUB"));

        // 2. OFFLINE HARDWARE (100% Zero Internet)
        fullToolList.add(new SuperTool("Speaker Cleaner", "🔊", "Offline Hardware", "165Hz Water & Dust Ejector", "OFFLINE", "ACTION_SPEAKER"));
        fullToolList.add(new SuperTool("Pocket Grab Alarm", "🚨", "Offline Hardware", "Anti-theft pocket siren", "OFFLINE", "ACTION_THEFT"));
        fullToolList.add(new SuperTool("Stud & Wire Finder", "🧲", "Offline Hardware", "Detects hidden wall wires", "OFFLINE", "ACTION_EMF"));
        fullToolList.add(new SuperTool("Mosquito Repeller", "🦟", "Offline Hardware", "Ultrasonic sound waves", "OFFLINE", "ACTION_MOSQUITO"));
        fullToolList.add(new SuperTool("Clap Phone Finder", "👏", "Offline Hardware", "Clap to flash and ring", "OFFLINE", "ACTION_CLAP"));
        fullToolList.add(new SuperTool("Hidden Cam Detector", "👁️", "Offline Hardware", "Finds hidden lens reflection", "OFFLINE", "ACTION_CAM_DETECTOR"));
        fullToolList.add(new SuperTool("Ghost Walkie-Talkie", "📻", "Offline Hardware", "100m Offline Voice Chat", "OFFLINE", "ACTION_WALKIE"));
        fullToolList.add(new SuperTool("Door Trap Alarm", "🚪", "Offline Hardware", "Motion-sensing room guard", "OFFLINE", "ACTION_TRAP"));
        fullToolList.add(new SuperTool("Spirit Surface Level", "📐", "Offline Hardware", "0.1° Surface alignment", "OFFLINE", "ACTION_LEVEL"));
        fullToolList.add(new SuperTool("Heart Pulse Checker", "❤️", "Offline Hardware", "Camera BPM pulse monitor", "OFFLINE", "ACTION_PULSE"));

        // 3. SOCIAL SAVER
        fullToolList.add(new SuperTool("Insta Reel Downloader", "📸", "Social Saver", "No watermark instant save", "FAST", "ACTION_INSTA"));
        fullToolList.add(new SuperTool("WhatsApp Status Saver", "🟢", "Social Saver", "Save photos & videos", "TOOL", "ACTION_WA_STATUS"));
        fullToolList.add(new SuperTool("Audio/MP3 Extractor", "🎵", "Social Saver", "Extract audio from video", "FAST", "ACTION_EXTRACT_AUDIO"));

        // 4. DESI DAILY
        fullToolList.add(new SuperTool("Vehicle Challan Check", "🚗", "Desi Daily", "Check plate & pending dues", "CHECK", "ACTION_CHALLAN"));
        fullToolList.add(new SuperTool("Mandi Live Rates", "🥦", "Desi Daily", "Today's mandi vegetable rates", "LIVE", "ACTION_MANDI"));
        fullToolList.add(new SuperTool("Smart Bill Splitter", "🧾", "Desi Daily", "Split bills with UPI QR", "UPI", "ACTION_SPLIT"));
        fullToolList.add(new SuperTool("Petrol Pump Radar", "⛽", "Desi Daily", "Detects meter jumps", "ALERT", "ACTION_PUMP"));

        // 5. OFFICE & PDF
        fullToolList.add(new SuperTool("Image to PDF Maker", "📑", "Office & PDF", "Convert images to clean PDF", "OFFLINE", "ACTION_IMG_PDF"));
        fullToolList.add(new SuperTool("PDF Compressor", "🗜️", "Office & PDF", "Shrink size below 100KB", "TOOL", "ACTION_COMPRESS_PDF"));
        fullToolList.add(new SuperTool("OCR Text Grabber", "📝", "Office & PDF", "Extract text from image", "TOOL", "ACTION_OCR"));
    }

    static class SuperTool {
        String name, icon, category, desc, badge, action;

        SuperTool(String name, String icon, String category, String desc, String badge, String action) {
            this.name = name;
            this.icon = icon;
            this.category = category;
            this.desc = desc;
            this.badge = badge;
            this.action = action;
        }
    }

    static class ToolAdapter extends RecyclerView.Adapter<ToolHolder> {
        private final List<SuperTool> list;
        private final OnToolClickListener listener;

        interface OnToolClickListener {
            void onClick(SuperTool tool);
        }

        ToolAdapter(List<SuperTool> list, OnToolClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ToolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool_card, parent, false);
            return new ToolHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ToolHolder holder, int position) {
            SuperTool tool = list.get(position);
            holder.tvIcon.setText(tool.icon);
            holder.tvName.setText(tool.name);
            holder.tvDesc.setText(tool.desc);
            holder.tvBadge.setText(tool.badge);

            if ("AI".equals(tool.badge)) {
                holder.tvBadge.setTextColor(Color.parseColor("#FF007F"));
            } else if ("OFFLINE".equals(tool.badge)) {
                holder.tvBadge.setTextColor(Color.parseColor("#00FFB2"));
            } else {
                holder.tvBadge.setTextColor(Color.parseColor("#FFAA00"));
            }

            holder.itemView.setOnClickListener(v -> listener.onClick(tool));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class ToolHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvName, tvDesc, tvBadge;

        ToolHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvToolIcon);
            tvName = itemView.findViewById(R.id.tvToolName);
            tvDesc = itemView.findViewById(R.id.tvToolDesc);
            tvBadge = itemView.findViewById(R.id.tvBadge);
        }
    }
          }
              
