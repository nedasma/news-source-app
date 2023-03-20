package com.example.newssourceapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newssourceapp.R
import com.example.newssourceapp.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random

private const val TAG = "FirebaseAppMessagingService"

/**
 * A helper class for handling everything related to the Firebase Cloud Messaging service.
 */
class FirebaseAppMessagingService : FirebaseMessagingService() {

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        // Send token to the server (if there's one available)
       // sendRegistrationToServer(token)
    }

    /**
     * This is called when the FCM message is received while the app is in the foreground. The [message]
     * can contain some data payload in order to customise the notification or just a simple notification
     * to be displayed in the notification tray.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "From: ${message.from}")
        val channelId = getString(R.string.default_notification_channel_id)

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")

            val title = if (message.data["title"].isNullOrBlank()) getString(R.string.app_name) else message.data["title"]
            val text = if (message.data["message"].isNullOrBlank()) "" else message.data["message"]

            sendNotification(
                title.orEmpty(),
                text.orEmpty(),
                channelId,
            )
        } else if (message.notification != null) {
            // Check if message contains a notification payload.
            val notification = message.notification

            Log.d(TAG, "Message Notification Body: ${notification?.body}")

            sendNotification(
                notification?.title.orEmpty(),
                notification?.body.orEmpty(),
                channelId,
            )
        }
    }

    /**
     * Sends the notification through the notification channel (by provided its [channelId]).
     * The notification contains a [title] and some [text] - all other custom parameters are set as
     * default.
     */
    private fun sendNotification(
        title: String,
        text: String,
        channelId: String,
    ) {
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val randomId = Random().nextInt(9999 - 1000) + 1000

        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            randomId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this.applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        notificationBuilder.flags = notificationBuilder.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Test Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(randomId, notificationBuilder)
    }
}