package com.example.easyphone.ui.button.settings

import android.app.Application
import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import androidx.lifecycle.*
import com.example.easyphone.R
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.utils.ButtonFreePlaceFinder
import kotlinx.coroutines.*

class ButtonSettingsViewModel(
    val repository: ButtonsRepository,
    application: Application,
    val resources: Resources
) : AndroidViewModel(application) {
    val colors = mapOf(
        "Green" to "#2e7d32",
        "Red" to "#b71c1c",
        "Blue" to "#283593",
        "Purple" to "#6a1b9a",
        "Brown" to "#4e342e",
        "Black" to "#424242"
    )

    val defaultColor = Color.parseColor("#2e7d32")

    private var viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //private val buttons = MutableLiveData<List<ProgrammedButton>>()
    private val buttons = MutableLiveData<List<ProgrammedButton>>(listOf())

    private val displayMetrics = DisplayMetrics()
    private val _buttonText = MutableLiveData("Text")
    private val _buttonLengthInColumns = MutableLiveData(1)
    private val _buttonHeightInRows = MutableLiveData(1)
    private val _color = MutableLiveData<Int>(Color.parseColor(colors["Green"]) ?: defaultColor)
    private val _colorName = MutableLiveData("Green")
    private val _action = MutableLiveData("None")
    private val _isExistingButton = MutableLiveData<Boolean>(false)
    private var _moveBackEvent = MutableLiveData<Boolean>(false)
    private var _moveToActionEditEvent = MutableLiveData<Boolean>(false)
    private var _haveErrorsEvent = MutableLiveData<Boolean>(false)
    private var _createActionChooserEvent = MutableLiveData<Boolean>(false)

    private var submitted: Boolean = false
    private var _currentButtonId: Int = -1
    private var _errorMessage: String = ""
    private var currentButtonColumn: Int = 0
    private var currentButtonRow: Int = 0
    private var currentButtonWidth: Int = 1
    private var currentButtonHeight: Int = 1
    private var errors = mutableMapOf<String, Boolean>()


    val buttonText: LiveData<String>
        get() = _buttonText
    val buttonLengthInColumns: LiveData<Int>
        get() = _buttonLengthInColumns
    val buttonHeightInRows: LiveData<Int>
        get() = _buttonHeightInRows
    val color: LiveData<Int>
        get() = _color
    val colorName: LiveData<String>
        get() = _colorName
    val action: LiveData<String>
        get() = _action
    val isExistingButton: LiveData<Boolean>
        get() = _isExistingButton
    val moveBackEvent: LiveData<Boolean>
        get() = _moveBackEvent
    val moveToActionEditEvent: LiveData<Boolean>
        get() = _moveToActionEditEvent
    val haveErrorsEvent: LiveData<Boolean>
        get() = _haveErrorsEvent
    val createActionChooserEvent: LiveData<Boolean>
        get() = _createActionChooserEvent
    val currentButtonId: Int
        get() = _currentButtonId


    init {
        updateButtons()
    }

    fun updateButtons() {
        uiScope.launch {
            buttons.value = repository.getButtons()
        }
    }

    fun onErrorStatusChange(field: String, status: Boolean) {
        errors[field] = status
    }

    fun getCurrentErrorMessage(): String {
        return _errorMessage
    }

    fun onSubmit() {
        if (submitted) {
            if (!_isExistingButton.value!!) {
                _createActionChooserEvent.value = true
            }
            return
        }

        uiScope.launch {
            if (errors.values.fold(false, { acc, b -> acc || b })) {
                _errorMessage = resources.getString(R.string.free_place_error)
                _haveErrorsEvent.value = true
                return@launch
            }

            val buttonsValue = repository.getButtons().filter { it.id != _currentButtonId }

            val tableHeight = resources.getInteger(R.integer.row_number)
            val tableWidth = resources.getInteger(R.integer.column_number)

            val width = _buttonLengthInColumns.value ?: 1
            val height = _buttonHeightInRows.value ?: 1

            val sizeNotChanged = width == currentButtonWidth && height == currentButtonHeight

            val positions =
                ButtonFreePlaceFinder.find(width, height, tableWidth, tableHeight, buttonsValue)

            if (positions.size == 0) {
                _errorMessage = resources.getString(R.string.free_place_error)
                _haveErrorsEvent.value = true
                return@launch
            }

            val pos = positions[0]

            val button = ProgrammedButton(
                id = if (_isExistingButton.value!!) {
                    _currentButtonId
                } else {
                    0
                },
                text = _buttonText.value ?: "Button",
                color = _color.value ?: defaultColor,
                colorName = _colorName.value ?: "Green",
                width = width,
                height = height,
                column = if (_isExistingButton.value!! && sizeNotChanged) {
                    currentButtonColumn
                } else {
                    pos.y
                },
                row = if (_isExistingButton.value!! && sizeNotChanged) {
                    currentButtonRow
                } else {
                    pos.x
                }
            )
            if (_isExistingButton.value!!) {
                repository.updateButton(button)
                _moveBackEvent.value = true
            } else {
                val ids = repository.insertButton(button)
                if (ids.size > 0) _currentButtonId = ids[0].toInt()
                _createActionChooserEvent.value = true
            }
        }
    }

    fun onTextChange(text: String) {
        _buttonText.value = text
    }

    fun onLengthChange(value: Int) {
        _buttonLengthInColumns.value = value
    }

    fun onHeightChange(value: Int) {
        _buttonHeightInRows.value = value
    }

    fun onColorChange(color: String) {
        _colorName.value = color
        _color.value = Color.parseColor(colors[color]) ?: defaultColor
    }

    fun onDeleteCurrentButton() {
        uiScope.launch {
            repository.deleteButton(_currentButtonId)
            _moveBackEvent.value = true
        }
    }

    fun updateCurrentButton(id: Int) {
        if (id < 0) {
            _isExistingButton.value = false
        } else {
            _currentButtonId = id
            _isExistingButton.value = true
            uiScope.launch {
                val button = repository.getButton(id)
                currentButtonColumn = button.column
                currentButtonRow = button.row
                currentButtonHeight = button.height
                currentButtonWidth = button.width

                _buttonText.value = button.text
                _buttonHeightInRows.value = button.height
                _buttonLengthInColumns.value = button.width
                _color.value = button.color
                _colorName.value = button.colorName
            }
        }
    }

    fun onMoveBackComplete() {
        _moveBackEvent.value = false
    }

    fun onActionChange(action: String) {
        _action.value = action
        _moveToActionEditEvent.value = true
    }

    fun onActionChangeComplete() {
        _moveToActionEditEvent.value = false
    }

    fun onHaveErrorsEventComplete() {
        _haveErrorsEvent.value = false
    }

    fun onCreateActionChooserEventComplete() {
        _createActionChooserEvent.value = false
    }
}