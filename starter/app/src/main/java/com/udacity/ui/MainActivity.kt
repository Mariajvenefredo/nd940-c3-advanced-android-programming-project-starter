package com.udacity.ui

import android.app.DownloadManager
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
import androidx.core.content.ContextCompat
import com.udacity.DownloadStatus
import com.udacity.R
import com.udacity.UrlOption
import com.udacity.utils.Constants.NOTIFICATION_ID
import com.udacity.utils.createCustomChannel
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager

    lateinit var detailIntent: Intent

    private var downloadID: Long = 0
    private lateinit var selectedUrl: UrlOption

    private lateinit var pendingIntent: PendingIntent
    //private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
        detailIntent = Intent(this, DetailActivity::class.java)
        createCustomChannel(applicationContext, notificationManager)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createRadioButtons()
    }

    private fun handleDownloadClick() {
        val selectedValue = radio_group.checkedRadioButtonId
        if (selectedValue == NO_SELECTED_VALUE) {
            Toast
                .makeText(applicationContext, R.string.no_url_selected, Toast.LENGTH_LONG)
                .show()

        } else {
            custom_button.stateClicked()
            val selectedOption = UrlOption.getById(selectedValue)

            download(selectedOption)
        }
    }

    private fun createRadioButtons() {
        UrlOption.ALL.forEach { option ->
            val radioButton = RadioButton(this.applicationContext)

            radioButton.id = option.id
            radioButton.text = applicationContext.getText(option.titleResId)
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
                val intentAction = intent.action
                val isSuccess = getDownloadStatus(context, id)
                val urlTitle = getShortTitle(context)

                detailIntent.putExtra(UrlOption.BUNDLE_KEY, selectedUrl)
                custom_button.stateDownloaded()

                if (isSuccess and intentAction.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    detailIntent.putExtra(DownloadStatus.BUNDLE_KEY, true)

                } else {
                    detailIntent.putExtra(DownloadStatus.BUNDLE_KEY, false)
                }

                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                notificationManager.sendNotification(
                    applicationContext,
                    pendingIntent,
                    isSuccess,
                    urlTitle
                )
            }
        }
    }

    private fun getDownloadStatus(context: Context?, id: Long): Boolean {
        val downloadManager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(id)
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            return status == DownloadManager.STATUS_SUCCESSFUL
        }
        return false
    }

    private fun getShortTitle(context: Context?): String {
        val titleResId = selectedUrl.shortTitleResId
        return context?.getString(titleResId) ?: ""
    }

    private fun download(selectedOption: UrlOption) {
        val request =
            DownloadManager.Request(Uri.parse(selectedOption.url))
                .setTitle(getString(selectedOption.titleResId))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        selectedUrl = selectedOption
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val NO_SELECTED_VALUE = -1
        private const val CHANNEL_ID = "channelId"
    }
}
