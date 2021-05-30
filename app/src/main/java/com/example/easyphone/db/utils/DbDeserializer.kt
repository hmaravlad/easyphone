package com.example.easyphone.db.utils

import com.example.easyphone.repository.ButtonsRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbDeserializer() {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<DbsData> = moshi.adapter<DbsData>(DbsData::class.java)

    suspend fun deserialize(data: String): DbsData? {
        return withContext(Dispatchers.IO) {
            return@withContext jsonAdapter.fromJson(data)
        }
    }
}