package com.example.otp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.msg91.sendotpandroid.library.internal.SendOTP
import com.msg91.sendotpandroid.library.listners.VerificationListener
import com.msg91.sendotpandroid.library.roots.SendOTPConfigBuilder
import com.msg91.sendotpandroid.library.roots.SendOTPResponseCode


class VerificationActivity : AppCompatActivity(), VerificationListener {
    var phone: String? = null
    lateinit var otp: EditText
    lateinit var verifyButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        phone = intent.extras!!.getString("ph")
        otp = findViewById(R.id.otp)
        verifyButton = findViewById(R.id.verify)
        verifyButton.setOnClickListener(View.OnClickListener {
            SendOTP.getInstance().getTrigger().verify(otp.getText().toString())
        })
        init(91)
    }

    override fun onSendOtpResponse(responseCode: SendOTPResponseCode, message: String) {
        runOnUiThread {
            Log.e(
                "VerificationActivity",
                "onSendOtpResponse: " + responseCode.getCode() + "=======" + message
            )
            if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode == SendOTPResponseCode.OTP_VERIFIED) {
                //otp verified OR direct verified by send otp 2.O
                AlertDialog.Builder(this@VerificationActivity)
                    .setTitle("Success!!").setMessage("Verified Successfully !").show()
                Toast.makeText(applicationContext, "Verified successfully", Toast.LENGTH_SHORT)
                    .show()
            } else if (responseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                //Auto read otp from sms successfully
                // you can get otp form message filled
                if (otp != null) {
                    otp!!.setText(message)
                    SendOTP.getInstance().getTrigger().verify(message)
                }
            } else if (responseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER) {
                // Otp send to number successfully
                Toast.makeText(applicationContext, "OTP send successfully", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //exception found
                Toast.makeText(
                    applicationContext,
                    "Error : " + responseCode.getCode(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun init(countryCode: Int) {
        Log.d("PHONE NO", phone)
        Log.d("PHONE NO", countryCode.toString())
        SendOTPConfigBuilder()
            .setCountryCode(countryCode)
            .setMobileNumber(phone)
            .setVerifyWithoutOtp(true) //direct verification while connect with mobile network
            .setAutoVerification(this@VerificationActivity) //Auto read otp from Sms And Verify
            .setSenderId("ABCDEF")
            .setMessage("##OTP## is Your verification digits.")
            .setOtpLength(OTP_LENGTH)
            .setVerificationCallBack(this).build()
        SendOTP.getInstance().getTrigger().initiate()
    }

    companion object {
        const val OTP_LENGTH = 6
    }
}