package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.setContent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CreateAccountEnterPassphraseFragment : Fragment() {

    private val viewModel: CreateAccountEnterPassphraseViewModel by viewModels()
    private val createNewAccountCoordinator: CreateAccountCoordinator by parentViewModel()

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
                text = "Enter Passphrase",
            )
            Button(
                onClick = { viewModel.onContinueClicked() }
            ) {
                Text(text = "Continue")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.continueWithPassphrase.onEach {
            createNewAccountCoordinator.onPassphraseConfirmed(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}