package com.cabdab.wifi.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.cabdab.wifi.R;
import com.cabdab.wifi.ui.declaration.DeclarationViewModel;
import com.cabdab.wifi.ui.mapview.PinView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ScanningModeFragment extends Fragment {

    private DeclarationViewModel declarationViewModel;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_PICK_MAP = 1;

    public PinView mapView;
    private EditText strideEditText;

    private Button setEndPosButton;
    private Button startWifiScanButton;

    private static String TAG = ScanningModeFragment.class.toString();


    public final static String LOGINSTATUS = "LOGINSTATUS";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        declarationViewModel =
                ViewModelProviders.of(this).get(DeclarationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_scanningmode, container, false);

        declarationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        strideEditText = root.findViewById(R.id.stride_length);
        this.setEndPosButton = root.findViewById(R.id.setEndPos);

        Button pickMapButton = root.findViewById(R.id.pick_map_button);

        this.startWifiScanButton = root.findViewById(R.id.startWifiScanning);

        this.startWifiScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog myDialog = new ProgressDialog(getContext());
                myDialog.setMessage("WiFi Scanning in Progress.. Please walk to end destination");
                myDialog.setCancelable(false);
                myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDialog.dismiss();//dismiss dialog


                        //Wifi Scanning Complete

                        wifiKNN.endRun((int)ScanningModeFragment.this.xEnd, (int)ScanningModeFragment.this.yEnd);

                        // 6) After finishing all the runs, Normalize data before saving (Implement saving soon)
                        wifiKNN.normalizeData();

                        // Saving Instructions Here:
                        // S1) Ensure floorplan filename is set. Can be used to load floorplan file later on.
                        wifiKNN.setFloorplan("building2lv3");

                        // S2) Get CSV formatted String using .toCSV() method
                        String testedString = wifiKNN.toCSV();

                        // S3) Save to file using Static Method
                        // Note that filename does not have .csv format inside. It is provided in the method.
                        com.example.selflib.wifi_algo.SaveLoadCSV.saveCSV("test",testedString);

                        // Loading Instructions Here:
                        // L1) Call static CSV loading method before starting Testing Mode
                        String receivedString =  com.example.selflib.wifi_algo.SaveLoadCSV.loadCSV("test");

                        //Reset to go back to the set starting point
                        ScanningModeFragment.this.POINT_MODE = 0; //Set back to start
                        ScanningModeFragment.this.startWifiScanButton.setVisibility(View.INVISIBLE);

                        initStartPoint();

                    }
                });
                myDialog.show();
                scanWifi();
            }
        });

        mapView = root.findViewById(R.id.mapImageView);
        pickMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Selected Map upload");
                selectMapFromPhone();
            }
        });


        return root;
    }
    private float xStart, yStart, xEnd, yEnd;
    private int timeTaken;
    private int currentTime;

    private com.example.selflib.wifi_algo.DataSet wifiKNN = new com.example.selflib.wifi_algo.DataSet();


    private void setupWifiScanning() {
        xStart = ScanningModeFragment.this.mapView.getCurrentTCoord().x;
        yStart = ScanningModeFragment.this.mapView.getCurrentTCoord().y;
        xEnd = ScanningModeFragment.this.mapView.getCurrentTCoord_end().x;
        yEnd = ScanningModeFragment.this.mapView.getCurrentTCoord_end().y;
        timeTaken = 5;
        currentTime = 0;
        wifiKNN.startRun((int)ScanningModeFragment.this.mapView.getCurrentTCoord_end().x , (int)ScanningModeFragment.this.mapView.getCurrentTCoord_end().y);


    }

    private WifiManager wifiManager;
    private void scanWifi(){
        //textView.setText(R.string.eeeee);
        setupWifiScanning();
        Log.i("scanWifi", "Hello");
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(getActivity(), "Scanning started",Toast.LENGTH_SHORT).show();
    }

    private void initStartPoint() {
        new AlertDialog.Builder(getContext())
                .setTitle("Please select start point")
                .setMessage("Please select the start point on the map (indicated in Blue)")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(R.drawable.location_marker)
                .show();

        this.setEndPosButton.setVisibility(View.VISIBLE);
        this.setEndPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEndPosButton.setVisibility(View.INVISIBLE);
                startWifiScanButton.setVisibility(View.VISIBLE);

                ScanningModeFragment.this.POINT_MODE = 1;

                new AlertDialog.Builder(getContext())
                        .setTitle("Please select end point")
                        .setMessage("Please select the end point on the map (indicated in Red)")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, null)
                        .setIcon(R.drawable.location_marker)
                        .show();

            }
        });
    }


    public void selectMapFromPhone() {
        Toast.makeText(getContext(), "Please select image", Toast.LENGTH_SHORT).show();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_PICK_MAP);  //one can be replaced with any action code

    }
    private static final int TTSWebActivityValue = 1;



    private void setMapWidthHeight(final Uri selectedImage) {
        loadMapImage(selectedImage, 100, 100);
    }


    private void loadMapImage(final Uri selectedImage, float width, float height) {
        Bitmap bitmap = null;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            mapView.setImage(ImageSource.bitmap(bitmap));
            mapView.initialCoordManager(width, height);
            mapView.setCurrentTPosition(new PointF(1.0f, 1.0f)); //initial current position
            mapView.setCurrentTPosition_end(new PointF(1.0f, 1.0f)); //initial current position
            setGestureDetectorListener(true);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_MAP:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    setMapWidthHeight(selectedImage);

                    //disable the stride length field
                    this.strideEditText.setFocusable(false);
                    this.mapView.setStride(Float.valueOf(this.strideEditText.getText().toString()));
                    this.strideEditText.setAlpha((float)0.5);

                    initStartPoint();

                } else {
                    Log.d(TAG, "No map found");
                }
                break;

            default:
                break;
        }

    }
    private GestureDetector gestureDetector = null;

    private int POINT_MODE = 0; //by default we set the start position first

    private void setGestureDetectorListener(boolean enable) {
        if (!enable)
            mapView.setOnTouchListener(null);
        else {
            if (gestureDetector == null) {
                gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        if (mapView.isReady()) {
                            mapView.moveBySingleTap(e, POINT_MODE);
                            Log.d(TAG, "Moving map view by single tap");

                        } else {
                            Log.d(TAG, "MapView is not ready");
                        }
                        return true;
                    }
                });
            }

            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
    }


    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            // TODO: add handling for scanFailure();
            getActivity().unregisterReceiver(this);
            Log.i("wifiReceiver", "onReceive");

            HashMap<String, Double> apData = new HashMap<>();

            for (ScanResult scanResult: results) {
                Log.i("wifiReceiver", "results get");
                //Map<String, String> datum = new HashMap<String, String>(2);
                String ssid = scanResult.SSID;
                String bssid = scanResult.BSSID;

               try {
                   int rssi = scanResult.level;
                   String rssiVal = String.valueOf(WifiManager.calculateSignalLevel(rssi, 101));
                   Log.d(TAG, "SSID: " + ssid + ", BSSID: " + bssid + ", RSSI: " + rssiVal);

                   apData.put(bssid, (double)rssi);

                   /* THIS USES A DEPRECATED VERSION OF CALCULATE SIGNAL LEVEL BUT NOT USING IT CAUSES CRASHES*/


               }
               catch (Exception e) {
                   Log.d(TAG, "Error parsing the scan results " + e.toString());
               }

            }

            // Sequence for Getting Current Position:
            // 1) Create new dataset holder in the activity
            wifiKNN.insert(apData);
            Log.d(TAG, "Wifi scanning complete, going to scan again");
            wifiManager.startScan();


        }

    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ActivityCompat.requestPermissions(getActivity(), PERMS_INITIAL, 127);


    }

}