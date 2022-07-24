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
import com.dgsd.android.solar.receive.ReceiveFragment
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

    setContentView(R.layout.view_fragment_container)

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
      Destination.Receive -> ScreenTransitionType.SLIDE_FROM_BOTTOM
      Destination.Settings -> ScreenTransitionType.DEFAULT
      Destination.TransactionList -> ScreenTransitionType.DEFAULT
      is Destination.TransactionDetails -> ScreenTransitionType.DEFAULT
    }
  }

  private fun getFragmentForDestination(destination: Destination): Fragment {
    return when (destination) {
      Destination.Home -> HomeFragment.newInstance()
      Destination.Onboarding -> OnboardingContainerFragment.newInstance()
      Destination.Settings -> SettingsFragment.newInstance()
      Destination.Receive -> ReceiveFragment.newInstance()
      Destination.TransactionList -> TransactionListFragment.newInstance()
      is Destination.TransactionDetails ->
        TransactionDetailsFragment.newInstance(destination.signature)
    }
  }

  private fun shouldResetBackStackForDestination(destination: Destination): Boolean {
    return when (destination) {
      Destination.Home,
      Destination.Onboarding -> true

      Destination.Receive,
      Destination.Settings,
      Destination.TransactionList,
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
