/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mor.nytnews.ui

import com.example.mor.nytnews.R
import com.example.mor.nytnews.ui.theme.Icon
import com.example.mor.nytnews.ui.theme.NytIcons


/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    TOPICS(
        selectedIcon = Icon.ImageVectorIcon(NytIcons.Home),
        unselectedIcon = Icon.ImageVectorIcon(NytIcons.HomeOutlined),
        iconTextId = R.string.topics,
        titleTextId = R.string.app_name,
    ),
    SEARCH(
        selectedIcon = Icon.ImageVectorIcon(NytIcons.Search),
        unselectedIcon = Icon.ImageVectorIcon(NytIcons.SearchOutlined),
        iconTextId = R.string.search,
        titleTextId = R.string.search,
    ),
    BOOKMARKS(
        selectedIcon = Icon.ImageVectorIcon(NytIcons.Bookmark),
        unselectedIcon = Icon.ImageVectorIcon(NytIcons.BookmarkOutlined),
        iconTextId = R.string.bookmarks,
        titleTextId = R.string.bookmarks,
    ),
}
