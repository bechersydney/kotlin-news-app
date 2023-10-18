package com.sample.newsapp.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.newsapp.data.db.models.NewsResponse
import com.sample.newsapp.data.repository.NewsRepository
import com.sample.newsapp.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val breakingNewsPage = 1

    val searchedNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private val searchedNewsPage = 1

    init {
        getBreakingNews()
    }

    private fun getBreakingNews(countryCode: String = "us") = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = repository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))

    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { res ->
                return Resource.Success(res)
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
                return Resource.Success(res)
            }
        }
        return Resource.Error(response.message());
    }
}