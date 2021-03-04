package com.cabdab.wifi.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.cabdab.wifi.R;

import java.util.ArrayList;
import java.util.List;

public class GetFromFirebaseActivity extends AppCompatActivity {

    // TextView and buttons can be replaced
    TextView user, Mac_1, Mac_2, temp ;
    Button Mac_1_enter, Mac_1_leave, Mac_2_enter, Mac_2_leave;
    // For registering
    Button SetMacAddress;
    RecyclerView recyclerView;

    String dateToDisplay ="01 Dec 2020";
    String venueToDisplay ="TT24";
    String studentID;
    String enter1 = "1A-2B-3C-4D-5E-6F";
    String enter3 = "7G-8H-9I-10J-11K-12L";
    final String TAG = "firebase";
    FirebaseUserPeriod fbh;

    List<EntryPeriod> entryPeriod;
    List<EntryPlace> entryPlace;
    PeriodViewAdapter periodViewAdapter;
    PlaceViewAdapter placeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_user_period);

        initialization();
//        setPeriodView(dateToDisplay);
        setPlaceView(venueToDisplay);
        timesVisited(venueToDisplay);

    }

    public void initialization(){
        // To be removed
        user = findViewById(R.id.user1);
        Mac_1 = findViewById(R.id.Mac_1);
        Mac_1_enter = findViewById(R.id.Mac_1_enter);
        Mac_1_leave = findViewById(R.id.Mac_1_leave);
        Mac_2 = findViewById(R.id.Mac_2);
        Mac_2_enter = findViewById(R.id.Mac_2_enter);
        Mac_2_leave = findViewById(R.id.Mac_2_leave);
        // This is for registering MacAddress
        SetMacAddress = findViewById(R.id.SetMacAddress);

        temp = findViewById(R.id.temp);

        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fbh = new FirebaseUserPeriod(studentID);

        // All these buttons are for mockup and should be replaced with original way of entering/exiting
        Mac_1_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbh.inRange(enter1);
            }
        });

        Mac_1_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbh.outOfRange(enter1);
            }
        });

        Mac_2_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbh.inRange(enter3);
            }
        });

        Mac_2_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbh.outOfRange(enter3);
            }
        });

        SetMacAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GetFromFirebaseActivity.this, FirebaseLocationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setPeriodView(String displayDate){
        entryPeriod = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(studentID+"Period").child(displayDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entryPeriod.clear();
                for (DataSnapshot snpsht: snapshot.getChildren()) {
                    EntryPeriod data = snpsht.getValue(EntryPeriod.class);
                    entryPeriod.add(0,data);
                    Log.i(TAG, snpsht.getValue().toString());
                }
                periodViewAdapter = new PeriodViewAdapter(entryPeriod);
                recyclerView.setAdapter(periodViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setPlaceView(String displayVenue){
        entryPlace = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(studentID+"Place").child(displayVenue);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entryPlace.clear();
                for (DataSnapshot snpsht: snapshot.getChildren()) {
                    EntryPlace data = snpsht.getValue(EntryPlace.class);
                    entryPlace.add(0,data);
                    Log.i(TAG, snpsht.getValue().toString());
                }
                placeViewAdapter = new PlaceViewAdapter(entryPlace);
                recyclerView.setAdapter(placeViewAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void timesVisited(String displayVenue){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(studentID+"Place").child(displayVenue);
        databaseReference.addValueEventListener(new ValueEventListener() {
            long times = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                times = snapshot.getChildrenCount();
                Log.i(TAG, "Number of times visited is: " + times);
                temp.setText(times + "");

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
