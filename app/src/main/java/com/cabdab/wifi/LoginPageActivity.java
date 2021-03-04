package com.cabdab.wifi;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.cabdab.wifi.ui.declaration.DeclarationFragment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

public class LoginPageActivity extends AppCompatActivity {

    EditText password;
    EditText username;
    TextView title_log;
    TextView title_in;
    Button login_button;
    //Button fake_login_button;


    //For storing username, pw, loginstatus when app is closed
    private PreferencesUtils preferencesUtils;
    String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGINSTAT = "loginstat";
    public static final String REMINDERSTATUS = "reminderstatus";

    // For getting login status when user clicked logout
    int loginStatus;
    int reminderStatus;

    View progressOverlay;

    private static String TAG = LoginPageActivity.class.toString();

    SUTD_TTS sutd_tts = SUTD_TTS.getSutd_tts();

    SharedPreferences encryptedPreferences;

    public LoginPageActivity() throws GeneralSecurityException, IOException {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencesUtils = new PreferencesUtils(LoginPageActivity.this);

        try {
            encryptedPreferences = EncryptedSharedPreferences.create(
                    "secret_shared_prefs",
                    masterKeyAlias,
                    LoginPageActivity.this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String uname = encryptedPreferences.getString(USERNAME, "");
        String pword = encryptedPreferences.getString(PASSWORD, "");
        int loginSts = encryptedPreferences.getInt(LOGINSTAT, 0);
        int reminderSts = encryptedPreferences.getInt(REMINDERSTATUS, 0);


        reminderStatus = reminderSts;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED); // Ask the user to give permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);


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
        password.setText(pword);
        username.setText(uname);
        title_log = findViewById(R.id.title_log);
        title_in = findViewById(R.id.title_in);
        login_button = findViewById(R.id.login_button);

        progressOverlay = findViewById(R.id.progress_overlay);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex) {
        }


        // Check if user logged out
        Intent decToLogin = getIntent();
        loginStatus = decToLogin.getIntExtra(DeclarationFragment.LOGINSTATUS, -1);
        Log.d(TAG, "loginStatus: " + String.valueOf(loginStatus));
        // if user didn't log out
        if (loginStatus == -1) {
            loginStatus = loginSts;
            Log.d(TAG, "loginStatustoSts: " + String.valueOf(loginStatus));
        }

        // Auto login if user did not logout when exiting the app
        if (loginStatus == 1) {
            login();
            Log.d(TAG, "loginStatusAuto: " + loginStatus);
        }

        // Manual login
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
        //End of onCreate

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


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefEditor = encryptedPreferences.edit();
        prefEditor.putString(USERNAME, username.getText().toString());
        prefEditor.putString(PASSWORD, password.getText().toString());
        prefEditor.putInt(LOGINSTAT, loginStatus);
        prefEditor.putInt(REMINDERSTATUS, reminderStatus);
        prefEditor.apply();
        Log.i(TAG,"onPause()");
    }

    // Helper methods
    public void login(){
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
                            Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            if (loginStatus == 0) {
                                createNotificationChannel();
                                Reminder1();
                                Reminder2();
                                reminderStatus = 1;
                            }
                            loginStatus = 1;
                            Log.d(TAG, "loginStatusManual: " + loginStatus);

                            preferencesUtils.saveSession(sutd_tts.user_id, sutd_tts.user_password);
                            Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME); //Ensure that user cannot press back button
                            startActivity(intent);
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

    public void Reminder1() {
        Intent intent1 = new Intent(LoginPageActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR, 10);
        calendar1.set(Calendar.AM_PM, Calendar.AM);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);
    }

    public void Reminder2(){
        Toast.makeText(LoginPageActivity.this, "Reminder set!", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(LoginPageActivity.this,ReminderBroadcast.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(),1,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR,5);
        calendar2.set(Calendar.AM_PM, Calendar.PM);
        calendar2.set(Calendar.MINUTE,0);
        calendar2.set(Calendar.SECOND,0);
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP,calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
    }


}