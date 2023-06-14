package com.example.mor.nytnews.data.search.api

data class SearchResponse(
    val status: String,
    val response: SearchStoriesResponse
)

data class SearchStoriesResponse(
    val docs: List<SearchStoryResponse>,
    val meta: Meta
)

data class SearchStoryResponse(
    val _id: String,
    val abstract: String,
    val byline: Byline,
    val document_type: String,
    val headline: Headline,
    val lead_paragraph: String,
    val multimedia: List<Multimedia>?,
    val pub_date: String,
    val section_name: String,
    val snippet: String,
    val source: String,
    val subsection_name: String?,
    val uri: String,
    val web_url: String,
)

data class Headline(
    val main: String
)

data class Multimedia(
    val width: Int,
    val height: Int,
    val type: String,
    val url: String,
)

data class Byline(
    val original: String,
)

data class Meta(
    val hits: Int,
    val offset: Int,
    val time: Int
)