package com.example.avmtest;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class PlaybackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(R.layout.activity_record_main);

        RadioGroup group = findViewById(R.id.rg_avm_title);
        RadioButton playback = findViewById(R.id.rb_playback);
        RadioButton format = findViewById(R.id.rb_format);
        RadioButton switchSetting = findViewById(R.id.rb_switch);

        group.setOnCheckedChangeListener((g, checkedId) -> {
            if (checkedId == R.id.rb_format) {
                Toast.makeText(this, "Demo 未实现格式化", Toast.LENGTH_SHORT).show();
                playback.setChecked(true);
            } else if (checkedId == R.id.rb_switch) {
                Toast.makeText(this, "Demo 未实现开关设置", Toast.LENGTH_SHORT).show();
                playback.setChecked(true);
            }
        });

        if (savedInstanceState == null) {
            Fragment fragment = new PlaybackListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
        }
    }
}
