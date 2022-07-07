package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.navigate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountContainerFragment : Fragment(R.layout.view_fragment_container) {

    private val coordinator by viewModel<CreateAccountCoordinator>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinator.destination
            .onEach(::onDestinationChanged)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onDestinationChanged(destination: CreateAccountCoordinator.Destination) {
        val fragment = when (destination) {
            CreateAccountCoordinator.Destination.AddressSelection -> CreateAccountAddressSelectionFragment()
            CreateAccountCoordinator.Destination.EnterPassphrase -> CreateAccountEnterPassphraseFragment()
            CreateAccountCoordinator.Destination.ViewSeedPhrase -> CreateAccountViewSeedPhraseFragment()
        }

        childFragmentManager.navigate(R.id.fragment_container, fragment)
    }

    companion object {

        fun newInstance(): CreateAccountContainerFragment {
            return CreateAccountContainerFragment()
        }
    }
}