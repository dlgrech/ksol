package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.OnboardingCoordinator.Destination
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountContainerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingContainerFragment : Fragment(R.layout.view_fragment_container) {

    private val coordinator: OnboardingCoordinator by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onEach(coordinator.destination, ::onDestinationChanged)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            coordinator.onCreate()
        }
    }

    private fun onDestinationChanged(destination: Destination) {
        val fragment = when (destination) {
            Destination.CreateNewWalletFlow -> CreateAccountContainerFragment.newInstance()
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