package com.sample.newsapp.data.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.newsapp.data.db.models.Source

@Entity(tableName = "article")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : java.io.Serializable