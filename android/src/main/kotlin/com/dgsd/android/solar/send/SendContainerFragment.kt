package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.send.SendCoordinator.Destination
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val ARG_STARTING_DESTINATION = "starting_destination"

class SendContainerFragment : Fragment(R.layout.view_fragment_container) {

  private val coordinator by viewModel<SendCoordinator> {
    val ordinal = requireArguments().getInt(ARG_STARTING_DESTINATION, -1)
    parametersOf(SendCoordinator.StartingDestination.values()[ordinal])
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    onEach(coordinator.destination, ::onDestinationChanged)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      coordinator.onCreate()
    }
  }

  private fun onDestinationChanged(destination: Destination) {
    val fragment = when (destination) {
      Destination.Confirmation -> TODO()
      Destination.DetectedRecipient -> TODO()
      Destination.EnterAddress -> SendEnterAddressFragment()
      Destination.EnterAmount -> TODO()
      Destination.ScanQR -> SendScanQRFragment()
      Destination.SendToPrevious -> TODO()
      Destination.Success -> TODO()
      Destination.PreviousTransactionPicker -> TODO()
    }

    childFragmentManager.navigate(R.id.fragment_container, fragment)
  }

  companion object {

    fun newQRScanInstance(): SendContainerFragment {
      return newInstance(SendCoordinator.StartingDestination.QR_SCAN)
    }

    fun newEnterAddressInstance(): SendContainerFragment {
      return newInstance(SendCoordinator.StartingDestination.ENTER_ADDRESS)
    }

    fun newInstance(startingDestination: SendCoordinator.StartingDestination): SendContainerFragment {
      return SendContainerFragment().apply {
        arguments = bundleOf(
          ARG_STARTING_DESTINATION to startingDestination.ordinal
        )
      }
    }
  }
}