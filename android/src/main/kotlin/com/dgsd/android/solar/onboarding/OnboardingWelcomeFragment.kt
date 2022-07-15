package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.di.util.parentViewModel

class OnboardingWelcomeFragment : Fragment(R.layout.frag_onboarding_welcome) {

  private val onboardingCoordinator: OnboardingCoordinator by parentViewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<View>(R.id.already_have_account).setOnClickListener {
      showActionSheet(
        ActionSheetItem(
          getString(R.string.onboarding_welcome_action_sheet_restore_from_private_key)
        ) {
          onboardingCoordinator.navigateToAddFromPrivateKey()
        },

        ActionSheetItem(
          getString(R.string.onboarding_welcome_action_sheet_restore_from_seed_phrase)
        ) {
          onboardingCoordinator.navigateToAddFromSeedPhrase()
        },
      )
    }

    view.findViewById<View>(R.id.create_new_wallet).setOnClickListener {
      onboardingCoordinator.navigateToCreateNewAccount()
    }

    view.findViewById<TextView>(R.id.app_name).text = RichTextFormatter.expandTemplate(
      requireContext(),
      R.string.onboarding_welcome_screen_title_app_name_template,
      RichTextFormatter.coloredTextAttr(
        requireContext(),
        R.attr.colorPrimary,
        getString(R.string.onboarding_welcome_screen_title_app_name),
      )
    )
  }
}