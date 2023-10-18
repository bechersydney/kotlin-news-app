package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.newsapp.R
import com.sample.newsapp.adapters.NewsAdapter
import com.sample.newsapp.databinding.FragmentBreakingNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel
import com.sample.newsapp.utils.Resource
import kotlinx.coroutines.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private var mBinding: FragmentBreakingNewsBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
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
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }
        // observe on breaking news
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressSpinner()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressSpinner()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_SHORT).show()
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
        newsAdapter = NewsAdapter()
        mBinding?.rvBreakingNews?.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        newsAdapter.setOnItemClickListener {
            Toast.makeText(activity, "I am clicked", Toast.LENGTH_SHORT).show()
        }
    }
}