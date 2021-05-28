package com.example.easyphone.ui.editor

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.ui.button.settings.ButtonSettingsViewModel

class EditorViewModelFactory (
    private val dataSource: ButtonsRepository,
    private val application: Application,
    private val resources: Resources
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            return EditorViewModel(dataSource, application, resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
