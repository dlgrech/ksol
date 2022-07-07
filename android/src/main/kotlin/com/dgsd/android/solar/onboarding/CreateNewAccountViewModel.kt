package com.dgsd.android.solar.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.anyTrue
import com.dgsd.android.solar.common.util.execute
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.ksol.keygen.KeyFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CreateNewAccountViewModel(
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val generateMnemonicConsumer = ResourceFlowConsumer<List<String>>(viewModelScope)

    private val _isGeneratingKeys = MutableStateFlow(false)

    val seedPhrase = generateMnemonicConsumer.data

    val isLoading = anyTrue(_isGeneratingKeys, generateMnemonicConsumer.isLoading)

    init {
        generateMnemonicConsumer.collectFlow {
            delay(3000)
            KeyFactory.createMnemonic()
        }
    }

    fun onNextButtonClicked() {
        val seedPhrase = checkNotNull(seedPhrase.value)
        execute {
            KeyFactory.createKeyPairFromMnemonic(seedPhrase)
        }.onEach {
            _isGeneratingKeys.value = it is Resource.Loading
            if (it is Resource.Success) {
                sessionManager.setActiveSession(it.data.publicKey)
            }
        }.launchIn(viewModelScope)
    }
}