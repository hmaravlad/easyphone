package com.example.easyphone.repository

import com.example.easyphone.network.UserData
import com.example.easyphone.network.UserDataApi
import org.json.JSONObject

class DataSyncRepository: IDataSyncRepository {
    override suspend fun getUserData(googleToken: String): String {
        return UserDataApi.retrofitService.getUserData("Bearer ${googleToken}").data
    }

    override suspend fun putUserData(googleToken: String, data: UserData) {
        UserDataApi.retrofitService.putUserData("Bearer ${googleToken}", data)
    }
}