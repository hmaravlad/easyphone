package com.example.easyphone.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData (
    val data: String
): Parcelable