package com.hatsumi.bluentry_declaration.ui.history;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.ui.history.PeriodEntry;
import com.hatsumi.bluentry_declaration.firebase.EntryPeriod;
import com.hatsumi.bluentry_declaration.firebase.PeriodViewAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hatsumi.bluentry_declaration.firebase.FirebaseUserPeriod.formatTime;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeriodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeriodFragment extends Fragment {

    Button helpButton;

    private RecyclerView periodRecyclerView;
    //    private RecyclerView periodEntryRecyclerView;
    private PeriodEntryAdapter periodEntryAdapter;
    //    private PeriodEntryAdapter periodEntryAdapter;
//    private ArrayList<PeriodRow> rowArrayList;
//    private ArrayList<PeriodEntry> entryArrayList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PeriodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeriodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeriodFragment newInstance(String param1, String param2) {
        PeriodFragment fragment = new PeriodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    private ArrayList<PeriodEntry> entryPeriod = new ArrayList<>();

    private static String TAG = PeriodFragment.class.toString();
    DateFormat dateTime = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss aa");

    //TODO: Refractor all of Firebase logic into a helper class
    private void setupFirebase() {
        Log.d(TAG, "Setting up firebase datasource");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("1001234Period");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entryPeriod.clear();
                Log.d(TAG, "Firebase data change");
                for (DataSnapshot day_snpsht: snapshot.getChildren()) {
                    // Iterate through the days
                    for (


                    DataSnapshot snpsht: day_snpsht.getChildren()) {
                        PeriodEntry data = snpsht.getValue(PeriodEntry.class);
                        data.setDate(day_snpsht.getKey());
                        entryPeriod.add(0, data);
                        Log.d(TAG, "Firebase data " + snpsht.getValue().toString());
                    }
                }
                periodEntryAdapter.notifyDataSetChanged();
                Log.d(TAG, "Notified data set changed");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupFirebaseToday() {
        Log.d(TAG, "Setting up firebase datasource (Today)");

        Date date = new Date();
        String dt = dateTime.format(date);
        String entryDate = dt.substring(0, dt.indexOf(","));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("1001234Period").child(entryDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entryPeriod.clear();
                Log.d(TAG, "Firebase data change");

                for (DataSnapshot day_snpsht: snapshot.getChildren()) {
                    PeriodEntry data = day_snpsht.getValue(PeriodEntry.class);
                    data.setDate(entryDate);
                    entryPeriod.add(0, data);

                    Log.d(TAG, "Firebase data " + day_snpsht.getValue().toString());

                }
                periodEntryAdapter.notifyDataSetChanged();
                Log.d(TAG, "Notified data set changed");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_period, container, false);


        Spinner spinner = view.findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.date, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                switch (position) {
                    case 0: {
                        //Update data for today only
                        setupFirebaseToday();
                        break;
                    }
                    case 1: {
                        setupFirebase();
                        break;
                    }
                    default: {
                        Log.d(TAG, "TODO: Need to implement this button");
                        break;
                    }
                }
                Toast.makeText(parentView.getContext(), selected, Toast.LENGTH_SHORT).show();

                Log.d(TAG, "Selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        periodRecyclerView = view.findViewById(R.id.periodRecyclerView);
        periodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        periodEntryAdapter = new PeriodEntryAdapter(getActivity(), entryPeriod);
        periodRecyclerView.setAdapter(periodEntryAdapter);

        setupFirebaseToday();

        // Inflate popup layout for Period fragment

        helpButton = view.findViewById(R.id.help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_period, null, false);
                PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                ImageButton popup_close_button = (ImageButton) popupView.findViewById(R.id.popup_close);
                popup_close_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
        return view;
    }

}