package com.udacity.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.udacity.Constants.NOTIFICATION_ID
import com.udacity.R
import com.udacity.models.DownloadStatus
import com.udacity.models.UrlOption
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var downloadUrl: UrlOption
    private lateinit var downloadStatus: DownloadStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        cancelNotification()

        val receivedStatus = intent.extras?.get(DownloadStatus.BUNDLE_KEY) as Boolean
        downloadStatus = DownloadStatus.getByBoolean(receivedStatus)
        downloadUrl = intent.extras?.get(UrlOption.BUNDLE_KEY) as UrlOption

        fillDetailedInformation()

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun cancelNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun fillDetailedInformation() {
        status_text.text = getString(downloadStatus.statusResId)

        val color = ResourcesCompat.getColor(resources, downloadStatus.answerColor, null)
        status_text.setTextColor(color)

        filename_text.text = getString(downloadUrl.titleResId)
    }
}
