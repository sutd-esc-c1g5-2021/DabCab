package com.hatsumi.bluentry_declaration;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hatsumi.bluentry_declaration.firebase.FirebaseUserPeriod;
import com.hatsumi.bluentry_declaration.ui.splash.OnBoardingActivity;
import com.hatsumi.bluentry_declaration.ui.splash.SplashActivity;

import java.util.Calendar;

public class LoginPageActivity extends AppCompatActivity {

    EditText password;
    EditText username;
    TextView title_log;
    TextView title_in;
    Button login_button;
    //Button fake_login_button;


    private PreferencesUtils preferencesUtils;

    View progressOverlay;

    private static String TAG = LoginPageActivity.class.toString();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencesUtils = new PreferencesUtils(LoginPageActivity.this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED); // Ask the user to give permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);


        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(overlayIntent, 0);
        }


        if (!Settings.canDrawOverlays(this)) {
            Intent overlayIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(overlayIntent, 0);
        }

        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        title_log = findViewById(R.id.title_log);
        title_in = findViewById(R.id.title_in);
        login_button = findViewById(R.id.login_button);

        progressOverlay = findViewById(R.id.progress_overlay);

        try{
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex){ }


        SUTD_TTS sutd_tts = SUTD_TTS.getSutd_tts();


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("key", "here");
                AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sutd_tts.setCredentials(username.getText().toString(), password.getText().toString());
                        boolean result = sutd_tts.attemptLogin();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);
                                if (result) {
                                    Log.d(TAG, "Success in UI thread");
                                    Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    preferencesUtils.saveSession(sutd_tts.user_id, sutd_tts.user_password);
                                    Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME); //Ensure that user cannot press back button
                                    startActivity(intent);
                                    createNotificationChannel();
                                    Reminder();
                                    LoginPageActivity.this.finish();
                                }
                                else {
                                    Log.d(TAG, "Fail in UI Thread");
                                    Toast.makeText(LoginPageActivity.this, "Invalid Username/Password Combination", Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    }
                });

            }
        });

       /* fake_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Simulated login");


               // Intent intent = new Intent(LoginPageActivity.this, OnBoardingActivity.class);
               // startActivity(intent);

                AndroidUtils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                AsyncTask.execute(new Runnable() {
                                      @Override
                                      public void run() {
                                          try {
                                              String studentID = "1001234";
                                              FirebaseUserPeriod fbh = new FirebaseUserPeriod(studentID);
                                              fbh.outOfRange("64:CF:D9:2D:C8:90");
                                              fbh.inRange("64:CF:D9:2D:C8:90");

                                              Log.d(TAG, "Sleep in background task complete");

                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {

                                                      AndroidUtils.animateView(progressOverlay, View.GONE, 0, 200);
                                                      Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                                      Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                                                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME); //Ensure that user cannot press back button
                                                      startActivity(intent);
                                                      LoginPageActivity.this.finish();
                                                  }
                                              });
                                          } catch (Exception e) {
                                              e.printStackTrace();
                                          }

                                      }
                                  });


            }
        });*/

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminderchannel";
            String description = "Channel for Hatsumi's Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void Reminder(){
        Toast.makeText(LoginPageActivity.this,"Reminder set!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginPageActivity.this,ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,11);
        calendar.set(Calendar.MINUTE,55 );
        calendar.set(Calendar.SECOND,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }


}