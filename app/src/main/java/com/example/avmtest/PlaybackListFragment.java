package com.example.avmtest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlaybackListFragment extends Fragment {

    private DeviceProfile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profile = ProfileStore.load(requireContext());

        ImageButton back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(v -> requireActivity().finish());

        RecyclerView list = view.findViewById(R.id.recy_playback);
        list.setLayoutManager(new GridLayoutManager(requireContext(), 6));
        list.setAdapter(new PlaybackAdapter(requireContext(), mockNames()));
    }

    private List<String> mockNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < profile.playbackCount; i++) {
            names.add(String.format("录像_%02d.mp4", i + 1));
        }
        return names;
    }
}
