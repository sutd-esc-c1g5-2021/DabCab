package com.hatsumi.bluentry_declaration.ui.history.history;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PeriodRowHolder extends RecyclerView.ViewHolder {
    private TextView periodDate;
    public PeriodRowHolder(View itemView) {
        super(itemView);
        periodDate = itemView.findViewById(R.id.periodDate);
    }

    public void setDetails(PeriodRow row) {
        DateFormat dateFormat = new SimpleDateFormat("M-d");
        periodDate.setText(dateFormat.format(row.getDate()));
    }
}
