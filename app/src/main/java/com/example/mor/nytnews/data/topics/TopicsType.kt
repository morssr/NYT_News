package com.example.mor.nytnews.data.topics

enum class TopicsType(val topicName: String) {
    HOME("home"),
    POLITICS("politics"),
    BOOKS("books"),
    HEALTH("health"),
    TECHNOLOGY("technology"),
    AUTOMOBILES("automobiles"),
    ARTS("arts"),
    SCIENCE("science"),
    FASHION("fashion"),
    SPORTS("sports"),
    BUSINESS("business"),
    FOOD("food"),
    TRAVEL("travel"),
    INSIDER("insider"),
    MAGAZINE("magazine"),
    MOVIES("movies"),
    NATIONAL("national"),
    NYREGION("nyregion"),
    OBITUARIES("obituaries"),
    OPINION("opinion"),
    REALESTATE("realestate"),
    SUNDAYREVIEW("sundayreview"),
    THEATER("theater"),
    TMAGAZINE("t-magazine"),
    UPSHOT("upshot"),
    WORLD("world");

    companion object {
        val allTopics = values().toList()

        fun listFromString(string: String) = string.split(",")
            .map { it.trim() }
            .map { valueOf(it) }
    }
}

fun List<TopicsType>.toTopicsString() = joinToString { it.name }
