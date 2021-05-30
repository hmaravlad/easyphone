package com.example.easyphone.db.utils

import android.os.Parcelable
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DbsData(
    val buttons: List<ProgrammedButton>,
    val actions: List<MyAction>,
    val args: List<ActionArg>
) : Parcelable