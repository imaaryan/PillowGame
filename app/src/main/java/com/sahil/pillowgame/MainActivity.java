package com.sahil.pillowgame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Random random = new Random();
    private Button startStopButton;
    private TextView counterTextView;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;
    private int elapsedTime = 0; // Elapsed time in seconds
    private int delay; // Delay time in seconds for stopping the music
    private int[] audioResIds = {R.raw.audio1, R.raw.audio2, R.raw.audio3, R.raw.audio4, R.raw.audio5, R.raw.audio6, R.raw.audio7, R.raw.audio8, R.raw.audio9, R.raw.audio10, R.raw.audio11, R.raw.audio12, R.raw.audio13, R.raw.audio14, R.raw.audio15}; // Array of audio files

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = findViewById(R.id.startStopButton);
        counterTextView = findViewById(R.id.counter);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicPlaying) {
                    stopMusic();
                } else {
                    startGame();
                }
            }
        });
    }

    public void startGame() {
        // Generate a random delay between 5 and 60 seconds
        delay = random.nextInt(56) + 5;
        isMusicPlaying = true;
        startStopButton.setText("STOP");

        // Randomly choose an audio file to play
        int randomAudioResId = audioResIds[random.nextInt(audioResIds.length)];

        // Release the previous MediaPlayer if it exists to avoid memory leaks
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Initialize the MediaPlayer with the randomly chosen audio
        mediaPlayer = MediaPlayer.create(this, randomAudioResId);

        // Start playing the music
        mediaPlayer.start();

        // Schedule the music to stop after the random delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopMusic();
            }
        }, delay * 1000);  // Delay in milliseconds

        // Start the timer
        elapsedTime = 0;
        handler.postDelayed(timerRunnable, 1000);  // Update every second
    }

    private void stopMusic() {
        if (isMusicPlaying) {
            isMusicPlaying = false;
            startStopButton.setText("START");

            // Stop and reset the music
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0); // Rewind to the start for the next play
            }
            handler.removeCallbacks(timerRunnable);  // Stop the timer updates
        }
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMusicPlaying) {
                elapsedTime++;
                updateCounterTextView();
                if (elapsedTime >= delay) {
                    stopMusic();  // Ensure it stops at the exact delay
                } else {
                    handler.postDelayed(this, 1000);  // Update every second
                }
            }
        }
    };

    private void updateCounterTextView() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        counterTextView.setText(timeString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();  // Release the media player resources
            mediaPlayer = null;
        }
        handler.removeCallbacks(timerRunnable);  // Remove any pending timer updates
    }
}
