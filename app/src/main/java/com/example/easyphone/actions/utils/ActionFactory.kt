package com.example.easyphone.actions.utils

import com.example.easyphone.db.entities.ActionArg
import com.example.easyphone.utils.ArgComposer

object ActionFactory {
    fun create(actionName: String, params: List<ActionArg>): ButtonAction {
        val convertedParams = ArgComposer.compose(params)
        val actionData = Actions.data[actionName] ?: throw Error("Wrong action type")
        return actionData.createActionFun(convertedParams)
    }
}