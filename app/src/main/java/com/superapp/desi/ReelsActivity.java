package com.superapp.desi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    private final String ADMIN_PIN = "1234"; // सिर्फ तुम्हारा पिन
    private int secretClickCount = 0;
    private List<String> videoIds = new ArrayList<>();
    private RecyclerView.Adapter<ReelHolder> adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        prefs = getSharedPreferences("SuperAppHostPrefs", Context.MODE_PRIVATE);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // डिफ़ॉल्ट वीडियो लिस्ट
        String savedVideos = prefs.getString("video_ids_list", "kJQP7kiw5Fk,9bZkp7q19f0,3JZ_D3ELwOQ");
        loadVideosFromString(savedVideos);

        ViewPager2 viewPager = findViewById(R.id.viewPagerReels);

        adapter = new RecyclerView.Adapter<ReelHolder>() {
            @NonNull
            @Override
            public ReelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reel, parent, false);
                return new ReelHolder(v);
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onBindViewHolder(@NonNull ReelHolder holder, int position) {
                String videoId = videoIds.get(position);
                holder.loader.setVisibility(View.VISIBLE);

                WebSettings ws = holder.webView.getSettings();
                ws.setJavaScriptEnabled(true);
                ws.setDomStorageEnabled(true);
                ws.setMediaPlaybackRequiresUserGesture(false);

                // बाहर YouTube ऐप या ब्राउज़र खुलने से रोकता है
                holder.webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return true; 
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        holder.loader.setVisibility(View.GONE);
                    }
                });

                holder.webView.setWebChromeClient(new WebChromeClient());

                // YouTube लोगो, टाइटल और बटन को स्क्रीन से ज़ूम करके बाहर काटने का HTML
                String customPlayerHtml = "<!DOCTYPE html><html><head>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                        + "<style>"
                        + "* { margin: 0; padding: 0; box-sizing: border-box; background-color: #000; overflow: hidden; }"
                        + "html, body { width: 100%; height: 100%; }"
                        + ".container { position: relative; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }"
                        + "iframe { width: 140%; height: 120%; border: none; transform: scale(1.15); }"
                        + "</style></head><body>"
                        + "<div class='container'>"
                        + "<iframe src='https://www.youtube.com/embed/" + videoId 
                        + "?autoplay=1&mute=0&controls=0&showinfo=0&rel=0&iv_load_policy=3&modestbranding=1&playsinline=1&loop=1&playlist=" + videoId + "' "
                        + "allow='autoplay; encrypted-media' allowfullscreen></iframe>"
                        + "</div></body></html>";

                holder.webView.loadDataWithBaseURL("https://www.youtube.com", customPlayerHtml, "text/html", "UTF-8", null);
            }

            @Override
            public int getItemCount() { return videoIds.size(); }
        });

        viewPager.setAdapter(adapter);

        // हेडर पर 5 बार टैप करने पर सिर्फ तुम्हारे लिए पिन बॉक्स खुलेगा
        TextView tvTitle = findViewById(R.id.tvHeaderTitle);
        if (tvTitle != null) {
            tvTitle.setOnClickListener(v -> {
                secretClickCount++;
                if (secretClickCount >= 5) {
                    secretClickCount = 0;
                    showAdminLoginDialog();
                }
            });
        }
    }

    private void loadVideosFromString(String rawData) {
        videoIds.clear();
        String[] arr = rawData.split(",");
        for (String id : arr) {
            if (!id.trim().isEmpty()) {
                videoIds.add(id.trim());
            }
        }
    }

    private void showAdminLoginDialog() {
        EditText inputPin = new EditText(this);
        inputPin.setHint("सीक्रेट पिन डालें");
        inputPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("🔒 Host Secret Access")
                .setView(inputPin)
                .setPositiveButton("Verify", (d, w) -> {
                    if (inputPin.getText().toString().equals(ADMIN_PIN)) {
                        showVideoManagementDialog();
                    } else {
                        Toast.makeText(this, "गलत पिन!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showVideoManagementDialog() {
        EditText inputVideos = new EditText(this);
        inputVideos.setHint("Video IDs डालें (कॉमा लगाकर, जैसे: id1,id2,id3)");
        inputVideos.setText(prefs.getString("video_ids_list", ""));

        new AlertDialog.Builder(this)
                .setTitle("🎬 Reels Controller")
                .setMessage("YouTube Video/Shorts ID यहाँ कॉमा लगाकर डालें:")
                .setView(inputVideos)
                .setPositiveButton("Save All", (d, w) -> {
                    String data = inputVideos.getText().toString().trim();
                    prefs.edit().putString("video_ids_list", data).apply();
                    loadVideosFromString(data);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "रील्स तुरंत अपडेट हो गईं!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class ReelHolder extends RecyclerView.ViewHolder {
        WebView webView;
        ProgressBar loader;

        ReelHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.reelWebView);
            loader = itemView.findViewById(R.id.reelLoader);
        }
    }
    }
