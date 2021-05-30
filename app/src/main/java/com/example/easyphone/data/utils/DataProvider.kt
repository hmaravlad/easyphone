package com.example.easyphone.data.utils

interface DataProvider {
    suspend fun getData(): String
}