package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.common.util.collectAsStateLifecycleAware
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CreateAccountAddressSelectionFragment : Fragment() {

    private val createNewAccountCoordinator: CreateAccountCoordinator by parentViewModel()
    private val viewModel: CreateAccountAddressSelectionViewModel by viewModel {
        parametersOf(
            checkNotNull(createNewAccountCoordinator.passphrase),
            checkNotNull(createNewAccountCoordinator.seedPhrase)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = setContent {

        val isLoading: Boolean by viewModel.isLoading.collectAsStateLifecycleAware(initial = false)
        val generatedAddress by viewModel.generatedAddress.collectAsStateLifecycleAware(initial = null)
        val alternativeAddresses by viewModel.alternativeAddresses.collectAsStateLifecycleAware(initial = null)

        if (isLoading) {
            Text(
                text = "Loading!",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            LazyColumn {
                if (generatedAddress != null) {
                    item {
                        Text("Generated Address = ${generatedAddress?.toBase58String()}")
                    }
                }

                item {
                    Text(
                        text = "Alternatives:",
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                alternativeAddresses?.forEach { address ->
                    item {
                        Text("Address = ${address.toBase58String()}")
                    }
                }
            }
        }
    }
}