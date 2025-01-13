package com.example.dmd_project.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dmd_project.databinding.ListItemBinding
import com.example.dmd_project.network.GdgChapter

// Adapter class for displaying a list of GDG chapters in a RecyclerView
class GdgListAdapter(private val clickListener: GdgClickListener) :
    ListAdapter<GdgChapter, GdgListAdapter.GdgListViewHolder>(DiffCallback) {

    // Companion object providing DiffUtil callback for efficient list updates
    companion object DiffCallback : DiffUtil.ItemCallback<GdgChapter>() {
        /**
         * Checks if two items are the same based on their references.
         */
        override fun areItemsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean =
            oldItem === newItem  // Reference equality check

        /**
         * Checks if the contents of two items are the same.
         */
        override fun areContentsTheSame(oldItem: GdgChapter, newItem: GdgChapter): Boolean =
            oldItem == newItem  // Structural equality check (compares field values)
    }

    // ViewHolder class for binding individual GDG chapter items
    class GdgListViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the GDG chapter data and click listener to the item layout.
         */
        fun bind(clickListener: GdgClickListener, gdgChapter: GdgChapter) {
            binding.apply {
                chapter = gdgChapter  // Binds the GDG chapter to the layout
                this.clickListener = clickListener  // Binds the click listener
                executePendingBindings()  // Ensures immediate execution of binding operations
            }
        }

        companion object {
            /**
             * Creates a ViewHolder instance by inflating the item layout.
             */
            fun from(parent: ViewGroup): GdgListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return GdgListViewHolder(binding)
            }
        }
    }

    /**
     * Called when a new ViewHolder is created.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GdgListViewHolder =
        GdgListViewHolder.from(parent)

    /**
     * Called to bind data to an existing ViewHolder at a specific position.
     */
    override fun onBindViewHolder(holder: GdgListViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))  // Binds the GDG chapter at the given position
    }
}

// Wrapper class for handling click events on GDG chapters
class GdgClickListener(val clickListener: (GdgChapter) -> Unit) {
    /**
     * Called when a chapter is clicked, passing the clicked chapter to the listener.
     */
    fun onClick(chapter: GdgChapter) = clickListener(chapter)
}

