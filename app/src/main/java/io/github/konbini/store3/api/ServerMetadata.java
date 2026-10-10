package io.github.konbini.store3.api;

import org.json.JSONObject;

/**
 * Created by Paul on 9/25/2026.
 */

public class ServerMetadata {
    private final long lastUpdated;

    public ServerMetadata(JSONObject response) {
        this.lastUpdated = response.optLong("lastUpdated", 0L);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }
}
