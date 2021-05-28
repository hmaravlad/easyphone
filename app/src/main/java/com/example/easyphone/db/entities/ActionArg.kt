package com.example.easyphone.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "args")
class ActionArg(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: String,
    val value: String,
    @ColumnInfo(name = "action_id") val actionId: Int
): Parcelable