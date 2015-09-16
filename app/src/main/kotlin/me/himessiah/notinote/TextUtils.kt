package me.himessiah.notinote

import android.text.format.DateUtils

public object TextUtils {

    public fun humanReadableDateText(ms: Long): String {
        return DateUtils.getRelativeTimeSpanString(
                ms,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_NO_YEAR).toString()
    }

}