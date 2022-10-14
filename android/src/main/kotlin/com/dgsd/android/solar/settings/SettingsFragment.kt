package com.dgsd.android.solar.settings

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.showBiometricPrompt
import com.dgsd.android.solar.common.actionsheet.extensions.showActionSheet
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.common.modalsheet.extensions.showModal
import com.dgsd.android.solar.common.modalsheet.extensions.showModalFromErrorMessage
import com.dgsd.android.solar.common.modalsheet.model.ModalInfo
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.showSnackbar
import com.dgsd.ksol.core.model.Cluster
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.frag_settings) {

  private val viewModel: SettingsViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val settingViewSecretPhrase = view.requireViewById<View>(R.id.setting_item_view_secret_phrase)
    val settingCluster = view.requireViewById<View>(R.id.setting_item_cluster)
    val settingClusterValue = view.requireViewById<TextView>(R.id.cluster_value)
    val settingSignOut = view.requireViewById<View>(R.id.setting_item_sign_out)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    settingViewSecretPhrase.setOnClickListener {
      viewModel.onSecretPhraseClicked()
    }

    settingCluster.setOnClickListener {
      viewModel.onClusterClicked()
    }

    settingSignOut.setOnClickListener {
      viewModel.onSignOutClicked()
    }

    onEach(viewModel.activeClusterText) {
      settingClusterValue.text = it
    }

    onEach(viewModel.showError) {
      showModalFromErrorMessage(it)
    }

    onEach(viewModel.showSuccessMessage) {
      showSnackbar(it)
    }

    onEach(viewModel.showClusterPicker) { clusterOptions ->
      showActionSheet(
        getString(R.string.settings_cluster_picker_bottomsheet_title),
        *clusterOptions.map {
          val title = when (it) {
            is Cluster.Custom -> it.rpcUrl
            Cluster.DEVNET -> getString(R.string.cluster_name_devnet)
            Cluster.MAINNET_BETA -> getString(R.string.cluster_name_mainnet_beta)
            Cluster.TESTNET -> getString(R.string.cluster_name_testnet)
          }

          ActionSheetItem(title) {
            viewModel.onClusterSelected(it)
          }
        }.toTypedArray()
      )
    }

    onEach(viewModel.showSeedPhrase) { seedPhrase ->
      val messageText = seedPhrase.joinToString(", ")
      showModal(
        ModalInfo(
          title = getString(R.string.settings_secret_phrase_bottomsheet_title),
          message = messageText,
          positiveButton = ModalInfo.ButtonInfo(
            getString(R.string.copy_to_clipboard),
          ) {
            viewModel.onCopySeedPhraseClicked(messageText)
          }
        )
      )
    }

    onEach(viewModel.showBiometricAuthenticationPrompt) {
      val result = showBiometricPrompt(it)
      viewModel.onBiometricPromptResult(result)
    }

    onEach(viewModel.showConfirmSignOut) {
      showModal(
        ModalInfo(
          title = getString(R.string.are_you_sure),
          message = getString(R.string.settings_confirm_sign_out_message),
          positiveButton = ModalInfo.ButtonInfo(
            getString(R.string.settings_confirm_sign_out_positive_button)
          ) {
            viewModel.onSignOutConfirmed()
          },
          negativeButton = ModalInfo.ButtonInfo(
            getString(R.string.settings_confirm_sign_out_negative_button)
          ),
        )
      )
    }
  }

  companion object {

    fun newInstance(): SettingsFragment {
      return SettingsFragment()
    }
  }
}