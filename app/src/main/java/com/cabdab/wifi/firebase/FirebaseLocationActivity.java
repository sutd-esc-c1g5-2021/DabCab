package com.cabdab.wifi.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.cabdab.wifi.R;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseLocationActivity extends AppCompatActivity {

    EditText MacAddress, MacAddressName;
    Button RegisterMacAddress, ReturnToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_location);

        MacAddress = findViewById(R.id.MacAddress);
        MacAddressName = findViewById(R.id.MacAddressName);
        RegisterMacAddress = findViewById(R.id.RegisterMacAddress);
        ReturnToMain = findViewById(R.id.ReturnToMain);

        RegisterMacAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MacAddressSet = MacAddress.getText().toString();
                String MacAddressNameSet = MacAddressName.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("Location").child(MacAddressSet).setValue(MacAddressNameSet);
                Toast.makeText(FirebaseLocationActivity.this, "Registered", Toast.LENGTH_SHORT).show();
            }
        });

        ReturnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirebaseLocationActivity.this, GetFromFirebaseActivity.class);
                startActivity(intent);
            }
        });
    }
}