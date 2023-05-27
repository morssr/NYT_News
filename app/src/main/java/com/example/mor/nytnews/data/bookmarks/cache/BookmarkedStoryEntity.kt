package com.example.mor.nytnews.data.bookmarks.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bookmarks")
data class BookmarkedStoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(defaultValue = "")
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    @ColumnInfo(name = "published_date")
    val publishedDate: Date,
    @ColumnInfo(name = "bookmarked_date")
    val bookmarkedDate: Date,
    @ColumnInfo(name = "image_url", defaultValue = "")
    val imageUrl: String,
    @ColumnInfo(name = "story_url", defaultValue = "")
    val storyUrl: String
)