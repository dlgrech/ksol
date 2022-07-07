package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
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
        Text(
            text = "Select Address",
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        )
    }
}