package com.example.mor.nytnews.data.topics

const val TOPICS_PREFERENCES_FILE_NAME = "topics_preferences"
const val FAVORITE_TOPICS_PREFERENCES_KEY = "favorite_topics"
const val TOPICS_LAST_UPDATE_PREFERENCES_FILE_NAME = "topics_last_update_pref"

val defaultTopics =
    listOf(TopicsType.HOME, TopicsType.TECHNOLOGY, TopicsType.POLITICS, TopicsType.SPORTS)
