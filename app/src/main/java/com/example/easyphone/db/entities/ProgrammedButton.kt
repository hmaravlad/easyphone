package com.example.easyphone.db.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "buttons")
data class ProgrammedButton(
    @PrimaryKey(autoGenerate = true) val id: Int,

    val text: String,
    @ColumnInfo(name = "color_name") val colorName: String,
    val color: Int,
    val row: Int,
    val column: Int,
    val width: Int,
    val height: Int
): Parcelable