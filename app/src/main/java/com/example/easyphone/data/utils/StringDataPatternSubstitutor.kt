package com.example.easyphone.data.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StringDataPatternSubstitutor(val context: Context) {
    private var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val dataProviderFactory = DataProviderFactory(context)

    fun launchWithString(str: String, action: (str: String) -> Unit) {
        var resStr = str
        uiScope.launch {
            val regex = Regex("\\$\\{[a-zA-Z]*\\}")
            while (true) {
                val occurence = regex.find(resStr)
                if (occurence == null) break
                val dataType = occurence.value.removePrefix("\${").removeSuffix("}")
                val dataProvider = dataProviderFactory.getDataProvider(dataType)

                Log.d("MY_DEBUG", "dataType: ${dataType}, value: ${occurence.value}")

                val data = if (dataProvider != null) {dataProvider.getData() } else { "{unknown pattern}"}
                Log.d("MY_DEBUG", "val: ${occurence.value} data: ${data}")

                resStr = resStr.replace(occurence.value, data)
                Log.d("MY_DEBUG", "str: ${resStr}")

            }
            action(resStr)
        }
    }
}