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

    private int secretClickCount = 0;
    private final List<String> videoIds = new ArrayList<>();
    private ReelAdapter adapter;
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

        String savedVideos = prefs.getString("video_ids_list", "kJQP7kiw5Fk,9bZkp7q19f0,3JZ_D3ELwOQ");
        loadVideosFromString(savedVideos);

        ViewPager2 viewPager = findViewById(R.id.viewPagerReels);
        adapter = new ReelAdapter(videoIds);
        viewPager.setAdapter(adapter);

        TextView tvTitle = findViewById(R.id.tvHeaderTitle);
        if (tvTitle != null) {
            tvTitle.setOnClickListener(v -> {
                secretClickCount++;
                if (secretClickCount >= 5) {
                    secretClickCount = 0;
                    handleAdminAccess();
                }
            });
        }
    }

    private void loadVideosFromString(String rawData) {
        videoIds.clear();
        String[] arr = rawData.split(",");
        for (String id : arr) {
            String clean = id.trim();
            if (!clean.isEmpty()) {
                videoIds.add(clean);
            }
        }
    }

    private void handleAdminAccess() {
        String savedPin = prefs.getString("custom_admin_pin", "");
        EditText pinInput = new EditText(this);
        pinInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        if (savedPin.isEmpty()) {
            pinInput.setHint("नया 4 अंकों का पिन बनाएँ");
            new AlertDialog.Builder(this)
                    .setTitle("🔑 पिन सेट करें")
                    .setView(pinInput)
                    .setPositiveButton("Save", (d, w) -> {
                        String pin = pinInput.getText().toString().trim();
                        if (pin.length() >= 4) {
                            prefs.edit().putString("custom_admin_pin", pin).apply();
                            Toast.makeText(this, "पिन सेट हो गया! अब दोबारा 5 बार दबाएँ।", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "कम से कम 4 अंक डालें", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            pinInput.setHint("पिन डालें");
            new AlertDialog.Builder(this)
                    .setTitle("🔒 Host Access")
                    .setView(pinInput)
                    .setPositiveButton("Login", (d, w) -> {
                        if (pinInput.getText().toString().trim().equals(savedPin)) {
                            showVideoManagementDialog();
                        } else {
                            Toast.makeText(this, "गलत पिन!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void showVideoManagementDialog() {
        EditText inputVideos = new EditText(this);
        inputVideos.setText(prefs.getString("video_ids_list", ""));
        inputVideos.setHint("Video IDs (कॉमा लगाकर)");

        new AlertDialog.Builder(this)
                .setTitle("🎬 Reels Controller")
                .setView(inputVideos)
                .setPositiveButton("Save All", (d, w) -> {
                    String data = inputVideos.getText().toString().trim();
                    prefs.edit().putString("video_ids_list", data).apply();
                    loadVideosFromString(data);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "अपडेट हो गया!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class ReelAdapter extends RecyclerView.Adapter<ReelHolder> {
        private final List<String> list;

        ReelAdapter(List<String> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ReelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reel, parent, false);
            return new ReelHolder(v);
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public void onBindViewHolder(@NonNull ReelHolder holder, int position) {
            String videoId = list.get(position);
            holder.loader.setVisibility(View.VISIBLE);

            WebSettings ws = holder.webView.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);
            ws.setMediaPlaybackRequiresUserGesture(false);

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
        public int getItemCount() {
            return list.size();
        }
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
