package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.utils.Constants.CHANNEL_ID
import com.udacity.utils.Constants.CHANNEL_NAME
import com.udacity.utils.Constants.NOTIFICATION_ID

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
    messageBody: String,
    pendingIntent: PendingIntent
) {

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
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(cloudImage)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}