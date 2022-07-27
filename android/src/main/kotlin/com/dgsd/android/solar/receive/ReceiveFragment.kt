package com.dgsd.android.solar.receive

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveFragment : Fragment(R.layout.frag_receive) {

  private val viewModel: ReceiveViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }
  }

  companion object {

    fun newInstance(): ReceiveFragment {
      return ReceiveFragment()
    }
  }
}