package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.onboarding.OnboardingCoordinator.Destination
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingContainerFragment : Fragment(R.layout.view_fragment_container) {

    private val coordinator: OnboardingCoordinator by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinator.destination
            .onEach(::onDestinationChanged)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun onDestinationChanged(destination: Destination) {
        val fragment = when (destination) {
            Destination.CreateNewWalletFlow -> CreateNewAccountFragment()
            Destination.Explainer -> OnboardingExplainerFragment()
            Destination.RestorePrivateKeyFlow -> AddAccountFromPrivateKeyFragment()
            Destination.RestoreSeedPhraseFlow -> AddAccountFromSeedPhraseFragment()
            Destination.Welcome -> OnboardingWelcomeFragment()
        }

        childFragmentManager.navigate(R.id.fragment_container, fragment)
    }

    companion object {

        fun newInstance(): OnboardingContainerFragment {
            return OnboardingContainerFragment()
        }
    }
}