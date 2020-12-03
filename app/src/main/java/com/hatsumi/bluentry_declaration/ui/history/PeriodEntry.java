package com.hatsumi.bluentry_declaration.ui.history;

public class PeriodEntry {
    private String duration;
    private String place;
    private String date;

    public PeriodEntry() {
    }

    public PeriodEntry(String duration, String place, String date) {
        this.duration = duration;
        this.place = place;
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String duration) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}