package com.hatsumi.bluentry_declaration.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;

public class FirebaseUserPeriod {

    private String usern;
    final String TAG = "firebase";
    final String heading1 = "duration";
    final String heading2 = "place";
    final String heading3 = "date";
    DateFormat dateTime = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss aa");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference locationRef = userRef.child("Location");
    DatabaseReference checkedInRef = userRef.child("CheckedIn");


    public FirebaseUserPeriod(String usern){
        this.usern = usern;
    }

    public void inRange(String macAddress){
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                ArrayList<String> whiteList = new ArrayList<>();
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (!whiteList.contains(snpsht.getKey())) {whiteList.add(snpsht.getKey());}
                }
//                Log.i(TAG, "white list is: " + whiteList);

                if (whiteList.contains(macAddress)){
                    checkIfExists(macAddress);

                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });
    }

    public void checkIfExists(String macaddr){
        checkedInRef.child(usern).child(macaddr).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    EnterDateTime(macaddr);
                }

                else {
                    Log.i(TAG, "Already Checked In");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

    }

    public void EnterDateTime(String MacAddress){

        Log.i(TAG, "Entered " + MacAddress);
        Date date = new Date();
        String dt = dateTime.format(date);
        String entryDate = dt.substring(0, dt.indexOf(","));
        String entryTime = formatTime(dt.substring(dt.indexOf(",")+2));
//        Log.i(TAG,entryTime);

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (snpsht.getKey().contains(MacAddress)) {
                        checkedInRef.child(usern).child(MacAddress).setValue(dt);
                        DatabaseReference userRefwithKeyPeriod = userRef.child(usern+"Period").child(entryDate).push();
                        userRefwithKeyPeriod.child(heading1).setValue(entryTime);
                        userRefwithKeyPeriod.child(heading2).setValue(snpsht.getValue().toString());
                        Enterplace(MacAddress, entryDate, entryTime);

                    }
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });

    }

    public void Enterplace(String MacAddress, String entryDate, String entryTime){
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                String checkInVenue = null;
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (snpsht.toString().contains(MacAddress)) {checkInVenue = snpsht.getValue().toString();}
                }
                DatabaseReference userRefwithKeyPlace = userRef.child(usern+"Place").child(checkInVenue).push();
                userRefwithKeyPlace.child(heading1).setValue(entryTime);
                userRefwithKeyPlace.child(heading3).setValue(entryDate.substring(0,6));
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });

    }


    public String formatTime(String dt) {
        int hour = Integer.valueOf(dt.substring(0,2));
        String ending = dt.substring(2,5) + dt.substring(9);;
        if (hour >= 22 || (hour > 12 && hour < 22)) {
            dt = (hour - 12) + ending;
        }
        else if (hour >=10 && hour <= 12 || hour >0 && hour < 10) {
            dt = hour + ending;
        }
        else {dt = "12" + ending;}
        return dt;
    }


    public void outOfRange(String macAddress){
        checkedInRef.child(usern).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (snpsht.getKey().contains(macAddress)) {
                        ExitDateTime(macAddress);
                        checkedInRef.child(usern).child(macAddress).removeValue();
                    }
                    else{
                        Log.i(TAG, "Nope, not this address");
                    }
                }

            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });

    }

    public void ExitDateTime(String MacAddress){
        Log.i(TAG, "Exit " + MacAddress);
        Date date = new Date();
        String dt = dateTime.format(date);
        String exitDate = dt.substring(0, dt.indexOf(","));
        String yesterday = getYesterday();
        String exitTime = formatTime(dt.substring(dt.indexOf(",")+2));
        ExitPeriod(exitDate, yesterday, exitTime, MacAddress);
        ExitPlace(exitDate, yesterday, exitTime, MacAddress);

    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getYesterday() {
        DateFormat yesterdayFormat = new SimpleDateFormat("dd MMM yyyy");
        return yesterdayFormat.format(yesterday());
    }

    public void ExitPeriod(String exitDate, String yesterday, String exitTime, String MacAddress){

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                String venue = null;
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (snpsht.toString().contains(MacAddress)) {venue = snpsht.getValue().toString();}
//                    Log.i(TAG, ">>>" + venue);
                }

                String finalVenue = venue;
                userRef.child(usern+"Period").child(exitDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot snapshot){
                        if (snapshot.exists()) {
                            for (DataSnapshot uniqueKey : snapshot.getChildren()) {
                                String key = uniqueKey.getKey();
//                        Log.i(TAG, key);
                                Boolean dur = false;
                                Boolean place = false;
                                String recordedTime = null;

                                for (DataSnapshot data: uniqueKey.getChildren()){
//                            Log.i(TAG,data.getKey() );
//                            Log.i(TAG,data.getValue().toString());
                                    if (data.getKey().contains(heading1) && data.getValue().toString().length() <= 7) {
                                        recordedTime = data.getValue().toString();
                                        dur = true;
//                                Log.i(TAG,"Time to remember: " + recordedTime);
                                    }
                                    if (data.getKey().contains(heading2) && data.getValue().toString().contains(finalVenue)) {
                                        place = true;
//                                Log.i(TAG, "place is: " + place);
                                    }

                                }
//                        Log.i(TAG, dur + ", " + place);
                                // Find the place to checkout from and check if the place has already been checked out of through its length
                                if (place && dur) {
                                    userRef.child(usern+"Period").child(exitDate).child(key).child(heading1).setValue(recordedTime + " - " + exitTime);
//                            Log.i(TAG, "got here");
                                } else {
                                    Log.i(TAG, "What is this");
                                }

                            }
                        }
                        else {
                            Log.i(TAG, "OVERNIGHT");

                            userRef.child(usern+"Period").child(yesterday).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot st) {
                                    for (DataSnapshot uniqKey : st.getChildren()) {
                                        String key = uniqKey.getKey();
//                                Log.i(TAG, key);
                                        Boolean dur = false;
                                        Boolean place = false;
                                        String recordedTime = null;
                                        for (DataSnapshot yesterdaySnapshot : uniqKey.getChildren()) {

                                            if (yesterdaySnapshot.getKey().contains(heading1) && yesterdaySnapshot.getValue().toString().length() <= 7) {
                                                recordedTime = yesterdaySnapshot.getValue().toString();
                                                dur = true;
//                                        Log.i(TAG,"Time to remember: " + recordedTime);
                                            }
                                            if (yesterdaySnapshot.getKey().contains(heading2) && yesterdaySnapshot.getValue().toString().contains(finalVenue)) {
                                                place = true;
//                                        Log.i(TAG, "place is: " + place);
                                            }

                                        }

                                        if (dur && place) {
                                            String overnight = recordedTime + " - " + exitDate.substring(0, 6) + ", " + exitTime;
                                            userRef.child(usern+"Period").child(yesterday).child(key).child(heading1).setValue(overnight);
//                                    Log.i(TAG, "Shown ovenight duration");
                                        } else {
                                            Log.i(TAG, "Did not show overnight...");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled (@NonNull DatabaseError error){
                        Log.d(TAG, error.getMessage());
                    }
                });


            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });


    }

    public void ExitPlace(String exitDate, String yesterday, String exitTime, String MacAddress){

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot){
                String checkOutVenue = null;
                for (DataSnapshot snpsht : snapshot.getChildren()) {
                    if (snpsht.toString().contains(MacAddress)) {checkOutVenue = snpsht.getValue().toString();}
                }

                String finalCheckOutVenue = checkOutVenue;
                String finalCheckOutVenue1 = checkOutVenue;
                userRef.child(usern+"Place").child(checkOutVenue).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot snapshot){
                        if (snapshot.exists()) {
                            for (DataSnapshot uniqueKey : snapshot.getChildren()) {
                                String key = uniqueKey.getKey();
//                        Log.i(TAG, key);
                                Boolean dur = false;
                                Boolean checkdate = false;
                                String recordedTime = null;

                                for (DataSnapshot data: uniqueKey.getChildren()){
//                            Log.i(TAG,data.getKey() );
//                            Log.i(TAG,data.getValue().toString());
                                    if (data.getKey().contains(heading1) && data.getValue().toString().length() <= 7) {
                                        recordedTime = data.getValue().toString();
                                        dur = true;
//                                Log.i(TAG,"Time to remember: " + recordedTime);
                                    }
                                    if (data.getKey().contains(heading3) && data.getValue().toString().contains(exitDate.substring(0,6))) {
                                        checkdate = true;
//                                Log.i(TAG, "place is: " + place);
                                    }

                                }
//                        Log.i(TAG, dur + ", " + place);
                                // Find the place to checkout from and check if the place has already been checked out of through its length
                                if (checkdate && dur) {
                                    userRef.child(usern+"Place").child(finalCheckOutVenue).child(key).child(heading1).setValue(recordedTime + " - " + exitTime);
//                            Log.i(TAG, "got here");
                                } else {
                                    Log.i(TAG, "What is this");
                                }

                            }
                        }
                        else {
                            Log.i(TAG, "OVERNIGHT");

                            userRef.child(usern+"Place").child(finalCheckOutVenue1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot st) {
                                    for (DataSnapshot uniqKey : st.getChildren()) {
                                        String key = uniqKey.getKey();
//                                Log.i(TAG, key);
                                        Boolean dur = false;
                                        Boolean checkdate = false;
                                        String recordedTime = null;
                                        for (DataSnapshot yesterdaySnapshot : uniqKey.getChildren()) {

                                            if (yesterdaySnapshot.getKey().contains(heading1) && yesterdaySnapshot.getValue().toString().length() <= 7) {
                                                recordedTime = yesterdaySnapshot.getValue().toString();
                                                dur = true;
//                                Log.i(TAG,"Time to remember: " + recordedTime);
                                            }
                                            if (yesterdaySnapshot.getKey().contains(heading3) && yesterdaySnapshot.getValue().toString().contains(exitDate.substring(0,6))) {
                                                checkdate = true;
//                                Log.i(TAG, "place is: " + place);
                                            }

                                        }

                                        if (dur && checkdate) {
                                            String overnight = recordedTime + " - " + exitDate.substring(0, 6) + ", " + exitTime;
                                            userRef.child(usern+"Place").child(finalCheckOutVenue1).child(key).child(heading1).setValue(overnight);
//                                    Log.i(TAG, "Shown ovenight duration");
                                        } else {
                                            Log.i(TAG, "Did not show overnight...");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled (@NonNull DatabaseError error){
                        Log.d(TAG, error.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Log.d(TAG, error.getMessage());
            }
        });

    }

}
