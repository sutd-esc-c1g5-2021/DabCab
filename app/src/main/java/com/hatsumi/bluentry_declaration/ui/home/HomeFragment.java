package com.hatsumi.bluentry_declaration.ui.home;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hatsumi.bluentry_declaration.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.toString();

    //private HomeViewModel homeViewModel;

    public final static String LOC_KEY = "LOC_KEY";
    private final static int BLUETOOTH_PERMISSION_CODE = 100;

    TextView userName;
    TextView badgeNotification;
    TextView location_1_count;

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

        location_1_count = getView().findViewById(R.id.location_1_count);
        bluetoothStatus = getView().findViewById(R.id.bluetoothStatus);

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

        // Profile settings
        ImageButton profileButton = (ImageButton) getView().findViewById(R.id.profileButton);
//        profileButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: Go to profile_icon settings
//            }
//        });


        //Notification badge counter(invisible if no notification)
        // TODO: Assign counter
//        if (counter!=0){
//            badgeNotification = findViewById(R.id.badge_notification);
//            badgeNotification.setText(counter);
//            badgeNotification.setVisibility(View.VISIBLE);
//        }


        userName = getView().findViewById(R.id.userName);
        // TODO: Change name shown
        //userName.setText("me");


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
        ArrayList<View> freqLocationFrame = new ArrayList<>();
        freqLocationFrame.add(getView().findViewById(R.id.frame_1));
        freqLocationFrame.add(getView().findViewById(R.id.frame_2));
        freqLocationFrame.add(getView().findViewById(R.id.frame_3));
        freqLocationFrame.add(getView().findViewById(R.id.frame_4));
        freqLocationFrame.add(getView().findViewById(R.id.frame_5));
        ArrayList<String> visitedLocation = new ArrayList<>();              // store the top 5 visited locations
        visitedLocation.add("Canteen");
        visitedLocation.add("Fablab");
        visitedLocation.add("Class");
        visitedLocation.add("Lt");
        visitedLocation.add("Campus Center");
        if (visitedLocation.size() == 0) {                                     // if no visited locations yet
            location_1_text.setText("No visited locations");
            location_1_count.setText(" ");
            freqLocationFrame.get(0).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < 5; i++) {
            if (visitedLocation.size() > i) {                                // set frame to visible if there is valid frequently visited location
                locationText.get(i).setText(visitedLocation.get(i));
                freqLocationFrame.get(i).setVisibility(View.VISIBLE);
            }
        }

/*
        // Set onClick for each frequently visited location button
        for (int i = 0; i < locationButtons.size(); i++) {
            ImageButton currButton = locationButtons.get(i);
            final TextView currText = locationText.get(i);
            currButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(homeActivity.this, Popup.class);
                    intent.putExtra(LOC_KEY, currText.getText().toString());                    //location shown in popup
                    startActivity(intent);
                }
            });
        }*/
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }

    public void updateBluetoothStatus(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            //Log.d(TAG, "Bluetooth not supported");
            bluetoothStatus.setText("Bluetooth not supported"); //TODO put into strings.xml
        }

        else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            bluetoothStatus = getView().findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothDenied);
        }
        else if (!bluetoothAdapter.isEnabled()){
            bluetoothStatus = getView().findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothOff);
        }
        else {
            bluetoothStatus = getView().findViewById(R.id.bluetoothStatus);
            bluetoothStatus.setText(R.string.bluetoothConnected);
        }
        //refresh(1000);              //update bluetooth status every sec
    }

    private void refresh(int milliseconds){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
//                updateBluetoothStatus();
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "Home fragment onCreateView");

        return inflater.inflate(R.layout.fragment_home, container, false);
       /* homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/

    }
}