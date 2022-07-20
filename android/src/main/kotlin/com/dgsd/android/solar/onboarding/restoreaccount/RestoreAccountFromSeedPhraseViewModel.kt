package com.dgsd.android.solar.onboarding.restoreaccount

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.common.model.UserFacingException
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.keygen.MnemonicPhraseLength
import kotlinx.coroutines.flow.*

class RestoreAccountViewSeedPhraseViewModel(
  private val application: Application,
  errorMessageFactory: ErrorMessageFactory,
) : ViewModel() {

  private val generateSeedKeyPairResourceConsumer =
    ResourceFlowConsumer<AccountSeedInfo>(viewModelScope)

  val continueWithSeed =
    generateSeedKeyPairResourceConsumer.data.filterNotNull().asEventFlow(viewModelScope)

  val isLoading = generateSeedKeyPairResourceConsumer.isLoading

  private val error = generateSeedKeyPairResourceConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }

  private val _errorMessage = MutableStateFlow<CharSequence>("")
  val errorMessage = _errorMessage.asStateFlow()

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

  init {
    onEach(error) {
      _errorMessage.value = it
    }
  }

  fun onInputChanged(text: String) {
    rawInput.value = text
    _errorMessage.value = ""
  }

  fun onNextButtonClicked() {
    generateSeedKeyPairResourceConsumer.collectFlow(
      resourceFlowOf {
        val seedPhrase = words.value

        val validPhraseLengths = MnemonicPhraseLength.values().map { it.wordCount }.toSet()
        if (seedPhrase.size !in validPhraseLengths) {
          throw UserFacingException(application.getString(R.string.error_invalid_seed_phrase_length))
        } else {
          val validWords = KeyFactory.getValidMnemonicWords().toSet()
          val invalidWords = seedPhrase.filter { it !in  validWords}

          if (invalidWords.isEmpty()) {
            val passPhrase = inputtedPassword.value

            // Make sure that keypair can be generated..
            KeyFactory.createKeyPairFromSeed(
              KeyFactory.createSeedFromMnemonic(seedPhrase, passPhrase)
            )

            AccountSeedInfo(SensitiveList(seedPhrase), SensitiveString(passPhrase))
          } else {
            throw UserFacingException(
              application.getString(
                R.string.error_invalid_seed_phrase_words_template,
                invalidWords.joinToString(", ")
              )
            )
          }
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