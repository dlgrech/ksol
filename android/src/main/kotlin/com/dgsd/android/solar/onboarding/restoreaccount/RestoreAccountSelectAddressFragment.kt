package com.dgsd.android.solar.onboarding.restoreaccount

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestoreAccountSelectAddressFragment :
  Fragment(R.layout.frag_onboarding_restore_account_select_address) {

  private val coordinator: RestoreAccountCoordinator by parentViewModel()
  private val viewModel: RestoreAccountSelectAddressViewModel by viewModel {
    parametersOf(
      checkNotNull(coordinator.seedPhrase)
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
    val loadingIndicator = view.findViewById<ProgressBar>(R.id.loading_indicator)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }
}