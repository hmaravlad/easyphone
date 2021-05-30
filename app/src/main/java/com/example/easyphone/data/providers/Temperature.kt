package com.example.easyphone.data.providers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.easyphone.data.utils.DataProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class Temperature(val context: Context) : DataProvider {
    val onStopChannel = Channel<Float>()


    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor?

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    }

    fun onStop(value: Float) {
        Log.d("MY_DEBUG", "value: $value")
        onStopChannel.offer(value)
        onStopChannel.close()
    }

    val eventListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            onStop(event.values[0])
        }
    }

    override suspend fun getData(): String {
        Log.d("MY_DEBUG", "Getting temperature")
        Log.d("MY_DEBUG", "have sensor ${temperatureSensor}")

        if (temperatureSensor != null) {
            sensorManager.registerListener(eventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            return "{no temperature sensor}"
        }

        return withContext(Dispatchers.IO) {
            val value = onStopChannel.receive()
            val formatted = String.format("%.2f", value)
            sensorManager.unregisterListener(eventListener)
            return@withContext formatted
        }
    }
}