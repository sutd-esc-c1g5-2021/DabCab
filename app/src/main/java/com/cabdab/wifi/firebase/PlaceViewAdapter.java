package com.cabdab.wifi.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cabdab.wifi.R;
import java.util.List;

public class PlaceViewAdapter extends RecyclerView.Adapter{

    List<EntryPlace> entryPlaceList;

    public PlaceViewAdapter(List<EntryPlace> entryPlaceList) {
        this.entryPlaceList = entryPlaceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_place_layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        EntryPlace entryPlace = entryPlaceList.get(position);
        myViewHolder.date.setText(entryPlace.getDate());
        myViewHolder.duration.setText(entryPlace.getDuration());

    }

    @Override
    public int getItemCount() {
        return entryPlaceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView duration, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}



