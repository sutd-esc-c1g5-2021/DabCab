package com.cabdab.wifi.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cabdab.wifi.R;
import com.cabdab.wifi.ui.declaration.DeclarationViewModel;
import com.cabdab.wifi.ui.mapview.PinView;
import com.davemorrissey.labs.subscaleview.ImageSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class WifiFragment extends Fragment {

    private DeclarationViewModel declarationViewModel;

    private WifiManager wifiManager;
    private ListView listView;
    //private TextView textView;
    private Button btnScan;
    private ArrayList<String> screenList = new ArrayList<>();
    private ArrayAdapter adapter;
    private boolean success = false;
    private IntentFilter intentFilter = new IntentFilter();


    private static String TAG = WifiFragment.class.toString();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        declarationViewModel =
                ViewModelProviders.of(this).get(DeclarationViewModel.class);
        View root = inflater.inflate(R.layout.activity_wifiscan, container, false);

        declarationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        return root;
    }


    private void scanWifi(){
        screenList.clear();
        Log.d("scanWifi", "scanWifi started");
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(getActivity(), "Scanning started",Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            // TODO: add handling for scanFailure();
            getActivity().unregisterReceiver(this);
            Log.d("wifiReceiver", "onReceive");

            for (ScanResult scanResult: results){
                Log.d("wifiReceiver", "results get");
                String ssid = scanResult.SSID;
                String bssid = scanResult.BSSID;
                int rssi = scanResult.level;
                String rssiVal  = String.valueOf(WifiManager.calculateSignalLevel(rssi, 101));
                /* THIS USES A DEPRECATED VERSION OF CALCULATE SIGNAL LEVEL BUT NOT USING IT CAUSES CRASHES*/

                screenList.add("SSID: " + ssid + ", BSSID: " + bssid + ", RSSI: " + rssiVal);
                adapter.notifyDataSetChanged();
            }

            Toast.makeText(context, "Scanning complete", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(wifiReceiver);
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to unregister");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            getActivity().registerReceiver(
                    wifiReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            );
        }
        catch (Exception e) {
            Log.d(TAG, "Failed to register");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(getActivity(), PERMS_INITIAL, 127);

        btnScan = getActivity().findViewById(R.id.scanButton);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onCreate", "Start Scan");
                scanWifi();
            }
        });

        listView = getActivity().findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //warn user if Wifi is not on - other option: turn it on in the app
        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(getActivity(), "Wifi currently disabled, please turn on Wifi.",Toast.LENGTH_LONG).show();
            // apps cannot turn on/off wifi after certain API versions
            // wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, screenList);
        listView.setAdapter(adapter);
        scanWifi(); // TODO: turn this off if you don't want the app to scan on startup



    }

}