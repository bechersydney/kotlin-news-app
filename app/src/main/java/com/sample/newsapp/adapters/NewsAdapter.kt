package com.sample.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sample.newsapp.data.db.models.Article
import com.sample.newsapp.databinding.ItemArticlePreviewBinding

class NewsAdapter(
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private lateinit var view: ItemArticlePreviewBinding

    inner class NewsViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setValue(article: Article) {
            Glide.with(binding.root).load(article.urlToImage).into(binding.ivArticleImage)
            binding.apply {
                tvSource.text = article.source.name
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
//                callback?.onItemClick(article)
                root.setOnClickListener{
                    onItemClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }

    // this is used to compare the items in adapter. passing list in constructor is not a good practice (performance issue since it will reload again the adapter)
    // while diffutils will compare only the items and run on background
    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        view = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.setValue(article)

    }
    //TODO:this is how to create callback with kotlin
//    private var callback: ItemClickListener? = null
//    interface ItemClickListener {
//        fun onItemClick(article: Article)
//    }
//    fun setOnItemClickListener(listener: ItemClickListener) {
//        callback = listener
//    }

    //TODO:this is the function type (contains only 1 callback)
    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
