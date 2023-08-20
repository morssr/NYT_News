package com.mls.mor.nytnews.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.ui.linkToWebpage

@Composable
fun DisclaimerDialog(
    modifier: Modifier = Modifier,
    onDismiss: (dontShowAgain: Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var dontShowAgain by rememberSaveable { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss(dontShowAgain) }) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.welcome_greeting),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.NYT_credits),
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { linkToWebpage(context) }
                ) {
                    Text(text = stringResource(R.string.go_to_new_york_times_website))

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(imageVector = Icons.Outlined.Web, contentDescription = null)
                }


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.app_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dontShowAgain = !dontShowAgain },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Checkbox(checked = dontShowAgain, onCheckedChange = { dontShowAgain = it })

                    Text(
                        text = stringResource(R.string.dont_show_again),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onDismiss(dontShowAgain) }) {
                    Text(text = stringResource(R.string.i_understand))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}