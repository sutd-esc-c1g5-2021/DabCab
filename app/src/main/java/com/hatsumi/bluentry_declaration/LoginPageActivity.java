package com.hatsumi.bluentry_declaration;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginPageActivity extends AppCompatActivity {

    EditText password;
    EditText username;
    TextView title_log;
    TextView title_in;
    Button login_button;
    Button fake_login_button;

    private static String TAG = LoginPageActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        password = findViewById(R.id.password);
        username = findViewById(R.id.username);
        title_log = findViewById(R.id.title_log);
        title_in = findViewById(R.id.title_in);
        login_button = findViewById(R.id.login_button);
        fake_login_button = findViewById(R.id.fake_login);

        try{
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex){ }


        SUTD_TTS sutd_tts = SUTD_TTS.getSutd_tts();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("key", "here");

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sutd_tts.setCredentials(username.getText().toString(), password.getText().toString());
                        boolean result = sutd_tts.attemptLogin();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result) {
                                    Log.d(TAG, "Success in UI thread");
                                    Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Log.d(TAG, "Fail in UI Thread");
                                    Toast.makeText(LoginPageActivity.this, "Invalid", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });

            }
        });

        fake_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Simulated login");
                Toast.makeText(LoginPageActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

}