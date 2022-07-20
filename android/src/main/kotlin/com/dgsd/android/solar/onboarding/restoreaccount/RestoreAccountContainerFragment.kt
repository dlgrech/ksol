package com.dgsd.android.solar.onboarding.restoreaccount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import com.dgsd.android.solar.onboarding.restoreaccount.RestoreAccountCoordinator.Destination
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestoreAccountContainerFragment : Fragment(R.layout.view_fragment_container) {

    private val onboardingCoordinator by parentViewModel<OnboardingCoordinator>()
    private val coordinator by viewModel<RestoreAccountCoordinator>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onEach(coordinator.destination, ::onDestinationChanged)

        onEach(coordinator.continueWithFlow) { (seedInfo, selectedWallet) ->
            onboardingCoordinator.navigateToSetupAppLock(seedInfo, selectedWallet)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            coordinator.onCreate()
        }
    }

    private fun onDestinationChanged(destination: Destination) {
        val fragment = when (destination) {
            Destination.EnterSeedPhrase -> RestoreAccountFromSeedPhraseFragment()
            Destination.SelectAccount -> RestoreAccountSelectAddressFragment()
        }

        childFragmentManager.navigate(R.id.fragment_container, fragment)
    }

    companion object {

        fun newInstance(): RestoreAccountContainerFragment {
            return RestoreAccountContainerFragment()
        }
    }
}