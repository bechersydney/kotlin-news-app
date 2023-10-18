package com.sample.newsapp.data.repository

import com.sample.newsapp.api.RetrofitInstance
import com.sample.newsapp.data.db.ArticleDatabase

class NewsRepository(
    private val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.newsAPI.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.newsAPI.searchNews(searchQuery, pageNumber)
}