package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sample.newsapp.base.BaseFragment
import com.sample.newsapp.databinding.FragmentArticleBinding

class ArticleFragment : BaseFragment() {
    private var mBinding: FragmentArticleBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

}