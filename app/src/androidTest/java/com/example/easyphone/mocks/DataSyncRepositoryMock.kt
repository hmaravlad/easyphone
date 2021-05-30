package com.example.easyphone.mocks

import android.util.Log
import com.example.easyphone.network.UserData
import com.example.easyphone.repository.IDataSyncRepository
import com.google.common.net.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Error

class DataSyncRepositoryMock: IDataSyncRepository {
    private val savedData = mutableMapOf<String, UserData>()

    private var onPut: (() -> Unit)? = null

    fun setOnPut(f: (() -> Unit)?) {
        onPut = f
    }

    override suspend fun getUserData(googleToken: String): String {
        val data = savedData[googleToken]
        return data?.data ?: ""
    }

    override suspend fun putUserData(googleToken: String, data: UserData) {
        savedData[googleToken] = data
        if (onPut != null) onPut
    }
}