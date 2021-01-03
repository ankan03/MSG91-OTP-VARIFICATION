package com.example.kotlinmsg91

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.TextView


@Suppress("DEPRECATION")
class DataManager private constructor() {
//    private val ourInstance = DataManager()
    private var isProgressDialogRunning = false
    private var mDialog: Dialog? = null
    fun hideProgressMessage() {
        isProgressDialogRunning = true
        try {
            if (mDialog != null) mDialog!!.dismiss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showProgressMessage(dialogActivity: Context?, msg: String?) {
        try {
            if (isProgressDialogRunning) {
                hideProgressMessage()
            }
            isProgressDialogRunning = true
            mDialog = Dialog(dialogActivity!!, R.style.MyMaterialTheme)
            mDialog!!.setContentView(R.layout.custom_progress_bar)
            //            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(dialogActivity,R.color.white_trance)));

            mDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val textView = mDialog!!.findViewById<TextView>(R.id.loadingText)
            textView.visibility = View.VISIBLE
            if (msg != null) textView.text = Html.fromHtml(msg) else textView.visibility = View.GONE
            val lp = mDialog!!.window!!.attributes
            lp.dimAmount = 0.8f
            mDialog!!.window!!.attributes = lp
            mDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            mDialog!!.setCancelable(false)
            mDialog!!.setCanceledOnTouchOutside(false)
            mDialog!!.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {
        @JvmName("getInstance1")
        fun getInstance(): DataManager{
            val instance = DataManager()
            return instance
        }
    }
}