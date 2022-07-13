package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel

class OnboardingWelcomeFragment : Fragment(R.layout.frag_onboarding_welcome) {

    private val onboardingCoordinator: OnboardingCoordinator by parentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.already_have_account).setOnClickListener {
            onboardingCoordinator.navigateToCreateNewAccount()
        }

        view.findViewById<View>(R.id.create_new_wallet).setOnClickListener {
            onboardingCoordinator.navigateToCreateNewAccount()
        }

        view.findViewById<View>(R.id.help).setOnClickListener {
            onboardingCoordinator.navigateToExplainer()
        }
    }
}