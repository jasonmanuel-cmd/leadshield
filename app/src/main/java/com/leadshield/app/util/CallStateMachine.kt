package com.leadshield.app.util

object CallStateMachine {
    fun isMissedCall(
        previousState: Int,
        currentState: String?,
        isIncoming: Boolean,
        ringingState: Int,
        idleState: String
    ): Boolean {
        return isIncoming &&
            previousState == ringingState &&
            currentState == idleState
    }
}
