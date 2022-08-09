package com.dgsd.android.solar.mobilewalletadapter.signandsendtransactions

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.biometrics.showBiometricPrompt
import com.dgsd.android.solar.di.util.activityViewModel
import com.dgsd.android.solar.extensions.ensureViewCount
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.setUrl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MobileWalletAdapterSignAndSendTransactionFragment :
  Fragment(R.layout.frag_mobile_wallet_adapter_sign_and_send_transactions) {

  private val appCoordinator by activityViewModel<AppCoordinator>()
  private val viewModel by viewModel<MobileWalletAdapterSignAndSendTransactionViewModel> {
    parametersOf(
      checkNotNull(appCoordinator.walletAdapterCoordinator?.signAndSendTransactionsRequest),
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val icon = view.requireViewById<ImageView>(R.id.icon)
    val name = view.requireViewById<TextView>(R.id.name)
    val url = view.requireViewById<TextView>(R.id.url)
    val signButton = view.requireViewById<View>(R.id.send)
    val declinedButton = view.requireViewById<View>(R.id.decline)
    val signLoadingIndicator = view.requireViewById<View>(R.id.sign_loading_indicator)
    val transactionsHeader = view.requireViewById<TextView>(R.id.transactions_header)
    val transactionsContainer = view.requireViewById<LinearLayout>(R.id.transactions_container)

    url.movementMethod = LinkMovementMethod.getInstance()

    signButton.setOnClickListener {
      viewModel.onSignClicked()
    }

    declinedButton.setOnClickListener {
      viewModel.onDeclineClicked()
    }

    onEach(viewModel.requesterName) {
      name.text = it
    }

    onEach(viewModel.requesterIconUrl) {
      if (it == null) {
        icon.isVisible = false
      } else {
        icon.isVisible = true
        icon.setUrl(it)
      }
    }

    onEach(viewModel.requestUrl) {
      url.text = it
      url.isVisible = !it.isNullOrEmpty()
    }

    onEach(viewModel.isAuthorizationButtonsVisible) {
      signButton.isInvisible = !it
      declinedButton.isInvisible = !it
    }

    onEach(viewModel.showSigningLoadingIndicator) {
      signLoadingIndicator.isInvisible = !it
      signButton.isInvisible = it
      declinedButton.isInvisible = it
    }

    onEach(viewModel.transactionSummaries) {
      transactionsHeader.text = resources.getQuantityString(
        R.plurals.mobile_wallet_adapter_sign_transaction_transactions_header_template,
        it.size,
        it.size
      )
      transactionsContainer.bindTransactionSummaries(it)
    }

    onEach(viewModel.showBiometricAuthenticationPrompt) {
      val result = showBiometricPrompt(it)
      viewModel.onBiometricPromptResult(result)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private fun LinearLayout.bindTransactionSummaries(summaries: List<CharSequence>) {
    ensureViewCount(summaries.size) {
      LayoutInflater.from(context).inflate(
        R.layout.view_mobile_wallet_adapter_send_transaction_summary,
        this,
        true
      )
    }

    children.toList().zip(summaries) { view, signature ->
      (view as TextView).text = signature
    }
  }

  companion object {

    fun newInstance(): MobileWalletAdapterSignAndSendTransactionFragment {
      return MobileWalletAdapterSignAndSendTransactionFragment()
    }
  }
}