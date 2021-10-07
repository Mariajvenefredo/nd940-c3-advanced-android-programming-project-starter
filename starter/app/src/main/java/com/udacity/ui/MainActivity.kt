package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.utils.Constants.NOTIFICATION_ID
import com.udacity.utils.createCustomChannel
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val notificationManager: NotificationManager =
        getSystemService(NotificationManager::class.java)
    private val detailIntent = Intent(applicationContext, DetailActivity::class.java)

    private var downloadID: Long = 0
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createCustomChannel(applicationContext, notificationManager)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createRadioButtons()
    }

    private fun handleDownloadClick() {
        val selectedValue = radio_group.checkedRadioButtonId
        if (selectedValue == -1) {
            Toast
                .makeText(applicationContext, R.string.no_url_selected, Toast.LENGTH_LONG)
                .show()

        } else {
            custom_button.stateClicked()
            val downloadInfo = URL_LIST[selectedValue]

            if (downloadInfo != null) {
                download(downloadInfo)
            }
        }
    }

    private fun createRadioButtons() {
        URL_LIST.forEach { (id, title_url) ->
            val radioButton = RadioButton(this.applicationContext)

            radioButton.id = id
            radioButton.text = applicationContext.getText(title_url.first)
            radioButton.setTextColor(resources.getColor(R.color.darkMint, null))
            radioButton.textSize = resources.getDimension(R.dimen.optionsTextSize)

            radio_group.addView(radioButton)
        }

        custom_button.setOnClickListener {
            handleDownloadClick()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                custom_button.stateDownloaded()

                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                notificationManager.sendNotification(
                    applicationContext,
                    "this is the message",
                    pendingIntent
                )
            }
        }
    }

    private fun download(downloadInfo: Pair<Int, String>) {
        val request =
            DownloadManager.Request(Uri.parse(downloadInfo.second))
                .setTitle(getString(downloadInfo.first))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val URL_PROJECT =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private val URL_LIST = mapOf(
            0 to Pair(R.string.udacity_button_title, URL_PROJECT),
            1 to Pair(R.string.glide_button_title, URL_GLIDE),
            2 to Pair(R.string.retrofit_button_title, URL_RETROFIT)
        )
        private const val CHANNEL_ID = "channelId"
    }

}
