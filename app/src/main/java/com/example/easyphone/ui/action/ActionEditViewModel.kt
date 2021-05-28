package com.example.easyphone.ui.action

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easyphone.actions.ActionData
import com.example.easyphone.actions.utils.Actions
import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.db.entities.MyAction
import com.example.easyphone.repository.ButtonsRepository
import kotlinx.coroutines.*

class ActionEditViewModel(
    val repository: ButtonsRepository,
    application: Application,
    val buttonId: Int,
    val actionType: String
) : AndroidViewModel(application) {
    private var viewModelJob = Job()
    private var args: MutableMap<String, String> = mutableMapOf()

    private var _isActionChooseApp = MutableLiveData(actionType == "app")
    private var _toEditorEvent = MutableLiveData(false)
    private var _setAppEvent = MutableLiveData(false)
    private var _needPermissionEvent = MutableLiveData(false)


    val isActionChooseApp: LiveData<Boolean>
        get() = _isActionChooseApp
    val toEditorEvent: LiveData<Boolean>
        get() = _toEditorEvent
    val setAppEvent: LiveData<Boolean>
        get() = _setAppEvent
    val needPermissionEvent: LiveData<Boolean>
        get() = _needPermissionEvent



    init {
        _isActionChooseApp.value = actionType == "app"
        if (actionType != "app") {
            val actionData = getActionData()
            actionData.args.map {
                onArgChange(it.name, "")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onToEditorCompleted() {
        _toEditorEvent.value = false
    }

    fun onSetAppComleted() {
        _setAppEvent.value = false
    }

    fun needPermissionEventCompleted() {
        _needPermissionEvent.value = false
    }


    fun getActionData(): ActionData {
        return Actions.data[actionType] ?: throw Error("Wrong action type")
    }

    fun onArgChange(argName: String, argValue: String) {
        Log.d("MY_DEBUG", "key: ${argName}, value: ${argValue}")
        checkLocation(argName, argValue)
        args[argName] = argValue
    }

    fun checkLocation(argName: String, argValue: String) {
        if (argName == "text" && argValue.contains(Regex("(\\$\\{location\\})|(\\\$\\{coordinates\\})"))) {
            _needPermissionEvent.value = true
        }
    }


    fun onSetApp() {
        _setAppEvent.value = true
    }

    fun onAppArgChange(packageName: String) {
        args["app"] = packageName
    }

    fun onSubmit() {
        uiScope.launch {
            val action = MyAction(
                id = 0,
                type = actionType,
                buttonId = buttonId
            )

            val id = repository.insertAction(action)[0]

            val actionArgs = args.map {
                ActionArg(
                    id = 0,
                    actionId = id.toInt(),
                    type = it.key,
                    value = it.value
                )
            }

            actionArgs.forEach {
                repository.insertActionArg(it)
            }

            _toEditorEvent.value = true
        }
    }

    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
}