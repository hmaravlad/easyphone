package com.example.easyphone.viewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.db.utils.DbSerializer
import com.example.easyphone.mocks.DataSyncRepositoryMock
import com.example.easyphone.mocks.InternetCheckerMock
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.ui.account.AccountViewModel
import com.example.easyphone.ui.account.AccountViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(MockitoJUnitRunner::class)
class AccountViewModelTests {
    @Mock
    private lateinit var mockApplication: Application

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    @Mock
    private lateinit var googleAccount: GoogleSignInAccount

    private lateinit var db: ButtonsDatabase
    private lateinit var serializer: DbSerializer

    lateinit var context: Context
    lateinit var buttonsRepository: ButtonsRepository
    lateinit var dataSyncRepository: DataSyncRepositoryMock
    lateinit var internetChecker: InternetCheckerMock
    lateinit var viewModelFactory: AccountViewModelFactory
    lateinit var viewModel: AccountViewModel

    private val programmedButton1 = ProgrammedButton(1, "text", "green", 200, 2, 3, 4, 2)
    private val programmedButton2 = ProgrammedButton(2, "text", "blue", 100, 2, 3, 4, 2)
    private val action = MyAction(1, "toast", 1)
    private val arg = ActionArg(1, "text", "hello", 1)


    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ButtonsDatabase::class.java
        ).build()
        buttonsRepository = ButtonsRepository(db)
        dataSyncRepository = DataSyncRepositoryMock()
        internetChecker = InternetCheckerMock()
        viewModelFactory =  AccountViewModelFactory(
            buttonsRepository,
            dataSyncRepository,
            mockApplication,
            internetChecker
        )
        viewModel = viewModelFactory.create(AccountViewModel::class.java)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun internetTrue() {
        uiScope.launch {
            internetChecker.setTrue()
            viewModel.checkInternet()
            val result = viewModel.haveInternet.getOrAwaitValue()
            Assert.assertEquals(true, result)
        }
    }

    @Test
    fun internetFalse() {
        uiScope.launch {
            internetChecker.setFalse()
            viewModel.checkInternet()
            val result = viewModel.haveInternet.getOrAwaitValue()
            Assert.assertEquals(false, result)
        }
    }

    @Test
    fun loggedSuccessfully() {
        uiScope.launch {
            val profile: GoogleSignInAccount = GoogleSignInAccount.createDefault()
            viewModel.onGetLastSignedAccount(profile)
            val result = viewModel.loggedInGoogle.getOrAwaitValue()
            Assert.assertEquals(true, result)
        }
    }

    @Test
    fun loggedUnsuccessfully() {
        uiScope.launch {
            val profile: GoogleSignInAccount? = null
            viewModel.onGetLastSignedAccount(profile)
            val result = viewModel.loggedInGoogle.getOrAwaitValue()
            Assert.assertEquals(false, result)
        }
    }

    @Test
    fun ExportImportTests() {
        uiScope.launch {
            `when`(googleAccount.idToken).thenReturn("abcde")
            viewModel.onGetLastSignedAccount(googleAccount)

            buttonsRepository.insertButton(programmedButton1)
            buttonsRepository.insertAction(action)
            buttonsRepository.insertActionArg(arg)

            dataSyncRepository.setOnPut {
                uiScope.launch {
                    buttonsRepository.clearDatabase()
                    buttonsRepository.insertButton(programmedButton2)


                    viewModel.onImport()


                    val buttons = buttonsRepository.getButtons()
                    val actions = buttonsRepository.getAllActions()
                    val args = buttonsRepository.getAllActionArgs()

                    Assert.assertEquals(buttons, listOf(programmedButton1))
                    Assert.assertEquals(actions, listOf(action))
                    Assert.assertEquals(args, listOf(arg))
                }
            }

            viewModel.onExport()
        }
    }


}

fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}