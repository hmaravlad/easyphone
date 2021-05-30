package com.example.easyphone.ui.account

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.repository.DataSyncRepository
import com.example.easyphone.repository.IDataSyncRepository
import com.example.easyphone.ui.main.MainViewModel
import com.example.easyphone.utils.IInternetChecker
import com.example.easyphone.utils.InternetChecker

class AccountViewModelFactory (
    private val buttonsRepository: ButtonsRepository,
    private val dataSyncRepository: IDataSyncRepository,
    private val application: Application,
    private val internetChecker: IInternetChecker
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(buttonsRepository, dataSyncRepository, application, internetChecker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}