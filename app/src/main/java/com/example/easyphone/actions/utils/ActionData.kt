package com.example.easyphone.actions

import com.example.easyphone.actions.utils.ActionArgData
import com.example.easyphone.actions.utils.ButtonAction


class ActionData(
    val name: String,
    val args: List<ActionArgData>,
    val createActionFun: (args: Map<String, String>) -> ButtonAction
)