package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sample.newsapp.R
import com.sample.newsapp.databinding.FragmentArticleBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel

class ArticleFragment : Fragment(R.layout.item_article_preview) {
    private var mBinding: FragmentArticleBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentArticleBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel

        val article = args.article
        mBinding?.webView?.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        mBinding?.fab?.apply {
            this.setOnClickListener {
                viewModel.saveArticle(article)
                Snackbar.make(it, "Article saved", Snackbar.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}