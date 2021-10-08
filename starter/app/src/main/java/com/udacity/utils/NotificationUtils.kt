package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.Constants.CHANNEL_ID
import com.udacity.Constants.CHANNEL_NAME
import com.udacity.Constants.NOTIFICATION_ID

fun createCustomChannel(
    context: Context,
    notificationManager: NotificationManager
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(true)
            }
        notificationChannel.enableVibration(true)

        notificationChannel.description =
            context.getString(R.string.download_channel_description)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.sendNotification(
    context: Context,
    pendingIntent: PendingIntent,
    success: Boolean,
    urlTitle: String = ""
) {
    val description = if (success) {
        context.getString(R.string.notification_description_success, urlTitle)
    } else {
        context.getString(R.string.notification_description_failed, urlTitle)
    }

    val cloudImage = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.cloud_image
    )

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(cloudImage)
        .bigLargeIcon(null)

    val builder = NotificationCompat.Builder(
        context,
        CHANNEL_ID
    )
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(description)
        .setContentIntent(pendingIntent)
        .setStyle(bigPicStyle)
        .setSmallIcon(R.drawable.cloud_image)
        .setLargeIcon(cloudImage)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}