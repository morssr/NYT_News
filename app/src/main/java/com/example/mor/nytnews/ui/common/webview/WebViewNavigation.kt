package com.example.mor.nytnews.ui.common.webview

import android.util.Log
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.example.mor.nytnews.ui.common.NavArgumentsConstants
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val TAG = "WebViewNavigation"

const val articleRoute = "articleRoute/{${NavArgumentsConstants.URL_KEY}}/{${NavArgumentsConstants.TITLE_KEY}}"

fun NavHostController.navigateToArticle(url: String, title: String, navOptions: NavOptions? = null) {
    Log.v(TAG, "navigateToArticle() called with: url = $url, title = $title")
    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    this.navigate("articleRoute/$encodedUrl/$title", navOptions)
}