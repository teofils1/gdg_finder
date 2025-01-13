package com.example.dmd_project.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dmd_project.databinding.ListItemBinding
import com.example.dmd_project.network.GdgChapter

class GdgListAdapter(private val clickListener: GdgClickListener) :
    ListAdapter<GdgChapter, GdgListAdapter.GdgListViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<GdgChapter>() {
        override fun areItemsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean =
            oldItem == newItem
    }

    class GdgListViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: GdgClickListener, gdgChapter: GdgChapter) {
            binding.apply {
                chapter = gdgChapter
                this.clickListener = clickListener
                executePendingBindings() // Ensures immediate data binding execution
            }
        }

        companion object {
            fun from(parent: ViewGroup): GdgListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return GdgListViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GdgListViewHolder =
        GdgListViewHolder.from(parent)

    override fun onBindViewHolder(holder: GdgListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }
}

class GdgClickListener(val clickListener: (GdgChapter) -> Unit) {
    fun onClick(chapter: GdgChapter) = clickListener(chapter)
}
