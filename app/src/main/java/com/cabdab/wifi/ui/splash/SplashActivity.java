package com.cabdab.wifi.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.cabdab.wifi.LoginPageActivity;
import com.cabdab.wifi.MainActivity;
import com.cabdab.wifi.PreferencesUtils;
import com.cabdab.wifi.R;
import com.cabdab.wifi.SUTD_TTS;

public class SplashActivity extends AppCompatActivity {

    private static final int SplashDuration = 500;

    ImageView logo;
    Animation sideAnim;

    PreferencesUtils preferencesUtils;
    SharedPreferences onBoardingScreen;

    private String TAG = SplashActivity.this.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesUtils = new PreferencesUtils(SplashActivity.this);

        setContentView(R.layout.activity_splash);
        //animations
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.side_anim);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {


                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                // Warning: Need to remove
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);

                //TODO: change !isFirstTime to isFirstTime when not debugging
                if (isFirstTime) {
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.apply();

                    Intent intent = new Intent(SplashActivity.this, OnBoardingActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    if (!preferencesUtils.getUsername().equals("Invalid") && !preferencesUtils.getPassword().equals("Invalid"))
                    {
                        //Attempt to do TTS Login
                        Log.d(TAG, "Got username & password");
                        String username = preferencesUtils.getUsername();
                        String password = preferencesUtils.getPassword();
                        SUTD_TTS sutd_tts = SUTD_TTS.getSutd_tts();

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                sutd_tts.setCredentials(username, password);
                                boolean result = sutd_tts.attemptLogin();
                                if (result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                            //TODO: also replace SecondActivity with LoginScreen
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }
                            }
                        });
                    }
                    else {
                        Intent intent = new Intent(SplashActivity.this, LoginPageActivity.class);
                        //TODO: also replace SecondActivity with LoginScreen
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, SplashDuration);

    }
}