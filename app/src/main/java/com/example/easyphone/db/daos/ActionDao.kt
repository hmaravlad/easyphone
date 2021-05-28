package com.example.easyphone.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction

@Dao
interface ActionDao {
    @Insert
    suspend fun insertAll(vararg action: MyAction): List<Long>

    @Query("DELETE FROM actions WHERE button_id = :buttonId")
    suspend fun deleteByButtonId(buttonId: Int)

    @Query("SELECT * FROM actions WHERE button_id = :buttonId LIMIT 1")
    suspend fun getByButtonId(buttonId: Int): MyAction

    @Query("SELECT * FROM actions")
    suspend fun getAll(): List<MyAction>

    @Query("DELETE FROM actions")
    suspend fun deleteAll()
}