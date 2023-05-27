package com.example.mor.nytnews.data.bookmarks.cache

import com.example.mor.nytnews.data.topics.cache.Story
import java.util.Date

//map BookmarkedStoryEntity to BookmarkedStory
fun BookmarkedStoryEntity.toBookmarkedStory() = BookmarkedStory(
    id = id,
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    publishedDate = publishedDate,
    bookmarkedDate = bookmarkedDate,
    imageUrl = imageUrl,
    storyUrl = storyUrl
)

//map List<BookmarkedStoryEntity> to List<BookmarkedStory>
fun List<BookmarkedStoryEntity>.toBookmarkedStoryList() = map { it.toBookmarkedStory() }

//map BookmarkedStory to BookmarkedStoryEntity
fun BookmarkedStory.toBookmarkedStoryEntity() = BookmarkedStoryEntity(
    id = id,
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    publishedDate = publishedDate,
    bookmarkedDate = bookmarkedDate,
    imageUrl = imageUrl,
    storyUrl = storyUrl
)

//map List<BookmarkedStory> to List<BookmarkedStoryEntity>
fun List<BookmarkedStory>.toBookmarkedStoryEntityList() = map { it.toBookmarkedStoryEntity() }

//map Story to BookmarkedStory
fun Story.toBookmarkedStory() = BookmarkedStory(
    id = id,
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    publishedDate = publishedDate,
    bookmarkedDate = Date(),
    imageUrl = imageUrl,
    storyUrl = storyUrl
)
