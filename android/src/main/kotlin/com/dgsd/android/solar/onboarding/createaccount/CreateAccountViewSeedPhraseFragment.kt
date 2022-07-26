package com.dgsd.android.solar.onboarding.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModal
import com.dgsd.android.solar.common.modalsheet.extensions.showModelFromErrorMessage
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountViewSeedPhraseFragment :
  Fragment(R.layout.frag_create_account_view_seed_phrase) {

  private val createAccountCoordinator: CreateAccountCoordinator by parentViewModel()
  private val viewModel: CreateAccountViewSeedPhraseViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val seedPhraseContainer =
      requireView().findViewById<ConstraintLayout>(R.id.seed_phrase_container)
    val explainerMessage = requireView().findViewById<TextView>(R.id.explainer_message)
    val nextButton = requireView().findViewById<View>(R.id.next)
    val copyButton = requireView().findViewById<TextView>(R.id.copy)
    val loadingIndicator = requireView().findViewById<View>(R.id.loading_indicator)

    view.findViewById<MaterialToolbar>(R.id.toolbar).apply {
      setNavigationOnClickListener {
        requireActivity().onBackPressed()
      }
    }

    explainerMessage.text = RichTextFormatter.expandTemplate(
      requireContext(),
      R.string.create_account_seed_phrase_explanation_template,
      RichTextFormatter.coloredTextAttr(
        requireContext(),
        R.attr.colorPrimary,
        RichTextFormatter.bold(
          requireContext(),
          R.string.create_account_seed_phrase_explanation_bold_text
        )
      )
    )

    nextButton.setOnClickListener {
      viewModel.onNextButtonClicked()
    }

    copyButton.setOnClickListener {
      viewModel.onCopyButtonClicked()
    }

    onEach(viewModel.continueWithSeedPhrase) { seedPhrase ->
      showModal(
        ModalInfo(
          title = getString(R.string.create_account_seed_phrase_confirmation_title),
          message = getString(R.string.create_account_seed_phrase_confirmation_message),
          positiveButton = ModalInfo.ButtonInfo(getString(R.string.create_account_seed_phrase_confirmation_positive_button)) {
            createAccountCoordinator.onSeedPhraseConfirmed(seedPhrase)
          },
        )
      )
    }

    onEach(viewModel.seedPhrase) { seedPhrase ->
      seedPhraseContainer.populate(seedPhrase.orEmpty())
    }

    onEach(viewModel.showLoadingState) {
      loadingIndicator.isInvisible = !it
      seedPhraseContainer.isInvisible = it
      copyButton.isInvisible = it
      nextButton.isInvisible = it
    }

    onEach(viewModel.errorMessage) {
      showModelFromErrorMessage(it)
    }

    onEach(viewModel.showSeedPhraseCopiedSuccess) {
      Snackbar.make(
        view,
        R.string.seed_phrase_copied_to_clipboard_success_message,
        Snackbar.LENGTH_SHORT
      ).show()
    }
  }

  private fun ConstraintLayout.populate(seedPhrase: List<String>) {
    removeAllViews()
    val flow = createGridFlow()
    addView(flow)

    val referencedIds = IntArray(seedPhrase.size)
    seedPhrase.forEachIndexed { index, word ->
      val wordContainer = LayoutInflater.from(requireContext())
        .inflate(R.layout.view_seed_phrase_word, this, false) as ViewGroup
      wordContainer.id = View.generateViewId()

      wordContainer.findViewById<TextView>(R.id.index).text = index.plus(1).toString()
      wordContainer.findViewById<TextView>(R.id.word).text = word

      addView(wordContainer)

      referencedIds[index] = wordContainer.id
    }

    flow.referencedIds = referencedIds
  }

  private fun createGridFlow(): Flow {
    return Flow(requireContext()).apply {
      id = View.generateViewId()
      setWrapMode(Flow.WRAP_CHAIN)
      setHorizontalStyle(Flow.CHAIN_PACKED)
      setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START)
      setOrientation(Flow.HORIZONTAL)
      setVerticalGap(resources.getDimensionPixelSize(R.dimen.padding_small))
      setHorizontalGap(resources.getDimensionPixelSize(R.dimen.padding_small))
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
      )
    }
  }
}