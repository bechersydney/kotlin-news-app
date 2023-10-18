package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.newsapp.R
import com.sample.newsapp.adapters.NewsAdapter
import com.sample.newsapp.base.BaseFragment
import com.sample.newsapp.databinding.FragmentSearchNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        // set up rv
        setUpRecyclerView()
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
                        searchAdapter.differ.submitList(newsResponse.articles)
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

    private fun showProgressSpinner() {
        mBinding?.apply {
            paginationProgressBar.visibility = View.VISIBLE
        }
    }

    private fun hideProgressSpinner() {
        mBinding?.apply {
            paginationProgressBar.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        searchAdapter = NewsAdapter()
        mBinding?.rvSearchNews?.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}