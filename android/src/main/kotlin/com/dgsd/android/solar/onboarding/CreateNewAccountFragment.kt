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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.common.util.collectAsStateLifecycleAware
import com.dgsd.android.solar.extensions.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateNewAccountFragment : Fragment() {

    private val viewModel by viewModel<CreateNewAccountViewModel>()

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
}