package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sample.newsapp.R
import com.sample.newsapp.databinding.FragmentSavedNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {
    private var mBinding: FragmentSavedNewsBinding ?= null
    private val binding get() = mBinding!!

    private lateinit var viewModel: NewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        return binding.root
    }
}