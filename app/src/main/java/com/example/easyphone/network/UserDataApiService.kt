package com.example.easyphone.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.http.*

private const val BASE_URL = "https://easyphone.herokuapp.com/"

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface UserDataApiService {
    @GET("data")
    suspend fun getUserData(@Header("Authorization") googleToken: String): UserData

    @PUT("data")
    suspend fun putUserData(@Header("Authorization") googleToken: String, @Body data: UserData): Response<Unit>
}

object UserDataApi {
    val retrofitService: UserDataApiService by lazy { retrofit.create(UserDataApiService::class.java) }
}
