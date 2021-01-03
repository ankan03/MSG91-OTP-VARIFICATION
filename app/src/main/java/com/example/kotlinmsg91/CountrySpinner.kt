package com.example.kotlinmsg91

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import java.util.*


class CountrySpinner : AppCompatSpinner {
    private val mCountries: MutableMap<String?, String> = TreeMap()
    private val mListeners: MutableList<CountryIsoSelectedListener> = ArrayList()

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    fun init(defaultCountry: String?) {
        initCountries()
        val countryList: MutableList<String?> = ArrayList(mCountries.keys)
        countryList.remove(defaultCountry)
        countryList.add(0, defaultCountry)
        val adapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(context, R.layout.simple_spinner_item, countryList as List<Any?>)
        setAdapter(adapter)
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedCountry = adapterView.getItemAtPosition(position) as String
                val textView = view as TextView
                textView.setTextColor(ContextCompat.getColor(context, R.color.white))
                notifyListeners(selectedCountry)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    fun addCountryIsoSelectedListener(listener: CountryIsoSelectedListener) {
        mListeners.add(listener)
    }

    private fun initCountries() {
        val isoCountryCodes = Locale.getISOCountries()
        for (iso in isoCountryCodes) {
            val country = Locale("", iso).displayCountry
            mCountries[country] = iso
        }
    }

    private fun notifyListeners(selectedCountry: String) {
        val selectedIso = mCountries[selectedCountry]
        for (listener in mListeners) {
            listener.onCountryIsoSelected(selectedIso)
        }
    }

    interface CountryIsoSelectedListener {
        fun onCountryIsoSelected(iso: String?)
    }
}