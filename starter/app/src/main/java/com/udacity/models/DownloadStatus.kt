package com.udacity.models

import com.udacity.R

enum class DownloadStatus(val success: Boolean, val statusResId: Int, val answerColor: Int) {
    FAILED(false, R.string.download_status_failed, R.color.pinkRed),
    SUCCESS(true, R.string.download_status_success, R.color.darkMint);

    companion object {
        const val BUNDLE_KEY = "DOWNLOAD_STATUS"

        fun getByBoolean(value: Boolean): DownloadStatus {
            return if (value) SUCCESS else FAILED
        }
    }
}