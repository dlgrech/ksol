package com.dgsd.android.solar.onboarding.restoreaccount

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.UserFacingException
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import com.dgsd.ksol.model.KeyPair
import kotlinx.coroutines.flow.*

class RestoreAccountViewSeedPhraseViewModel(
  private val application: Application,
  errorMessageFactory: ErrorMessageFactory,
) : ViewModel() {

  private val generateSeedKeyPairResourceConsumer =
    ResourceFlowConsumer<KeyPair>(viewModelScope)

  val continueWithSeed =
    generateSeedKeyPairResourceConsumer.data.filterNotNull().asEventFlow(viewModelScope)

  val isLoading = generateSeedKeyPairResourceConsumer.isLoading

  val errorMessage = generateSeedKeyPairResourceConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  private val rawInput = MutableStateFlow("")
  private val words = rawInput
    .map { input ->
      input.filter { c -> c != ',' }
    }.map { input ->
      input
        .split(' ')
        .map { it.trim() }
        .filter { word -> word.isNotEmpty() }
    }
    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

  private val _showPasswordInput = MutableEventFlow<String>()
  val showPasswordInput = _showPasswordInput.asEventFlow()

  private val _inputtedPassword = MutableStateFlow("")
  val inputtedPassword = _inputtedPassword.asStateFlow()

  fun onInputChanged(text: String) {
    rawInput.value = text
  }

  fun onNextButtonClicked() {
    generateSeedKeyPairResourceConsumer.collectFlow(
      resourceFlowOf {
        val seedPhrase = words.value

        val validPhraseLengths = MnemonicPhraseLength.values().map { it.wordCount }.toSet()
        if (seedPhrase.size !in validPhraseLengths) {
          throw UserFacingException(application.getString(R.string.error_invalid_seed_phrase_length))
        } else {
          val seed = KeyFactory.createSeedFromMnemonic(seedPhrase, inputtedPassword.value)
          KeyFactory.createKeyPairFromSeed(seed)
        }
      }
    )
  }

  fun onAddPassphraseClicked() {
    _showPasswordInput.tryEmit(_inputtedPassword.value)
  }

  fun onPasswordInputConfirmed(password: String) {
    _inputtedPassword.value = password
  }
}