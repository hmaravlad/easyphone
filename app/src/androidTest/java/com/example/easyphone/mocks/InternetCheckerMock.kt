package com.example.easyphone.mocks

import com.example.easyphone.utils.IInternetChecker

class InternetCheckerMock : IInternetChecker {
    private var state = true
    fun setTrue() {
        state = true
    }

    fun setFalse() {
        state = false
    }

    override fun isOnline(): Boolean {
        return state
    }
}