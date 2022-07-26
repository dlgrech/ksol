package com.dgsd.android.solar.transaction.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModelFromErrorMessage
import com.dgsd.android.solar.extensions.ensureViewCount
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.ksol.model.TransactionSignature
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARG_TRANSACTION_SIGNATURE = "transaction_signature"

class TransactionDetailsFragment : Fragment(R.layout.frag_transaction_details) {

  private val viewModel: TransactionDetailsViewModel by viewModel {
    parametersOf(
      checkNotNull(requireArguments().getString(ARG_TRANSACTION_SIGNATURE))
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
    val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val transactionSignatureHeader = view.findViewById<TextView>(R.id.transaction_signatures_header)
    val transactionSignatureContainer =
      view.findViewById<LinearLayout>(R.id.transaction_signatures_container)
    val blockTime = view.findViewById<TextView>(R.id.block_time)
    val changeInBalance = view.findViewById<TextView>(R.id.change_in_balance)
    val fee = view.findViewById<TextView>(R.id.fee)
    val logsContainer = view.findViewById<LinearLayout>(R.id.logs_container)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    swipeRefresh.setOnRefreshListener {
      viewModel.onSwipeToRefresh()
    }

    onEach(viewModel.showLoadingState) {
      // Coming soon: Show loading shimmer
    }

    onEach(viewModel.isLoading) {
      if (!it) {
        swipeRefresh.isRefreshing = false
      }
    }

    onEach(viewModel.errorMessage) {
      showModelFromErrorMessage(it)
    }

    onEach(viewModel.transactionSignatureHeaderText) {
      transactionSignatureHeader.text = it
    }

    onEach(viewModel.transactionSignatures) {
      transactionSignatureContainer.bindSignatures(it)
    }

    onEach(viewModel.feeText) {
      fee.text = it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private fun LinearLayout.bindSignatures(signatures: List<TransactionSignature>) {
    ensureViewCount(
      signatures.size
    ) {
      LayoutInflater.from(context).inflate(
        R.layout.view_transaction_details_transaction_signature,
        this,
        true
      )
    }

    children.toList().zip(signatures) { view, signature ->
      (view as TextView).text = signature
    }
  }

  companion object {

    fun newInstance(transactionSignature: TransactionSignature): TransactionDetailsFragment {
      return TransactionDetailsFragment().apply {
        arguments = bundleOf(
          ARG_TRANSACTION_SIGNATURE to transactionSignature
        )
      }
    }
  }
}