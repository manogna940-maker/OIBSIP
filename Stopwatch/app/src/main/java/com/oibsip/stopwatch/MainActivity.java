package com.oibsip.stopwatch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView timeText;
    private Button startButton, pauseButton;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long accumulatedMillis = 0;
    private long startedAt = 0;
    private boolean running = false;
    private SharedPreferences preferences;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            updateDisplay();
            if (running) handler.postDelayed(this, 50);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeText = findViewById(R.id.timeText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        Button resetButton = findViewById(R.id.resetButton);
        preferences = getSharedPreferences("stopwatch", MODE_PRIVATE);
        restoreState();
        startButton.setOnClickListener(v -> startTimer());
        pauseButton.setOnClickListener(v -> pauseTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        updateButtons();
        updateDisplay();
    }

    private void startTimer() {
        if (running) return;
        startedAt = SystemClock.elapsedRealtime();
        running = true;
        updateButtons();
        handler.post(ticker);
        saveState();
    }

    private void pauseTimer() {
        if (!running) return;
        accumulatedMillis += SystemClock.elapsedRealtime() - startedAt;
        running = false;
        handler.removeCallbacks(ticker);
        updateButtons();
        updateDisplay();
        saveState();
    }

    private void resetTimer() {
        running = false;
        accumulatedMillis = 0;
        startedAt = 0;
        handler.removeCallbacks(ticker);
        updateButtons();
        updateDisplay();
        saveState();
    }

    private long elapsedMillis() {
        return accumulatedMillis + (running ? SystemClock.elapsedRealtime() - startedAt : 0);
    }

    private void updateDisplay() {
        long totalSeconds = elapsedMillis() / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        timeText.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void updateButtons() {
        startButton.setEnabled(!running);
        pauseButton.setEnabled(running);
    }

    private void saveState() {
        preferences.edit().putBoolean("running", running).putLong("accumulated", accumulatedMillis)
                .putLong("startedWallTime", running ? System.currentTimeMillis() : 0).apply();
    }

    private void restoreState() {
        accumulatedMillis = preferences.getLong("accumulated", 0);
        running = preferences.getBoolean("running", false);
        if (running) {
            long wallStart = preferences.getLong("startedWallTime", System.currentTimeMillis());
            accumulatedMillis += Math.max(0, System.currentTimeMillis() - wallStart);
            startedAt = SystemClock.elapsedRealtime();
            handler.post(ticker);
        }
    }

    @Override protected void onPause() { super.onPause(); saveState(); }
    @Override protected void onDestroy() { handler.removeCallbacks(ticker); super.onDestroy(); }
}
