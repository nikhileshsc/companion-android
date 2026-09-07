package com.companion.astrodating.util

import android.Manifest
import android.app.Activity
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.app.ActivityCompat
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal fun formatDate(date: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val formattedDate = OffsetDateTime.parse(date)
        DateTimeFormatter.ofPattern(FREE_KUNDALI_DATE_FORMAT, Locale.ENGLISH)
            .format(formattedDate)
    } else {
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        SimpleDateFormat(
            REQUIRED_DATE_FORMAT,
            Locale.getDefault()
        ).format(formatter.parse(date)!!)
    }
}

internal fun formatDateForBirthDate(date: String): String {
    val inputFormat = SimpleDateFormat(DATE_FORMAT)
    val outputFormat = SimpleDateFormat(DATE_FORMAT1)
    val outputDate = inputFormat.parse(date)
    return outputFormat.format(outputDate!!)
}

fun getTimeInMilliSeconds(seconds: Long): Long {
    return seconds * 1000
}

fun showTimeDuration(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = (milliseconds / (1000 * 60 * 60))
    return if(hours == 0L) {
        String.format(FORMAT_MINUTES_AND_SECONDS, minutes , seconds)
    } else {
        String.format(FORMAT_HOURS_MINUTES_AND_SECONDS, hours, minutes, seconds)
    }
}

fun getTimeIntoHoursAndMinutes(seconds: Int): String {
    val hours = seconds/1000
    val minutes = (seconds%3600) / 60
    return String.format(FORMAT_MINUTES_AND_SECONDS, hours, minutes)
}

/*fun convertToByteArray(data: CoHostEndUserData): ByteArray {
    val gson = Gson()
    val json = gson.toJson(data)
    return json.toByteArray(Charsets.UTF_8)
}

fun convertFromByteArray(byteArray: ByteArray): CoHostEndUserData {
    val gson = Gson()
    val json = byteArray.toString(Charsets.UTF_8)
    return gson.fromJson(json, CoHostEndUserData::class.java)
}

fun convertCoHostTypeData(byteArray: ByteArray): CoHostTypeOfCall {
    Log.e(TAG, "In convertCoHostTypeData: ")
    val gson = Gson()
    val json = byteArray.toString(Charsets.UTF_8)
    Log.e(TAG, "convertCoHostTypeData: json = $json")
    return gson.fromJson(json, CoHostTypeOfCall::class.java)
}*/

fun inFromLeftAnimation(): Animation {
    val inFromLeft: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, -1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    inFromLeft.duration = 800
    inFromLeft.interpolator = AccelerateInterpolator()
    return inFromLeft
}

fun outToLeftAnimation(): Animation {
    val outToLeft: Animation = TranslateAnimation(
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, -1.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f,
        Animation.RELATIVE_TO_PARENT, 0.0f
    )
    outToLeft.duration = 800
    outToLeft.interpolator = AccelerateInterpolator()
    return outToLeft
}
internal fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat(CHAT_DATE_FORMAT, Locale.ENGLISH)
    return format.format(date)
}
internal fun convertMessageTimeFromLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat(MESSAGE_CHAT_DATE_FORMAT, Locale.ENGLISH)
    return format.format(date)
}

internal fun currentTimeToLong(): Long {
    return System.currentTimeMillis()
}

internal fun convertDateToLong(date: String): Long {
    val df = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.ENGLISH)
    return df.parse(date).time
}

internal fun getAge(dobString: String): Int {
    var date: Date? = null
    val sdf = SimpleDateFormat(DATE_FORMAT1,Locale.US)
    try {
        date = sdf.parse(dobString)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    if (date == null) return 0
    val dob: Calendar = Calendar.getInstance()
    val today: Calendar = Calendar.getInstance()
    dob.time = date
    val year: Int = dob.get(Calendar.YEAR)
    val month: Int = dob.get(Calendar.MONTH)
    val day: Int = dob.get(Calendar.DAY_OF_MONTH)
    dob.set(year, month + 1, day)
    var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}

internal fun requestPermissions(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES)
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE)
    } else {
        // For older versions, use READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
}
fun formatNumber(number: Double): String {
    return if (number == number.toInt().toDouble()) {
        String.format("%.0f", number) // No decimal places for whole numbers
    } else {
        String.format("%.1f", number) // One decimal place for non-whole numbers
    }
}
fun inputFilter(regexString: String) = object : InputFilter {
    val regex = regexString.toRegex()
    override fun filter(
        p0: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        if (p0!!.equals("")) {
            return p0
        }
        if (p0.toString().matches(regex)) {
            return p0
        }
        return ""
    }
}
fun facebookLogEvent(activity: Activity,eventName:String){
    FacebookSdk.setAutoInitEnabled(true)
    FacebookSdk.fullyInitialize()
    val logger = AppEventsLogger.newLogger(activity)
    logger.logEvent(eventName)
}