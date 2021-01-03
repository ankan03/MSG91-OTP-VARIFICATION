package com.example.msg91;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText phoneNo;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNo = findViewById(R.id.editTextPhone);
        send = findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VerificationActivity.class);
                intent.putExtra("ph",phoneNo.getText().toString());
                startActivity(intent);
            }
        });
    }
}