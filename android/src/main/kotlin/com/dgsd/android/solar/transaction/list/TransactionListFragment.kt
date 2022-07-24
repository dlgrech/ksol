package com.dgsd.android.solar.transaction.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionListFragment : Fragment(R.layout.frag_transaction_list) {

  private val viewModel: TransactionListViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }
  }

  companion object {

    fun newInstance(): TransactionListFragment {
      return TransactionListFragment()
    }
  }
}