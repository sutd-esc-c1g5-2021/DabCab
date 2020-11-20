package com.hatsumi.bluentry_declaration.ui.declaration;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    TextView percentage_temp_1, percentage_temp_2;
    Button log_daily_declaration, log_temperature_button1, log_temperature_button2;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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