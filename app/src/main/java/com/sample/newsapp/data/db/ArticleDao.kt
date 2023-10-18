package com.sample.newsapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sample.newsapp.data.db.entities.Article

@Dao
interface ArticleDao {
    // if id exist? update: insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM article")
    fun getArticles(): LiveData<List<Article>>
}