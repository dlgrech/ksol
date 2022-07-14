package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountConfirmationFragment : Fragment(R.layout.frag_create_account_confirmation) {

  private val createAccountCoordinator: CreateAccountCoordinator by parentViewModel()
  private val viewModel: CreateAccountViewSeedPhraseViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }
}