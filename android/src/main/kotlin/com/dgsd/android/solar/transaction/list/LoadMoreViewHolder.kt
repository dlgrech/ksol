package com.dgsd.android.solar.transaction.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.R

class LoadMoreViewHolder private constructor(
  view: View
) : RecyclerView.ViewHolder(view) {

  private val loadMore = itemView.requireViewById<View>(R.id.load_more)
  private val loadingIndicator = itemView.requireViewById<View>(R.id.loading)

  fun bind(loadMoreLoading: Boolean) {
    loadMore.isVisible = !loadMoreLoading
    loadingIndicator.isVisible = loadMoreLoading
  }

  companion object {

    fun create(
      parent: ViewGroup,
    ): LoadMoreViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(
        R.layout.view_transaction_list_load_more,
        parent,
        false
      )

      return LoadMoreViewHolder(view)
    }
  }
}