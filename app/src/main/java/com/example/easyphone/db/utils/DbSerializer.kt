package com.example.easyphone.db.utils

import android.util.Log
import com.example.easyphone.repository.ButtonsRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class DbSerializer(val repository: ButtonsRepository) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<DbsData> = moshi.adapter<DbsData>(DbsData::class.java)

    suspend fun serialize(): String {
        val dbsData = DbsData(
            repository.getButtons(),
            repository.getAllActions(),
            repository.getAllActionArgs()
        )
        Log.d("MY_DEBUG", dbsData.toString() ?: "")

        return jsonAdapter.toJson(dbsData)
    }
}