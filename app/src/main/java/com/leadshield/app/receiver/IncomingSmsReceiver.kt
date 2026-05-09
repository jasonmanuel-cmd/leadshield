package com.leadshield.app.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.leadshield.app.data.AiConversationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver for incoming SMS messages.
 *
 * When an SMS arrives from a number that has an active AI conversation (started
 * within the last 4 hours from a missed-call auto-reply), this receiver passes
 * the message to [AiConversationManager] and sends the generated reply automatically.
 *
 * The broadcast is aborted after handling so the default SMS app does not show
 * a notification for the auto-handled reply.
 *
 * Requires: android.permission.RECEIVE_SMS
 */
@AndroidEntryPoint
class IncomingSmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var aiConversationManager: AiConversationManager

    companion object {
        private const val TAG = "IncomingSmsReceiver"
    }

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        // Parse incoming messages
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        // All parts of a multi-part SMS share the same originating address
        val senderNumber = messages[0].originatingAddress ?: return
        val messageBody = messages.joinToString(separator = "") { it.messageBody ?: "" }

        if (messageBody.isBlank()) return

        Log.d(TAG, "Incoming SMS from $senderNumber")

        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                val result = aiConversationManager.handleIncomingReply(senderNumber, messageBody)
                if (result != null) {
                    Log.d(TAG, "Sending AI reply to $senderNumber (urgent=${result.isUrgent})")
                    sendSms(context, senderNumber, result.replyText)
                    // Abort so the system SMS app doesn't show a notification for this exchange
                    pendingResult.abortBroadcast()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling incoming SMS", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "SEND_SMS permission not granted")
                return
            }

            val smsManager = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            }

            Log.i(TAG, "AI reply sent to $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send AI reply SMS to $phoneNumber", e)
        }
    }
}
