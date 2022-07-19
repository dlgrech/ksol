package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.ksol.keygen.KeyFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

private const val GENERATE_MNEMONIC_ARTIFICIAL_DELAY_MS = 1500L

class CreateAccountViewSeedPhraseViewModel(
  errorMessageFactory: ErrorMessageFactory,
  private val systemClipboard: SystemClipboard,
) : ViewModel() {

  private val generateMnemonicConsumer =
    ResourceFlowConsumer<SensitiveList<String>>(viewModelScope)

  val seedPhrase = generateMnemonicConsumer.data

  val isLoading = generateMnemonicConsumer.isLoading

  val errorMessage = generateMnemonicConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  private val _continueWithSeedPhrase = MutableEventFlow<SensitiveList<String>>()
  val continueWithSeedPhrase = _continueWithSeedPhrase.asEventFlow()

  private val _showSeedPhraseCopiedSuccess = SimpleMutableEventFlow()
  val showSeedPhraseCopiedSuccess = _showSeedPhraseCopiedSuccess.asEventFlow()

  init {
    generateMnemonicConsumer.collectFlow(
      resourceFlowOf {
        delay(GENERATE_MNEMONIC_ARTIFICIAL_DELAY_MS)
        SensitiveList(KeyFactory.createMnemonic())
      }
    )
  }

  fun onNextButtonClicked() {
    val seedPhrase = checkNotNull(seedPhrase.value)
    _continueWithSeedPhrase.tryEmit(seedPhrase)
  }

  fun onCopyButtonClicked() {
    val seedPhrase = checkNotNull(seedPhrase.value)
    systemClipboard.copy(seedPhrase.joinToString(" "))
      _showSeedPhraseCopiedSuccess.call()
  }
}