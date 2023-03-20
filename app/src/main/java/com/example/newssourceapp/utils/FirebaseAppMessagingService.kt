package com.example.newssourceapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        var notificationUrl = ""
        var notificationUrlOpenType = ""

        Log.d(TAG, "From: ${message.from}")

        // Check if message contains a data payload.
        if (message.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${message.data}")

            val title = if (message.data["title"].isNullOrBlank()) getString(R.string.app_name) else message.data["title"]
            val text = if (message.data["message"].isNullOrBlank()) "" else message.data["message"]
            val rubric = if (message.data["rubric"].isNullOrBlank() && message.data["rubric"]?.isNotBlank() == true) "" else message.data["rubric"]
            val color = message.data["color"].orEmpty()

            if (message.data["url"] != null) {
                notificationUrl = message.data["url"].toString()
                notificationUrlOpenType = "INSIDE"
            }

            val channelId = getString(R.string.default_notification_channel_id)
            sendNotification(
                title.orEmpty(),
                text.orEmpty(),
                rubric.orEmpty(),
                channelId,
                notificationUrl,
                notificationUrlOpenType,
                color
            )
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    private fun sendNotification(
        title: String,
        text: String,
        rubric: String,
        channelId: String,
        notificationUrl: String,
        notificationUrlOpenType: String,
        color: String
    ) {
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        if (notificationUrl.isNotEmpty() && notificationUrlOpenType.isNotEmpty()) {
            intent.putExtra("notificationUrl", notificationUrl)
            intent.putExtra("notificationType", notificationUrlOpenType)
        }
        val randomId = Random().nextInt(9999 - 1000) + 1000

        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            randomId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this.applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(rubric)
            .setContentText(title)
            .setColor(Color.parseColor(color))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(text))
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