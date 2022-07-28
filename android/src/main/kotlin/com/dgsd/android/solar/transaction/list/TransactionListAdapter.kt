package com.dgsd.android.solar.transaction.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.model.TransactionViewState
import com.dgsd.ksol.model.TransactionSignature

class TransactionListAdapter(
  private val onTransactionClickedListener: (TransactionSignature) -> Unit,
) : RecyclerView.Adapter<TransactionListViewHolder>() {

  var transactionItems: List<TransactionViewState> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  init {
    setHasStableIds(true)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
    return TransactionListViewHolder.create(parent, onTransactionClickedListener)
  }

  override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
    holder.bind(transactionItems[position])
  }

  override fun getItemId(position: Int): Long {
    return when (val item = transactionItems[position]) {
      is TransactionViewState.Error -> item.transactionSignature?.hashCode() ?: item.hashCode()
      is TransactionViewState.Loading -> item.transactionSignature?.hashCode() ?: item.hashCode()
      is TransactionViewState.Transaction -> item.transactionSignature.hashCode()
    }.toLong()
  }

  override fun getItemCount(): Int {
    return transactionItems.size
  }
}