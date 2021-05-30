package com.example.easyphone.repository

import com.example.easyphone.network.UserData
import com.example.easyphone.network.UserDataApi

interface IDataSyncRepository {
    suspend fun getUserData(googleToken: String): String

    suspend fun putUserData(googleToken: String, data: UserData)
}