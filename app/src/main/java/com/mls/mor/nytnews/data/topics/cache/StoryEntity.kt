package com.mls.mor.nytnews.data.topics.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Stories")
data class StoryEntity(
    @PrimaryKey
    val id: String,
    val topic: String,
    @ColumnInfo(defaultValue = "")
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    @ColumnInfo(name = "item_type")
    val itemType: String,
    @ColumnInfo(name = "published_date")
    val publishedDate: Date,
    @ColumnInfo(name = "created_date")
    val createdDate: Date,
    @ColumnInfo(name = "updated_date")
    val updatedDate: Date,
    @ColumnInfo(name = "image_url", defaultValue = "")
    val imageUrl: String,
    @ColumnInfo(name = "story_url", defaultValue = "")
    val storyUrl: String
)

