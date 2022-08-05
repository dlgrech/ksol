package com.dgsd.android.solar.transaction.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.model.TransactionViewState
import com.dgsd.ksol.model.TransactionSignature

class TransactionListViewHolder private constructor(
  view: View,
  private val onTransactionClickedListener: (TransactionSignature) -> Unit,
) : RecyclerView.ViewHolder(view) {

  private val loadingContainer = view.requireViewById<View>(R.id.loading)
  private val contentContainer = view.requireViewById<View>(R.id.content)
  private val errorContainer = view.requireViewById<View>(R.id.error)

  private val publicKeyView = contentContainer.findViewById<TextView>(R.id.public_key)
  private val dateTimeView = contentContainer.findViewById<TextView>(R.id.date_time)
  private val amountView = contentContainer.findViewById<TextView>(R.id.amount)
  private val iconView = contentContainer.findViewById<ImageView>(R.id.icon)

  fun bind(transactionViewState: TransactionViewState) {
    when (transactionViewState) {
      is TransactionViewState.Error -> bindError(transactionViewState)
      is TransactionViewState.Loading -> bindLoading()
      is TransactionViewState.Transaction -> bindTransaction(transactionViewState)
    }
  }

  private fun bindLoading() {
    loadingContainer.isInvisible = false
    contentContainer.isInvisible = true
    errorContainer.isInvisible = true

    itemView.setOnClickListener(null)
  }

  private fun bindError(viewState: TransactionViewState.Error) {
    loadingContainer.isInvisible = true
    contentContainer.isInvisible = true
    errorContainer.isInvisible = false

    if (viewState.transactionSignature == null) {
      itemView.setOnClickListener(null)
    } else {
      itemView.setOnClickListener {
        onTransactionClickedListener.invoke(viewState.transactionSignature)
      }
    }
  }

  private fun bindTransaction(transaction: TransactionViewState.Transaction) {
    loadingContainer.isInvisible = true
    contentContainer.isInvisible = false
    errorContainer.isInvisible = true

    publicKeyView.text = transaction.displayAccountText
    amountView.text = transaction.amountText
    when (transaction.direction) {
      TransactionViewState.Transaction.Direction.INCOMING -> {
        amountView.setTextColor(itemView.context.getColor(R.color.positive_text_color))
        amountView.setBackgroundResource(R.drawable.amount_background_incoming)
      }
      else -> {
        amountView.setTextColor(itemView.context.getColorAttr(android.R.attr.textColorPrimary))
        amountView.background = null
      }
    }

    if (transaction.dateText == null) {
      dateTimeView.isVisible = false
    } else {
      dateTimeView.isVisible = true
      dateTimeView.text = transaction.dateText
    }

    when (transaction.direction) {
      TransactionViewState.Transaction.Direction.INCOMING -> {
        iconView.setImageResource(R.drawable.ic_baseline_chevron_left_24)
      }
      TransactionViewState.Transaction.Direction.OUTGOING -> {
        iconView.setImageResource(R.drawable.ic_baseline_chevron_right_24)
      }
      TransactionViewState.Transaction.Direction.NONE -> {
        iconView.setImageResource(R.drawable.ic_baseline_commit_24)
      }
    }

    itemView.setOnClickListener {
      onTransactionClickedListener.invoke(transaction.transactionSignature)
    }
  }

  companion object {

    fun create(
      parent: ViewGroup,
      onTransactionClickedListener: (TransactionSignature) -> Unit,
    ): TransactionListViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(
        R.layout.view_transaction,
        parent,
        false
      )

      return TransactionListViewHolder(view, onTransactionClickedListener)
    }
  }
}