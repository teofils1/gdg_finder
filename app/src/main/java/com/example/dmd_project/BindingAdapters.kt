package com.example.dmd_project

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dmd_project.network.GdgChapter
import com.example.dmd_project.search.GdgListAdapter

/**
 * Binds a list of [GdgChapter] to the [RecyclerView] and scrolls to the top on list update.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<GdgChapter>?) {
    (recyclerView.adapter as? GdgListAdapter)?.submitList(data) {
        recyclerView.scrollToPosition(0) // Scroll to top after the update
    }
}

/**
 * Sets the visibility of a [View] to VISIBLE only when the given data is null or empty.
 */
@BindingAdapter("showOnlyWhenEmpty")
fun View.showOnlyWhenEmpty(data: List<GdgChapter>?) {
    visibility = if (data.isNullOrEmpty()) View.VISIBLE else View.GONE
}
