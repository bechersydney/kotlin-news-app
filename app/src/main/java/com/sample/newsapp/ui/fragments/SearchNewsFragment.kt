package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.newsapp.R
import com.sample.newsapp.adapters.NewsAdapter
import com.sample.newsapp.base.BaseFragment
import com.sample.newsapp.databinding.FragmentSearchNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel
import com.sample.newsapp.utils.Constants
import com.sample.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.sample.newsapp.utils.Constants.Companion.SEARCH_DELAY
import com.sample.newsapp.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    private var mBinding: FragmentSearchNewsBinding? = null
    private val binding get() = mBinding!!
    lateinit var viewModel: NewsViewModel
    lateinit var searchAdapter: NewsAdapter
    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        // set up rv
        setUpRecyclerView()
        searchAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle)
        }
        // listen on every text type on search bar
        var job: Job? = null
        mBinding?.etSearch?.addTextChangedListener {input ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY)
                if(input.toString().isNotEmpty()) {
                    viewModel.searchNews(input.toString())
                }
            }
        }
        // observed search
        viewModel.searchedNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressSpinner()
                    response.data?.let { newsResponse ->
                        searchAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = totalPages == viewModel.searchedNewsPage
                        if(isLastPage) {
                            mBinding?.rvSearchNews?.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressSpinner()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is Resource.Loading -> {
                    showProgressSpinner()
                }
            }
        }
        return binding.root
    }
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount // visible count
            val totalItemCount = layoutManager.itemCount // all count

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchNews(mBinding?.etSearch?.text.toString())
                isScrolling = false
            }
        }
    }

    private fun showProgressSpinner() {
        isLoading = true
        mBinding?.apply {
            paginationProgressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressSpinner() {
        isLoading = false
        mBinding?.apply {
            paginationProgressBar.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        searchAdapter = NewsAdapter()
        mBinding?.rvSearchNews?.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

}