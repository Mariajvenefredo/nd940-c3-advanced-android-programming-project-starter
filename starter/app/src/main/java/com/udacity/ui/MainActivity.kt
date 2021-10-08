package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.Constants.NOTIFICATION_ID
import com.udacity.R
import com.udacity.models.DownloadStatus
import com.udacity.models.UrlOption
import com.udacity.utils.createCustomChannel
import com.udacity.utils.download
import com.udacity.utils.getDownloadStatus
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var notificationManager: NotificationManager

    lateinit var detailIntent: Intent

    private var downloadID: Long = 0

    private lateinit var selectedUrl: UrlOption

    private lateinit var pendingIntent: PendingIntent

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

        custom_button.setOnClickListener {
            handleDownloadClick()
        }
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

            downloadID = download(this, selectedOption)
            selectedUrl = selectedOption
        }
    }

    private fun createRadioButtons() =
        UrlOption.ALL.forEach { option ->
            val radioButton = RadioButton(this.applicationContext)

            radioButton.apply {
                id = option.id
                text = applicationContext.getText(option.titleResId)
                setTextColor(resources.getColor(R.color.darkMint, null))
                textSize = resources.getDimension(R.dimen.optionsTextSize)
            }
            radio_group.addView(radioButton)
        }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val intentAction = intent.action
                val isSuccess = getDownloadStatus(this@MainActivity, id, intentAction)
                val urlTitle = getShortTitle(context)

                detailIntent.putExtra(UrlOption.BUNDLE_KEY, selectedUrl)
                detailIntent.putExtra(DownloadStatus.BUNDLE_KEY, isSuccess)

                custom_button.stateDownloaded()

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

    private fun getShortTitle(context: Context?): String {
        val titleResId = selectedUrl.shortTitleResId
        return context?.getString(titleResId) ?: ""
    }

    companion object {
        private const val NO_SELECTED_VALUE = -1
    }
}
