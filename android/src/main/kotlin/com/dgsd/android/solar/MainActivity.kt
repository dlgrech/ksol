package com.dgsd.android.solar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator.Destination
import com.dgsd.android.solar.extensions.navigate
import com.dgsd.android.solar.home.HomeFragment
import com.dgsd.android.solar.onboarding.OnboardingContainerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val appCoordinator: AppCoordinator by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // We explicitly dont restore our state here, so that we're always starting afresh
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

        navigateToFragment(fragment, shouldResetBackStack)
    }

    private fun getFragmentForDestination(destination: Destination): Fragment {
        return when (destination) {
            Destination.Home -> HomeFragment.newInstance()
            Destination.Onboarding -> OnboardingContainerFragment.newInstance()
        }
    }

    private fun shouldResetBackStackForDestination(destination: Destination): Boolean {
        return when (destination) {
            Destination.Home,
            Destination.Onboarding -> true
        }
    }

    private fun navigateToFragment(
        fragment: Fragment,
        resetBackStack: Boolean,
    ) {
        if (resetBackStack) {
            supportFragmentManager.popBackStackImmediate(null, POP_BACK_STACK_INCLUSIVE)
        }
        supportFragmentManager.navigate(R.id.fragment_container, fragment)
    }
}
