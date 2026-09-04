package com.superapp.desi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ScrollView homeScrollView, moviesScrollView;
    private LinearLayout chatLayout, panicScreen, secretPinOverlay;
    private TextView navTxtHome, navTxtReels, navTxtMovies, navTxtChat, navTxtProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setBackgroundColor(Color.parseColor("#090A10"));

        // Content Area
        FrameLayout contentArea = new FrameLayout(this);
        FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentParams.bottomMargin = dp(56);
        contentArea.setLayoutParams(contentParams);

        // 1. HOME SCREEN
        homeScrollView = buildHomeScreen();
        contentArea.addView(homeScrollView);

        // 2. MOVIES SCREEN
        moviesScrollView = buildMoviesScreen();
        moviesScrollView.setVisibility(View.GONE);
        contentArea.addView(moviesScrollView);

        // 3. CHAT SCREEN
        chatLayout = buildChatScreen();
        chatLayout.setVisibility(View.GONE);
        contentArea.addView(chatLayout);

        // 4. SECRET PIN OVERLAY
        secretPinOverlay = buildSecretPinOverlay();
        secretPinOverlay.setVisibility(View.GONE);
        contentArea.addView(secretPinOverlay);

        // 5. PANIC STUDY SCREEN
        panicScreen = buildPanicScreen();
        panicScreen.setVisibility(View.GONE);
        contentArea.addView(panicScreen);

        root.addView(contentArea);

        // FLOATING PANIC SWITCH (⚡)
        TextView floatingPanic = new TextView(this);
        FrameLayout.LayoutParams panicParams = new FrameLayout.LayoutParams(dp(40), dp(40));
        panicParams.gravity = Gravity.BOTTOM | Gravity.END;
        panicParams.bottomMargin = dp(68);
        panicParams.rightMargin = dp(14);
        floatingPanic.setLayoutParams(panicParams);
        floatingPanic.setText("⚡");
        floatingPanic.setTextSize(18);
        floatingPanic.setTextColor(Color.WHITE);
        floatingPanic.setGravity(Gravity.CENTER);
        floatingPanic.setBackgroundColor(Color.parseColor("#FF0055"));
        floatingPanic.setAlpha(0.45f);
        floatingPanic.setOnClickListener(v -> panicScreen.setVisibility(View.VISIBLE));
        root.addView(floatingPanic);

        // BOTTOM NAVIGATION BAR (5 TABS)
        LinearLayout bottomNav = buildBottomNav();
        root.addView(bottomNav);

        setContentView(root);
    }

    private ScrollView buildHomeScreen() {
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sv.setVerticalScrollBarEnabled(false);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(dp(16), dp(16), dp(16), dp(24));

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("AstraX");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(null, Typeface.BOLD);
        titles.addView(tvTitle);

        TextView tvSub = new TextView(this);
        tvSub.setText("All-In-One Super App");
        tvSub.setTextColor(Color.parseColor("#8D92A3"));
        tvSub.setTextSize(12);
        titles.addView(tvSub);
        header.addView(titles);

        TextView tvCrown = new TextView(this);
        tvCrown.setText("👑  🔍  ⋮");
        tvCrown.setTextColor(Color.WHITE);
        tvCrown.setTextSize(18);
        header.addView(tvCrown);
        ll.addView(header);

        // 165+ Banner
        LinearLayout banner = new LinearLayout(this);
        LinearLayout.LayoutParams bannerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(115));
        bannerParams.topMargin = dp(16);
        banner.setLayoutParams(bannerParams);
        banner.setBackgroundColor(Color.parseColor("#1F1532"));
        banner.setPadding(dp(16), dp(16), dp(16), dp(16));
        banner.setOrientation(LinearLayout.HORIZONTAL);
        banner.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout bannerText = new LinearLayout(this);
        bannerText.setOrientation(LinearLayout.VERTICAL);
        bannerText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView bTitle = new TextView(this);
        bTitle.setText("165+ Powerful Tools");
        bTitle.setTextColor(Color.WHITE);
        bTitle.setTextSize(18);
        bTitle.setTypeface(null, Typeface.BOLD);
        bannerText.addView(bTitle);

        TextView bDesc = new TextView(this);
        bDesc.setText("AI • Offline • Privacy • Media • Office\nMoney • System • More");
        bDesc.setTextColor(Color.parseColor("#B2B7D0"));
        bDesc.setTextSize(11);
        bDesc.setPadding(0, dp(4), 0, 0);
        bannerText.addView(bDesc);
        banner.addView(bannerText);

        TextView bIcon = new TextView(this);
        bIcon.setText("🌀");
        bIcon.setTextSize(44);
        banner.addView(bIcon);
        ll.addView(banner);

        // Quick Access Title
        TextView qaTitle = new TextView(this);
        qaTitle.setText("Quick Access");
        qaTitle.setTextColor(Color.WHITE);
        qaTitle.setTextSize(16);
        qaTitle.setTypeface(null, Typeface.BOLD);
        qaTitle.setPadding(0, dp(20), 0, dp(10));
        ll.addView(qaTitle);

        // 12 Boxes Grid (4 Rows)
        ll.addView(createRow("🤖", "AI Studio", "(1 – 25)", "#2E1B4E", "#B39DDB", "📶", "Offline Tools", "(26 – 50)", "#0A3323", "#80CBC4", "🎭", "Privacy Tools", "(51 – 70)", "#2B1A3D", "#CE93D8"));
        ll.addView(createRow("▶️", "Reels", "(Direct Play)", "#4A1525", "#F48FB1", "🎬", "Movies", "(Direct Play)", "#102542", "#90CAF9", "💬", "Chat", "(Secure)", "#153B2F", "#A5D6A7"));
        ll.addView(createRow("📥", "Social Savers", "(71 – 90)", "#422510", "#FFCC80", "📑", "Office & PDF", "(91 – 110)", "#103A3A", "#80DEEA", "₹", "Desi Life", "(111 – 130)", "#3D320A", "#FFE082"));
        ll.addView(createRow("⚡", "System Boost", "(131 – 150)", "#14213D", "#90CAF9", "🎵", "Media Tools", "(151 – 165)", "#1B1C4B", "#C5CAE9", "⸬", "More Tools", "(Extra)", "#20222D", "#B0BEC5"));

        // System Status Header
        TextView statTitle = new TextView(this);
        statTitle.setText("System Status");
        statTitle.setTextColor(Color.WHITE);
        statTitle.setTextSize(15);
        statTitle.setTypeface(null, Typeface.BOLD);
        statTitle.setPadding(0, dp(20), 0, dp(10));
        ll.addView(statTitle);

        // Status Row (4 Metrics)
        LinearLayout statusRow = new LinearLayout(this);
        statusRow.setOrientation(LinearLayout.HORIZONTAL);
        statusRow.setWeightSum(4);

        statusRow.addView(createMetricBox("Storage", "62%", "40 GB / 64 GB", "#00FFB2"));
        statusRow.addView(createMetricBox("Battery", "78%", "Normal", "#00FFB2"));
        statusRow.addView(createMetricBox("RAM", "61%", "2.3 GB / 4 GB", "#FFAA00"));
        statusRow.addView(createMetricBox("Temp", "32°C", "Good", "#00FFB2"));
        ll.addView(statusRow);

        sv.addView(ll);
        return sv;
    }

    private LinearLayout createRow(String i1, String t1, String s1, String bg1, String subC1,
                                  String i2, String t2, String s2, String bg2, String subC2,
                                  String i3, String t3, String s3, String bg3, String subC3) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(3);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8);
        row.setLayoutParams(params);

        row.addView(createBox(i1, t1, s1, bg1, subC1));
        row.addView(createBox(i2, t2, s2, bg2, subC2));
        row.addView(createBox(i3, t3, s3, bg3, subC3));
        return row;
    }

    private LinearLayout createBox(String icon, String title, String sub, String bgColor, String subColor) {
        LinearLayout box = new LinearLayout(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(96), 1.0f);
        p.setMargins(dp(3), 0, dp(3), 0);
        box.setLayoutParams(p);
        box.setBackgroundColor(Color.parseColor(bgColor));
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);

        TextView tvI = new TextView(this);
        tvI.setText(icon);
        tvI.setTextSize(26);
        box.addView(tvI);

        TextView tvT = new TextView(this);
        tvT.setText(title);
        tvT.setTextColor(Color.WHITE);
        tvT.setTextSize(12);
        tvT.setTypeface(null, Typeface.BOLD);
        tvT.setPadding(0, dp(4), 0, 0);
        box.addView(tvT);

        TextView tvS = new TextView(this);
        tvS.setText(sub);
        tvS.setTextColor(Color.parseColor(subColor));
        tvS.setTextSize(10);
        box.addView(tvS);

        box.setOnClickListener(v -> {
            if ("Reels".equals(title)) {
                startActivity(new Intent(MainActivity.this, ReelsActivity.class));
            } else if ("Movies".equals(title)) {
                switchTab("MOVIES");
            } else if ("Chat".equals(title)) {
                switchTab("CHAT");
            } else if ("Offline Tools".equals(title)) {
                Intent it = new Intent(MainActivity.this, HardwareEngineActivity.class);
                it.putExtra("TARGET_ACTION", "ACTION_SPEAKER");
                startActivity(it);
            } else if ("Privacy Tools".equals(title)) {
                Intent it = new Intent(MainActivity.this, HardwareEngineActivity.class);
                it.putExtra("TARGET_ACTION", "ACTION_THEFT");
                startActivity(it);
            } else {
                Toast.makeText(this, title + " Launching...", Toast.LENGTH_SHORT).show();
            }
        });

        return box;
    }

    private LinearLayout createMetricBox(String title, String val, String sub, String valColor) {
        LinearLayout box = new LinearLayout(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        p.setMargins(dp(3), 0, dp(3), 0);
        box.setLayoutParams(p);
        box.setBackgroundColor(Color.parseColor("#151724"));
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(4), dp(10), dp(4), dp(10));

        TextView tvT = new TextView(this);
        tvT.setText(title);
        tvT.setTextColor(Color.parseColor("#8E92B2"));
        tvT.setTextSize(11);
        box.addView(tvT);

        TextView tvV = new TextView(this);
        tvV.setText(val);
        tvV.setTextColor(Color.parseColor(valColor));
        tvV.setTextSize(15);
        tvV.setTypeface(null, Typeface.BOLD);
        tvV.setPadding(0, dp(2), 0, dp(2));
        box.addView(tvV);

        TextView tvS = new TextView(this);
        tvS.setText(sub);
        tvS.setTextColor(Color.parseColor("#5C607E"));
        tvS.setTextSize(9);
        box.addView(tvS);

        return box;
    }

    private ScrollView buildMoviesScreen() {
        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText("Movies & Cinema 🍿");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        ll.addView(title);

        TextView kgf = new TextView(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(180));
        p.topMargin = dp(16);
        kgf.setLayoutParams(p);
        kgf.setBackgroundColor(Color.parseColor("#2E1B15"));
        kgf.setGravity(Gravity.CENTER);
        kgf.setText("K.G.F: Chapter 2\n[Poster Preview]");
        kgf.setTextColor(Color.parseColor("#FFB74D"));
        kgf.setTextSize(20);
        kgf.setTypeface(null, Typeface.BOLD);
        ll.addView(kgf);

        Button btnWatch = new Button(this);
        btnWatch.setText("▶ Watch Now");
        btnWatch.setBackgroundColor(Color.parseColor("#7C5DFA"));
        btnWatch.setTextColor(Color.WHITE);
        ll.addView(btnWatch);

        sv.addView(ll);
        return sv;
    }

    private LinearLayout buildChatScreen() {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("Chat");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        top.addView(title);

        TextView dots = new TextView(this);
        dots.setText("⋮");
        dots.setTextColor(Color.WHITE);
        dots.setTextSize(24);
        dots.setPadding(dp(8), dp(4), dp(8), dp(4));
        dots.setOnClickListener(v -> {
            String[] opts = {"New Group", "Broadcast", "Starred Messages", "🔒 Privacy (Secret Vault)", "Settings"};
            new AlertDialog.Builder(this)
                    .setTitle("Chat Menu")
                    .setItems(opts, (d, w) -> {
                        if (w == 3) secretPinOverlay.setVisibility(View.VISIBLE);
                    }).show();
        });
        top.addView(dots);
        ll.addView(top);

        TextView info = new TextView(this);
        info.setText("\n🧔 Aman: Hey! Kaise ho? (12:25 PM)\n\n👩 Neha: Kal milte hain (11:45 AM)");
        info.setTextColor(Color.parseColor("#B0BEC5"));
        info.setTextSize(14);
        ll.addView(info);

        return ll;
    }

    private LinearLayout buildSecretPinOverlay() {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setBackgroundColor(Color.parseColor("#090A10"));
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(dp(24), dp(24), dp(24), dp(24));

        TextView tvT = new TextView(this);
        tvT.setText("🔒 Secret Chat Access");
        tvT.setTextColor(Color.WHITE);
        tvT.setTextSize(20);
        tvT.setTypeface(null, Typeface.BOLD);
        ll.addView(tvT);

        TextView tvDots = new TextView(this);
        tvDots.setText("○ ○ ○ ○");
        tvDots.setTextColor(Color.parseColor("#7C5DFA"));
        tvDots.setTextSize(28);
        tvDots.setPadding(0, dp(20), 0, dp(20));
        ll.addView(tvDots);

        Button btnBack = new Button(this);
        btnBack.setText("Cancel");
        btnBack.setBackgroundColor(Color.parseColor("#151724"));
        btnBack.setTextColor(Color.WHITE);
        btnBack.setOnClickListener(v -> secretPinOverlay.setVisibility(View.GONE));
        ll.addView(btnBack);

        return ll;
    }

    private LinearLayout buildPanicScreen() {
        LinearLayout ll = new LinearLayout(this);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setBackgroundColor(Color.WHITE);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(dp(20), dp(30), dp(20), dp(20));

        TextView tvH = new TextView(this);
        tvH.setText("NCERT Class 12: Physics Notes");
        tvH.setTextColor(Color.BLACK);
        tvH.setTextSize(20);
        tvH.setTypeface(null, Typeface.BOLD);
        ll.addView(tvH);

        TextView tvC = new TextView(this);
        tvC.setText("\nChapter 1: Electric Charges and Fields\n\n1. Coulomb's Law: The electrostatic force between two stationary point charges is directly proportional to the product of charges and inversely proportional to the square of distance between them.\n\nF = k * (|q1 * q2|) / r²\n\n2. Gauss's Law: Total electric flux through a closed surface is equal to 1/ε0 times the total charge enclosed.");
        tvC.setTextColor(Color.parseColor("#333333"));
        tvC.setTextSize(14);
        ll.addView(tvC);

        Button btnResume = new Button(this);
        btnResume.setText("Resume App");
        btnResume.setOnClickListener(v -> panicScreen.setVisibility(View.GONE));
        ll.addView(btnResume);

        return ll;
    }

    private LinearLayout buildBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56));
        params.gravity = Gravity.BOTTOM;
        nav.setLayoutParams(params);
        nav.setBackgroundColor(Color.parseColor("#0C0D15"));
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setWeightSum(5);

        nav.addView(createNavItem("🏠", "Home", true, () -> switchTab("HOME")));
        nav.addView(createNavItem("🎬", "Reels", false, () -> startActivity(new Intent(MainActivity.this, ReelsActivity.class))));
        nav.addView(createNavItem("🍿", "Movies", false, () -> switchTab("MOVIES")));
        nav.addView(createNavItem("💬", "Chat", false, () -> switchTab("CHAT")));
        nav.addView(createNavItem("👤", "Profile", false, () -> Toast.makeText(this, "Profile: Logged In", Toast.LENGTH_SHORT).show()));

        return nav;
    }

    private LinearLayout createNavItem(String icon, String label, boolean isSelected, Runnable onClick) {
        LinearLayout item = new LinearLayout(this);
        item.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);

        TextView tvI = new TextView(this);
        tvI.setText(icon);
        tvI.setTextSize(18);
        item.addView(tvI);

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(10);
        tvL.setTextColor(isSelected ? Color.parseColor("#7C5DFA") : Color.parseColor("#8E92B2"));
        if ("Ho
