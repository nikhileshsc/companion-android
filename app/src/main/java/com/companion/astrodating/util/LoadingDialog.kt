package com.companion.astrodating.util

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import com.companion.astrodating.R

class LoadingDialog(
    context: Activity
) {

    private var dialog: Dialog? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(0))
        }
    }

    fun showDialog() {
        dialog?.show()
    }

    fun hideDialog() {
        if(dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    fun isShowing(): Boolean? {
       return dialog?.isShowing
    }
}