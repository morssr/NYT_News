package com.mls.mor.nytnews.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.mls.mor.nytnews.R

fun linkToWebpage(context: Context) {
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.data = Uri.parse(context.getString(R.string.nyt_website_url))
    ContextCompat.startActivity(context, openURL, null)
}