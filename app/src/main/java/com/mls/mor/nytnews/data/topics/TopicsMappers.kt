package com.mls.mor.nytnews.data.topics

import com.mls.mor.nytnews.data.topics.api.LastTopicUpdateResponse
import com.mls.mor.nytnews.data.topics.api.TopicResponse
import com.mls.mor.nytnews.data.topics.api.TopicsResponse
import com.mls.mor.nytnews.data.topics.cache.LastTopicUpdateData
import com.mls.mor.nytnews.data.topics.cache.Story
import com.mls.mor.nytnews.data.topics.cache.StoryEntity
import com.mls.mor.nytnews.utilities.api.parseDateFromString

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
fun TopicResponse.toStoryEntity(topic: String) = StoryEntity(
    id = uri,
    topic = topic.lowercase(),
    subsection = section,
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
fun TopicsResponse.toStoryEntityList(topic: TopicsType): List<StoryEntity> {
    return results.map { it.toStoryEntity(topic.topicName) }
}

// Map LastTopicUpdateResponse to LastTopicUpdateData
fun LastTopicUpdateResponse.toLastTopicUpdateData() = LastTopicUpdateData(
    topic = section,
    lastUpdated = parseDateFromString(last_updated),
    numResults = num_results
)