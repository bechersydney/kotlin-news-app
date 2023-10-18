package com.sample.newsapp.data.db.models

import com.sample.newsapp.data.db.entities.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)