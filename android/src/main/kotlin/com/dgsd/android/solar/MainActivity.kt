package com.dgsd.android.solar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator.Destination
import com.dgsd.android.solar.applock.verification.AppEntryLockScreenFragment
import com.dgsd.android.solar.common.model.ScreenTransitionType
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.home.HomeFragment
import com.dgsd.android.solar.mobilewalletadapter.authorize.MobileWalletAdapterAuthorizeFragment
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

    onEach(appCoordinator.destination) {
      onDestinationChanged(it)
    }

    onEach(appCoordinator.close) {
      finish()
    }

    lifecycleScope.launchWhenStarted {
      appCoordinator.onCreate()
    }
  }

  override fun onResume() {
    super.onResume()
    lifecycleScope.launchWhenResumed {
      appCoordinator.onResume(intent.data, callingPackage)
      intent = intent?.apply { data = null }
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    appCoordinator.onNewIntent(intent?.data, callingPackage)
    setIntent(intent?.apply { data = null })
  }

  private fun onDestinationChanged(destination: Destination) {
    if (destination is Destination.CompositeDestination) {
      destination.destinations.forEachIndexed { index, dest ->
        onDestinationChanged(dest, commitNow = index == 0)
      }
    } else {
      onDestinationChanged(destination, commitNow = false)
    }
  }

  private fun onDestinationChanged(destination: Destination, commitNow: Boolean) {
    val fragment = getFragmentForDestination(destination)
    val shouldResetBackStack = shouldResetBackStackForDestination(destination)
    val transitionType = getScreenTransitionForDestination(destination)

    navigateToFragment(
      fragment = fragment,
      resetBackStack = shouldResetBackStack,
      screenTransitionType = transitionType,
      commitNow = commitNow,
    )
  }

  private fun getScreenTransitionForDestination(destination: Destination): ScreenTransitionType {
    return when (destination) {
      Destination.Home -> ScreenTransitionType.FADE
      Destination.Onboarding -> ScreenTransitionType.FADE
      Destination.AppEntryLock -> ScreenTransitionType.FADE
      Destination.MobileWalletAdapterAuthorize -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.RequestAmount -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.ShareWalletAddress -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithAddress -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithNearby -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.SendWithQR -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      is Destination.SendWithSolPayRequest -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.Settings -> ScreenTransitionType.DEFAULT
      Destination.TransactionList -> ScreenTransitionType.DEFAULT
      is Destination.TransactionDetails -> ScreenTransitionType.DEFAULT
      is Destination.CompositeDestination -> error("Trying to get transition for composite")
    }
  }

  private fun getFragmentForDestination(destination: Destination): Fragment {
    return when (destination) {
      Destination.AppEntryLock -> AppEntryLockScreenFragment.newInstance()
      Destination.Home -> HomeFragment.newInstance()
      Destination.Onboarding -> OnboardingContainerFragment.newInstance()
      Destination.RequestAmount -> RequestAmountContainerFragment.newInstance()
      Destination.Settings -> SettingsFragment.newInstance()
      Destination.ShareWalletAddress -> ReceiveShareAddressFragment.newInstance()
      Destination.TransactionList -> TransactionListFragment.newInstance()
      is Destination.TransactionDetails ->
        TransactionDetailsFragment.newInstance(destination.signature)
      Destination.SendWithAddress -> SendContainerFragment.newEnterAddressInstance()
      Destination.SendWithNearby -> SendContainerFragment.newEnterAddressInstance()
      Destination.SendWithQR -> SendContainerFragment.newQRScanInstance()
      is Destination.SendWithSolPayRequest ->
        SendContainerFragment.newTransferRequestInstance(destination.requestUrl)
      is Destination.CompositeDestination -> error("Trying to get fragment for composite")
      Destination.MobileWalletAdapterAuthorize -> MobileWalletAdapterAuthorizeFragment.newInstance()
    }
  }

  private fun shouldResetBackStackForDestination(destination: Destination): Boolean {
    return when (destination) {
      Destination.AppEntryLock,
      Destination.Home,
      Destination.Onboarding,
      Destination.MobileWalletAdapterAuthorize -> true
      else -> false
    }
  }

  private fun navigateToFragment(
    fragment: Fragment,
    resetBackStack: Boolean,
    screenTransitionType: ScreenTransitionType,
    commitNow: Boolean
  ) {
    supportFragmentManager.navigate(
      containerId = R.id.fragment_container,
      fragment = fragment,
      screenTransitionType = screenTransitionType,
      resetBackStack = resetBackStack,
      commitNow = commitNow,
    )
  }
}
