package com.superapp.desi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View tabHomeView, tabMoviesView, tabChatView;
    private View panicStudyScreen, secretPinPadOverlay;
    private TextView txtNavHome, txtNavReels, txtNavMovies, txtNavChat, txtNavProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. स्क्रीन व्यूज को लिंक करना
        tabHomeView = findViewById(R.id.tabHomeView);
        tabMoviesView = findViewById(R.id.tabMoviesView);
        tabChatView = findViewById(R.id.tabChatView);
        panicStudyScreen = findViewById(R.id.panicStudyScreen);
        secretPinPadOverlay = findViewById(R.id.secretPinPadOverlay);

        txtNavHome = findViewById(R.id.txtNavHome);
        txtNavReels = findViewById(R.id.txtNavReels);
        txtNavMovies = findViewById(R.id.txtNavMovies);
        txtNavChat = findViewById(R.id.txtNavChat);
        txtNavProfile = findViewById(R.id.txtNavProfile);

        // 2. सारे फीचर्स चालू करना
        setupAstraXNavigation();
        setupAstraXBoxes();
        setupPanicAndSecretVault();
    }

    // --- 5 बॉटम टैब्स का कंट्रोल ---
    private void setupAstraXNavigation() {
        // 1. Home टैब
        findViewById(R.id.navHome).setOnClickListener(v -> switchTab("HOME"));

        // 2. Reels टैब (सीधे 9:16 रील्स प्लेयर खुलेगा)
        findViewById(R.id.navReels).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReelsActivity.class);
            startActivity(intent);
        });

        // 3. Movies टैब (KGF-2 पोस्टर और मूवी स्क्रीन)
        findViewById(R.id.navMovies).setOnClickListener(v -> switchTab("MOVIES"));

        // 4. Chat टैब (कम्युनिटी व चैट स्क्रीन)
        findViewById(R.id.navChat).setOnClickListener(v -> switchTab("CHAT"));

        // 5. Profile टैब
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            Toast.makeText(this, "Profile: Logged in as AstraX User 🌟", Toast.LENGTH_SHORT).show();
        });
    }

    // --- स्क्रीन बदलने का लॉजिक ---
    private void switchTab(String tab) {
        tabHomeView.setVisibility(View.GONE);
        tabMoviesView.setVisibility(View.GONE);
        tabChatView.setVisibility(View.GONE);
        secretPinPadOverlay.setVisibility(View.GONE);

        // टैब के रंग रीसेट करना
        txtNavHome.setTextColor(Color.parseColor("#8E92B2"));
        txtNavReels.setTextColor(Color.parseColor("#8E92B2"));
        txtNavMovies.setTextColor(Color.parseColor("#8E92B2"));
        txtNavChat.setTextColor(Color.parseColor("#8E92B2"));
        txtNavProfile.setTextColor(Color.parseColor("#8E92B2"));

        if ("HOME".equals(tab)) {
            tabHomeView.setVisibility(View.VISIBLE);
            txtNavHome.setTextColor(Color.parseColor("#7C5DFA"));
        } else if ("MOVIES".equals(tab)) {
            tabMoviesView.setVisibility(View.VISIBLE);
            txtNavMovies.setTextColor(Color.parseColor("#7C5DFA"));
        } else if ("CHAT".equals(tab)) {
            tabChatView.setVisibility(View.VISIBLE);
            txtNavChat.setTextColor(Color.parseColor("#7C5DFA"));
        }
    }

    // --- 12 बॉक्सेस पर क्लिक करने पर क्या होगा ---
    private void setupAstraXBoxes() {
        // AI Studio (1 - 25)
        findViewById(R.id.boxAiStudio).setOnClickListener(v -> 
            Toast.makeText(this, "AI Studio (Tools 1–25) Launching...", Toast.LENGTH_SHORT).show());

        // Offline Tools (26 - 50) -> स्पीकर वाटर क्लीनर
        findViewById(R.id.boxOfflineTools).setOnClickListener(v -> {
            Intent intent = new Intent(this, HardwareEngineActivity.class);
            intent.putExtra("TARGET_ACTION", "ACTION_SPEAKER");
            startActivity(intent);
        });

        // Privacy Tools (51 - 70) -> एंटी-थेफ्ट जेबकतरा अलार्म
        findViewById(R.id.boxPrivacyTools).setOnClickListener(v -> {
            Intent intent = new Intent(this, HardwareEngineActivity.class);
            intent.putExtra("TARGET_ACTION", "ACTION_THEFT");
            startActivity(intent);
        });

        // Reels (Direct Play)
        findViewById(R.id.boxReels).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReelsActivity.class);
            startActivity(intent);
        });

        // Movies (Direct Play)
        findViewById(R.id.boxMovies).setOnClickListener(v -> switchTab("MOVIES"));

        // Chat (Secure)
        findViewById(R.id.boxChat).setOnClickListener(v -> switchTab("CHAT"));

        // Social Savers (71 - 90)
        findViewById(R.id.boxSocialSavers).setOnClickListener(v -> 
            Toast.makeText(this, "Social Savers: Insta, WA & Video Grabber...", Toast.LENGTH_SHORT).show());

        // Office & PDF (91 - 110)
        findViewById(R.id.boxOfficePdf).setOnClickListener(v -> 
            Toast.makeText(this, "Office & PDF Scanner Tools Launching...", Toast.LENGTH_SHORT).show());

        // Desi Life (111 - 130)
        findViewById(R.id.boxDesiLife).setOnClickListener(v -> 
            Toast.makeText(this, "Desi Life: Mandi, Challan & Fuel Rates...", Toast.LENGTH_SHORT).show());

        // System Boost (131 - 150)
        findViewById(R.id.boxSystemBoost).setOnClickListener(v -> 
            Toast.makeText(this, "System Boost: Cleaning RAM & Junk...", Toast.LENGTH_SHORT).show());

        // Media Tools (151 - 165)
        findViewById(R.id.boxMediaTools).setOnClickListener(v -> 
            Toast.makeText(this, "Media Tools: MP3, Equalizer & Player...", Toast.LENGTH_SHORT).show());

        // More Tools (Extra)
        findViewById(R.id.boxMoreTools).setOnClickListener(v -> 
            Toast.makeText(this, "All 165+ Master Tools Loaded!", Toast.LENGTH_SHORT).show());
    }

    // --- पैनिक स्विच और गुप्त चैट का लॉजिक ---
    private void setupPanicAndSecretVault() {
        // फ्लोटिंग पैनिक स्विच: छूते ही NCERT Physics Notes (पढ़ाई) खुल जाएगी
        findViewById(R.id.floatingPanicSwitch).setOnClickListener(v -> {
            panicStudyScreen.setVisibility(View.VISIBLE);
        });

        // पढ़ाई मोड से वापस आने का बटन
        findViewById(R.id.btnExitPanic).setOnClickListener(v -> {
            panicStudyScreen.setVisibility(View.GONE);
        });

        // चैट स्क्रीन पर 3-डॉट मेनू
        findViewById(R.id.btnChatMenuDots).setOnClickListener(v -> {
            String[] options = {"New Group", "Broadcast", "Starred Messages", "🔒 Privacy (Secret Vault)", "Settings"};
            new AlertDialog.Builder(this)
                    .setTitle("Chat Options")
                    .setItems(options, (dialog, which) -> {
                        if (which == 3) {
                            // Secret PIN Pad खोलना
                            secretPinPadOverlay.setVisibility(View.VISIBLE);
                        }
                    })
                    .show();
        });

        // सीक्रेट चैट पिन कैंसिल बटन
        findViewById(R.id.btnCancelPin).setOnClickListener(v -> {
            secretPinPadOverlay.setVisibility(View.GONE);
        });
    }

    // फोन का बैक बटन दबाने पर हैंडलिंग
    @Override
    public void onBackPressed() {
        if (panicStudyScreen.getVisibility() == View.VISIBLE) {
            panicStudyScreen.setVisibility(View.GONE);
        } else if (secretPinPadOverlay.getVisibility() == View.VISIBLE) {
            secretPinPadOverlay.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
                            }
