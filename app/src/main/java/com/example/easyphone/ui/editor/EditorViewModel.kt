package com.example.easyphone.ui.editor

import android.app.Application
import android.content.res.Resources
import android.graphics.Point
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easyphone.R
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.utils.ButtonFreePlaceFinder
import kotlinx.coroutines.*

class EditorViewModel(
    val repository: ButtonsRepository,
    application: Application,
    resources: Resources
) : AndroidViewModel(application) {
    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _buttons = MutableLiveData<List<ProgrammedButton>>(listOf())
    val buttons: LiveData<List<ProgrammedButton>>
        get() = _buttons


    private var currDraggedButton: ProgrammedButton? = null
    val maxColumns = resources.getInteger(R.integer.column_number)
    val maxRows = resources.getInteger(R.integer.row_number)
    var validPoints = if (_buttons.value != null) {
        ButtonFreePlaceFinder.find(1, 1, maxColumns, maxRows, _buttons.value!!)
    } else {
        mutableListOf()
    }


    init {
        updateButtons()
    }

    fun updateButtons() {
        uiScope.launch {
            _buttons.value = repository.getButtons()
        }
    }

    fun setCurrentDraggedButton(button: ProgrammedButton) {
        currDraggedButton = button
        validPoints = if (_buttons.value != null) {
            ButtonFreePlaceFinder.find(button.width, button.height, maxColumns, maxRows, _buttons.value!!)
        } else {
            mutableListOf()
        }
    }

    fun canPlaceButton(point: Point): Boolean {
        return validPoints.find { it.x == point.x && it.y == point.y } != null
    }

    fun saveButtonsNewPosition(point: Point) {
        uiScope.launch {
            repository.updatePosition(currDraggedButton?.id ?: -1, point.x, point.y)
            updateButtons()
        }
    }

    fun onDeleteButtonsWithoutActions() {
        uiScope.launch {
            repository.deleteButtonsWithoutActions()
        }
    }
}