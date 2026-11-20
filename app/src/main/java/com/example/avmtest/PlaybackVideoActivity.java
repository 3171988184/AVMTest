package com.example.avmtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaybackVideoActivity extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private int progress = 0;
    private boolean playing = true;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!playing) return;
            progress = Math.min(100, progress + 1);
            SeekBar seek = findViewById(R.id.seek_video);
            seek.setProgress(progress);
            updateTime(progress);
            if (progress < 100) handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_video);

        String name = getIntent().getStringExtra("name");
        if (name == null || name.isEmpty()) name = "录像文件";
        ((TextView) findViewById(R.id.tv_video_name)).setText(name);
        ((TextView) findViewById(R.id.tv_video_placeholder)).setText("模拟视频画面\n" + name);

        SeekBar seek = findViewById(R.id.seek_video);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) progress = value;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                updateTime(progress);
            }
        });

        Button play = findViewById(R.id.btn_play_pause);
        play.setOnClickListener(v -> {
            playing = !playing;
            play.setText(playing ? "暂停" : "播放");
            if (playing) handler.post(ticker);
        });
        findViewById(R.id.btn_video_back).setOnClickListener(v -> finish());

        updateTime(0);
        handler.postDelayed(ticker, 1000);
    }

    private void updateTime(int p) {
        int total = 120;
        int sec = total * p / 100;
        TextView time = findViewById(R.id.tv_video_time);
        time.setText(String.format("%02d:%02d / %02d:%02d", sec / 60, sec % 60, total / 60, total % 60));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
