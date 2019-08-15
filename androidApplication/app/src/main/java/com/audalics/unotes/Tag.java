package com.audalics.unotes;

import android.graphics.Color;

/**
 * Created by alexb on 5/14/2017.
 */

public class Tag {
    private long id;
    private String name;
    private String description;
    private Color color;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String desc) { this.description = desc; }

    @Override
    public String toString() {
        return "Tag " + id + ": " + name + " (" + description + ")";
    }
}
