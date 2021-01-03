package com.example.msg91;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.msg91.sendotpandroid.library.internal.SendOTP;
import com.msg91.sendotpandroid.library.listners.VerificationListener;
import com.msg91.sendotpandroid.library.roots.SendOTPConfigBuilder;
import com.msg91.sendotpandroid.library.roots.SendOTPResponseCode;

public class VerificationActivity extends AppCompatActivity implements VerificationListener {
    String phone;
    EditText otp;
    Button verifyButton;
    public static final int OTP_LENGTH = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        phone = getIntent().getExtras().getString("ph");

        otp = findViewById(R.id.otp);
        verifyButton = findViewById(R.id.verify);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendOTP.getInstance().getTrigger().verify(otp.getText().toString() );
            }
        });
        init(91);

    }

    @Override
    public void onSendOtpResponse(final SendOTPResponseCode responseCode, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("VerificationActivity", "onSendOtpResponse: " + responseCode.getCode() + "=======" + message);
                if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode == SendOTPResponseCode.OTP_VERIFIED) {
                    //otp verified OR direct verified by send otp 2.O
                    new AlertDialog.Builder(VerificationActivity.this).setTitle("Success!!").setMessage("Verified Successfully !").show();
                    Toast.makeText(getApplicationContext(),"Verified successfully",Toast.LENGTH_SHORT).show();
                } else if (responseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                    //Auto read otp from sms successfully
                    // you can get otp form message filled
                    if (otp !=null){
                        otp.setText(message);
                        SendOTP.getInstance().getTrigger().verify(message);
                    }
                } else if (responseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER)
                {
                    // Otp send to number successfully
                    Toast.makeText(getApplicationContext(),"OTP send successfully",Toast.LENGTH_SHORT).show();
                } else {
                    //exception found
                    Toast.makeText(getApplicationContext(),"Error : "+responseCode.getCode(),Toast.LENGTH_SHORT).show();
                }
            }


        });

    }
    private void init(int countryCode){
        new SendOTPConfigBuilder()
                .setCountryCode(countryCode)
                .setMobileNumber(phone)
                .setVerifyWithoutOtp(true)//direct verification while connect with mobile network
                .setAutoVerification(VerificationActivity.this)//Auto read otp from Sms And Verify
                .setSenderId("ABCDEF")
                .setMessage("##OTP## is Your verification digits.")
                .setOtpLength(OTP_LENGTH)
                .setVerificationCallBack(this).build();
        SendOTP.getInstance().getTrigger().initiate();
    }
}