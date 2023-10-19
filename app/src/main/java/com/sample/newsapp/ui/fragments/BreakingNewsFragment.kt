package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.newsapp.R
import com.sample.newsapp.adapters.NewsAdapter
import com.sample.newsapp.databinding.FragmentBreakingNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel
import com.sample.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.sample.newsapp.utils.Resource

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var mBinding: FragmentBreakingNewsBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        // set up rv
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }
        // observe on breaking news
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressSpinner()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = totalPages == viewModel.breakingNewsPage
                        if(isLastPage){
                            mBinding?.rvBreakingNews?.setPadding(0,0,0,0)
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

//        private val scrollListener =
//        View.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
//            if(scrollY != oldScrollY) {
//                isScrolling = true
//            }
//            val recyclerView = v as RecyclerView
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//            val visibleItemCount = layoutManager.childCount // visible count
//            val totalItemCount = layoutManager.itemCount // all count
//
//            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
//            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
//            val isNotAtBeginning = firstVisibleItemPosition >= 0
//            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
//            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
//            if(shouldPaginate){
//                viewModel.getBreakingNews("us")
//            }
//        }
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
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }


    private fun showProgressSpinner() {
        mBinding?.apply {
            paginationProgressBar.visibility = View.VISIBLE
        }
        isLoading = true
    }

    private fun hideProgressSpinner() {
        mBinding?.apply {
            paginationProgressBar.visibility = View.GONE
        }
        isLoading = false
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        mBinding?.rvBreakingNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
        newsAdapter.setOnItemClickListener {
            Toast.makeText(activity, "I am clicked", Toast.LENGTH_SHORT).show()
        }
    }
}