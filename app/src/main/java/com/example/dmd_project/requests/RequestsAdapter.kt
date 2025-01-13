package com.example.dmd_project.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dmd_project.databinding.ListItemRequestBinding
import com.example.dmd_project.database.GdgChapterEntity

class RequestsAdapter :
    ListAdapter<GdgChapterEntity, RequestsAdapter.RequestViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<GdgChapterEntity>() {
        override fun areItemsTheSame(oldItem: GdgChapterEntity, newItem: GdgChapterEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GdgChapterEntity, newItem: GdgChapterEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ListItemRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RequestViewHolder(private val binding: ListItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: GdgChapterEntity) {
            binding.request = request
            binding.executePendingBindings()
        }
    }
}
