package com.dgsd.android.solar.onboarding

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.di.util.parentViewModel

class OnboardingWelcomeFragment : Fragment(R.layout.frag_onboarding_welcome) {

  private val onboardingCoordinator: OnboardingCoordinator by parentViewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.requireViewById<View>(R.id.already_have_account).setOnClickListener {
      onboardingCoordinator.navigateToAddFromSeedPhrase()
    }

    view.requireViewById<View>(R.id.create_new_wallet).setOnClickListener {
      onboardingCoordinator.navigateToCreateNewAccount()
    }

    view.requireViewById<TextView>(R.id.app_name).text = RichTextFormatter.expandTemplate(
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