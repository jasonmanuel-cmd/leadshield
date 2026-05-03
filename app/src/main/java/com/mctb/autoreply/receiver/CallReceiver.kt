package com.mctb.autoreply.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.mctb.autoreply.data.LeadShieldSyncManager
import com.mctb.autoreply.util.CallStateMachine
import com.mctb.autoreply.util.SmsHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver that listens for phone state changes to detect missed calls.
 *
 * This receiver is triggered by the PHONE_STATE intent and tracks call state
 * transitions to identify when a call goes from RINGING to IDLE without ever
 * reaching OFFHOOK (answered), which indicates a missed call.
 *
 * When a missed call is detected, it delegates to SmsHandler to send the auto-reply.
 */
@AndroidEntryPoint
class CallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var smsHandler: SmsHandler

    @Inject
    lateinit var leadShieldSync: LeadShieldSyncManager

    companion object {
        private const val TAG = "CallReceiver"

        // Track call state across broadcasts
        private var lastState = TelephonyManager.CALL_STATE_IDLE
        private var isIncoming = false
        private var incomingNumber: String? = null
    }

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Handle a missed call by sending an auto-reply SMS.
     * Uses goAsync() to safely extend the broadcast receiver lifetime for async work.
     */
    private fun handleMissedCall(phoneNumber: String) {
        // goAsync() allows us to keep the process alive for async work
        val pendingResult = goAsync()

        try {
            receiverScope.launch {
                try {
                    // Send auto-reply SMS (existing behavior)
                    smsHandler.processMissedCall(phoneNumber)

                    // Sync missed call to LeadShield command center if customer has operator tier
                    leadShieldSync.syncMissedCallAsync(phoneNumber, null)
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering SmsHandler or sync", e)
            pendingResult.finish()
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        try {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            @Suppress("DEPRECATION")
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            Log.d(TAG, "Phone state changed: $state, Number: $phoneNumber")

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    isIncoming = true
                    incomingNumber = phoneNumber
                    lastState = TelephonyManager.CALL_STATE_RINGING
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    lastState = TelephonyManager.CALL_STATE_OFFHOOK
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    if (CallStateMachine.isMissedCall(
                            previousState = lastState,
                            currentState = state,
                            isIncoming = isIncoming,
                            ringingState = TelephonyManager.CALL_STATE_RINGING,
                            idleState = TelephonyManager.EXTRA_STATE_IDLE
                        )) {
                        incomingNumber?.let { handleMissedCall(it) }
                    }
                    lastState = TelephonyManager.CALL_STATE_IDLE
                    isIncoming = false
                    incomingNumber = null
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException: Missing required permission", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onReceive", e)
        }
    }

}
