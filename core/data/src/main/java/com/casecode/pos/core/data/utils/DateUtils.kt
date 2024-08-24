package com.casecode.pos.core.data.utils

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.toFormattedDateTimeString(): String =
    this.let {
        SimpleDateFormat("MMM dd, yyyy hh:mm a ", Locale.getDefault()).format(this)
    }

fun Date.toDateFormatString(): String {
    val dataFormated =
        this
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM, dd, yyyy"))

    return dataFormated
}

fun Date.toTimeFormatedString(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this)

fun Calendar.toDateFormatString(): String {
    val year = this.get(Calendar.YEAR)
    val month = this.get(Calendar.MONTH) + 1
    val dayOfMonth = this.get(Calendar.DAY_OF_MONTH)
    val dataFormated =
        String.format(
            Locale.getDefault(),
            "%s %02d ,%0d",
            year,
            month,
            dayOfMonth,
        )
    return dataFormated
}