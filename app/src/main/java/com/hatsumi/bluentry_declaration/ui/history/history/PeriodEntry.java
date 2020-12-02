package com.hatsumi.bluentry_declaration.ui.history.history;

import java.util.Date;

public class PeriodEntry {
    private String location;
    private Date checkIn;
    private Date checkOut;

    public PeriodEntry(String location, Date checkIn, Date checkOut) {
        this.location = location;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }
}
