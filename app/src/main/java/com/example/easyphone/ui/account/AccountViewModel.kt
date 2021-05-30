package com.example.easyphone.ui.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easyphone.db.utils.DbDeserializer
import com.example.easyphone.db.utils.DbSerializer
import com.example.easyphone.network.UserData
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.repository.DataSyncRepository
import com.example.easyphone.repository.IDataSyncRepository
import com.example.easyphone.utils.IInternetChecker
import com.example.easyphone.utils.InternetChecker
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AccountViewModel(
    val buttonsRepository: ButtonsRepository,
    val dataSyncRepository: IDataSyncRepository,
    application: Application,
    private val internetChecker: IInternetChecker
) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var account: GoogleSignInAccount? = null

    private val serializer = DbSerializer(buttonsRepository)
    private val deserializer = DbDeserializer()


    private var _data = MutableLiveData("")
    private var _haveInternet = MutableLiveData(false)
    private var _loggedInGoogle = MutableLiveData(false)
    private var _apiErrorEvent = MutableLiveData(false)
    private var _messageEvent = MutableLiveData(false)

    private var _errorText = ""
    private var _messageText = ""
    private var _needToRelog = false


    val data: LiveData<String>
        get() = _data
    val haveInternet: LiveData<Boolean>
        get() = _haveInternet
    val loggedInGoogle: LiveData<Boolean>
        get() = _loggedInGoogle
    val apiErrorEvent: LiveData<Boolean>
        get() = _apiErrorEvent
    val errorText: String
        get() = _errorText
    val messageEvent: LiveData<Boolean>
        get() = _messageEvent
    val messageText: String
        get() = _messageText

    val needToRelog: Boolean
        get() = _needToRelog



    fun onApiErrorEventEnded() {
        _apiErrorEvent.value = false
        _errorText = ""
        _needToRelog = false
    }

    fun onMessageEventEnded() {
        _messageEvent.value = false
        _messageText = ""
    }

    fun onGetLastSignedAccount(googleSignInAccount: GoogleSignInAccount?) {
        account = googleSignInAccount

        if (googleSignInAccount == null) {
            _loggedInGoogle.value = false
            return
        }
        _loggedInGoogle.value = true
    }

    fun onImport() {
        uiScope.launch {
            try {
                val savedData = dataSyncRepository.getUserData(account!!.idToken)
                val data = deserializer.deserialize(savedData.replace("\\\"","\""))
                if (data != null) {
                    buttonsRepository.importDatabase(data)
                }
                _messageText = "Import successful"
                _messageEvent.value = true
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 400) {
                    _needToRelog = true
                    _errorText = "Please log in again in your Google account"
                } else {
                    _errorText = "Service unavailable. Please try again later"
                }
                _apiErrorEvent.value = true
            }
        }
    }

    fun onExport() {
        uiScope.launch {
            try {
                val data = serializer.serialize()
                dataSyncRepository.putUserData(account!!.idToken, UserData(data))
                _messageText = "Export successful"
                _messageEvent.value = true
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 400) {
                    _needToRelog = true
                    _errorText = "Please log in again in your Google account"
                } else {
                    _errorText = "Service unavailable. Please try again later"
                }
                _apiErrorEvent.value = true
            } catch (e: JsonEncodingException) {
                Log.d("MY_DEBUG", "invalid json")
            }
        }
    }

    fun onLaunch() {
    }

    fun checkInternet() {
        _haveInternet.value = internetChecker.isOnline()
    }

}