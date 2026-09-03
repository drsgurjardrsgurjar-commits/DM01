package com.superapp.desi;

import android.content.Context;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HardwareEngineActivity extends AppCompatActivity implements SensorEventListener {

    private String actionType = "ACTION_SPEAKER";
    private boolean isRunning = false;

    // Speaker Cleaner AudioTrack
    private AudioTrack audioTrack;
    private Thread soundThread;

    // Pocket-Grab Anti-Theft
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Ringtone alarmRingtone;
    private boolean wasInPocket = false;

    private TextView tvVisualIcon, tvStatusHeader, tvInstruction;
    private Button btnToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_engine);

        if (getIntent().hasExtra("TARGET_ACTION")) {
            actionType = getIntent().getStringExtra("TARGET_ACTION");
        }

        findViewById(R.id.btnBackHardware).setOnClickListener(v -> finish());
        tvVisualIcon = findViewById(R.id.tvToolVisualIcon);
        tvStatusHeader = findViewById(R.id.tvToolStatusHeader);
        tvInstruction = findViewById(R.id.tvToolInstruction);
        btnToggle = findViewById(R.id.btnToggleEngine);

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
            tvInstruction.setText("फोन जेब में रखें। जेब से निकालते ही फुल सायरन बज जाएगा।");
            btnToggle.setText("ACTIVATE GUARD");
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            }
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        } else {
            // डिफॉल्ट: स्पीकर क्लीनर
            tvVisualIcon.setText("🔊");
            tvStatusHeader.setText("Speaker Water Cleaner");
            tvInstruction.setText("वॉल्यूम 100% करें और फोन का स्पीकर नीचे की तरफ रखें।");
            btnToggle.setText("START CLEANING (165Hz)");
        }
    }

    private void startFeature() {
        isRunning = true;
        btnToggle.setText("STOP");

        if ("ACTION_THEFT".equals(actionType)) {
            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                tvStatusHeader.setText("Guard Activated 🔒");
                tvInstruction.setText("अब फोन अपनी जेब में डालें...");
                Toast.makeText(this, "Guard On! Phone pocket me dalein", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Proximity Sensor Not Found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 165Hz अल्ट्रा रेजोनेंस पल्स स्टार्ट
            tvStatusHeader.setText("Ejecting Water & Dust...");
            start165HzTone();
        }
    }

    private void stopFeature() {
        isRunning = false;
        btnToggle.setText("ACTION_THEFT".equals(actionType) ? "ACTIVATE GUARD" : "START CLEANING");
        tvStatusHeader.setText("Stopped");

        if ("ACTION_THEFT".equals(actionType)) {
            if (sensorManager != null) sensorManager.unregisterListener(this);
            if (alarmRingtone != null && alarmRingtone.isPlaying()) alarmRingtone.stop();
            wasInPocket = false;
        } else {
            stop165HzTone();
        }
    }

    // 165Hz Sound Generator Logic
    private void start165HzTone() {
        soundThread = new Thread(() -> {
            int sampleRate = 44100;
            int numSamples = sampleRate;
            double[] sample = new double[numSamples];
            byte[] generatedSnd = new byte[2 * numSamples];
            double freqOfTone = 165.0; // 165Hz पानी निकालने की सटीक फ्रीक्वेंसी

            for (int i = 0; i < numSamples; ++i) {
                sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
            }

            int idx = 0;
            for (final double dVal : sample) {
                final short val = (short) ((dVal * 32767));
                generatedSnd[idx++] = (byte) (val & 0x00ff);
                generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
            }

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            while (isRunning) {
                audioTrack.write(generatedSnd, 0, generatedSnd.length);
            }
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
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            // जब फोन जेब के अंदर है (सेंसर के पास कोई चीज़ है)
            if (distance < proximitySensor.getMaximumRange()) {
                wasInPocket = true;
                tvInstruction.setText("फोन जेब के अंदर सुरक्षित है...");
            } else {
                // फोन जेब से बाहर खींचा गया
                if (wasInPocket && isRunning) {
                    triggerSiren();
                }
            }
        }
    }

    private void triggerSiren() {
        tvStatusHeader.setText("🚨 THEFT DETECTED! 🚨");
        tvInstruction.setText("अलार्म बंद करने के लिए STOP दबाएँ!");
        if (alarmRingtone != null && !alarmRingtone.isPlaying()) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
            }
            alarmRingtone.play();
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
                
