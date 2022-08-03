package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class SendEnterAmountViewModel(
  application: Application,
  private val errorMessageFactory: ErrorMessageFactory,
  private val session: WalletSession,
  private val solanaApi: SolanaApi,
) : AndroidViewModel(application) {

  private val balanceResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)

  private val _errorMessage = MutableEventFlow<CharSequence>()
  val errorMessage = _errorMessage.asEventFlow()

  val balanceText = balanceResourceConsumer.data.filterNotNull().map {
    RichTextFormatter.expandTemplate(
      application,
      R.string.send_enter_amount_balance_template,
      RichTextFormatter.bold(SolTokenFormatter.formatLong(it))
    )
  }

  fun onCreate() {
    balanceResourceConsumer.collectFlow(
      resourceFlowOf {
        solanaApi.getBalance(session.publicKey)
      }
    )
  }

  fun onNextButtonClicked() {
  }
}