package com.mls.mor.nytnews.ui.topics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContactSupport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.ui.theme.customThemeAttributes


@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    showMainMenu: Boolean = false,
    onDismissMenu: () -> Unit = {},
    onLogoClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
    ) {
        IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = onLogoClick) {
            Image(
                painter = painterResource(id = MaterialTheme.customThemeAttributes.appLogoResId),
                contentDescription = "app logo",
            )
        }

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = "main menu")
            }

            DropdownMenu(expanded = showMainMenu, onDismissRequest = onDismissMenu) {

                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.settings)) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = "settings")
                    },
                    onClick = {
                        onDismissMenu()
                        onSettingClick()
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.about)) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "about us")
                    },
                    onClick = {
                        onDismissMenu()
                        onAboutUsClick()
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.contact_us)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ContactSupport,
                            contentDescription = "contact us"
                        )
                    },
                    onClick = {
                        onDismissMenu()
                        onContactUsClick()
                    },
                )
            }
        }
    }
}