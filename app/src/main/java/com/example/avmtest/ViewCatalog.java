package com.example.avmtest;

import java.util.Map;

public final class ViewCatalog {
    private final Map<String, Integer> ids;

    public ViewCatalog(DeviceProfile profile) {
        this.ids = profile.viewIds;
    }

    public int id(String semantic) {
        Integer v = ids.get(semantic);
        return v == null ? -1 : v;
    }
}
