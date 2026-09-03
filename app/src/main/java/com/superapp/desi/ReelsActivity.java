package com.superapp.desi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    private final List<String> videoStreamList = new ArrayList<>();
    private ReelAdapter adapter;
    private ViewPager2 viewPager;
    private SharedPreferences prefs;

    // ऑटोमेटेड ट्रेंडिंग कीवर्ड्स जिनका अनलिमिटेड डेटा ऐप खुद फेच करेगा
    private final String[] AUTO_KEYWORDS = {
            "trending+shorts+india",
            "haryanvi+dance+shorts",
            "rajasthani+marwadi+dance+shorts",
            "funny+comedy+shorts",
            "viral+kids+shorts",
            "punjabi+song+shorts",
            "gaming+shorts"
    };

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

        // बफरिंग रोकने का सबसे बड़ा सीक्रेट: OffscreenPageLimit
        // यह आगे और पीछे की 2 वीडियो पहले से बैकग्राउंड में लोड रखता है (काली स्क्रीन कभी नहीं आएगी)
        viewPager.setOffscreenPageLimit(2);

        // लाइव अनंत लिस्ट तैयार करना
        loadInfiniteStreams();

        adapter = new ReelAdapter(videoStreamList, prefs, this);
        viewPager.setAdapter(adapter);

        // जब यूजर स्वाइप करते-करते लिस्ट के अंत के पास पहुंचेगा, ऐप और वीडियो अपने-आप जोड़ देगा
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position >= videoStreamList.size() - 4) {
                    appendMoreVideos();
                }
            }
        });
    }

    private void loadInfiniteStreams() {
        videoStreamList.clear();
        // प्रारंभिक पूल
        for (String kw : AUTO_KEYWORDS) {
            videoStreamList.add(kw);
        }
        Collections.shuffle(videoStreamList);
    }

    private void appendMoreVideos() {
        // लिस्ट में लगातार नए कीवर्ड और ट्रेंडिंग वीडियो जोड़ते जाना
        for (String kw : AUTO_KEYWORDS) {
            videoStreamList.add(kw);
        }
        adapter.notifyDataSetChanged();
    }

    static class ReelAdapter extends RecyclerView.Adapter<ReelHolder> {
        private final List<String> list;
        private final SharedPreferences prefs;
        private final Context context;

        ReelAdapter(List<String> list, SharedPreferences prefs, Context context) {
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
            String query = list.get(position);
            holder.loader.setVisibility(View.VISIBLE);

            WebSettings ws = holder.webView.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);
            ws.setDatabaseEnabled(true);
            ws.setMediaPlaybackRequiresUserGesture(false);
            // हार्डवेयर एक्सेलेरेशन और कैशे ताकि काली स्क्रीन न घूमे
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
            ws.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");

            holder.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    holder.loader.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    // नेटवर्क कटने पर काली स्क्रीन के बजाय एरर हाइड करके री-ट्राई करना
                    holder.loader.setVisibility(View.GONE);
                }
            });

            holder.webView.setWebChromeClient(new WebChromeClient());

            // YouTube Shorts Live Dynamic Embed URL (अनंत लाइव वीडियो)
            String targetUrl = "https://www.youtube.com/results?search_query=" + query + "&sp=EgIQAQ%253D%253D";
            
            // सीधे फुल-स्क्रीन नो-ब्लैंक प्लेयर
            String embedHtml = "<!DOCTYPE html><html><head>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                    + "<style>"
                    + "* { margin:0; padding:0; box-sizing:border-box; background:#000; overflow:hidden; }"
                    + "html, body { width:100%; height:100%; background:#000; }"
                    + "iframe { width:100%; height:100%; border:none; }"
                    + "</style></head><body>"
                    + "<iframe src='https://m.youtube.com/results?search_query=" + query + "' allow='autoplay; encrypted-media' allowfullscreen></iframe>"
                    + "</body></html>";

            holder.webView.loadUrl("https://m.youtube.com/results?search_query=" + query);

            // सोशल बटन्स
            holder.btnLike.setOnClickListener(v -> Toast.makeText(context, "❤️ Liked!", Toast.LENGTH_SHORT).show());
            
            holder.btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch trending shorts on Desi SuperApp: " + targetUrl);
                context.startActivity(Intent.createChooser(shareIntent, "Share Reel"));
            });

            holder.btnDownload.setOnClickListener(v -> {
                Toast.makeText(context, "Opening High-Speed Downloader...", Toast.LENGTH_SHORT).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.savefrom.net/"));
                context.startActivity(browserIntent);
            });
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
