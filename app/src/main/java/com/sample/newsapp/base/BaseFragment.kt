package com.sample.newsapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel

open class BaseFragment : Fragment() {
    lateinit var viewModel: NewsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as NewsActivity).viewModel
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}