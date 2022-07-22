package com.dgsd.android.solar.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.frag_home) {

  private val viewModel: HomeViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val receiveButton = view.findViewById<View>(R.id.send)
    val sendButton = view.findViewById<View>(R.id.receive)
    val balanceText = view.findViewById<TextView>(R.id.balance)

    sendButton.setOnClickListener {
    }

    receiveButton.setOnClickListener {
    }

    onEach(viewModel.balanceText) {
      balanceText.text = it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}