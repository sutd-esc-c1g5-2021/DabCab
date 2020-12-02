package com.hatsumi.bluentry_declaration.ui.history;

public class PlaceEntry  {
    private String duration;
    private String date;
    private String place;

    public PlaceEntry() {
    }

    public PlaceEntry(String duration, String date, String place) {
        this.duration = duration;
        this.date = date;
        this.place = place;
    }

    public String getPlace(){ return place;}

    public void setPlace(String place) {
        this.place = place;
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
