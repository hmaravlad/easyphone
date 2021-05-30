package com.example.easyphone.data.providers

import android.icu.text.SimpleDateFormat
import com.example.easyphone.data.utils.DataProvider
import java.util.*

class Time : DataProvider {
    override suspend fun getData(): String {
        val currentDateTime = Calendar.getInstance().getTime()
        val sdf = SimpleDateFormat.getTimeInstance()
        val currentDate = sdf.format(currentDateTime)
        return currentDate
    }
}