package com.hatsumi.bluentry_declaration.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hatsumi.bluentry_declaration.R;

import java.time.Period;
import java.util.ArrayList;

public class PeriodRowAdapter extends RecyclerView.Adapter<PeriodRowHolder> {
    private Context context;
    private ArrayList<PeriodRow> rows;
    public PeriodRowAdapter(Context context, ArrayList<PeriodRow> rows) {
        this.context = context;
        this.rows = rows;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @Override
    public PeriodRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.period_row, parent, false);
        return new PeriodRowHolder(view);
    }

    @Override
    public void onBindViewHolder(PeriodRowHolder holder, int position) {
        PeriodRow row = rows.get(position);
        holder.setDetails(row);
    }
}
