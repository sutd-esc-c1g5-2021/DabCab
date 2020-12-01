package com.hatsumi.bluentry_declaration.ui.history;

import java.util.Date;

public class PeriodRow {
    private Date date;

    public PeriodRow(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
