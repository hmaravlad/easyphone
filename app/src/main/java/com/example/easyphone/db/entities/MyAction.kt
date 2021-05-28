package com.example.easyphone.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "actions")
data class MyAction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: String,
    @ColumnInfo(name = "button_id") val buttonId: Int
): Parcelable