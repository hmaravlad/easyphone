package com.example.easyphone.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.easyphone.db.daos.ActionArgDao
import com.example.easyphone.db.daos.ActionDao
import com.example.easyphone.db.daos.ProgrammedButtonDao
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton

@Database(entities = [ProgrammedButton::class, MyAction::class, ActionArg::class], version = 5, exportSchema = false)
abstract class ButtonsDatabase : RoomDatabase() {

    abstract val programmedButtonDao: ProgrammedButtonDao
    abstract val actionArgDao: ActionArgDao
    abstract val actionDao: ActionDao


    companion object {

        @Volatile
        private var INSTANCE: ButtonsDatabase? = null

        fun getInstance(context: Context): ButtonsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ButtonsDatabase::class.java,
                        "buttons"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}