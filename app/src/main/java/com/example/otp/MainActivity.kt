package com.example.otp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var phoneNo: EditText
    lateinit var send: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        phoneNo = findViewById(R.id.editTextPhone)
        send = findViewById(R.id.button_send)
        send.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, VerificationActivity::class.java)
            intent.putExtra("ph", phoneNo.getText().toString())
            startActivity(intent)
        })
    }
}