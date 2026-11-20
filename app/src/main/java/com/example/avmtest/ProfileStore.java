package com.example.avmtest;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class ProfileStore {
    private ProfileStore() {
    }

    public static DeviceProfile load(Context context) {
        return load(context, BuildConfig.PROFILE_ASSET);
    }

    public static DeviceProfile load(Context context, String assetPath) {
        try {
            AssetManager assets = context.getAssets();
            InputStream in = assets.open(assetPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
            in.close();
            JSONObject root = new JSONObject(out.toString("UTF-8"));

            JSONArray fa = root.optJSONArray("features");
            LinkedHashSet<String> features = new LinkedHashSet<>();
            if (fa != null) for (int i = 0; i < fa.length(); i++) features.add(fa.getString(i));

            JSONArray ca = root.optJSONArray("cameras");
            List<DeviceProfile.Camera> cameras = new ArrayList<>();
            if (ca != null) {
                for (int i = 0; i < ca.length(); i++) {
                    JSONObject c = ca.getJSONObject(i);
                    cameras.add(new DeviceProfile.Camera(
                            c.optInt("index", i),
                            c.optString("name", "相机 " + i),
                            c.optString("position", "")
                    ));
                }
            }

            JSONObject vo = root.optJSONObject("viewIds");
            Map<String, Integer> viewIds = new LinkedHashMap<>();
            if (vo != null) {
                java.util.Iterator<String> keys = vo.keys();
                while (keys.hasNext()) {
                    String k = keys.next();
                    viewIds.put(k, vo.optInt(k, -1));
                }
            }

            return new DeviceProfile(
                    root.optString("model", BuildConfig.FLAVOR),
                    root.optString("title", "影像辅助"),
                    root.optString("layout", "compact"),
                    features,
                    root.optInt("gridColumns", 2),
                    root.optInt("playbackCount", 3),
                    cameras,
                    viewIds
            );
        } catch (Exception e) {
            throw new RuntimeException("load profile failed: " + assetPath, e);
        }
    }

    public static int layoutRes(DeviceProfile profile) {
        return "expanded".equals(profile.layout)
                ? R.layout.activity_main_expanded
                : R.layout.activity_main_compact;
    }
}
