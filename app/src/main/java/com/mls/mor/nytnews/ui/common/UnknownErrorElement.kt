package com.mls.mor.nytnews.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NetworkCheck
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mls.mor.nytnews.R

@Composable
fun UnknownErrorElement(
    modifier: Modifier = Modifier,
    onActionButtonClick: () -> Unit = {},
) {
    GenericErrorScreen(
        modifier = modifier,
        icon = Icons.Rounded.NetworkCheck,
        title = stringResource(R.string.something_went_wrong),
        description = stringResource(R.string.please_try_again_later),
        actionButtonText = stringResource(id = R.string.try_again),
        onActionButtonClick = onActionButtonClick
    )
}