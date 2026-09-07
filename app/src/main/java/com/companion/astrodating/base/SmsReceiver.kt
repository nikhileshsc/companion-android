package com.companion.astrodating.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.companion.astrodating.ui.otp.ui.verify.VerifyOtpActivity


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<Any>?
            if (pdus != null) {
                for (pdu in pdus) {
                    val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdu as ByteArray)
                    val messageBody: String = smsMessage.messageBody
                    // Here you can extract the OTP from the messageBody
                    // For example, if the message is "Your OTP is: 123456"
                    val otp = extractOtp(messageBody)
                    // Update the TextView in your activity
                    val verifyOtpActivity: VerifyOtpActivity = context as VerifyOtpActivity
                    verifyOtpActivity.populateOtpMessage(otp)
                }
            }
        }
    }

    private fun extractOtp(message: String): String {
        // Simple regex to extract a 6-digit OTP
        val otp = message.replace("[^0-9]".toRegex(), "")
        return if (otp.length >= 6) otp.substring(0, 6) else ""
    }
}

