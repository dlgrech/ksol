package com.dgsd.android.solar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator.Destination
import com.dgsd.android.solar.extensions.findActiveFragmentById
import com.dgsd.android.solar.onboarding.OnboardingContainerFragment
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val appCoordinator: AppCoordinator by viewModel()

    private val fragmentContainerView by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<View>(R.id.fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        appCoordinator.destination
            .onEach(::onDestinationChanged)
            .launchIn(lifecycleScope)
    }

    private fun onDestinationChanged(destination: Destination) {
        val fragment = getFragmentForDestination(destination)
        val shouldResetBackStack = shouldResetBackStackForDestination(destination)

        navigateToFragment(fragment, shouldResetBackStack)
    }

    private fun getFragmentForDestination(destination: Destination): Fragment {
        return when (destination) {
            Destination.Home -> TODO("Not implemented yet")
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
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        supportFragmentManager.commit(allowStateLoss = true) {
            if (getCurrentFragment() != null) {
                setCustomAnimations(
                    R.anim.default_fragment_entry,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out,
                )

                addToBackStack(null)
            }

            replace(fragmentContainerView.id, fragment)
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findActiveFragmentById(fragmentContainerView.id)
    }

}
