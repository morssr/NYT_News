package com.example.mor.nytnews.data.search.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Search")
data class SearchEntity(
    @PrimaryKey
    val id: String,
    val headline: String,
    val abstract: String,
    @ColumnInfo(name = "lead_paragraph")
    val leadParagraph: String,
    @ColumnInfo(defaultValue = ColumnInfo.VALUE_UNSPECIFIED)
    val subsection: String?,
    val byline: String,
    @ColumnInfo(name = "published_date")
    val publishedDate: Date,
    @ColumnInfo(name = "image_url", defaultValue = "")
    val imageUrl: String,
    @ColumnInfo(name = "story_url", defaultValue = "")
    val storyUrl: String
)