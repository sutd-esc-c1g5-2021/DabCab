package com.hatsumi.bluentry_declaration.firebase;

public class EntryPlace {
    private String duration;
    private String date;

    public EntryPlace() {
    }

    public EntryPlace(String duration, String date) {
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
