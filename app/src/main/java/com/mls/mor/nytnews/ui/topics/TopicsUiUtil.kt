package com.mls.mor.nytnews.ui.topics

import android.content.Context
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.data.topics.TopicsType

fun interestEnumToStringResources(context: Context, interests: TopicsType): String {
    return when (interests) {
        TopicsType.HOME -> context.getString(R.string.home)
        TopicsType.POLITICS -> context.getString(R.string.politics)
        TopicsType.BOOKS -> context.getString(R.string.books)
        TopicsType.HEALTH -> context.getString(R.string.health)
        TopicsType.TECHNOLOGY -> context.getString(R.string.technology)
        TopicsType.AUTOMOBILES -> context.getString(R.string.automobiles)
        TopicsType.ARTS -> context.getString(R.string.arts)
        TopicsType.SCIENCE -> context.getString(R.string.science)
        TopicsType.FASHION -> context.getString(R.string.fashion)
        TopicsType.SPORTS -> context.getString(R.string.sports)
        TopicsType.BUSINESS -> context.getString(R.string.business)
        TopicsType.FOOD -> context.getString(R.string.food)
        TopicsType.TRAVEL -> context.getString(R.string.travel)
        TopicsType.INSIDER -> context.getString(R.string.insider)
        TopicsType.MAGAZINE -> context.getString(R.string.magazine)
        TopicsType.MOVIES -> context.getString(R.string.movies)
        TopicsType.NATIONAL -> context.getString(R.string.national)
        TopicsType.NYREGION -> context.getString(R.string.ny_region)
        TopicsType.OBITUARIES -> context.getString(R.string.obituaries)
        TopicsType.OPINION -> context.getString(R.string.opinion)
        TopicsType.REALESTATE -> context.getString(R.string.real_estate)
        TopicsType.SUNDAYREVIEW -> context.getString(R.string.sunday_review)
        TopicsType.THEATER -> context.getString(R.string.theater)
        TopicsType.TMAGAZINE -> context.getString(R.string.t_magazine)
        TopicsType.UPSHOT -> context.getString(R.string.upshot)
        TopicsType.WORLD -> context.getString(R.string.world)
    }
}
