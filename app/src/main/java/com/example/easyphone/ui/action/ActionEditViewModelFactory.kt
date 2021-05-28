package com.example.easyphone.ui.action

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.ui.main.MainViewModel

class ActionEditViewModelFactory (
    private val dataSource: ButtonsRepository,
    private val application: Application,
    private val buttonId: Int,
    private val actionType: String
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActionEditViewModel::class.java)) {
            return ActionEditViewModel(dataSource, application, buttonId, actionType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}