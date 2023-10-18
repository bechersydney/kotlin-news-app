package com.sample.newsapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sample.newsapp.R
import com.sample.newsapp.data.db.ArticleDatabase
import com.sample.newsapp.data.repository.NewsRepository
import com.sample.newsapp.databinding.ActivityNewsBinding
import com.sample.newsapp.ui.viewModels.NewsViewModel
import com.sample.newsapp.ui.viewModels.NewsViewModelProviderFactory

class NewsActivity : AppCompatActivity() {
    // binding
    private lateinit var mBinding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val repository = NewsRepository(ArticleDatabase(this))
        val factory = NewsViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(
            owner = this,
            factory = factory
        ).get(modelClass = NewsViewModel::class.java)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        mBinding.bottomNavigationView.setupWithNavController(navController)

    }
}