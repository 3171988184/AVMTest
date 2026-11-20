package com.example.avmtest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DemoTextFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";

    public static DemoTextFragment newInstance(String title, String desc) {
        DemoTextFragment f = new DemoTextFragment();
        Bundle b = new Bundle();
        b.putString(ARG_TITLE, title);
        b.putString(ARG_DESC, desc);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.TRANSPARENT);
        root.setPadding(40, 40, 40, 40);

        TextView title = new TextView(requireContext());
        title.setText(getArguments() == null ? "" : getArguments().getString(ARG_TITLE, ""));
        title.setTextColor(Color.WHITE);
        title.setTextSize(40);
        title.setGravity(Gravity.CENTER);

        TextView desc = new TextView(requireContext());
        desc.setText(getArguments() == null ? "" : getArguments().getString(ARG_DESC, ""));
        desc.setTextColor(Color.parseColor("#94A3B8"));
        desc.setTextSize(20);
        desc.setGravity(Gravity.CENTER);
        desc.setPadding(0, 20, 0, 0);

        root.addView(title);
        root.addView(desc);
        return root;
    }
}
