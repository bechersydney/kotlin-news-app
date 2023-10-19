package com.sample.newsapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.newsapp.data.db.models.Article
import com.sample.newsapp.data.db.models.NewsResponse
import com.sample.newsapp.data.repository.NewsRepository
import com.sample.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    private var breakingNewsResponse: NewsResponse? = null

    val searchedNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchedNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews()
    }

    fun getBreakingNews(countryCode: String = "us") = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))

    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { res ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = res
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = res.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: res)
            }
        }
        return Resource.Error(response.message());
    }

    fun searchNews(searchText: String) = viewModelScope.launch {
        searchedNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchText, searchedNewsPage)
        searchedNews.postValue(handleSearchNewsResponse(response))
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { res ->
                searchedNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = res
                } else {
                    // tricky part. this part is mutating the searchnews response. dont get bother of unsed oldArticles(it was mutating directly the searchNewResponse)
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = res.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: res)
            }
        }
        return Resource.Error(response.message());
    }

    // articles
    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) =
        viewModelScope.launch { repository.deleteArticle(article) }

    fun getArticles() = repository.getArticles()
}