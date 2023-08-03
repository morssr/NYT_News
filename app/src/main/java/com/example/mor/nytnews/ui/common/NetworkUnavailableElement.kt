package com.example.mor.nytnews.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mor.nytnews.R

@Composable
fun NetworkUnavailableElement(
    modifier: Modifier = Modifier,
    onActionButtonClick: () -> Unit = {},
) {
    GenericErrorScreen(
        modifier = modifier,
        icon = Icons.Rounded.NetworkCheck,
        title = stringResource(R.string.no_internet_connection),
        description = stringResource(R.string.please_check_your_internet_connection_and_try_again),
        actionButtonText = stringResource(R.string.try_again),
        onActionButtonClick = onActionButtonClick
    )
}