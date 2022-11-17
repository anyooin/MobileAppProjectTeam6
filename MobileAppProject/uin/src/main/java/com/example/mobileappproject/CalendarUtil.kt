package com.example.mobileappproject

import android.os.Build
import android.util.Log
import java.time.LocalDate

class CalendarUtil {
    companion object {
        var selectedDate: LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val today : LocalDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}
