package com.dgsd.android.solar.onboarding.restoreaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.AccountSeedInfo
import com.dgsd.android.solar.onboarding.restoreaccount.model.CandidateAccount
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.model.KeyPair
import com.dgsd.ksol.keygen.KeyFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

private const val NUMBER_OF_ACCOUNTS = 20

class RestoreAccountSelectAddressViewModel(
  private val errorMessageFactory: ErrorMessageFactory,
  private val solanaApi: SolanaApi,
  private val seedInfo: AccountSeedInfo,
) : ViewModel() {

  private val _continueWithResult = MutableEventFlow<KeyPair>()
  val continueWithResult = _continueWithResult.asEventFlow()

  private val generateKeysResourceConsumer = ResourceFlowConsumer<List<KeyPair>>(viewModelScope)

  private val candidateAccountList = (0 until NUMBER_OF_ACCOUNTS).map { accountIndex ->
    MutableStateFlow<CandidateAccount>(CandidateAccount.Empty(accountIndex))
  }

  val accountData = combine(candidateAccountList) {
    it.toList()
  }

  val errorMessage = generateKeysResourceConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  fun onCreate() {
    onEach(generateKeysResourceConsumer.data.filterNotNull().take(1)) { keys ->
      val backgroundScope = viewModelScope + Dispatchers.IO
      keys.forEachIndexed { accountIndex, candidateKeyPair ->
        val flow = candidateAccountList[accountIndex]

        flow.value = CandidateAccount.Loading(accountIndex, candidateKeyPair)

        backgroundScope.launch {
          runCatching {
            solanaApi.getBalance(candidateKeyPair.publicKey)
          }.onSuccess { lamports ->
            flow.value = CandidateAccount.AccountWithBalance(
              accountIndex,
              candidateKeyPair,
              lamports
            )
          }.onFailure { error ->
            flow.value = CandidateAccount.Error(
              accountIndex,
              candidateKeyPair,
              errorMessageFactory.create(error)
            )
          }
        }
      }
    }

    generateKeysResourceConsumer.collectFlow(
      resourceFlowOf {
        (0 until NUMBER_OF_ACCOUNTS).map { accountIndex ->
          KeyFactory.createKeyPairFromMnemonic(
            seedInfo.seedPhrase,
            seedInfo.passPhrase.sensitiveValue,
            accountIndex
          )
        }
      }
    )

  }

  fun onCandidateAccountClicked(candidateAccount: CandidateAccount) {
    val keyPair = candidateAccount.keyPairOrNull()
    if (keyPair != null) {
      _continueWithResult.tryEmit(keyPair)
    }
  }
}