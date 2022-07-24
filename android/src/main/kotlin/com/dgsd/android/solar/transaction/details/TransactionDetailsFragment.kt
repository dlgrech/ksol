package com.dgsd.android.solar.transaction.details

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.ksol.model.TransactionSignature
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val ARG_TRANSACTION_SIGNATURE = "transaction_signature"

class TransactionDetailsFragment : Fragment(R.layout.frag_transaction_details) {

  private val viewModel: TransactionDetailsViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
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