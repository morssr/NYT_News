package com.example.mor.nytnews.ui.topics

import com.example.mor.nytnews.data.topics.cache.Story

data class StoryUI(
    val id: String,
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: String,
    val imageUrl: String,
    val storyUrl: String,
    val favorite: Boolean
)

fun Story.toStoryUI(bookmarked: Boolean = false) = StoryUI(
    id = id,
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    publishedDate = publishedDate.toString(),
    imageUrl = imageUrl,
    storyUrl = storyUrl,
    favorite = bookmarked
)
