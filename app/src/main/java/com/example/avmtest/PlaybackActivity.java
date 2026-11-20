package com.example.avmtest;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
            if (checkedId == R.id.rb_playback) {
                show(new PlaybackListFragment());
            } else if (checkedId == R.id.rb_format) {
                show(DemoTextFragment.newInstance("格式化（演示）", "未接入真实存储，仅验证页面结构"));
            } else if (checkedId == R.id.rb_switch) {
                show(new SwitchDemoFragment());
            }
        });

        if (savedInstanceState == null) {
            playback.setChecked(true);
            show(new PlaybackListFragment());
        }
    }

    private void show(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_container, fragment)
                .commitAllowingStateLoss();
    }
}
