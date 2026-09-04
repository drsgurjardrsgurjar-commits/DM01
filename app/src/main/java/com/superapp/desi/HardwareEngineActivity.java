package com.superapp.desi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HardwareEngineActivity extends AppCompatActivity implements SensorEventListener {

    private String actionType = "ACTION_SPEAKER";
    private boolean isRunning = false;

    // Speaker Cleaner
    private AudioTrack audioTrack;
    private Thread soundThread;

    // Sensor Management
    private SensorManager sensorManager;
    private Sensor activeSensor;
    private Ringtone alarmRingtone;
    private boolean wasInPocket = false;

    private TextView tvVisualIcon, tvStatusHeader, tvInstruction;
    private Button btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("TARGET_ACTION")) {
            actionType = getIntent().getStringExtra("TARGET_ACTION");
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 100% Dynamic Screen Layout (No XML File Needed)
        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0D0E15"));
        root.setPadding(45, 45, 45, 45);

        // Header
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView btnBack = new TextView(this);
        btnBack.setText("❮ Back");
        btnBack.setTextColor(Color.parseColor("#FF0055"));
        btnBack.setTextSize(16);
        btnBack.setTypeface(null, Typeface.BOLD);
        btnBack.setPadding(10, 10, 20, 10);
        btnBack.setOnClickListener(v -> finish());
        header.addView(btnBack);

        TextView tvTitle = new TextView(this);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        tvTitle.setText("Hardware Utility");
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setGravity(Gravity.CENTER);
        header.addView(tvTitle);

        TextView tvBadge = new TextView(this);
        tvBadge.setText("OFFLINE");
        tvBadge.setTextColor(Color.parseColor("#00FFB2"));
        tvBadge.setTextSize(11);
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setBackgroundColor(Color.parseColor("#242538"));
        tvBadge.setPadding(15, 8, 15, 8);
        header.addView(tvBadge);

        root.addView(header);

        // Center Content Container
        LinearLayout centerBox = new LinearLayout(this);
        centerBox.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        centerBox.setOrientation(LinearLayout.VERTICAL);
        centerBox.setGravity(Gravity.CENTER);

        tvVisualIcon = new TextView(this);
        tvVisualIcon.setText("🔊");
        tvVisualIcon.setTextSize(85);
        tvVisualIcon.setGravity(Gravity.CENTER);
        centerBox.addView(tvVisualIcon);

        tvStatusHeader = new TextView(this);
        tvStatusHeader.setText("Ready to Start");
        tvStatusHeader.setTextColor(Color.WHITE);
        tvStatusHeader.setTextSize(20);
        tvStatusHeader.setTypeface(null, Typeface.BOLD);
        tvStatusHeader.setGravity(Gravity.CENTER);
        tvStatusHeader.setPadding(0, 30, 0, 10);
        centerBox.addView(tvStatusHeader);

        tvInstruction = new TextView(this);
        tvInstruction.setText("वॉल्यूम 100% करें और फोन नीचे रखें।");
        tvInstruction.setTextColor(Color.parseColor("#88889D"));
        tvInstruction.setTextSize(13);
        tvInstruction.setGravity(Gravity.CENTER);
        tvInstruction.setPadding(30, 0, 30, 0);
        centerBox.addView(tvInstruction);

        root.addView(centerBox);

        // Bottom Action Button
        btnToggle = new Button(this);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 150);
        btnToggle.setLayoutParams(btnParams);
        btnToggle.setBackgroundColor(Color.parseColor("#FF0055"));
        btnToggle.setTextColor(Color.WHITE);
        btnToggle.setTextSize(16);
        btnToggle.setTypeface(null, Typeface.BOLD);
        root.addView(btnToggle);

        setContentView(root);

        setupToolMode();

        btnToggle.setOnClickListener(v -> {
            if (isRunning) {
                stopFeature();
            } else {
                startFeature();
            }
        });
    }

    private void setupToolMode() {
        if ("ACTION_THEFT".equals(actionType)) {
            tvVisualIcon.setText("🚨");
            tvStatusHeader.setText("Anti-Theft Pocket Guard");
            tvInstruction.setText("फ़ोन जेब में रखें। जेब से बाहर खींचते ही सायरन बजेगा।");
            btnToggle.setText("ACTIVATE GUARD");
            if (sensorManager != null) {
                activeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            }
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        } else if ("ACTION_EMF".equals(actionType)) {
            tvVisualIcon.setText("🧲");
            tvStatusHeader.setText("Stud & Wire Finder");
            tvInstruction.setText("फ़ोन को दीवार पर फिराएं। तार या लोहे के पास रीडिंग बढ़ेगी।");
            btnToggle.setText("START DETECTOR");
            if (sensorManager != null) {
                activeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            }
        } else {
            tvVisualIcon.setText("🔊");
            tvStatusHeader.setText("Speaker Water Cleaner");
            tvInstruction.setText("वॉल्यूम 100% करें और फ़ोन का स्पीकर नीचे की तरफ रखें।");
            btnToggle.setText("START CLEANING (165Hz)");
        }
    }

    private void startFeature() {
        isRunning = true;
        btnToggle.setText("STOP");

        if ("ACTION_THEFT".equals(actionType)) {
            if (activeSensor != null) {
                sensorManager.registerListener(this, activeSensor, SensorManager.SENSOR_DELAY_NORMAL);
                tvStatusHeader.setText("Guard Activated 🔒");
                tvInstruction.setText("अब फ़ोन जेब में रख लें...");
                Toast.makeText(this, "Guard On! Phone pocket me dalein", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Proximity Sensor Not Supported!", Toast.LENGTH_SHORT).show();
            }
        } else if ("ACTION_EMF".equals(actionType)) {
            if (activeSensor != null) {
                sensorManager.registerListener(this, activeSensor, SensorManager.SENSOR_DELAY_UI);
                tvStatusHeader.setText("Scanning Magnetic Field...");
            } else {
                Toast.makeText(this, "Magnetic Sensor Not Available!", Toast.LENGTH_SHORT).show();
            }
        } else {
            tvStatusHeader.setText("Ejecting Water & Dust...");
            start165HzTone();
        }
    }

    private void stopFeature() {
        isRunning = false;
        if ("ACTION_THEFT".equals(actionType)) {
            btnToggle.setText("ACTIVATE GUARD");
        } else if ("ACTION_EMF".equals(actionType)) {
            btnToggle.setText("START DETECTOR");
        } else {
            btnToggle.setText("START CLEANING (165Hz)");
        }
        tvStatusHeader.setText("Stopped");

        if (sensorManager != null) sensorManager.unregisterListener(this);
        if (alarmRingtone != null && alarmRingtone.isPlaying()) alarmRingtone.stop();
        wasInPocket = false;
        stop165HzTone();
    }

    private void start165HzTone() {
        soundThread = new Thread(() -> {
            int sampleRate = 44100;
            double freqOfTone = 165.0;
            double[] sample = new double[sampleRate];
            byte[] generatedSnd = new byte[2 * sampleRate];

            for (int i = 0; i < sampleRate; ++i) {
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
            }

            int idx = 0;
            for (final double dVal : sample) {
                final short val = (short) (dVal * 32767);
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                    AudioTrack.MODE_STREAM);

            try {
                audioTrack.play();
                while (isRunning) {
                    audioTrack.write(generatedSnd, 0, generatedSnd.length);
                }
            } catch (Exception ignored) {}
        });
        soundThread.start();
    }

    private void stop165HzTone() {
        if (audioTrack != null) {
            try {
                audioTrack.stop();
                audioTrack.release();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if ("ACTION_THEFT".equals(actionType) && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            if (distance < activeSensor.getMaximumRange()) {
                wasInPocket = true;
                tvInstruction.setText("फ़ोन जेब में सुरक्षित है...");
            } else {
                if (wasInPocket && isRunning) {
                    tvStatusHeader.setText("🚨 THEFT DETECTED! 🚨");
                    tvInstruction.setText("सायरन बज रहा है! बंद करने के लिए STOP दबाएँ!");
                    if (alarmRingtone != null && !alarmRingtone.isPlaying()) {
                        alarmRingtone.play();
                    }
                }
            }
        } else if ("ACTION_EMF".equals(actionType) && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double field = Math.sqrt(x * x + y * y + z * z);
            tvStatusHeader.setText(String.format("Magnetic: %.1f µT", field));
            if (field > 65.0) {
                tvInstruction.setText("⚠️ धातु या बिजली का तार पास है!");
            } else {
                tvInstruction.setText("दीवार साफ़ है।");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDestroy() {
        stopFeature();
        super.onDestroy();
    }
                    }
                    
