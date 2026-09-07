//package com.astreus.seetara.ui.otp.usecase
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.View
//import android.widget.EditText
//import com.astreus.seetara.R
//
//class OtpTextWatcherUseCase(view: View, private val editText: Array<EditText>) : TextWatcher {
//
//    private val view: View
//
//    init {
//        this.view = view
//    }
//
//    override fun afterTextChanged(editable: Editable) {
//        val text = editable.toString()
//        when (view.id) {
//            R.id.etFirst -> if (text.length == 1) editText[1].requestFocus()
//            R.id.etSecond -> if (text.length == 1) editText[2].requestFocus() else if (text.isEmpty()) editText[0].requestFocus()
//            R.id.etThird -> if (text.length == 1) editText[3].requestFocus() else if (text.isEmpty()) editText[1].requestFocus()
//            R.id.etFour -> if (text.isEmpty()) editText[2].requestFocus()
//        }
//    }
//
//    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
//    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
//}