package com.mls.mor.nytnews.ui.common

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mls.mor.nytnews.R

@Composable
fun EmailChooserMenu(
    recipient: String,
    subject: String = "",
    body: String = "",
    onActionNotSupported: () -> Unit = {}
) {
    val context = LocalContext.current

    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        if (recipient.isNotEmpty()) {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        }
        if (subject.isNotEmpty()) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (body.isNotEmpty()) {
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }

    val intent = Intent.createChooser(emailIntent, context.getString(R.string.send_email_using))
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        onActionNotSupported()
    }
}
