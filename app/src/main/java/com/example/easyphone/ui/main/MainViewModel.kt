package com.example.easyphone.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.repository.ButtonsRepository
import kotlinx.coroutines.*

class ActionAndArgAndButton(
    val button: ProgrammedButton,
    val action: MyAction,
    val args: List<ActionArg>
)

class MainViewModel(
    val repository: ButtonsRepository,
    application: Application
) : AndroidViewModel(application) {
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _buttons = MutableLiveData<List<ActionAndArgAndButton>>(listOf())
    val buttons: LiveData<List<ActionAndArgAndButton>>
        get() = _buttons


    init {
        updateButtons()
    }

    fun updateButtons() {
        uiScope.launch {
            _buttons.value = repository.getButtons().map {
                val action = repository.getActionByButtonId(it.id)
                val args = repository.getActionArgsByButtonId(it.id)
                if (args == null || action == null || it == null) {
                    null
                } else {
                    ActionAndArgAndButton(
                        it,
                        action,
                        args
                    )
                }
            }.filter { it != null }.map { it!! }
        }
    }
}