package com.example.kotlinmsg91

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.msg91.sendotpandroid.library.internal.Iso2Phone
import com.msg91.sendotpandroid.library.utils.PhoneNumberUtils
import java.util.*


class MainActivity() : AppCompatActivity() {
    lateinit var mPhoneNumber: EditText//? = null
    private var mSmsButton: TextView? = null
    private var mCountryIso: String? = null
    private var mNumberTextWatcher: TextWatcher? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)
        mPhoneNumber = findViewById(R.id.phoneNumber)
        mSmsButton = findViewById(R.id.smsVerificationButton)
        mCountryIso = PhoneNumberUtils.getDefaultCountryIso(this)
        val defaultCountryName: String = Locale("", mCountryIso).displayName
        val spinner: CountrySpinner = findViewById<View>(R.id.spinner) as CountrySpinner
        spinner.init(defaultCountryName)
        spinner.addCountryIsoSelectedListener(object : CountrySpinner.CountryIsoSelectedListener {
            override fun onCountryIsoSelected(selectedIso: String?) {
                if (selectedIso != null) {
                    mCountryIso = selectedIso
                    resetNumberTextWatcher(mCountryIso)
                    // force update:

                    mNumberTextWatcher!!.afterTextChanged(mPhoneNumber.getText())
                }
            }
        })
        resetNumberTextWatcher(mCountryIso)
        tryAndPrefillPhoneNumber()
    }

    private fun tryAndPrefillPhoneNumber() {
        if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            val manager: TelephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            mPhoneNumber!!.setText(manager.line1Number)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
            tryAndPrefillPhoneNumber()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions.get(0))) {
                Toast.makeText(
                    this,
                    ("This application needs permission to read your phone number to automatically "
                            + "pre-fill it"),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openActivity(phoneNumber: String) {
        val verification: Intent = Intent(this, VerificationActivity::class.java)
        verification.putExtra(INTENT_PHONENUMBER, phoneNumber)
        verification.putExtra(INTENT_COUNTRY_CODE, Iso2Phone.getPhone(mCountryIso))
        startActivity(verification)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        mSmsButton!!.isEnabled = enabled
    }

    fun onButtonClicked(view: View?) {
        openActivity(e164Number)
    }

    private fun resetNumberTextWatcher(countryIso: String?) {
        if (mNumberTextWatcher != null) {
            mPhoneNumber!!.removeTextChangedListener(mNumberTextWatcher)
        }
        mNumberTextWatcher = object : PhoneNumberFormattingTextWatcher(countryIso) {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                super.beforeTextChanged(s, start, count, after)
            }

            @Synchronized
            override fun afterTextChanged(s: Editable) {
                super.afterTextChanged(s)
                if (isPossiblePhoneNumber) {
                    setButtonsEnabled(true)
                    mPhoneNumber!!.setTextColor(Color.WHITE)
                } else {
                    setButtonsEnabled(false)
                    mPhoneNumber!!.setTextColor(Color.RED)
                }
            }
        }
        mPhoneNumber!!.addTextChangedListener(mNumberTextWatcher)
    }

    private val isPossiblePhoneNumber: Boolean
        private get() = PhoneNumberUtils.isPossibleNumber(
            mPhoneNumber!!.text.toString(), mCountryIso
        )

    // return PhoneNumberUtils.formatNumberToE164(mPhoneNumber.getText().toString(), mCountryIso);
    private val e164Number: String
        private get() {
            return mPhoneNumber!!.text.toString().replace("\\D".toRegex(), "").trim { it <= ' ' }
            // return PhoneNumberUtils.formatNumberToE164(mPhoneNumber.getText().toString(), mCountryIso);
        }

    companion object {
        val INTENT_PHONENUMBER: String = "phonenumber"
        val INTENT_COUNTRY_CODE: String = "code"
    }
}