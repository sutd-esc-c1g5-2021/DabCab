package com.hatsumi.bluentry_declaration.ui.history;

public class PlaceEntry  {
    private String duration;
    private String date;

    public PlaceEntry() {
    }

    public PlaceEntry(String duration, String date) {
        this.duration = duration;
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
