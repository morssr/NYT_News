package com.mls.mor.nytnews.ui.bookmarks

val fakeBookmarkUiModel = BookmarkUi(
    id = "xcvxcvwe321",
    topic = "home",
    title = "Biden’s Vaccine Mandate Is Popular, but the Politics Are Tricky",
    abstract = "The president’s new vaccine requirements are supported by a majority of Americans.",
    imageUrl = "https://static01.nyt.com/images/2023/04/26/multimedia/26dc-cong-1-vtlh/26dc-cong-1-vtlh-threeByTwoSmallAt2X.jpg",
    subsection = "Politics",
    byline = "By Catie Edmondson and Carl Hulse",
    publishedDate = "13/09/21",
    storyUrl = "https://www.nytimes.com/2021/09/09/us/politics/biden-vaccine-mandate.html",
)

val fakeBookmarksUiList = arrayListOf<BookmarkUi>().apply {
    for (i in 1..10) add(fakeBookmarkUiModel.copy(id = "xcvxcvwe32 $i"))
}