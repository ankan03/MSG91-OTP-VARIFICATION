package com.example.kotlinmsg91

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.msg91.sendotpandroid.library.internal.SendOTP
import com.msg91.sendotpandroid.library.listners.VerificationListener
import com.msg91.sendotpandroid.library.roots.RetryType
import com.msg91.sendotpandroid.library.roots.SendOTPConfigBuilder
import com.msg91.sendotpandroid.library.roots.SendOTPResponseCode

@Suppress("DEPRECATION")
class VerificationActivity : AppCompatActivity(),
    OnRequestPermissionsResultCallback, VerificationListener {
    var resend_timer: TextView? = null
    private var mOtpEditText: OtpEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        resend_timer = findViewById<View>(R.id.resend_timer) as TextView
        resend_timer!!.setOnClickListener { ResendCode() }
        startTimer()
        mOtpEditText = findViewById(R.id.inputCode)
//        mOtpEditText.setMaxLength(OTP_LNGTH)
        enableInputField(true)
        initiateVerification()
    }

    fun createVerification(phoneNumber: String?, countryCode: Int) {
        SendOTPConfigBuilder()
            .setCountryCode(countryCode)
            .setMobileNumber(phoneNumber) //////////////////direct verification while connect with mobile network/////////////////////////
            .setVerifyWithoutOtp(true) //////////////////Auto read otp from Sms And Verify///////////////////////////
            .setAutoVerification(this@VerificationActivity)
            .setOtpExpireInMinute(5) //default value is one day
            .setOtpHits(3) //number of otp request per number
            .setOtpHitsTimeOut(0L) //number of otp request time out reset in milliseconds default is 24 hours
            .setSenderId("ABCDEF")
            .setMessage("##OTP## is Your verification digits.")
            .setOtpLength(OTP_LNGTH)
            .setVerificationCallBack(this).build()
        SendOTP.getInstance().getTrigger().initiate()
    }

    fun initiateVerification() {
        val intent = intent
        if (intent != null) {
            //DataManager.getInstance().showProgressMessage(this, "")
            val phoneNumber = intent.getStringExtra(MainActivity.INTENT_PHONENUMBER)
            val countryCode = intent.getIntExtra(MainActivity.INTENT_COUNTRY_CODE, 0)
            val phoneText = findViewById<View>(R.id.numberText) as TextView
            phoneText.text = "+$countryCode$phoneNumber"
            createVerification(phoneNumber, countryCode)
        }
    }

    fun ResendCode() {
        startTimer()
        SendOTP.getInstance().getTrigger().resend(RetryType.VOICE)
    }

    fun onSubmitClicked(view: View?) {
        val code = mOtpEditText!!.text.toString()
        if (!code.isEmpty()) {
            hideKeypad()
            verifyOtp(code)
            //DataManager.getInstance().showProgressMessage(this, "")
            val messageText = findViewById<View>(R.id.textView) as TextView
            messageText.text = "Verification in progress"
            enableInputField(false)
        }
    }

    fun enableInputField(enable: Boolean) {
        runOnUiThread {
            val container = findViewById<View>(R.id.inputContainer)
            if (enable) {
                container.visibility = View.VISIBLE
                mOtpEditText!!.requestFocus()
            } else {
                container.visibility = View.GONE
            }
            val resend_timer = findViewById<View>(R.id.resend_timer) as TextView
            resend_timer.isClickable = false
        }
    }

    fun hideProgressBarAndShowMessage(message: Int) {
        hideProgressBar()
        val messageText = findViewById<View>(R.id.textView) as TextView
        messageText.setText(message)
    }

    fun hideProgressBar() {
//        val progressBar = findViewById<View>(R.id.progressIndicator) as ProgressBar
//        progressBar.visibility = View.INVISIBLE
        val progressText = findViewById<View>(R.id.progressText) as TextView
        progressText.visibility = View.INVISIBLE
    }

//    fun showProgress() {
//        val progressBar = findViewById<View>(R.id.progressIndicator) as ProgressBar
//        progressBar.visibility = View.VISIBLE
//    }

    fun showCompleted(isDirect: Boolean) {
        val checkMark = findViewById<View>(R.id.checkmarkImage) as ImageView
        if (isDirect) {
            checkMark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_magic))
        } else {
            checkMark.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_checkmark))
        }
        checkMark.visibility = View.VISIBLE
    }

    fun verifyOtp(otp: String?) {
        SendOTP.getInstance().getTrigger().verify(otp)
    }

    override fun onSendOtpResponse(responseCode: SendOTPResponseCode, message: String) {
        runOnUiThread {
            Log.e(
                TAG,
                "onSendOtpResponse: " + responseCode.getCode().toString() + "=======" + message
            )
            if (responseCode === SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode === SendOTPResponseCode.OTP_VERIFIED) {
                DataManager.getInstance().hideProgressMessage()
                enableInputField(false)
                hideKeypad()
                val textView2 = findViewById<View>(R.id.textView2) as TextView
                val textView1 = findViewById<View>(R.id.textView1) as TextView
                val messageText = findViewById<View>(R.id.textView) as TextView
                val topImg =
                    findViewById<View>(R.id.topImg) as ImageView
                val phoneText = findViewById<View>(R.id.numberText) as TextView
                val topLayout = findViewById<RelativeLayout>(R.id.topLayout)
                if (Build.VERSION.SDK_INT > 16) topLayout.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        this@VerificationActivity,
                        R.drawable.gradient_bg_white
                    )
                ) else topLayout.setBackgroundResource(R.drawable.gradient_bg_white)
                messageText.visibility = View.GONE
                phoneText.visibility = View.GONE
                topImg.visibility = View.INVISIBLE
                textView1.visibility = View.VISIBLE
                textView2.visibility = View.VISIBLE
                if (responseCode === SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER) textView2.text =
                    "Mobile verified using Invisible OTP." else textView2.text =
                    "Your Mobile number has been successfully verified."
                hideProgressBarAndShowMessage(R.string.verified)
                hideProgressBar()
                showCompleted(responseCode === SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER)
            } else if (responseCode === SendOTPResponseCode.READ_OTP_SUCCESS) {
                DataManager.getInstance().hideProgressMessage()
                mOtpEditText!!.setText(message)
            } else if (responseCode === SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode === SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER) {
                DataManager.getInstance().hideProgressMessage()
            } else if (responseCode === SendOTPResponseCode.NO_INTERNET_CONNECTED) {
                DataManager.getInstance().hideProgressMessage()
            } else {
                DataManager.getInstance().hideProgressMessage()
                hideKeypad()
                hideProgressBarAndShowMessage(R.string.failed)
                enableInputField(true)
            }
        }
    }

    private fun startTimer() {
        resend_timer!!.isClickable = false
        resend_timer!!.setTextColor(
            ContextCompat.getColor(
                this@VerificationActivity,
                R.color.white
            )
        )
        object : CountDownTimer(30000, 1000) {
            var secondsLeft = 0
            override fun onTick(ms: Long) {
                if (Math.round(ms.toFloat() / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round(ms.toFloat() / 1000.0f)
                    resend_timer!!.text = "Resend via call ( $secondsLeft )"
                }
            }

            override fun onFinish() {
                resend_timer!!.isClickable = true
                resend_timer!!.text = "Resend via call"
                resend_timer!!.setTextColor(
                    ContextCompat.getColor(
                        this@VerificationActivity,
                        R.color.white
                    )
                )
            }
        }.start()
    }

    private fun hideKeypad() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SendOTP.getInstance().getTrigger().stop()
    }

    companion object {
        private const val TAG = "VerificationActivity"
        private const val OTP_LNGTH = 4
    }
}

