package com.dgsd.android.solar.onboarding.createaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.model.SensitiveList
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.ksol.keygen.KeyFactory
import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.flow.mapNotNull

private const val NUMBER_OF_ADDRESSES_TO_GENERATE = 20

class CreateAccountAddressSelectionViewModel(
    private val passphrase: SensitiveString,
    private val seedPhrase: SensitiveList<String>,
) : ViewModel() {

    private val addressesResourceConsumer = ResourceFlowConsumer<List<KeyPair>>(viewModelScope)

    private val keyPairs = addressesResourceConsumer.data

    val generatedAddress = keyPairs.mapNotNull { keyPairs ->
        keyPairs?.firstOrNull()?.publicKey
    }

    val alternativeAddresses = keyPairs.mapNotNull { keyPairs ->
        keyPairs?.drop(1)?.map { it.publicKey }
    }

    val isLoading = addressesResourceConsumer.isLoading

    private val _continueWithGeneratedKeyPair = MutableEventFlow<KeyPair>()
    val continueWithGeneratedKeyPair = _continueWithGeneratedKeyPair.asEventFlow()

    init {
        addressesResourceConsumer.collectFlow(
            resourceFlowOf {
                0.rangeTo(NUMBER_OF_ADDRESSES_TO_GENERATE)
                    .map { accountIndex ->
                        KeyFactory.createKeyPairFromMnemonic(
                            seedPhrase,
                            passphrase?.sensitiveValue,
                            accountIndex
                        )
                    }
            }
        )
    }

    fun onAddressSelected(key: PublicKey) {
        val selectedKeyPair = keyPairs.value.orEmpty().single { it.publicKey == key }
        _continueWithGeneratedKeyPair.tryEmit(selectedKeyPair)
    }

}