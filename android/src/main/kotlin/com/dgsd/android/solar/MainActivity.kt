package com.dgsd.android.solar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator.Destination
import com.dgsd.android.solar.common.model.ScreenTransitionType
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.home.HomeFragment
import com.dgsd.android.solar.onboarding.OnboardingContainerFragment
import com.dgsd.android.solar.receive.requestamount.RequestAmountContainerFragment
import com.dgsd.android.solar.receive.shareaddress.ReceiveShareAddressFragment
import com.dgsd.android.solar.send.SendContainerFragment
import com.dgsd.android.solar.settings.SettingsFragment
import com.dgsd.android.solar.transaction.details.TransactionDetailsFragment
import com.dgsd.android.solar.transaction.list.TransactionListFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

  private val appCoordinator: AppCoordinator by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    // We explicitly don't restore our state here, so that we're always starting afresh
    super.onCreate(null)

    setContentView(R.layout.act_main)

    appCoordinator.destination
      .onEach(::onDestinationChanged)
      .launchIn(lifecycleScope)

    lifecycleScope.launchWhenStarted {
      appCoordinator.onCreate()
    }
  }

  private fun onDestinationChanged(destination: Destination) {
    val fragment = getFragmentForDestination(destination)
    val shouldResetBackStack = shouldResetBackStackForDestination(destination)
    val transitionType = getScreenTransitionForDestination(destination)

    navigateToFragment(
      fragment = fragment,
      resetBackStack = shouldResetBackStack,
      screenTransitionType = transitionType,
    )
  }

  private fun getScreenTransitionForDestination(destination: Destination): ScreenTransitionType {
    return when (destination) {
      Destination.Home -> ScreenTransitionType.FADE
      Destination.Onboarding -> ScreenTransitionType.FADE
      Destination.RequestAmount -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.ShareWalletAddress -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithAddress -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithHistoricalAddress -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithNearby -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithQR -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.Settings -> ScreenTransitionType.DEFAULT
      Destination.TransactionList -> ScreenTransitionType.DEFAULT
      is Destination.TransactionDetails -> ScreenTransitionType.DEFAULT
    }
  }

  private fun getFragmentForDestination(destination: Destination): Fragment {
    return when (destination) {
      Destination.Home -> HomeFragment.newInstance()
      Destination.Onboarding -> OnboardingContainerFragment.newInstance()
      Destination.RequestAmount -> RequestAmountContainerFragment.newInstance()
      Destination.Settings -> SettingsFragment.newInstance()
      Destination.ShareWalletAddress -> ReceiveShareAddressFragment.newInstance()
      Destination.TransactionList -> TransactionListFragment.newInstance()
      is Destination.TransactionDetails ->
        TransactionDetailsFragment.newInstance(destination.signature)
      Destination.SendWithAddress -> SendContainerFragment.newEnterAddressInstance()
      Destination.SendWithHistoricalAddress -> SendContainerFragment.newPreviousTransactionAddressInstance()
      Destination.SendWithNearby -> SendContainerFragment.newEnterAddressInstance()
      Destination.SendWithQR -> SendContainerFragment.newQRScanInstance()
    }
  }

  private fun shouldResetBackStackForDestination(destination: Destination): Boolean {
    return when (destination) {
      Destination.Home,
      Destination.Onboarding -> true

      Destination.RequestAmount,
      Destination.ShareWalletAddress,
      Destination.Settings,
      Destination.TransactionList,
      Destination.SendWithAddress,
      Destination.SendWithHistoricalAddress,
      Destination.SendWithNearby,
      Destination.SendWithQR,
      is Destination.TransactionDetails -> false
    }
  }

  private fun navigateToFragment(
    fragment: Fragment,
    resetBackStack: Boolean,
    screenTransitionType: ScreenTransitionType,
  ) {
    supportFragmentManager.navigate(
      containerId = R.id.fragment_container,
      fragment = fragment,
      screenTransitionType = screenTransitionType,
      resetBackStack = resetBackStack
    )
  }
}
