package com.sample.newsapp.ui.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sample.newsapp.NewsApplication
import com.sample.newsapp.data.db.models.Article
import com.sample.newsapp.data.db.models.NewsResponse
import com.sample.newsapp.data.repository.NewsRepository
import com.sample.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val repository: NewsRepository
) : AndroidViewModel(app) {

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
        safeBreakingNewsCall(countryCode)
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
        safeSearchNewsCall(searchText)
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

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                searchedNews.postValue(Resource.Error("No Internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("NetworkFailure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(q: String) {
        searchedNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchNews(q, searchedNewsPage)
                searchedNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchedNews.postValue(Resource.Error("No Internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchedNews.postValue(Resource.Error("NetworkFailure"))
                else -> searchedNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // articles
    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) =
        viewModelScope.launch { repository.deleteArticle(article) }

    fun getArticles() = repository.getArticles()


    @SuppressLint("ObsoleteSdkInt")
    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) ||
                        capabilities.hasTransport(TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(TRANSPORT_ETHERNET) -> return true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}