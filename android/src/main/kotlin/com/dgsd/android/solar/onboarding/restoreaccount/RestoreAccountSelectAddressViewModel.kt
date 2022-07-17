package com.dgsd.android.solar.onboarding.restoreaccount

import androidx.lifecycle.ViewModel
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.ksol.SolanaApi

private const val NUMBER_OF_ACCOUNTS = 20

class RestoreAccountSelectAddressViewModel(
  errorMessageFactory: ErrorMessageFactory,
  private val solanaApi: SolanaApi,
  private val seedPhrase: SensitiveList<String>,
) : ViewModel() {

  fun onCreate() {
    // TODO: Load Balances & account info
  }
}