package com.mls.mor.nytnews.data.popular.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mls.mor.nytnews.data.popular.common.PopularType
import java.util.Date

@Entity(tableName = "popular")
data class PopularEntity(
    @PrimaryKey
    val id: String,
    val type: PopularType,
    val title: String,
    val abstract: String,
    val byline: String,
    val section: String,
    @ColumnInfo(name = "published_date")
    val publishedDate: Date,
    @ColumnInfo(name = "image_url", defaultValue = "")
    val imageUrl: String,
    @ColumnInfo(name = "story_url", defaultValue = "")
    val storyUrl: String
)