package com.example.easyphone.data.utils

import android.content.Context
import android.util.Log
import com.example.easyphone.data.providers.Coordiantes
import com.example.easyphone.data.providers.Date
import com.example.easyphone.data.providers.Temperature
import com.example.easyphone.data.providers.Time

class DataProviderFactory(val context: Context) {
    fun getDataProvider(dataType: String): DataProvider? {
        Log.d("MY_DEBUG", "dataType $dataType")
        return when(dataType) {
            "temperature" -> Temperature(context)
            "location" -> Coordiantes(context)
            "coordinates" -> Coordiantes(context)
            "date" -> Date()
            "time" -> Time()
            else -> null
        }
    }
}