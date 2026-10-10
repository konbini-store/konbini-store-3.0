package io.github.konbini.store3.api;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Paul on 9/24/2026.
 */

public class Category implements Serializable {
    private String id = "";
    private String name = "";

    public Category(JSONObject object) {
        if (object != null) {
            this.id = object.optString("id", "other");
            this.name = object.optString("name", "Other");
        }
    }

    public Category(String id, String name) {
        this.id = id != null ? id : "";
        this.name = name != null ? name : "";
    }

    public String getId() { return this.id; }
    public String getName() { return this.name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
