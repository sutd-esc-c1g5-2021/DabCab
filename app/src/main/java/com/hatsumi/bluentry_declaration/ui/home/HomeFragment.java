package com.hatsumi.bluentry_declaration.ui.home;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.firebase.EntryPlace;
import com.hatsumi.bluentry_declaration.firebase.PlaceViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.toString();

    static boolean active = false;

    public final static String LOC_KEY = "LOC_KEY";
    private final static int BLUETOOTH_PERMISSION_CODE = 100;

    TextView userName;
    TextView location_1_count;
    TextView location_2_count;
    TextView location_3_count;
    TextView location_4_count;
    TextView location_5_count;


    TextView location_1_text;
    TextView location_2_text;
    TextView location_3_text;
    TextView location_4_text;
    TextView location_5_text;

    Button bluetoothStatus;

    ImageButton location_1_button;
    ImageButton location_2_button;
    ImageButton location_3_button;
    ImageButton location_4_button;
    ImageButton location_5_button;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bluetoothStatus = getView().findViewById(R.id.bluetoothStatus);

        ArrayList<TextView> locationFreqList = new ArrayList<>();
        locationFreqList.add(location_1_count =(getView()).findViewById(R.id.location_1_count));
        locationFreqList.add(location_2_count =(getView()).findViewById(R.id.location_2_count));
        locationFreqList.add(location_3_count =(getView()).findViewById(R.id.location_3_count));
        locationFreqList.add(location_4_count =(getView()).findViewById(R.id.location_4_count));
        locationFreqList.add(location_5_count =(getView()).findViewById(R.id.location_5_count));

        ArrayList<TextView> locationText = new ArrayList<>();
        locationText.add(location_1_text = getView().findViewById(R.id.location_1_text));
        locationText.add(location_2_text = getView().findViewById(R.id.location_2_text));
        locationText.add(location_3_text = getView().findViewById(R.id.location_3_text));
        locationText.add(location_4_text = getView().findViewById(R.id.location_4_text));
        locationText.add(location_5_text = getView().findViewById(R.id.location_5_text));

        ArrayList<ImageButton> locationButtons = new ArrayList<>();
        locationButtons.add(location_1_button = getView().findViewById(R.id.location_1_button));
        locationButtons.add(location_2_button = getView().findViewById(R.id.location_2_button));
        locationButtons.add(location_3_button = getView().findViewById(R.id.location_3_button));
        locationButtons.add(location_4_button = getView().findViewById(R.id.location_4_button));
        locationButtons.add(location_5_button = getView().findViewById(R.id.location_5_button));

        ArrayList<String> visitedLocation = new ArrayList<>();              // store the top 5 visited locations
        ArrayList<Integer> locationFreq = new ArrayList<>();
        ArrayList<View> freqLocationFrame = new ArrayList<>();
        freqLocationFrame.add(getView().findViewById(R.id.frame_1));
        freqLocationFrame.add(getView().findViewById(R.id.frame_2));
        freqLocationFrame.add(getView().findViewById(R.id.frame_3));
        freqLocationFrame.add(getView().findViewById(R.id.frame_4));
        freqLocationFrame.add(getView().findViewById(R.id.frame_5));

        HashMap<String, Integer> hm = new HashMap<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("1001234"+"Place");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int times =0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    times = (int) dataSnapshot.getChildrenCount();
                    hm.put(dataSnapshot.getKey(), times);
                }
                for (String location: hm.keySet()){
                    if (!visitedLocation.contains(location)) {
                        visitedLocation.add(location);
                        locationFreq.add(hm.get(location));
                    }
                    if (visitedLocation.size() == 0) {                                     // if no visited locations yet
                        location_1_text.setText("No visited locations");
                        location_1_count.setText(" ");
                        freqLocationFrame.get(0).setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i < 5; i++) {
                        if (visitedLocation.size() > i) {                                // set frame to visible if there is valid frequently visited location
                            locationText.get(i).setText(visitedLocation.get(i));
                            String freq = locationFreq.get(i).toString();
                            locationFreqList.get(i).setText(freq);
                            freqLocationFrame.get(i).setVisibility(View.VISIBLE);
                        }
                    }

                }
//                location_1_count.setText(Integer.toString(times));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        //OPTIONS page
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ImageButton profileButton = getView().findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open options page
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_popup, null, false);
                PopupWindow popupWindow = new PopupWindow(popupView, (int) (width*0.48), WindowManager.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.showAtLocation(popupView, Gravity.LEFT, 0, 0);

                // Open help page
                Button helpButton = popupView.findViewById(R.id.help_button);
                helpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View helpView = LayoutInflater.from(getActivity()).inflate(R.layout.help_page, null, false);
                        PopupWindow helpPopup = new PopupWindow(helpView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                        helpPopup.showAtLocation(helpView, Gravity.CENTER, 0, 0);

                        // Close help page
                        Button helpBack = helpView.findViewById(R.id.help_back);
                        helpBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                helpPopup.dismiss();
                            }
                        });
                    }
                });
            }
        });


        active = true;              //To enable updating of bluetooth status
        updateBluetoothStatus();
        bluetoothStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.BLUETOOTH, BLUETOOTH_PERMISSION_CODE);
                if (bluetoothStatus.getText().equals("Turn On Bluetooth")) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intent);
                }
            }
        });


        //Set Freq location visibility
//        ArrayList<View> freqLocationFrame = new ArrayList<>();
//        freqLocationFrame.add(getView().findViewById(R.id.frame_1));
//        freqLocationFrame.add(getView().findViewById(R.id.frame_2));
//        freqLocationFrame.add(getView().findViewById(R.id.frame_3));
//        freqLocationFrame.add(getView().findViewById(R.id.frame_4));
//        freqLocationFrame.add(getView().findViewById(R.id.frame_5));
//        visitedLocation.add("Canteen");
//        visitedLocation.add("Fablab");
//        visitedLocation.add("Class");
//        visitedLocation.add("Lt");
//        visitedLocation.add("Campus Center");
//        if (visitedLocation.size() == 0) {                                     // if no visited locations yet
//            location_1_text.setText("No visited locations");
//            location_1_count.setText(" ");
//            freqLocationFrame.get(0).setVisibility(View.VISIBLE);
//        }
//        for (int i = 0; i < 5; i++) {
//            if (visitedLocation.size() > i) {                                // set frame to visible if there is valid frequently visited location
//                locationText.get(i).setText(visitedLocation.get(i));
//                freqLocationFrame.get(i).setVisibility(View.VISIBLE);
//            }
//        }


        // Popup for frequently visited location
        for (int i = 0; i < locationButtons.size(); i++) {
            ImageButton currButton = locationButtons.get(i);
            int finalI = i;
            currButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.location_popup, null, false);
                    ((TextView)popupView.findViewById(R.id.popup_text)).setText(visitedLocation.get(finalI));
                    ((TextView)popupView.findViewById(R.id.popup_count)).setText(locationFreq.get(finalI).toString());
                    PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
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
        }
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{permission}, requestCode);
        }
    }

    public void updateBluetoothStatus(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            //Log.d(TAG, "Bluetooth not supported");
            bluetoothStatus.setText("Bluetooth not supported"); //TODO put into strings.xml
        } else if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            bluetoothStatus = Objects.requireNonNull(getView()).findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothDenied);
        } else if (!bluetoothAdapter.isEnabled()) {
            bluetoothStatus = Objects.requireNonNull(getView()).findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothOff);
        } else {
            bluetoothStatus = Objects.requireNonNull(getView()).findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothConnected);
        }

        refresh(1000);          //update bluetooth status every sec
    }

    private void refresh(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (active){
                updateBluetoothStatus();
                }
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "Home fragment onCreateView");

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        Log.i(TAG,"onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        updateBluetoothStatus();
        Log.i(TAG,"onResume");
    }
}