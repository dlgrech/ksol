package com.dgsd.android.solar.receive.requestamount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequestAmountContainerFragment : Fragment(R.layout.view_fragment_container) {

  private val coordinator by viewModel<RequestAmountCoordinator>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    onEach(coordinator.destination, ::onDestinationChanged)

    onEach(coordinator.closeFlow) {
      parentFragmentManager.popBackStackImmediate()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      coordinator.onCreate()
    }
  }

  private fun onDestinationChanged(destination: RequestAmountCoordinator.Destination) {
    val fragment = when (destination) {
      RequestAmountCoordinator.Destination.EnterAmount -> RequestEnterAmountFragment()
      RequestAmountCoordinator.Destination.EnterMessage -> RequestEnterMessageFragment()
      RequestAmountCoordinator.Destination.ViewQR -> RequestAmountViewQRFragment()
    }

    childFragmentManager.navigate(R.id.fragment_container, fragment)
  }

  companion object {

    fun newInstance(): RequestAmountContainerFragment {
      return RequestAmountContainerFragment()
    }
  }
}