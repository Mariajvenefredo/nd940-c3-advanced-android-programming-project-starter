package com.udacity.models

import com.udacity.R

enum class UrlOption(val id: Int, val titleResId: Int, val shortTitleResId: Int, val url: String) {
    UDACITY(
        R.id.udacity_id,
        R.string.udacity_button_title,
        R.string.udacity_short_title,
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
    ),
    GLIDE(
        R.id.glide_id,
        R.string.glide_button_title,
        R.string.glide_short_title,
        "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    ),
    RETROFIT(
        R.id.retrofit_id,
        R.string.retrofit_button_title,
        R.string.retrofit_short_title,
        "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    );

    companion object {

        fun getById(selectedValue: Int): UrlOption {
            return ALL.first { url -> selectedValue == url.id }
        }

        val ALL = listOf(UDACITY, GLIDE, RETROFIT)
        const val BUNDLE_KEY = "URL_OPTION"
    }
}