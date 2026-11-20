package com.example.avmtest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DeviceProfile profile;
    private TextView tabAssist;
    private TextView tabPlayback;
    private View contentAssist;
    private View contentPlayback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profile = ProfileStore.load(this);
        setContentView(ProfileStore.layoutRes(profile));

        ((TextView) findViewById(R.id.tv_title)).setText(profile.title);
        ((TextView) findViewById(R.id.tv_profile)).setText(
                "渠道包 " + BuildConfig.FLAVOR + "  ·  profile " + profile.model
                        + "  ·  " + ("expanded".equals(profile.layout) ? "大屏左右布局" : "紧凑上下布局"));

        AssistPreviewView preview = findViewById(R.id.assist_view);
        ViewCatalog catalog = new ViewCatalog(profile);
        int frontLeft = catalog.id("front_left");
        String viewText = frontLeft < 0 ? "ViewId: 未配置" : "左前 ViewId = " + frontLeft;
        preview.setProfile(profile, viewText);

        FeatureBinder.bind(this, findViewById(R.id.feature_container), profile);

        GridLayout cameras = findViewById(R.id.camera_grid);
        cameras.setColumnCount(profile.gridColumns);
        for (DeviceProfile.Camera c : profile.cameras) {
            cameras.addView(makeCard((c.index + 1) + "\n" + c.name, 64));
        }
        cameras.post(() -> resizeCameraCards(cameras));

        LinearLayout playback = findViewById(R.id.playback_list);
        for (int i = 0; i < profile.playbackCount; i++) playback.addView(makePlaybackRow(i));

        ((TextView) findViewById(R.id.tv_view_id)).setText(
                viewText + "  ·  " + profile.cameras.size() + " 路相机  ·  "
                        + profile.gridColumns + " 列");

        tabAssist = findViewById(R.id.tab_assist);
        tabPlayback = findViewById(R.id.tab_playback);
        contentAssist = findViewById(R.id.content_assist);
        contentPlayback = findViewById(R.id.content_playback);
        tabAssist.setOnClickListener(v -> showTab(true));
        tabPlayback.setOnClickListener(v -> showTab(false));
        showTab(true);
    }

    private void showTab(boolean assist) {
        contentAssist.setVisibility(assist ? View.VISIBLE : View.GONE);
        contentPlayback.setVisibility(assist ? View.GONE : View.VISIBLE);
        tabAssist.setTextColor(assist ? Color.parseColor("#0F766E") : Color.parseColor("#667085"));
        tabPlayback.setTextColor(assist ? Color.parseColor("#667085") : Color.parseColor("#0F766E"));
        tabAssist.setTypeface(null, assist ? Typeface.BOLD : Typeface.NORMAL);
        tabPlayback.setTypeface(null, assist ? Typeface.NORMAL : Typeface.BOLD);
    }

    private void resizeCameraCards(GridLayout grid) {
        if (grid.getWidth() <= 0 || profile.gridColumns <= 0) return;
        int cell = (grid.getWidth() - (profile.gridColumns + 1) * dp(4)) / profile.gridColumns;
        if (cell <= 0) return;
        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            GridLayout.LayoutParams lp = (GridLayout.LayoutParams) child.getLayoutParams();
            lp.width = cell;
            child.setLayoutParams(lp);
        }
    }

    private TextView makeCard(String text, int heightDp) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(13);
        v.setTextColor(Color.parseColor("#0F172A"));
        v.setGravity(Gravity.CENTER);
        v.setPadding(dp(6), dp(6), dp(6), dp(6));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(8));
        bg.setStroke(dp(1), Color.parseColor("#D5DCE4"));
        v.setBackground(bg);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = dp(heightDp);
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        return v;
    }

    private TextView makePlaybackRow(int index) {
        TextView v = new TextView(this);
        String file = String.format("2026-11-20_%02d-%02d-00_%s.mp4",
                9 + index, 15 + index * 2,
                profile.cameras.isEmpty() ? "cam" : profile.cameras.get(index % profile.cameras.size()).position);
        v.setText((index + 1) + "   " + file + "\n      02:" + String.format("%02d", 10 + index * 3)
                + "  ·  " + (12 + index * 7) + " MB  ·  " + profile.model);
        v.setTextSize(13);
        v.setTextColor(Color.parseColor("#101828"));
        v.setPadding(dp(12), dp(10), dp(12), dp(10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(8));
        bg.setStroke(dp(1), Color.parseColor("#D5DCE4"));
        v.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(8));
        v.setLayoutParams(lp);
        return v;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
