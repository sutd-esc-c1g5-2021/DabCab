package com.hatsumi.bluentry_declaration.firebase;

public class EntryPeriod {
    private String duration;
    private String place;

    public EntryPeriod() {
    }

    public EntryPeriod(String duration, String place) {
        this.duration = duration;
        this.place = place;
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
