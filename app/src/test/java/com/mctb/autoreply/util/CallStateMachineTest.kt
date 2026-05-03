package com.mctb.autoreply.util
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CallStateMachineTest {

    @Test
    fun `ringing to idle incoming is a missed call`() {
        val result = CallStateMachine.isMissedCall(
            previousState = 1,
            currentState = "IDLE",
            isIncoming = true,
            ringingState = 1,
            idleState = "IDLE"
        )
        assertTrue(result)
    }

    @Test
    fun `offhook to idle is not a missed call`() {
        val result = CallStateMachine.isMissedCall(
            previousState = 2,
            currentState = "IDLE",
            isIncoming = true,
            ringingState = 1,
            idleState = "IDLE"
        )
        assertFalse(result)
    }
}
