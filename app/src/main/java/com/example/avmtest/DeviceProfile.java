package com.example.avmtest;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DeviceProfile {
    public final String model;
    public final String title;
    public final String layout;
    public final Set<String> features;
    public final int gridColumns;
    public final int playbackCount;
    public final List<Camera> cameras;
    public final Map<String, Integer> viewIds;

    public DeviceProfile(String model, String title, String layout,
                         Set<String> features, int gridColumns, int playbackCount,
                         List<Camera> cameras, Map<String, Integer> viewIds) {
        this.model = model;
        this.title = title;
        this.layout = layout;
        this.features = Collections.unmodifiableSet(new LinkedHashSet<>(features));
        this.gridColumns = gridColumns;
        this.playbackCount = playbackCount;
        this.cameras = Collections.unmodifiableList(cameras);
        this.viewIds = Collections.unmodifiableMap(new LinkedHashMap<>(viewIds));
    }

    public boolean hasFeature(String feature) {
        return features.contains(feature);
    }

    public static final class Camera {
        public final int index;
        public final String name;
        public final String position;

        public Camera(int index, String name, String position) {
            this.index = index;
            this.name = name;
            this.position = position;
        }
    }
}
