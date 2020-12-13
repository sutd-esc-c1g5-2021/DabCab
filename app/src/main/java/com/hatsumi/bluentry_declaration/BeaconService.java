package com.hatsumi.bluentry_declaration;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hatsumi.bluentry_declaration.firebase.FirebaseUserPeriod;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**Both RX and RSSI (Received Signal Strength Indication) are indications of the power level being received
 * by an antenna
 * The difference between RX and RSSI is that RX is measured in milliWatts (mW) or decibel-milliwatts (dBm)
 * whereas RSSI is a signal strength percentage—the higher the RSSI number, the stronger the signal
 *
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconService extends Service {
    private static final String TAG = BeaconService.class.getSimpleName();

    private BluetoothGatt btGatt;
    private BluetoothAdapter mBluetoothAdapter;
    // Object of the current firebase instance
    FirebaseUserPeriod fbh = null;

    private String cachedStudentID;

    private boolean hasBluetoothError = false;


    static final String ACTION_LEFT_APP = "com.hatsumi.beaconservice.left_app";

    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        writeLine("Automate service created...");
        cachedStudentID = SUTD_TTS.getSutd_tts().user_id;
        Log.d(TAG, "onCreate cachedStudentID " + cachedStudentID);
        getBTService();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Log.d(TAG, "Running the check BLE runnable");
                    checkBleErrors();
                    for (String macAddress: discoveredMacs.keySet()) {
                        long lastTime = discoveredMacs.get(macAddress);
                        Log.d(TAG, "Current mac " + macAddress + " " + lastTime);
                        if (System.currentTimeMillis() - lastTime > 5000) {
                            Log.d(TAG, "Beacon " + macAddress + " has been out of range for > 10 seconds");
                            putNotification("BluEntry Check Out", "You have checked out");

                            fbh = new FirebaseUserPeriod(cachedStudentID);
                            Log.d(TAG, "checkOut Cached " + cachedStudentID);
                            fbh.outOfRange(macAddress);

                            //stopService(new Intent(getApplicationContext(), FloatingService.class));

                            discoveredMacs.remove(macAddress);

                        }
                    }
                }
            }, 0, 5, TimeUnit.SECONDS);
        }

    private NotificationCompat.Builder notificationBuilder;
    private Notification notification;


    private void checkBleErrors() {
        if (hasBluetoothError) {
            Log.d(TAG, "had a bluetooth error, will retry scanning");
            // Attempt to re-initialize Bluetooth
            getBTService();
            if (mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
                startBLEscan();
            }
        }
    }

    private static final String NOTIFICATION_CHANNEL_ID = "com.hatsumi.beaconservice";
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){

        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }


    private void showBluetoothError() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "com.hatsumi.bluentry.ble_errornotification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Bluetooth Error")
                        .setContentText("Please turn on your Bluetooth")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Please turn on your Bluetooth"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "com.hatsumi.channel",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            Random rand = new Random();
            notificationManager.notify(2, notificationBuilder.build()); //ID 2 is reserved for check-in, check-out
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showBubble() {

    }

    static final int MSG_LEFT_APP = 1;
    static final int MSG_ENTERED_APP = 2;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LEFT_APP:
                    //Start the FloatingService
                    Log.d(TAG, "Got the left app");
                    if (fbh != null)
                        //startService(new Intent(getApplicationContext(), FloatingService.class));
                    break;
                case MSG_ENTERED_APP:
                    Log.d(TAG, "Got the enntered app");
                        //stopService(new Intent(getApplicationContext(), FloatingService.class));
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BeaconService bind");
        return mMessenger.getBinder();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        writeLine("Automate service start...");

        if (!isBluetoothSupported()) {
            Log.d(TAG, "Bluetooth not supported");
            showBluetoothError();
            //stopSelf();
        }else{
            if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled()){
                startBLEscan();
            }else{
                showBluetoothError();
                //stopSelf();
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        writeLine("Automate service destroyed...");
        stopBLEscan();
        super.onDestroy();

        if(btGatt!=null){
            btGatt.disconnect();
            btGatt.close();
            btGatt = null;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        writeLine("Automate service stop...");
        stopSelf();
        return super.stopService(name);
    }

    // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
    // BluetoothAdapter through BluetoothManager.
    public BluetoothAdapter getBTService(){
        BluetoothManager btManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = (BluetoothAdapter) btManager.getAdapter();
        return mBluetoothAdapter;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth is off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        showBluetoothError();
                        Log.d(TAG, "Bluetooth is turning off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Bluetooth is on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Bluetooth is turning on");
                        break;
                }
            }
        }
    };


    private void handleLeScanDevice(BluetoothDevice device) {
        if (!discoveredMacs.containsKey(device.getAddress())) {
            Log.d(TAG, "New device!");
            putNotification("BluEntry Check In", "You have checked in");
            Log.d(TAG, "Cached student ID " + cachedStudentID);
            fbh = new FirebaseUserPeriod(cachedStudentID);
            fbh.inRange(device.getAddress());
        }
        discoveredMacs.put(device.getAddress(), System.currentTimeMillis());
    }
    public boolean isBluetoothSupported() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startBLEscan(){
        hasBluetoothError = false;
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setReportDelay(0L)
                .build();

        // TODO: Connect this to whitelist

        String[] peripheralName = new String[]{"BLE_NFC"};
        // Build filters list
        List<ScanFilter> filters = null;
        if (peripheralName != null) {
            filters = new ArrayList<>();
            for (String address : peripheralName) {
                ScanFilter filter = new ScanFilter.Builder()
                        .setDeviceName(address)
                        .build();
                filters.add(filter);

            }
        }
        Log.d(TAG, "Start BLE scan");
        mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, scanSettings, scanCallback);
        //mBluetoothAdapter.startLeScan(this);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "Got scan result " + result.getDevice().getAddress() + " " + result.getDevice().getName());
            handleLeScanDevice(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                Log.d(TAG, "Got scan result " + result.getDevice().getAddress() + " " + result.getDevice().getName());
                handleLeScanDevice(result.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public void stopBLEscan(){
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
    }

    /**
     *
     * @param enable
     */
    public void scanLeDevice(final boolean enable) {
        if (enable) {
            startBLEscan();
        } else {
            stopBLEscan();
        }
    }

    public static void enableDisableBluetooth(boolean enable){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if(enable) {
                bluetoothAdapter.enable();
            }else{
                bluetoothAdapter.disable();
            }
        }
    }

    private void putNotification(String title, String message) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "com.hatsumi.bluentry.blenotification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "com.hatsumi.channel",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            Random rand = new Random();
            notificationManager.notify(2, notificationBuilder.build()); //ID 2 is reserved for check-in, check-out
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String, Long> discoveredMacs = new HashMap<>();

    BluetoothGattCallback bleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            writeLine("Automate service connection state: "+ newState);
            if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED){
                writeLine("Automate service connection state: STATE_CONNECTED");
                Log.v("BLEService", "BLE Connected now discover services");
                Log.v("BLEService", "BLE Connected");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeLine("Automate service go for discover services");
                        gatt.discoverServices();
                    }
                }).start();
            }else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED){
                writeLine("Automate service connection state: STATE_DISCONNECTED");
                Log.v("BLEService", "BLE Disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeLine("Automate service discover service: GATT_SUCCESS");
                Log.v("BLEService", "BLE Services onServicesDiscovered");
                //Get service
                List<BluetoothGattService> services = gatt.getServices();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

    private void writeLine(final String message) {
       /* Handler h = new Handler(getApplicationContext().getMainLooper());
        // Although you need to pass an appropriate context
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });*/
       Log.d(TAG, message);
    }

}