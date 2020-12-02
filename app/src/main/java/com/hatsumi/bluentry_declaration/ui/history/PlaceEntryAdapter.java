package com.hatsumi.bluentry_declaration.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.firebase.EntryPlace;

import java.util.ArrayList;
import java.util.List;

public class PlaceEntryAdapter extends RecyclerView.Adapter<PlaceEntryHolder> {
    private Context context;
    private ArrayList<PlaceEntry> entries;
    public PlaceEntryAdapter(Context context, ArrayList<PlaceEntry> entries) {
        this.context = context;
        this.entries = entries;
    }


    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public PlaceEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.place_row, parent, false);
        return new PlaceEntryHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PlaceEntryHolder holder, int position) {
        PlaceEntry entry = entries.get(position);
        holder.setDetails(entry);

    }

}

