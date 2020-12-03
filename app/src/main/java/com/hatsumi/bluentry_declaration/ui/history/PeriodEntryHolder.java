package com.hatsumi.bluentry_declaration.ui.history;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PeriodEntryHolder extends RecyclerView.ViewHolder {
    private TextView periodDate,periodLocation, periodDuration;
    public PeriodEntryHolder(View itemView) {
        super(itemView);
        periodDate = itemView.findViewById(R.id.period_date);
        periodLocation = itemView.findViewById(R.id.period_location);
        periodDuration = itemView.findViewById(R.id.period_duration);
    }

    public void setDetails(PeriodEntry entry) {
        DateFormat timeFormat = new SimpleDateFormat("M-d K:ma");
        periodDate.setText(entry.getDate());


        periodLocation.setText(entry.getPlace());
        periodDuration.setText(entry.getDuration());
//        periodDate.setText(dateFormat.format(row.getDate()));
    }
}
