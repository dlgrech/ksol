package com.dgsd.android.solar.transaction.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.model.TransactionViewState
import com.dgsd.ksol.core.model.TransactionSignature

class TransactionListAdapter(
  private val onTransactionClickedListener: (TransactionSignature) -> Unit,
  private val onLoadMoreClickedListener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var transactionItems: List<TransactionViewState> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  var isLoadMoreEnabled: Boolean = true
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  var isLoadMoreLoading: Boolean = true
    set(value) {
      field = value
      notifyDataSetChanged()
    }


  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      VIEW_TYPE_TRANSACTION -> {
        TransactionListViewHolder.create(parent, onTransactionClickedListener)
      }

      VIEW_TYPE_LOAD_MORE -> {
        LoadMoreViewHolder.create(parent)
      }

      else -> error("Unknown view type: $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (getItemViewType(position)) {
      VIEW_TYPE_TRANSACTION -> {
        (holder as TransactionListViewHolder).bind(transactionItems[position])
      }

      VIEW_TYPE_LOAD_MORE -> {
        (holder as LoadMoreViewHolder).bind(isLoadMoreLoading)
        holder.itemView.setOnClickListener { onLoadMoreClickedListener.invoke() }
      }
    }
  }

  override fun getItemId(position: Int): Long {
    return if (position == transactionItems.size) {
      // "Load More" item
      Long.MAX_VALUE
    } else {
      when (val item = transactionItems[position]) {
        is TransactionViewState.Error -> item.transactionSignature?.hashCode() ?: item.hashCode()
        is TransactionViewState.Loading -> item.transactionSignature?.hashCode() ?: item.hashCode()
        is TransactionViewState.Transaction -> item.transactionSignature.hashCode()
      }.toLong()
    }
  }

  override fun getItemCount(): Int {
    return if (transactionItems.isEmpty()) {
      0
    } else if (isLoadMoreEnabled) {
      transactionItems.size + 1
    } else {
      transactionItems.size
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (position == transactionItems.size) {
      VIEW_TYPE_LOAD_MORE
    } else {
      VIEW_TYPE_TRANSACTION
    }
  }

  private companion object {

    const val VIEW_TYPE_TRANSACTION = 0
    const val VIEW_TYPE_LOAD_MORE = 1
  }
}