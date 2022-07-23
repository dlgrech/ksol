package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.model.KeyPair
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

private const val GENERATE_KEYPAIR_ARTIFICIAL_DELAY_MS = 1500L

class CreateAccountConfirmationViewModel(
  errorMessageFactory: ErrorMessageFactory,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val systemClipboard: SystemClipboard,
  private val accountSeedInfo: AccountSeedInfo,
) : ViewModel() {

  private val generateKeyPairConsumer = ResourceFlowConsumer<KeyPair>(viewModelScope)

  val isLoading = generateKeyPairConsumer.isLoading

  val publicKeyText =
    generateKeyPairConsumer.data.mapNotNull { it?.publicKey?.let(publicKeyFormatter::format) }

  val errorMessage = generateKeyPairConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  private val _continueWithFlow = MutableEventFlow<KeyPair>()
  val continueWithFlow = _continueWithFlow.asEventFlow()

  private val _showCopiedSuccessMessage = SimpleMutableEventFlow()
  val showCopiedSuccessMessage = _showCopiedSuccessMessage.asEventFlow()

  init {
    generateKeyPairConsumer.collectFlow(
      resourceFlowOf {
        delay(GENERATE_KEYPAIR_ARTIFICIAL_DELAY_MS)
        KeyFactory.createKeyPairFromMnemonic(
          accountSeedInfo.seedPhrase,
          accountSeedInfo.passPhrase.sensitiveValue
        )
      }
    )
  }

  fun onContinueClicked() {
    val keyPair = generateKeyPairConsumer.data.value
    if (keyPair != null) {
      _continueWithFlow.tryEmit(keyPair)
    }
  }

  fun onAddressClicked() {
    val keyPair = generateKeyPairConsumer.data.value
    if (keyPair != null) {
      systemClipboard.copy(keyPair.publicKey.toBase58String())
      _showCopiedSuccessMessage.call()
    }
  }
}