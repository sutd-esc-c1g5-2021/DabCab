package com.hatsumi.bluentry_declaration.ui.home;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hatsumi.bluentry_declaration.AndroidUtils;
import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.SUTD_TTS;
import com.hatsumi.bluentry_declaration.TTSWebActivity;

import java.util.ArrayList;
import java.util.Objects;

import static android.text.InputType.TYPE_CLASS_TEXT;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.toString();

    static boolean active = false;

    public final static String LOC_KEY = "LOC_KEY";
    private final static int BLUETOOTH_PERMISSION_CODE = 100;

    TextView userName;
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

    private DeclarationViewModel declarationViewModel;
    SwipeRefreshLayout swipeRefreshLayout;

    private static SUTD_TTS tts;

    CardView daily_card_view, morning_card_view, evening_card_view;
    View progressOverlay;

    TextView percentage_temp_1, percentage_temp_2;
    Button log_daily_declaration, log_temperature_button1, log_temperature_button2;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        location_1_count = Objects.requireNonNull(getView()).findViewById(R.id.location_1_count);
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


        // OPTIONS page
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


        //Notification badge counter(invisible if no notification)
        // TODO: Assign counter
//        if (counter!=0){
//            badgeNotification = findViewById(R.id.badge_notification);
//            badgeNotification.setText(counter);
//            badgeNotification.setVisibility(View.VISIBLE);
//        }

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


        // Popup for frequently visited location
        for (int i = 0; i < locationButtons.size(); i++) {
            ImageButton currButton = locationButtons.get(i);
            int finalI = i;
            currButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.location_popup, null, false);
                    ((TextView)popupView.findViewById(R.id.popup_text)).setText(visitedLocation.get(finalI));
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



        tts = SUTD_TTS.getSutd_tts();

        swipeRefreshLayout = getView().findViewById(R.id.declaration_pull_to_refresh);
        daily_card_view = getView().findViewById(R.id.daily_declaration_card_view);
        morning_card_view = getView().findViewById(R.id.morning_card_view);
        evening_card_view = getView().findViewById(R.id.evening_card_view);
        log_daily_declaration = getView().findViewById(R.id.log_daily_button);
        log_temperature_button1 = getView().findViewById(R.id.log_temperature_button1);
        log_temperature_button2 = getView().findViewById(R.id.log_temperature_button2);

        percentage_temp_1 = getView().findViewById(R.id.temp_declaration_circle1);
        percentage_temp_2 = getView().findViewById(R.id.temp_declaration_circle2);

        progressOverlay = getView().findViewById(R.id.progress_overlay);

        updateTTSData();

        log_temperature_button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                launchTemperatureDialog();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Simulate the refreshing takes 5 seconds
                updateTTSData();
            }
        });



        log_daily_declaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Performing declaration for temperature (1)");
                Intent intent = new Intent(getActivity(), TTSWebActivity.class);
                intent.putExtra("declaration", "daily");
                startActivity(intent);
            }
        });

        daily_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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



    //Declaration View Helper Methods
    private void updateTTSData() {
        //TODO: Move this into the declaration view model
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final int declarationCount = tts.completedTempDeclarationCount();
                    Log.d(TAG, "Declaration count " + declarationCount);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (declarationCount >= 2) {
                                log_temperature_button2.setVisibility(View.GONE);
                                percentage_temp_2.setText("100%"); //TODO: use strings.xml

                            }
                            if (declarationCount > 1) {
                                log_temperature_button1.setVisibility(View.GONE);
                                percentage_temp_1.setText("100%");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void launchTemperatureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Temperature");

        // Set up the input
        final EditText input = new EditText(getActivity());

        input.setInputType(TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Clicked on the dialog");
                performTemperatureDeclaration(input.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void performTemperatureDeclaration(String temperature) {

        AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                tts.attemptTemperatureDeclaration(temperature);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);
                    }
                });
            }
        });

    }
}