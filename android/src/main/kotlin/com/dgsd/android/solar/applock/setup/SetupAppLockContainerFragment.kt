package com.dgsd.android.solar.applock.setup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.OnboardingCoordinator
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupAppLockContainerFragment : Fragment(R.layout.view_fragment_container) {

  private val onboardingCoordinator by parentViewModel<OnboardingCoordinator>()
  private val coordinator by viewModel<SetupAppLockCoordinator>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    onEach(coordinator.destination, ::onDestinationChanged)

    onEach(coordinator.continueWithPin) {
      onboardingCoordinator.navigateFromAppLockSetup()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      coordinator.onCreate()
    }
  }

  private fun onDestinationChanged(destination: SetupAppLockCoordinator.Destination) {
    val fragment = when (destination) {
      SetupAppLockCoordinator.Destination.EnterPin -> SetupAppLockEnterPinFragment()
      SetupAppLockCoordinator.Destination.ConfirmPin -> SetupAppLockConfirmPinFragment()
    }

    childFragmentManager.navigate(R.id.fragment_container, fragment)
  }

  companion object {

    fun newInstance(): SetupAppLockContainerFragment {
      return SetupAppLockContainerFragment()
    }
  }
}