package com.cabdab.wifi.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cabdab.wifi.R;

import java.util.List;
import java.util.ArrayList;

//import java.util.HashMap; for future ue
//import android.widget.TextView;
//import java.util.Map;

public class WifiScanActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    //private TextView textView;
    private Button btnScan;
    private ArrayList<String> screenList = new ArrayList<>();
    private ArrayAdapter adapter;
    private boolean success = false;
    private IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);

        btnScan = findViewById(R.id.scanButton);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onCreate", "Start Scan");
                scanWifi();
            }
        });

        listView = findViewById(R.id.wifiList);
        //textView = findViewById(R.id.wifiText);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //warn user if Wifi is not on - other option: turn it on in the app
        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Wifi currently disabled, please turn on Wifi.",Toast.LENGTH_LONG).show();
            // apps cannot turn on/off wifi after certain api versions
            // wifiManager.setWifiEnabled(true);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, screenList);
        listView.setAdapter(adapter);
        scanWifi(); // you can turn this off if you don't want' the app to scan on startup
    }

    private void scanWifi(){
        screenList.clear();
        //textView.setText(R.string.eeeee);
        Log.i("scanWifi", "Hello");
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning started",Toast.LENGTH_SHORT).show();
    }

    public static float wifi1(int x, int y){
        double result = 10-Math.sqrt(Math.pow(x-3,2)+Math.pow(y-7,2));
        System.out.println("\nResult 1:"+result);
        if (result<1){result=0;}
        return (float)result;
    }
    public static float wifi2(int x, int y){
        double result = 10-Math.sqrt(Math.pow(x-8,2)+Math.pow(y-2,2));
        System.out.println("Result 2:"+result);
        if (result<1){result=0;}
        return (float)result;
    }
    public static float wifi3(int x, int y){
        double result = 10-Math.sqrt(Math.pow(x-0,2)+Math.pow(y-0,2));
        System.out.println("Result 3:"+result);
        if (result<1){result=0;}
        return (float)result;
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Float> rssiArrayList = new ArrayList<>();

            List<ScanResult> results = wifiManager.getScanResults();
            // TODO: add handling for scanFailure();
            unregisterReceiver(this);
            Log.i("wifiReceiver", "onReceive");

            for (ScanResult scanResult: results){
                Log.i("wifiReceiver", "results get");
                //Map<String, String> datum = new HashMap<String, String>(2);
                String ssid = scanResult.SSID;
                String bssid = scanResult.BSSID;
                int rssi = scanResult.level;

                String rssiVal  = String.valueOf(WifiManager.calculateSignalLevel(rssi, 101));
                /* THIS USES A DEPRECATED VERSION OF CALCULATE SIGNAL LEVEL BUT NOT USING IT CAUSES CRASHES*/

                screenList.add("SSID: " + ssid + ", BSSID: " + bssid + ", RSSI: " + rssiVal);
                //datum.put("RSSI", rssiVal);
                //textView.append("\nSSID: " + ssid + ", RSSI:" + rssiVal);
                adapter.notifyDataSetChanged();
            }

            Toast.makeText(context, "Scanning complete", Toast.LENGTH_SHORT).show();
        }
    };

    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(
                wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        super.onResume();
    }
}