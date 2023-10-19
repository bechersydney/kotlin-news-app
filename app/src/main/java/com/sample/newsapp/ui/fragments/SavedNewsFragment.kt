package com.sample.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sample.newsapp.R
import com.sample.newsapp.adapters.NewsAdapter
import com.sample.newsapp.databinding.FragmentSavedNewsBinding
import com.sample.newsapp.ui.NewsActivity
import com.sample.newsapp.ui.viewModels.NewsViewModel

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    private var mBinding: FragmentSavedNewsBinding? = null
    private val binding get() = mBinding!!

    private lateinit var viewModel: NewsViewModel
    private lateinit var mSavedNewsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        viewModel = (activity as NewsActivity).viewModel
        setUpRecyclerView()
        mSavedNewsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment, bundle)
        }

        viewModel.getArticles().observe(viewLifecycleOwner, Observer {
            mSavedNewsAdapter.differ.submitList(it)
        })

        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = mSavedNewsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(this@SavedNewsFragment.view!!, "Successfully deleted!", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(mBinding?.rvSavedNews)
        }
        return binding.root
    }

    private fun setUpRecyclerView() {
        mSavedNewsAdapter = NewsAdapter()
        mBinding?.rvSavedNews?.apply {
            adapter = mSavedNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        mSavedNewsAdapter.setOnItemClickListener {
            Toast.makeText(activity, "I am clicked", Toast.LENGTH_SHORT).show()
        }
    }
}