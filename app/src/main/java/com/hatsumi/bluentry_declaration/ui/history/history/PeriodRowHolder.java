package com.hatsumi.bluentry_declaration.ui.history.history;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.firebase.EntryPeriod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PeriodRowHolder extends RecyclerView.ViewHolder {
    private TextView periodDate;
    public PeriodRowHolder(View itemView) {
        super(itemView);
        periodDate = itemView.findViewById(R.id.periodDate);
    }

    public void setDetails(EntryPeriod period) {
        DateFormat dateFormat = new SimpleDateFormat("M-d");
        periodDate.setText("Place: " + period.getPlace() + ", duration: " + period.getDuration());
    }
}
