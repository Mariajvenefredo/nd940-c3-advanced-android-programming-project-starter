package com.udacity.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.models.UrlOption

fun getDownloadStatus(context: Context, id: Long, intentAction: String?): Boolean {
    val downloadManager =
        context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(id)
    val cursor = downloadManager.query(query)

    if (cursor.moveToFirst()) {
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        val succeeded = status == DownloadManager.STATUS_SUCCESSFUL
        return succeeded and
                (intentAction == DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    }
    return false
}

fun download(context: Context, selectedOption: UrlOption): Long {
    val request =
        DownloadManager.Request(Uri.parse(selectedOption.url))
            .setTitle(context.getString(selectedOption.titleResId))
            .setDescription(context.getString(R.string.app_description))
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

    val downloadManager =
        context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
    return downloadManager.enqueue(request)
}