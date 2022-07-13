package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModal
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.google.android.material.appbar.MaterialToolbar

class CreateAccountEnterPassphraseFragment :
  Fragment(R.layout.frag_create_account_enter_passphrase) {

  private val createNewAccountCoordinator: CreateAccountCoordinator by parentViewModel()
  private val viewModel: CreateAccountEnterPassphraseViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
      setNavigationOnClickListener {
        requireActivity().onBackPressed()
      }
    }

    view.findViewById<View>(R.id.skip).setOnClickListener {
      viewModel.onSkipClicked()
    }

    view.findViewById<View>(R.id.next).setOnClickListener {
      val password = view.findViewById<EditText>(R.id.password_input).text?.toString()
      viewModel.onContinueClicked(password)
    }

    onEach(viewModel.continueWithPassphrase) {
      createNewAccountCoordinator.onPassphraseConfirmed(it)
    }

    onEach(viewModel.showContinueWithoutPassphraseWarning) {
      showModal(
        ModalInfo(
          title = getString(R.string.are_you_sure),
          message = getString(R.string.create_account_enter_passphrase_skip_warning),
          positiveButton = ModalInfo.ButtonInfo(
            text = getString(R.string.create_account_enter_passphrase_skip_warning_positive_button)
          ) {
            viewModel.onSkipConfirmed()
          },
          negativeButton = ModalInfo.ButtonInfo(
            text = getString(R.string.go_back)
          ),
        )
      )
    }
  }
}