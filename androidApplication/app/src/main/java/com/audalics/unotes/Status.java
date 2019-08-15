package com.audalics.unotes;

import android.graphics.Color;

/**
 * Created by alexb on 5/14/2017.
 */

public class Status {
    private long id;
    private String name;
    private String description;
    private int color;
    private int progress;

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getColor() { return color; }
    public int getProgress() { return progress; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String desc) { this.description = desc; }
    public void setColor(int color) { this.color = color; }
    public void setProgress(int progress) { this.progress = progress; }

    @Override
    public String toString() {
        return "Status " + String.valueOf(id) + ": " + name + " (" + description + ")";
    }
}
