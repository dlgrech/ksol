package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.keygen.KeyFactory
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class CreateAccountViewSeedPhraseViewModel(
    errorMessageFactory: ErrorMessageFactory,
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

    init {
        generateMnemonicConsumer.collectFlow {
            SensitiveList(KeyFactory.createMnemonic())
        }
    }

    fun onNextButtonClicked() {
        val seedPhrase = checkNotNull(seedPhrase.value)
        _continueWithSeedPhrase.tryEmit(seedPhrase)
    }
}