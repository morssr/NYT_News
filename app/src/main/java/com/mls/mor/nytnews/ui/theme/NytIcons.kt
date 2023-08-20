package com.mls.mor.nytnews.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

object NytIcons {
    val Home = Icons.Filled.Home
    val HomeOutlined = Icons.Outlined.Home
    val Search = Icons.Filled.Search
    val SearchOutlined = Icons.Outlined.Search
    val Bookmark = Icons.Filled.Bookmarks
    val BookmarkOutlined = Icons.Outlined.Bookmarks
}

/**
 * A sealed class to make dealing with [ImageVector] and [DrawableRes] icons easier.
 */
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
