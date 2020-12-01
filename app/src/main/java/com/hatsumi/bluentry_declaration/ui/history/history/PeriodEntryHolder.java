package com.hatsumi.bluentry_declaration.ui.history.history;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PeriodEntryHolder extends RecyclerView.ViewHolder {
    private TextView periodLocation, periodCheckIn, periodCheckOut;
    public PeriodEntryHolder(View itemView) {
        super(itemView);
        periodLocation = itemView.findViewById(R.id.periodLocation);
        periodCheckIn = itemView.findViewById(R.id.periodCheckIn);
        periodCheckOut = itemView.findViewById(R.id.periodCheckOut);
    }

    public void setDetails(PeriodEntry entry) {
        DateFormat timeFormat = new SimpleDateFormat("M-d K:ma");
        periodLocation.setText(entry.getLocation());
        periodCheckIn.setText(timeFormat.format(entry.getCheckIn()));
        periodCheckOut.setText(timeFormat.format(entry.getCheckOut()));

    }
}
