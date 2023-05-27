package com.example.mor.nytnews.data.topics

import com.example.mor.nytnews.data.topics.api.LastTopicUpdateResponse
import com.example.mor.nytnews.data.topics.api.TopicResponse
import com.example.mor.nytnews.data.topics.api.TopicsResponse
import com.example.mor.nytnews.data.topics.cache.LastTopicUpdateData
import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.data.topics.cache.StoryEntity
import com.example.mor.nytnews.utilities.api.parseDateFromString

// Map StoryEntity to Story
fun StoryEntity.toStory() = Story(
    id = id,
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    publishedDate = publishedDate,
    imageUrl = imageUrl,
    storyUrl = storyUrl
)

// Map List<StoryEntity> to List<Story>
fun List<StoryEntity>.toStoryList() = map { it.toStory() }

// Map TopicResponse to StoryEntity
fun TopicResponse.toStoryEntity(section: String) = StoryEntity(
    id = uri,
    section = section.lowercase(),
    subsection = subsection,
    title = title,
    abstract = abstract,
    byline = byline,
    itemType = item_type,
    publishedDate = parseDateFromString(published_date),
    createdDate = parseDateFromString(created_date),
    updatedDate = parseDateFromString(updated_date),
    imageUrl = multimedia?.find { it.height in 300..500 }?.url ?: "",
    storyUrl = url
)

// Map List<TopicResponse> to List<StoryEntity>
fun TopicsResponse.toStoryEntityList(): List<StoryEntity> {
    return results.map { it.toStoryEntity(section) }
}

// Map LastTopicUpdateResponse to LastTopicUpdateData
fun LastTopicUpdateResponse.toLastTopicUpdateData() = LastTopicUpdateData(
    topic = section,
    lastUpdated = parseDateFromString(last_updated),
    numResults = num_results
)