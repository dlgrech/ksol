package com.dgsd.android.solar.transaction.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.DateTimeFormatter
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.ui.TransactionViewStateFactory
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.model.TransactionViewState
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.Transaction
import com.dgsd.ksol.model.TransactionSignature
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class TransactionDetailsViewModel(
  application: Application,
  private val session: WalletSession,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val solanaApiRepository: SolanaApiRepository,
  private val errorMessageFactory: ErrorMessageFactory,
  private val transactionViewStateFactory: TransactionViewStateFactory,
  private val systemClipboard: SystemClipboard,
  private val transactionSignature: TransactionSignature,
) : AndroidViewModel(application) {

  private val transactionResourceConsumer = ResourceFlowConsumer<Transaction>(viewModelScope)

  private val transaction = transactionResourceConsumer.data.filterNotNull()

  val isLoading = transactionResourceConsumer.isLoading

  val showLoadingState = transactionResourceConsumer.isLoadingOrError

  val transactionSignatureHeaderText = transaction.map {
    if (it.signatures.size != 1) {
      getString(R.string.transaction_details_section_title_signatures)
    } else {
      getString(R.string.transaction_details_section_title_signature)
    }
  }

  val transactionSignatures = transaction.map { it.signatures }

  val feeText = transaction.map {
    when (transactionViewStateFactory.getTransactionDirection(it)) {
      TransactionViewState.Transaction.Direction.OUTGOING -> {
        SolTokenFormatter.formatLong(it.metadata.fee)
      }
      TransactionViewState.Transaction.Direction.INCOMING,
      TransactionViewState.Transaction.Direction.NONE -> {
        null
      }
    }
  }

  val blockTimeText = transaction.map {
    val blockTime = it.blockTime
    if (blockTime == null) {
      null
    } else {
      DateTimeFormatter.formatDateAndTimeLong(application, blockTime)
    }
  }

  val recentBlockHashText = transaction.map {
    publicKeyFormatter.format(it.message.recentBlockhash)
  }

  val amountText = transaction.map {
    transactionViewStateFactory.extractCurrentWalletTransactionAmount(it, useLongFormat = true)
  }

  val logMessages = transaction.map { it.metadata.logMessages }

  val accountDetails = transaction.map { transaction ->
    transaction.message.accountKeys.map { accountMetadata ->
      val accountDisplayText = if (session.publicKey == accountMetadata.publicKey) {
        getString(R.string.your_wallet)
      } else {
        publicKeyFormatter.format(accountMetadata.publicKey)
      }

      val programAccounts = transaction.message.instructions.map { it.programAccount }.toSet()

      val balanceAfter = transaction.metadata.accountBalances.firstOrNull {
        it.accountKey == accountMetadata.publicKey
      }?.balanceAfter

      val changeInBalanceText = if (balanceAfter == null) {
        null
      } else {
        SolTokenFormatter.formatLong(balanceAfter)
      }

      TransactionAccountViewState(
        accountKey = accountMetadata.publicKey,
        accountDisplayText = accountDisplayText,
        isWriter = accountMetadata.isWritable,
        isSigner = accountMetadata.isSigner,
        isFeePayer = accountMetadata.isFeePayer,
        isProgram = accountMetadata.publicKey in programAccounts,
        balanceAfterText = changeInBalanceText,
      )
    }
  }

  val errorMessage = transactionResourceConsumer.error
    .filterNotNull()
    .map { errorMessageFactory.create(it) }
    .asEventFlow(viewModelScope)

  private val _showConfirmationMessage = MutableEventFlow<CharSequence>()
  val showConfirmationMessage = _showConfirmationMessage.asEventFlow()

  fun onCreate() {
    reloadData(CacheStrategy.CACHE_IF_PRESENT)
  }

  fun onSwipeToRefresh() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  fun onRecentBlockhasClicked() {
    val blockHash = transactionResourceConsumer.data.value?.message?.recentBlockhash
    if (blockHash != null) {
      systemClipboard.copy(blockHash.toBase58String())
      _showConfirmationMessage.tryEmit(
        getString(R.string.copied_to_clipboard)
      )
    }
  }

  fun onSignatureClicked(signature: TransactionSignature) {
    systemClipboard.copy(signature)
    _showConfirmationMessage.tryEmit(
      getString(R.string.copied_to_clipboard)
    )
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    transactionResourceConsumer.collectFlow(
      solanaApiRepository.getTransaction(
        cacheStrategy,
        transactionSignature
      )
    )
  }

  fun onAccountClicked(accountKey: PublicKey) {
    systemClipboard.copy(accountKey.toBase58String())
    _showConfirmationMessage.tryEmit(
      getString(R.string.account_key_copied_to_clipboard)
    )
  }
}