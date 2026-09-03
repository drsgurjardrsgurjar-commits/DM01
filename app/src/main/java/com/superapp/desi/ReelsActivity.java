package com.superapp.desi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    private int secretClickCount = 0;
    private final List<ReelItem> feedList = new ArrayList<>();
    private ReelAdapter adapter;
    private ViewPager2 viewPager;
    private SharedPreferences prefs;

    // 100% वेरिफाइड 1M+ व्यूज वाले शॉर्ट्स का डिफॉल्ट पूल (राजस्थानी, हरियाणवी, कॉमेडी, वायरल गाने)
    private static final String DEFAULT_POOL = 
            "zdLcWQUAFAk:comedy,GA0W1gsDozg:comedy,7HD_WpkdRPY:haryanvi,kBWTo6N_2iE:haryanvi," +
            "DwqyqXZmiXU:haryanvi,xbsPqNeO560:haryanvi,m8e-Cq9I_d4:rajasthani,fRh_vgS2dFE:haryanvi," +
            "djV11Xbc914:haryanvi,tVlcKp3bwh8:haryanvi,9rLYy-2l0CI:rajasthani,agKsaExzxbk:rajasthani," +
            "gEQMX5mePT4:rajasthani,1PuJ31FcWBc:rajasthani,KFvzaTNE1G0:rajasthani,5K2V78BtaPc:gaming," +
            "2Vv-BfVoq4g:gaming,kJQP7kiw5Fk:song,J---aiyznGQ:song,OPf0YbXqDm0:song,L_LUpnjgPso:song," +
            "JGwWNGJdvx8:song,3JZ_D3ELwOQ:song,pRpeEdMmmQ0:song,hLQl3WQQoQ0:song,9bZkp7q19f0:song," +
            "Zi_XLOBDo_Y:song,7ghSziUQnhs:comedy,e-ORhEE9VVg:comedy,k2qgadSvNyU:comedy,fKopy74weus:comedy," +
            "astISOttCQ0:comedy,oHg5SJYRHA0:comedy,3tmd-ClpJxA:comedy,VbfpW0pbWNU:comedy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        prefs = getSharedPreferences("SuperAppHostPrefs", Context.MODE_PRIVATE);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        viewPager = findViewById(R.id.viewPagerReels);
        // आगे और पीछे की वीडियो पहले से बफर रहेगी ताकि काली स्क्रीन न आए
        viewPager.setOffscreenPageLimit(2);

        buildSmartFeed();

        adapter = new ReelAdapter(feedList, prefs, this);
        viewPager.setAdapter(adapter);

        // 5-टैप एडमिन पैनल (हेडर पर)
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

    private void buildSmartFeed() {
        feedList.clear();
        String rawData = prefs.getString("video_pool_data", DEFAULT_POOL);
        String[] entries = rawData.split(",");

        List<ReelItem> rawList = new ArrayList<>();
        for (String entry : entries) {
            String[] parts = entry.trim().split(":");
            String id = extractVideoId(parts[0]);
            String tag = parts.length > 1 ? parts[1] : "general";
            if (!id.isEmpty()) {
                rawList.add(new ReelItem(id, tag));
            }
        }

        // ताश के पत्तों की तरह रैंडम मिक्स (हर बार अलग वीडियो आएगी)
        Collections.shuffle(rawList);

        // जो कैटेगरी यूजर लाइक करेगा वो पहले आएगी
        Collections.sort(rawList, (o1, o2) -> {
            int score1 = prefs.getInt("pref_tag_" + o1.tag, 0);
            int score2 = prefs.getInt("pref_tag_" + o2.tag, 0);
            return Integer.compare(score2, score1);
        });

        feedList.addAll(rawList);
    }

    private String extractVideoId(String input) {
        String trimmed = input.trim();
        if (trimmed.contains("shorts/")) {
            String[] parts = trimmed.split("shorts/");
            if (parts.length > 1) {
                String sub = parts[1];
                int q = sub.indexOf('?');
                return (q != -1) ? sub.substring(0, q) : sub;
            }
        } else if (trimmed.contains("v=")) {
            String[] parts = trimmed.split("v=");
            if (parts.length > 1) {
                String sub = parts[1];
                int amp = sub.indexOf('&');
                return (amp != -1) ? sub.substring(0, amp) : sub;
            }
        }
        int q = trimmed.indexOf('?');
        return (q != -1) ? trimmed.substring(0, q) : trimmed;
    }

    private void handleAdminAccess() {
        EditText input = new EditText(this);
        input.setHint("Link या ID:Category (उदा. 9rLYy-2l0CI:rajasthani)");

        new AlertDialog.Builder(this)
                .setTitle("🎬 Add Reel to Feed")
                .setMessage("नया वीडियो जोड़ें (पुरानी लिस्ट कभी डिलीट नहीं होगी):")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String str = input.getText().toString().trim();
                    if (!str.isEmpty()) {
                        String oldPool = prefs.getString("video_pool_data", DEFAULT_POOL);
                        prefs.edit().putString("video_pool_data", oldPool + "," + str).apply();
                        buildSmartFeed();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "रील जुड़ गई!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNeutralButton("Reset All", (dialog, which) -> {
                    prefs.edit().putString("video_pool_data", DEFAULT_POOL).apply();
                    buildSmartFeed();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "डिफ़ॉल्ट वायरल लिस्ट रीसेट हो गई!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class ReelItem {
        String videoId;
        String tag;

        ReelItem(String videoId, String tag) {
            this.videoId = videoId;
            this.tag = tag;
        }
    }

    static class ReelAdapter extends RecyclerView.Adapter<ReelHolder> {
        private final List<ReelItem> list;
        private final SharedPreferences prefs;
        private final Context context;

        ReelAdapter(List<ReelItem> list, SharedPreferences prefs, Context context) {
            this.list = list;
            this.prefs = prefs;
            this.context = context;
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
            ReelItem item = list.get(position);
            holder.loader.setVisibility(View.VISIBLE);

            // 1. LIKE
            boolean isLiked = prefs.getBoolean("liked_" + item.videoId, false);
            holder.btnLike.setText(isLiked ? "❤️" : "🤍");
            holder.btnLike.setOnClickListener(v -> {
                boolean currentLiked = prefs.getBoolean("liked_" + item.videoId, false);
                boolean newStatus = !currentLiked;
                prefs.edit().putBoolean("liked_" + item.videoId, newStatus).apply();
                holder.btnLike.setText(newStatus ? "❤️" : "🤍");

                int tagScore = prefs.getInt("pref_tag_" + item.tag, 0);
                prefs.edit().putInt("pref_tag_" + item.tag, newStatus ? tagScore + 2 : Math.max(0, tagScore - 2)).apply();
                Toast.makeText(context, newStatus ? "Liked! इस कैटेगरी की रील्स अब ज़्यादा आएंगी।" : "Unliked", Toast.LENGTH_SHORT).show();
            });

            // 2. COMMENT
            holder.btnComment.setOnClickListener(v -> showCommentsDialog(item.videoId));

            // 3. SHARE
            holder.btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch on Desi SuperApp: https://youtube.com/shorts/" + item.videoId);
                context.startActivity(Intent.createChooser(shareIntent, "Share Reel"));
            });

            // 4. DOWNLOAD
            holder.btnDownload.setOnClickListener(v -> {
                Toast.makeText(context, "Opening Fast Downloader...", Toast.LENGTH_SHORT).show();
                String dlUrl = "https://en.savefrom.net/#url=https://youtube.com/shorts/" + item.videoId;
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dlUrl)));
            });

            // Webview Settings
            WebSettings ws = holder.webView.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);
            ws.setDatabaseEnabled(true);
            ws.setMediaPlaybackRequiresUserGesture(false);
            ws.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

            holder.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    holder.loader.setVisibility(View.GONE);
                }
            });

            holder.webView.setWebChromeClient(new WebChromeClient());

            // बिल्कुल Clean HTML: यूट्यूब लोगो, सर्च बार, हेडर सब कुछ कट (Pure 9:16 Full Screen Reel)
            String cleanHtml = "<!DOCTYPE html><html><head>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                    + "<style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; background: #000; overflow: hidden; }"
                    + "html, body { width: 100%; height: 100%; background: #000; }"
                    + ".wrapper { position: absolute; top: 0; left: 0; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; overflow: hidden; }"
                    + "iframe { width: 160%; height: 135%; border: none; transform: scale(1.25); pointer-events: auto; }"
                    + "</style></head><body>"
                    + "<div class='wrapper'>"
                    + "<iframe src='https://www.youtube-nocookie.com/embed/" + item.videoId 
                    + "?autoplay=1&mute=0&controls=0&showinfo=0&rel=0&iv_load_policy=3&modestbranding=1&playsinline=1&enablejsapi=1' "
                    + "allow='autoplay; fullscreen; encrypted-media' allowfullscreen></iframe>"
                    + "</div></body></html>";

            holder.webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", cleanHtml, "text/html", "UTF-8", null);
        }

        private void showCommentsDialog(String videoId) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(30, 20, 30, 10);

            String savedComments = prefs.getString("comments_" + videoId, "पहला कमेंट लिखें!");
            String[] commentArr = savedComments.split("###");
            List<String> commentList = new ArrayList<>();
            Collections.addAll(commentList, commentArr);

            ListView listView = new ListView(context);
            ArrayAdapter<String> commentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, commentList);
            listView.setAdapter(commentAdapter);
            layout.addView(listView);

            EditText inputComment = new EditText(context);
            inputComment.setHint("अपना कमेंट लिखें...");
            layout.addView(inputComment);

            new AlertDialog.Builder(context)
                    .setTitle("💬 Comments")
                    .setView(layout)
                    .setPositiveButton("Post", (dialog, which) -> {
                        String newC = inputComment.getText().toString().trim();
                        if (!newC.isEmpty()) {
                            String existing = prefs.getString("comments_" + videoId, "");
                            String updated = existing.isEmpty() ? newC : existing + "###" + newC;
                            prefs.edit().putString("comments_" + videoId, updated).apply();
                            Toast.makeText(context, "कमेंट पोस्ट हो गया!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class ReelHolder extends RecyclerView.ViewHolder {
        WebView webView;
        ProgressBar loader;
        TextView btnLike, btnComment, btnShare, btnDownload;

        ReelHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.reelWebView);
            loader = itemView.findViewById(R.id.reelLoader);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
                }
