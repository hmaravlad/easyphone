package com.example.easyphone.db.daos

import androidx.room.*
import com.example.easyphone.db.entities.ProgrammedButton


@Dao
interface ProgrammedButtonDao {
    @Insert
    suspend fun insertAll(vararg buttons: ProgrammedButton): List<Long>

    @Update
    suspend fun update(button: ProgrammedButton)

    @Delete
    suspend fun delete(button: ProgrammedButton)

    @Query("DELETE FROM buttons")
    suspend fun deleteAll()

    @Query("SELECT * FROM buttons")
    suspend fun getAll(): List<ProgrammedButton>

    @Query("SELECT * FROM buttons WHERE id = :id LIMIT 1")
    suspend fun getButtonById(id: Int): ProgrammedButton

    @Query("DELETE FROM buttons WHERE id = :id")
    suspend fun deleteButtonById(id: Int)

    @Query("UPDATE buttons SET `row` = :row, `column` = :column WHERE id = :id")
    suspend fun updatePosition(id: Int, row: Int, column: Int)

    @Query("DELETE FROM buttons WHERE id NOT IN (SELECT button_id FROM actions)")
    suspend fun deleteButtonsWithoutActions()
}