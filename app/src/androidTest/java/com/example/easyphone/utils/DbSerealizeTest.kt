package com.example.easyphone.utils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.db.utils.DbSerializer
import com.example.easyphone.repository.ButtonsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DbSerealizeTest {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private lateinit var repository: ButtonsRepository
    private lateinit var db: ButtonsDatabase
    private lateinit var serializer: DbSerializer

    private val programmedButton = ProgrammedButton(1, "text", "green", 434, 2, 3, 4,2)
    private val action = MyAction(1, "email", 1)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ButtonsDatabase::class.java
        ).build()
        repository = ButtonsRepository(db)
        serializer = DbSerializer(repository)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun serializeEmpty() {
        uiScope.launch {
            repository.clearDatabase()
            val result = serializer.serialize()
            Assert.assertEquals(result, "{\"buttons\":[],\"actions\":[],\"args\":[]}")
        }
    }

    @Test
    @Throws(Exception::class)
    fun serializeWithData() {
        uiScope.launch {
            repository.insertButton(programmedButton)
            repository.insertAction(action)
            val result = serializer.serialize()
            Assert.assertEquals(result, "{\"buttons\":[{\"id\":1,\"text\":\"text\",\"colorName\":\"green\",\"color\":434,\"row\":2,\"column\":3,\"width\":4,\"height\":2}],\"actions\":[{\"id\":1,\"type\":\"email\",\"buttonId\":1}],\"args\":[]}")
            repository.clearDatabase()
        }
    }
}