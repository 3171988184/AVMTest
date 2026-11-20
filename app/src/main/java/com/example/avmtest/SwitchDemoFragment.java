package com.example.avmtest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SwitchDemoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        DeviceProfile profile = ProfileStore.load(requireContext());
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.TRANSPARENT);
        root.setPadding(80, 180, 80, 40);

        TextView title = new TextView(requireContext());
        title.setText("开关设置（演示）");
        title.setTextColor(Color.WHITE);
        title.setTextSize(40);
        root.addView(title);

        for (String feature : profile.features) {
            Switch sw = new Switch(requireContext());
            sw.setText(FeatureBinder.label(feature));
            sw.setTextColor(Color.WHITE);
            sw.setTextSize(28);
            sw.setChecked(true);
            sw.setOnCheckedChangeListener((buttonView, isChecked) ->
                    Toast.makeText(requireContext(),
                            FeatureBinder.label(feature) + (isChecked ? " 开" : " 关"),
                            Toast.LENGTH_SHORT).show());
            root.addView(sw);
        }
        return root;
    }
}
