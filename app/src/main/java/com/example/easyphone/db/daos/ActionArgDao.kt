package com.example.easyphone.db.daos

import androidx.room.*
import com.example.easyphone.db.entities.ActionArg

@Dao
interface ActionArgDao {
    @Insert
    suspend fun insertAll(vararg args: ActionArg): List<Long>

    @Query("DELETE FROM args WHERE action_id IN (SELECT id FROM actions WHERE button_id = :buttonId)")
    suspend fun deleteByButtonId(buttonId: Int)

    @Query("SELECT * FROM args WHERE action_id IN (SELECT id FROM actions WHERE button_id = :buttonId)")
    suspend fun getByButtonId(buttonId: Int): List<ActionArg>

    @Query("SELECT * FROM args")
    suspend fun getAll(): List<ActionArg>

    @Query("DELETE FROM args")
    suspend fun deleteAll()
}