package com.rodcollab.readermate.core.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object DateUtils {
    @SuppressLint("SimpleDateFormat")
    fun getDateFormatted(time: Long) : String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = Date(time)
        return dateFormat.format(date)
    }
    fun Long.toLocalDate(): LocalDate {
        val instant = Instant.ofEpochMilli(this)
        val zoneId = ZoneId.systemDefault()
        return instant.atZone(zoneId).toLocalDate()
    }
}