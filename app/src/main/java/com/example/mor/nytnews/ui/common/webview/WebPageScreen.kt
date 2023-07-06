package com.example.mor.nytnews.ui.common.webview

import android.view.LayoutInflater
import android.webkit.WebView
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mor.nytnews.R

private const val TAG = "WebPageScreen"

@Composable
fun WebViewRoute(
    modifier: Modifier = Modifier,
    url: String,
    title: String = "",
    onBackClick: () -> Unit = {}
) {
    WebPageScreen(
        modifier = modifier,
        url = url,
        title = title,
        onBackClick = onBackClick
    )
}

@Composable
private fun WebPageScreen(
    modifier: Modifier = Modifier,
    url: String,
    title: String = "",
    onBackClick: () -> Unit = {}
) {
    AndroidView(factory = { context ->
        LayoutInflater.from(context).inflate(R.layout.web_view_collapsing_toolbar, null).apply {
            findViewById<WebView>(R.id.webview).apply {
                loadUrl(url)
            }
            findViewById<Toolbar>(R.id.toolbar).apply {
                setTitle(title)
                setNavigationOnClickListener { onBackClick() }
            }
        }
    }, modifier = modifier)
}

// For displaying preview in
// the Android Studio IDE emulator
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WebPageScreen(url = "https://www.google.com/")
}