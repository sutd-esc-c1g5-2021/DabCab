package com.hatsumi.bluentry_declaration.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hatsumi.bluentry_declaration.R;
import java.util.List;

public class PeriodViewAdapter extends RecyclerView.Adapter{

    List<EntryPeriod> entryPeriodList;

    public PeriodViewAdapter(List<EntryPeriod> entryPeriodList) {
        this.entryPeriodList = entryPeriodList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_period_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        EntryPeriod entryPeriod = entryPeriodList.get(position);
        myViewHolder.place.setText(entryPeriod.getPlace());
        myViewHolder.duration.setText(entryPeriod.getDuration());

    }

    @Override
    public int getItemCount() {
        return entryPeriodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView duration, place;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.place);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}



