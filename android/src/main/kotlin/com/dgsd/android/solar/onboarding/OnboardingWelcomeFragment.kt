package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.setContent

class OnboardingWelcomeFragment : Fragment() {

    private val onboardingCoordinator: OnboardingCoordinator by parentViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = setContent {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Welcome!",
            )

            Button(onClick = { onboardingCoordinator.navigateToAddFromPrivateKey() }) {
                Text("Add account from private key")
            }

            Button(onClick = { onboardingCoordinator.navigateToAddFromSeedPhrase() }) {
                Text("Add account from seed phrase")
            }

            Button(onClick = { onboardingCoordinator.navigateToCreateNewAccount() }) {
                Text("Create new account")
            }

            Button(onClick = { onboardingCoordinator.navigateToExplainer() }) {
                Text("Help me")
            }
        }
    }
}