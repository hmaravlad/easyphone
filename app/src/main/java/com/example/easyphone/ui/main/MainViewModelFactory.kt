package com.example.easyphone.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.ui.button.settings.ButtonSettingsViewModel
import com.example.easyphone.ui.editor.EditorViewModel

class MainViewModelFactory (
    private val dataSource: ButtonsRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}