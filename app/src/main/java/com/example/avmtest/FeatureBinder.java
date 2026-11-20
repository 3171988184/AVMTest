package com.example.avmtest;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class FeatureBinder {
    private FeatureBinder() {
    }

    public static void bind(Activity activity, LinearLayout container, DeviceProfile profile) {
        container.removeAllViews();
        for (String f : profile.features) {
            TextView chip = new TextView(activity);
            chip.setText(label(f));
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            chip.setTextColor(Color.parseColor("#0F172A"));
            chip.setPadding(dp(activity, 12), dp(activity, 6), dp(activity, 12), dp(activity, 6));
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#EAF1F8"));
            bg.setCornerRadius(dp(activity, 14));
            bg.setStroke(dp(activity, 1), Color.parseColor("#CBD5E1"));
            chip.setBackground(bg);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(activity, 8), 0);
            container.addView(chip, lp);
        }
    }

    public static String label(String f) {
        if ("AVM".equals(f)) return "AVM 全景";
        if ("BSD".equals(f)) return "BSD 盲区";
        if ("RECORD".equals(f)) return "录像";
        if ("TF".equals(f)) return "TF 卡";
        if ("DMS".equals(f)) return "DMS";
        if ("LDW".equals(f)) return "LDW";
        return f;
    }

    static int dp(Activity a, int v) {
        return Math.round(v * a.getResources().getDisplayMetrics().density);
    }
}
