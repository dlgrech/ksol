package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.showBiometricPrompt
import com.dgsd.android.solar.applock.setup.SetupAppLockContainerFragment
import com.dgsd.android.solar.common.modalsheet.extensions.showModal
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo
import com.dgsd.android.solar.common.model.ScreenTransitionType
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.OnboardingCoordinator.Destination
import com.dgsd.android.solar.onboarding.createaccount.CreateAccountContainerFragment
import com.dgsd.android.solar.onboarding.restoreaccount.RestoreAccountContainerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingContainerFragment : Fragment(R.layout.view_fragment_container) {

  private val coordinator: OnboardingCoordinator by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    onEach(coordinator.destination, ::onDestinationChanged)

    onEach(coordinator.showBiometricAuthenticationPrompt) { promptInfo ->
      val result = showBiometricPrompt(promptInfo)
      coordinator.onBiometricPromptResult(result)
    }

    onEach(coordinator.showErrorPersistingSecrets) {
      showModal(
        modalInfo = ModalInfo(
          title = getString(R.string.onboarding_error_persisting_secrets_title),
          message = getString(R.string.onboarding_error_persisting_secrets_message),
          positiveButton = ModalInfo.ButtonInfo(
            getString(android.R.string.ok)
          ),
          onDismiss = {
            coordinator.onErrorPersistingSecretsModalDismissed()
          }
        )
      )
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      coordinator.onCreate()
    }
  }

  private fun onDestinationChanged(destination: Destination) {
    val fragment = when (destination) {
      Destination.CreateNewWalletFlow -> CreateAccountContainerFragment.newInstance()
      Destination.RestoreSeedPhraseFlow -> RestoreAccountContainerFragment.newInstance()
      Destination.Welcome -> OnboardingWelcomeFragment()
      Destination.SetupAppLock -> SetupAppLockContainerFragment.newInstance()
    }

    val screenTransitionType = when (destination) {
      Destination.CreateNewWalletFlow,
      Destination.RestoreSeedPhraseFlow -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      else -> ScreenTransitionType.DEFAULT
    }

    childFragmentManager.navigate(R.id.fragment_container, fragment, screenTransitionType)
  }

  companion object {

    fun newInstance(): OnboardingContainerFragment {
      return OnboardingContainerFragment()
    }
  }
}