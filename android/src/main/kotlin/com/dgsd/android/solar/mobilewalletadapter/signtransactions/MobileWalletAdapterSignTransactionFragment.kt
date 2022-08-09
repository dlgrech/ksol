package com.dgsd.android.solar.mobilewalletadapter.signtransactions

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.AppCoordinator
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.activityViewModel
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.setUrl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MobileWalletAdapterSignTransactionFragment :
  Fragment(R.layout.frag_mobile_wallet_adapter_sign_transactions) {

  private val appCoordinator by activityViewModel<AppCoordinator>()
  private val viewModel by viewModel<MobileWalletAdapterSignTransactionViewModel> {
    parametersOf(
      checkNotNull(appCoordinator.walletAdapterCoordinator?.signTransactionsRequest),
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val icon = view.requireViewById<ImageView>(R.id.icon)
    val name = view.requireViewById<TextView>(R.id.name)
    val url = view.requireViewById<TextView>(R.id.url)
    val signButton = view.requireViewById<View>(R.id.sign)
    val declinedButton = view.requireViewById<View>(R.id.decline)
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

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(): MobileWalletAdapterSignTransactionFragment {
      return MobileWalletAdapterSignTransactionFragment()
    }
  }
}