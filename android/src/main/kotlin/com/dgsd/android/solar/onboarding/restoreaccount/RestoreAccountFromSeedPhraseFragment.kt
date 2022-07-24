package com.dgsd.android.solar.onboarding.restoreaccount

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.transition.TransitionManager
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class RestoreAccountFromSeedPhraseFragment :
  Fragment(R.layout.frag_onboarding_restore_account_from_seed) {

  private val restoreAccountCoordinator: RestoreAccountCoordinator by parentViewModel()
  private val viewModel: RestoreAccountViewSeedPhraseViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
    val loadingIndicator = view.findViewById<ProgressBar>(R.id.loading_indicator)
    val seedPhraseInput = view.findViewById<EditText>(R.id.seed_phrase_input)
    val nextButton = view.findViewById<View>(R.id.next)
    val addPassphrase = view.findViewById<View>(R.id.add_passphrase)
    val passphraseValue = view.findViewById<TextView>(R.id.passphrase_value)
    val errorMessage = view.findViewById<TextView>(R.id.error_message)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    seedPhraseInput.doAfterTextChanged { text ->
      viewModel.onInputChanged(text.toString())
    }

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    addPassphrase.setOnClickListener {
      viewModel.onAddPassphraseClicked()
    }

    passphraseValue.setOnClickListener {
      viewModel.onAddPassphraseClicked()
    }

    onEach(viewModel.isLoading) {
      TransitionManager.beginDelayedTransition(view as ViewGroup)
      loadingIndicator.isInvisible = !it
      nextButton.isInvisible = it
    }

    onEach(viewModel.errorMessage) {
      TransitionManager.beginDelayedTransition(view as ViewGroup)
      errorMessage.text = it

      if (it.isEmpty()) {
        errorMessage.isVisible = false
        seedPhraseInput.backgroundTintList = null
        seedPhraseInput.setTextColor(requireContext().getColorAttr(android.R.attr.textColorPrimary))
      } else {
        errorMessage.isVisible = true
        seedPhraseInput.backgroundTintList = ColorStateList.valueOf(
          requireContext().getColorAttr(R.attr.colorError)
        )
        seedPhraseInput.setTextColor(requireContext().getColorAttr(R.attr.colorError))
      }
    }

    onEach(viewModel.continueWithSeed) { seedInfo ->
      restoreAccountCoordinator.onSeedGenerated(seedInfo)
    }

    onEach(viewModel.inputtedPassword) { password ->
      passphraseValue.isVisible = password.isNotEmpty()
      passphraseValue.text = TextUtils.concat(
        RichTextFormatter.bold(
          requireContext(),
          R.string.onboarding_restore_account_from_seed_passphrase_label
        ),
        "\t",
        password
      )
    }

    onEach(viewModel.showPasswordInput) { passwordValue ->
      val dialogInputContainer = LayoutInflater.from(requireContext()).inflate(
        R.layout.dialog_input_password,
        null
      ) as ViewGroup
      val inputView = dialogInputContainer.findViewById<EditText>(R.id.input_value)
      inputView.setText(passwordValue)

      MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.onboarding_restore_account_from_seed_passphrase_dialog_title)
        .setView(dialogInputContainer)
        .setPositiveButton(R.string.save) { _, _ ->
          viewModel.onPasswordInputConfirmed(inputView.text?.toString().orEmpty())
        }
        .show()
    }
  }
}