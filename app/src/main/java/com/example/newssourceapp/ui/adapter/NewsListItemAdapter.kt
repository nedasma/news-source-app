package com.example.newssourceapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.newssourceapp.R
import com.example.newssourceapp.data.model.Article
import com.example.newssourceapp.databinding.FragmentNewsListItemBinding

class NewsListItemAdapter : RecyclerView.Adapter<NewsListItemAdapter.NewsListItemViewHolder>() {

    inner class NewsListItemViewHolder(val binding: FragmentNewsListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListItemViewHolder =
        NewsListItemViewHolder(FragmentNewsListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

    override fun onBindViewHolder(holder: NewsListItemViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.binding.apply {
            articleImage.load(article.urlToImage) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(RoundedCornersTransformation())
            }
            articleTitle.text = article.title
            date.text = article.publishedAt
            listItem.setOnClickListener { onItemClickListener?.invoke(article) }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val diffUtilCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    // Exposed to the outer world - adapter data can be updated with differ.submitList(items)
    val differ = AsyncListDiffer(this, diffUtilCallback)
}