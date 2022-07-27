package com.dgsd.android.solar.transaction.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModelFromErrorMessage
import com.dgsd.android.solar.extensions.dpToPx
import com.dgsd.android.solar.extensions.ensureViewCount
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.roundedCorners
import com.dgsd.ksol.model.TransactionSignature
import com.google.android.material.snackbar.Snackbar
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

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val swipeRefresh = view.requireViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
    val transactionSignatureHeader =
      view.requireViewById<TextView>(R.id.transaction_signatures_header)
    val transactionSignatureContainer =
      view.requireViewById<LinearLayout>(R.id.transaction_signatures_container)
    val blockTimeHeader = view.requireViewById<TextView>(R.id.block_time_header)
    val blockTime = view.requireViewById<TextView>(R.id.block_time)
    val amount = view.requireViewById<TextView>(R.id.amount)
    val recentBlockHash = view.requireViewById<TextView>(R.id.recent_blockhash)
    val feeHeader = view.requireViewById<TextView>(R.id.fee_header)
    val fee = view.requireViewById<TextView>(R.id.fee)
    val logsHeader = view.requireViewById<TextView>(R.id.logs_header)
    val logsContainer = view.requireViewById<LinearLayout>(R.id.logs_container)
    val accountsHeader = view.requireViewById<TextView>(R.id.accounts_header)
    val accountsContainer = view.requireViewById<LinearLayout>(R.id.accounts_container)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    swipeRefresh.setOnRefreshListener {
      viewModel.onSwipeToRefresh()
    }

    recentBlockHash.setOnClickListener {
      viewModel.onRecentBlockhasClicked()
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
      if (it.isNullOrEmpty()) {
        feeHeader.isVisible = false
        fee.isVisible = false
      } else {
        feeHeader.isVisible = true
        fee.isVisible = true
        fee.text = it
      }
    }

    onEach(viewModel.showConfirmationMessage) {
      Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
    }

    onEach(viewModel.blockTimeText) {
      if (it.isNullOrEmpty()) {
        blockTime.isVisible = false
        blockTimeHeader.isVisible = false
      } else {
        blockTime.isVisible = true
        blockTimeHeader.isVisible = true

        blockTime.text = it
      }
    }

    onEach(viewModel.recentBlockHashText) {
      recentBlockHash.text = it
    }

    onEach(viewModel.amountText) {
      amount.text = it
    }

    onEach(viewModel.logMessages) {
      logsHeader.isVisible = it.isNotEmpty()
      logsContainer.isVisible = it.isNotEmpty()

      logsContainer.bindLogs(it)
    }

    onEach(viewModel.accountDetails) { accountDetails ->
      accountsHeader.isVisible = accountDetails.isNotEmpty()
      accountsContainer.isVisible = accountDetails.isNotEmpty()

      accountsContainer.bindAccounts(accountDetails)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private fun LinearLayout.bindSignatures(signatures: List<TransactionSignature>) {
    ensureViewCount(signatures.size) {
      LayoutInflater.from(context).inflate(
        R.layout.view_transaction_details_transaction_signature,
        this,
        true
      )
    }

    children.toList().zip(signatures) { view, signature ->
      (view as TextView).text = signature
      view.setOnClickListener {
        viewModel.onSignatureClicked(signature)
      }
    }
  }

  private fun LinearLayout.bindLogs(logEntries: List<String>) {
    ensureViewCount(logEntries.size) {
      LayoutInflater.from(context).inflate(
        R.layout.view_transaction_details_log_row, this, true
      )
    }

    children.toList().zip(logEntries) { view, logMessage ->
      (view as TextView).text = logMessage
    }
  }

  private fun LinearLayout.bindAccounts(accounts: List<TransactionAccountViewState>) {
    ensureViewCount(accounts.size) {
      LayoutInflater.from(context).inflate(
        R.layout.view_transaction_details_account, this, true
      )
    }

    children.toList().zip(accounts) { view, account ->
      val accountKey = view.requireViewById<TextView>(R.id.account_key)
      val balance = view.requireViewById<TextView>(R.id.balance)
      val writerBadge = view.requireViewById<View>(R.id.writer_badge)
      val signerBadge = view.requireViewById<View>(R.id.signer_badge)
      val feePayerBadge = view.requireViewById<View>(R.id.fee_payer_badge)
      val programBadge = view.requireViewById<View>(R.id.program_badge)

      arrayOf(
        writerBadge,
          signerBadge,
          feePayerBadge,
          programBadge,
      ).forEach {
        it.roundedCorners(it.context.dpToPx(8))
      }

      accountKey.text = account.accountDisplayText
      balance.text = account.balanceAfterText
      balance.isVisible = !account.balanceAfterText.isNullOrEmpty()

      writerBadge.isVisible = account.isWriter
      signerBadge.isVisible = account.isSigner
      feePayerBadge.isVisible = account.isFeePayer
      programBadge.isVisible = account.isProgram

      view.setOnClickListener {
        viewModel.onAccountClicked(account.accountKey)
      }
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