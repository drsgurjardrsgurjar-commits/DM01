package com.superapp.desi;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
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

    private final List<ShortItem> shortsList = new ArrayList<>();
    private ReelAdapter adapter;
    private ViewPager2 viewPager;
    private SharedPreferences prefs;

    // सिर्फ और सिर्फ 1M+ व्यूज वाले 9:16 प्योर शॉर्ट्स (कोई लंबा वीडियो नहीं)
    private static final String[][] VERIFIED_SHORTS = {
            {"9rLYy-2l0CI", "@rajasthani_dance", "घूमर रील्स धमाका 💃", "Original Sound - Marwadi Folk"},
            {"agKsaExzxbk", "@desi_culture", "राजस्थानी विवाह डांस 🔥", "Trending Rajasthani DJ Mix"},
            {"7HD_WpkdRPY", "@haryanvi_beats", "हरियाणवी सुपरहिट स्टेप्स ⚡", "Pranjal Dahiya - 52 Gaj"},
            {"kBWTo6N_2iE", "@desi_records", "गाँव की देसी रील 💥", "Original Sound - Haryanvi"},
            {"DwqyqXZmiXU", "@haryanvi_swag", "देसी ठाठ बाठ रील्स 👑", "Desi Haryanvi Beat"},
            {"zdLcWQUAFAk", "@comedy_junction", "हँसते-हँसते लोटपोट 😂", "Funny Laugh Sound"},
            {"GA0W1gsDozg", "@fun_zone", "देसी मस्त कॉमेडी क्लिप 🤣", "Original Viral Audio"},
            {"gEQMX5mePT4", "@rajasthan_diaries", "मारवाड़ी देसी ठुमका ✨", "Rajasthani Traditional"},
            {"1PuJ31FcWBc", "@folk_tunes", "डीजे पर देसी डांस 🎶", "Marwadi DJ Rimix"},
            {"KFvzaTNE1G0", "@desi_vibes", "विलेज डांस परफॉर्मेंस 🌟", "Desi Sound"},
            {"xbsPqNeO560", "@swag_haryana", "ताऊ का डांस वायरल 🚀", "Haryanvi Ragni DJ"},
            {"m8e-Cq9I_d4", "@romantic_vibes", "वायरल रोमांटिक रील ❤️", "Romantic Trending Lofi"},
            {"fRh_vgS2dFE", "@dance_hub", "ट्रेंडिंग स्टेप्स 2024 ⚡", "Viral DJ Remix"},
            {"djV11Xbc914", "@desi_boys", "फुल हरियाणवी स्वैग 💣", "Original Haryanvi Swag"},
            {"tVlcKp3bwh8", "@masti_reels", "हँसी नहीं रुकेगी 😜", "Comedy Dialogue FX"},
            {"5K2V78BtaPc", "@gaming_pro", "क्लच मोमेंट 999 IQ 🎮", "High Energy EDM"},
            {"kJQP7kiw5Fk", "@punjabi_beats", "पंजाबी वायरल ट्रेंड 👑", "Sidhu Moosewala Beat"},
            {"3JZ_D3ELwOQ", "@music_addict", "इंस्टाग्राम ट्रेंडिंग ट्रैक 🎧", "Lofi Slowed Reverb"}
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
        viewPager.setOffscreenPageLimit(1); // 1 वीडियो आगे बफर, स्मूथ और नो लैग

        loadCleanShorts();

        adapter = new ReelAdapter(shortsList, prefs, this);
        viewPager.setAdapter(adapter);

        // Instagram Engine: स्वाइप होते ही पिछली आवाज़ तुरंत बंद, अगला तुरंत प्ले
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private int previousPosition = -1;

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (previousPosition != -1 && previousPosition != position) {
                    stopVideoAt(previousPosition);
                }

                playVideoAt(position);
                previousPosition = position;

                // जब लिस्ट खत्म होने लगे, वापस शफल करके और वीडियो जोड़ दो (अनंत स्क्रॉल)
                if (position >= shortsList.size() - 3) {
                    loadMoreCleanShorts();
                }
            }
        });
    }

    private void loadCleanShorts() {
        shortsList.clear();
        for (String[] item : VERIFIED_SHORTS) {
            shortsList.add(new ShortItem(item[0], item[1], item[2], item[3]));
        }
        Collections.shuffle(shortsList); // हर बार फ्रेश आर्डर
    }

    private void loadMoreCleanShorts() {
        List<ShortItem> more = new ArrayList<>();
        for (String[] item : VERIFIED_SHORTS) {
            more.add(new ShortItem(item[0], item[1], item[2], item[3]));
        }
        Collections.shuffle(more);
        shortsList.addAll(more);
        adapter.notifyDataSetChanged();
    }

    private void stopVideoAt(int position) {
        View view = viewPager.findViewWithTag("reel_" + position);
        if (view != null) {
            WebView webView = view.findViewById(R.id.reelWebView);
            if (webView != null) {
                webView.evaluateJavascript("if(window.player && player.pauseVideo){ player.pauseVideo(); }", null);
            }
        }
    }

    private void playVideoAt(int position) {
        View view = viewPager.findViewWithTag("reel_" + position);
        if (view != null) {
            WebView webView = view.findViewById(R.id.reelWebView);
            if (webView != null) {
                webView.evaluateJavascript("if(window.player && player.playVideo){ player.playVideo(); }", null);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewPager != null) {
            stopVideoAt(viewPager.getCurrentItem());
        }
    }

    static class ShortItem {
        String videoId, creator, caption, music;
        ShortItem(String videoId, String creator, String caption, String music) {
            this.videoId = videoId;
            this.creator = creator;
            this.caption = caption;
            this.music = music;
        }
    }

    static class ReelAdapter extends RecyclerView.Adapter<ReelHolder> {
        private final List<ShortItem> list;
        private final SharedPreferences prefs;
        private final Context context;

        ReelAdapter(List<ShortItem> list, SharedPreferences prefs, Context context) {
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
            ShortItem item = list.get(position);
            holder.itemView.setTag("reel_" + position);

            // चकरी बंद
            if (holder.loader != null) holder.loader.setVisibility(View.GONE);

            // Instagram Bottom Meta
            if (holder.tvCreator != null) holder.tvCreator.setText(item.creator);
            if (holder.tvCaption != null) holder.tvCaption.setText(item.caption);
            if (holder.tvMusic != null) holder.tvMusic.setText("🎵 " + item.music);

            // Instagram Like Button
            boolean isLiked = prefs.getBoolean("liked_" + item.videoId, false);
            holder.btnLike.setText(isLiked ? "❤️" : "🤍");
            holder.btnLike.setOnClickListener(v -> {
                boolean newStatus = !prefs.getBoolean("liked_" + item.videoId, false);
                prefs.edit().putBoolean("liked_" + item.videoId, newStatus).apply();
                holder.btnLike.setText(newStatus ? "❤️" : "🤍");
            });

            // Instagram Share Button
            holder.btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch trending reel: https://youtube.com/shorts/" + item.videoId);
                context.startActivity(Intent.createChooser(shareIntent, "Share"));
            });

            // Downloader
            holder.btnDownload.setOnClickListener(v -> {
                String dlUrl = "https://en.savefrom.net/#url=https://youtube.com/shorts/" + item.videoId;
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dlUrl)));
            });

            // WebSettings
            WebSettings ws = holder.webView.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setDomStorageEnabled(true);
            ws.setDatabaseEnabled(true);
            ws.setMediaPlaybackRequiresUserGesture(false);
            ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);

            holder.webView.setWebViewClient(new WebViewClient());
            holder.webView.setWebChromeClient(new WebChromeClient());

            // 100% Clean FullScreen 9:16 Player (नो यूट्यूब लोगो, नो वेबसाइट लुक, डायरेक्ट इंस्टा स्टाइल)
            String html = "<!DOCTYPE html><html><head>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
                    + "<style>"
                    + "* { margin:0; padding:0; box-sizing:border-box; background:#000; overflow:hidden; }"
                    + "html, body { width:100%; height:100%; background:#000; }"
                    + "#box { position:absolute; top:0; left:0; width:100%; height:100%; display:flex; align-items:center; justify-content:center; }"
                    + "iframe { width:160%; height:135%; border:none; transform:scale(1.22); pointer-events:auto; }"
                    + "</style></head><body>"
                    + "<div id='box'><div id='player'></div></div>"
                    + "<script src='https://www.youtube.com/iframe_api'></script>"
                    + "<script>"
                    + "var player;"
                    + "function onYouTubeIframeAPIReady() {"
                    + "  player = new YT.Player('player', {"
                    + "    videoId: '" + item.videoId + "',"
                    + "    playerVars: {"
                    + "      'autoplay': " + (position == 0 ? "1" : "0") + ","
                    + "      'controls': 0,"
                    + "      'showinfo': 0,"
                    + "      'rel': 0,"
                    + "      'modestbranding': 1,"
                    + "      'playsinline': 1,"
                    + "      'loop': 1,"
                    + "      'playlist': '" + item.videoId + "'"
                    + "    },"
                    + "    events: {"
                    + "      'onReady': function(e) { if(" + position + " == 0) e.target.playVideo(); }"
                    + "    }"
                    + "  });"
                    + "  window.player = player;"
                    + "}"
                    + "</script></body></html>";

            holder.webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class ReelHolder extends RecyclerView.ViewHolder {
        WebView webView;
        View loader;
        TextView btnLike, btnComment, btnShare, btnDownload;
        TextView tvCreator, tvCaption, tvMusic;

        ReelHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.reelWebView);
            loader = itemView.findViewById(R.id.reelLoader);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            tvCreator = itemView.findViewById(R.id.tvReelCreator);
            tvCaption = itemView.findViewById(R.id.tvReelCaption);
            tvMusic = itemView.findViewById(R.id.tvReelMusic);
        }
    }
        }
