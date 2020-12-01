package com.hatsumi.bluentry_declaration.ui.history.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.util.ArrayList;

public class PeriodEntryAdapter extends RecyclerView.Adapter<PeriodEntryHolder> {
    private Context context;
    private ArrayList<PeriodEntry> entries;
    public PeriodEntryAdapter(Context context, ArrayList<PeriodEntry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public PeriodEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.period_row, parent, false);
        return new PeriodEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(PeriodEntryHolder holder, int position) {
        PeriodEntry entry = entries.get(position);
        holder.setDetails(entry);
    }
}
