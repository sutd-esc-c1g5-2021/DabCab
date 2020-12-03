package com.hatsumi.bluentry_declaration.ui.declaration;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hatsumi.bluentry_declaration.AndroidUtils;
import com.hatsumi.bluentry_declaration.R;
import com.hatsumi.bluentry_declaration.SUTD_TTS;
import com.hatsumi.bluentry_declaration.TTSWebActivity;

import static android.text.InputType.*;

public class DeclarationFragment extends Fragment {

    private DeclarationViewModel declarationViewModel;
    SwipeRefreshLayout swipeRefreshLayout;


    private static String TAG = DeclarationFragment.class.toString();

    private static SUTD_TTS tts;

    CardView daily_card_view, morning_card_view, evening_card_view;
    View progressOverlay;

    TextView percentage_temp_1, percentage_temp_2, percentage_daily;
    Button log_daily_declaration, log_temperature_button1, log_temperature_button2;

    Button helpButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        declarationViewModel =
                ViewModelProviders.of(this).get(DeclarationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_declaration, container, false);
//        final TextView textView = root.findViewById(R.id.text_declaration);
        declarationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        /*declarationViewModel.getTempDeclaration1_Done().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
            }
        })*/
        return root;
    }

    private static final int TTSWebActivityValue = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case TTSWebActivityValue: {
                Log.d(TAG, "Got TTS Web Activity");
                updateTTSData();
                break;
            }
            default:
                Log.d(TAG, "Invalid result code");
                updateTTSData();
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
                PopupWindow popupWindow = new PopupWindow(popupView, (int) (width * 0.48), WindowManager.LayoutParams.MATCH_PARENT);
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

        helpButton = getView().findViewById(R.id.help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.declaration_help, null, false);
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
        percentage_daily = getView().findViewById(R.id.declaration_circle0);

        progressOverlay = getView().findViewById(R.id.progress_overlay);

        updateTTSData();

        log_temperature_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Performing declaration for temperature (1)");
                Intent intent = new Intent(getActivity(), TTSWebActivity.class);
                intent.putExtra("declaration", "temperature");
                startActivityForResult(intent, TTSWebActivityValue);
            }
        });
        log_temperature_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Performing declaration for temperature (2)");
                Intent intent = new Intent(getActivity(), TTSWebActivity.class);
                intent.putExtra("declaration", "temperature");
                startActivityForResult(intent, TTSWebActivityValue);
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
                Log.d(TAG, "Performing declaration for daily (1)");
                Intent intent = new Intent(getActivity(), TTSWebActivity.class);
                intent.putExtra("declaration", "daily");
                startActivityForResult(intent, TTSWebActivityValue);
            }
        });

        daily_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void updateTTSData() {
        //TODO: Move this into the declaration view model
        swipeRefreshLayout.setRefreshing(true);
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
                            if (declarationCount >= 1) {
                                log_temperature_button1.setVisibility(View.GONE);
                                percentage_temp_1.setText("100%");
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean hasCompletedDeclaration = tts.hasCompletedDailyDeclaration();
                    Log.d(TAG, "Has completed declaration " + hasCompletedDeclaration);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log_daily_declaration.setVisibility(View.GONE);
                            percentage_daily.setText("100%");
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