package com.example.easyphone.utils

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.db.utils.DbDeserializer
import com.example.easyphone.db.utils.DbSerializer
import com.example.easyphone.db.utils.DbsData
import com.example.easyphone.repository.ButtonsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class DbDeserializeTest {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private lateinit var deserializer: DbDeserializer

    private val programmedButton = ProgrammedButton(1, "text", "green", 434, 2, 3, 4,2)
    private val action = MyAction(1, "email", 1)

    @Before
    fun createDb() {
        deserializer = DbDeserializer()
    }

    @Test
    @Throws(Exception::class)
    fun deserializeEmpty() {
        uiScope.launch {
            val str = "{\"buttons\":[],\"actions\":[],\"args\":[]}"
            val result = deserializer.deserialize(str)
            Assert.assertEquals(result, DbsData(listOf(), listOf(), listOf()))
        }
    }

    @Test
    @Throws(Exception::class)
    fun deserializeWithData() {
        uiScope.launch {
            val str = "{\"buttons\":[{\"id\":1,\"text\":\"text\",\"colorName\":\"green\",\"color\":434,\"row\":2,\"column\":3,\"width\":4,\"height\":2}],\"actions\":[{\"id\":1,\"type\":\"email\",\"buttonId\":1}],\"args\":[]}"
            val result = deserializer.deserialize(str)
            Assert.assertEquals(result, DbsData(listOf(programmedButton), listOf(action), listOf()))
        }
    }
}