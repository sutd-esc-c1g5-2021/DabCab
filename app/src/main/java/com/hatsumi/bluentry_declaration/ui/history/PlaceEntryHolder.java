package com.hatsumi.bluentry_declaration.ui.history;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PlaceEntryHolder extends RecyclerView.ViewHolder {
    private TextView placeDate, placeDuration, placeLocation;
    public PlaceEntryHolder(View itemView) {
        super(itemView);
        placeDate = itemView.findViewById(R.id.place_date);
        placeDuration = itemView.findViewById(R.id.place_duration);
        placeLocation = itemView.findViewById(R.id.place_location);
    }

    public void setDetails(PlaceEntry entry) {
        DateFormat timeFormat = new SimpleDateFormat("M-d K:ma");
        placeDate.setText(entry.getDate());
        placeDuration.setText(entry.getDuration());
        placeLocation.setText(entry.getPlace());
    }
}
