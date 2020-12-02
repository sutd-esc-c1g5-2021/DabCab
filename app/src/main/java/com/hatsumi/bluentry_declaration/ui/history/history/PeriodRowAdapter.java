package com.hatsumi.bluentry_declaration.ui.history.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.firebase.EntryPeriod;

import java.util.ArrayList;
import java.util.List;

public class PeriodRowAdapter extends RecyclerView.Adapter<PeriodRowHolder> {
    private Context context;
    private List<EntryPeriod> entryPeriods;
    public PeriodRowAdapter(Context context, List<EntryPeriod> entryPeriods) {
        this.context = context;
        this.entryPeriods = entryPeriods;
    }

    @Override
    public int getItemCount() {
        return entryPeriods.size();
    }

    @Override
    public PeriodRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.period_row, parent, false);
        return new PeriodRowHolder(view);
    }

    @Override
    public void onBindViewHolder(PeriodRowHolder holder, int position) {
        EntryPeriod entryPeriod = entryPeriods.get(position);
        holder.setDetails(entryPeriod);
    }
}
