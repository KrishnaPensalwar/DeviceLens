package com.example.devicelens.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeFormatter {

    fun formatDuration(millis: Long): String {
        if (millis <= 0) return "0m"
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    fun formatLastUsed(millis: Long): String {
        if (millis <= 0) return "Not available"
        val now = Calendar.getInstance()
        val then = Calendar.getInstance().apply { timeInMillis = millis }
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
        return when {
            isSameDay(now, then) -> "Today $time"
            isYesterday(now, then) -> "Yesterday $time"
            else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(millis))
        }
    }

    fun formatSession(startMillis: Long, endMillis: Long): String {
        val start = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(startMillis))
        val end = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(endMillis))
        return "$start – $end"
    }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(now: Calendar, then: Calendar): Boolean {
        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, then)
    }
}
