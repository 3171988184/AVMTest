package com.example.avmtest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackAdapter.Holder> {

    private final Context context;
    private final List<String> names;

    public PlaybackAdapter(Context context, List<String> names) {
        this.context = context;
        this.names = names;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playback, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = names.get(position);
        holder.name.setText(name);
        holder.bg.setText("▶");
        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, name, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView bg;
        TextView name;

        Holder(@NonNull View itemView) {
            super(itemView);
            bg = itemView.findViewById(R.id.item_iv_playback_bg);
            name = itemView.findViewById(R.id.item_tv_playback_name);
        }
    }
}
