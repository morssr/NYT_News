package com.example.mor.nytnews.ui.topics

import com.example.mor.nytnews.data.topics.TopicsType

val fakeTopicsStoriesMap by lazy {
    hashMapOf(
        TopicsType.HOME to fakeStoriesUiList,
        TopicsType.POLITICS to fakeStoriesUiList,
        TopicsType.SPORTS to fakeStoriesUiList,
        TopicsType.TECHNOLOGY to fakeStoriesUiList
    )
}

val fakeStoriesUiList by lazy { arrayListOf<StoryUI>().apply {
        add(
            StoryUI(
                id = "100000007980254",
                title = "Biden’s Vaccine Mandate Is Popular, but the Politics Are Tricky",
                abstract = "The president’s new vaccine requirements are supported by a majority of Americans.",
                imageUrl = "https://static01.nyt.com/images/2023/04/26/multimedia/26dc-cong-1-vtlh/26dc-cong-1-vtlh-threeByTwoSmallAt2X.jpg",
                subsection = "Politics",
                byline = "By Catie Edmondson and Carl Hulse",
                publishedDate = "2021-09-09T09:00:10-04:00",
                storyUrl = "https://www.nytimes.com/2021/09/09/us/politics/biden-vaccine-mandate.html",
                favorite = true
            )
        )

        add(
            StoryUI(
                id = "100000234007980254",
                title = "After a Hellish Start and a Honeymoon, McCarthy Faces His First Big Test",
                abstract = "The Republican speaker has stuck to partisan bills that have kept his party united. That ends with a tough vote to raise the debt limit in exchange for spending cuts.",
                imageUrl = "https://static01.nyt.com/images/2023/04/26/multimedia/26dc-mccarthy-cvhq/26dc-mccarthy-cvhq-threeByTwoSmallAt2X.jpg",
                subsection = "Politics",
                byline = "By Catie Edmondson and Carl Hulse",
                publishedDate = "2021-09-09T09:00:10-04:00",
                storyUrl = "https://www.nytimes.com/2021/09/09/us/politics/biden-vaccine-mandate.html",
                favorite = false
            )
        )

        add(
            StoryUI(
                id = "10000000798022354",
                title = "U.K. Blocks Microsoft’s 69 Billion Bid for Activision, a Blow for Tech Deals",
                abstract = "The decision barring the takeover of a big video game publisher is a major victory for proponents of regulating tech giants, which have faced obstacles in the United States.",
                imageUrl = "https://static01.nyt.com/images/2023/04/25/business/00microsoft-antitrust-hfo-sub/00microsoft-antitrust-hfo-sub-threeByTwoSmallAt2X.jpg",
                subsection = "Politics",
                byline = "By Catie Edmondson and Carl Hulse",
                publishedDate = "2021-09-09T09:00:10-04:00",
                storyUrl = "https://www.nytimes.com/2021/09/09/us/politics/biden-vaccine-mandate.html",
                favorite = false
            )
        )
    }
}

val fakeStory by lazy { StoryUI(
        id  = "1000000023425980254",
        title = "Biden’s Vaccine Mandate Is Popular, but the Politics Are Tricky",
        abstract = "The president’s new vaccine requirements are supported by a majority of Americans.",
        imageUrl = "https://static01.nyt.com/images/2023/04/26/multimedia/26dc-cong-1-vtlh/26dc-cong-1-vtlh-threeByTwoSmallAt2X.jpg",
        subsection = "Politics",
        byline = "By Catie Edmondson and Carl Hulse",
        publishedDate = "2021-09-09T09:00:10-04:00",
        storyUrl = "https://www.nytimes.com/2021/09/09/us/politics/biden-vaccine-mandate.html",
        favorite = true
    )
}