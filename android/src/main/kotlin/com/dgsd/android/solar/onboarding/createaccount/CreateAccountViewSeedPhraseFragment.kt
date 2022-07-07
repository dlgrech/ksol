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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.common.util.collectAsStateLifecycleAware
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.setContent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountViewSeedPhraseFragment : Fragment() {

    private val createAccountCoordinator: CreateAccountCoordinator by parentViewModel()
    private val viewModel: CreateAccountViewSeedPhraseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = setContent {

        val isLoading: Boolean by viewModel.isLoading.collectAsStateLifecycleAware(initial = false)
        val seedPhraseWords by viewModel.seedPhrase.collectAsStateLifecycleAware(initial = emptyList())

        Column(
            Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
        ) {
            if (isLoading) {
                Text(
                    text = "Loading!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                Text(
                    text = "Your seed phrase: ${seedPhraseWords.orEmpty().joinToString(",")}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                )

                Button(onClick = { viewModel.onNextButtonClicked() }) {
                    Text(text = "Continue")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.continueWithSeedPhrase.onEach { seedPhrase ->
            createAccountCoordinator.onSeedPhraseConfirmed(seedPhrase)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}