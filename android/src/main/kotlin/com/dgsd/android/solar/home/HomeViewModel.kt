package com.dgsd.android.solar.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.ui.*
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.nfc.NfcManager
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import kotlinx.coroutines.flow.*

private const val NUM_TRANSACTIONS_TO_DISPLAY = 5

class HomeViewModel(
  application: Application,
  private val systemClipboard: SystemClipboard,
  private val errorMessageFactory: ErrorMessageFactory,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val transactionViewStateFactory: TransactionViewStateFactory,
  private val solanaApiRepository: SolanaApiRepository,
  private val solPay: SolPay,
  private val nfcManager: NfcManager,
) : AndroidViewModel(application) {

  private val balanceResourceConsumer = ResourceFlowConsumer<LamportsWithTimestamp>(viewModelScope)
  val isLoadingBalance = balanceResourceConsumer.isLoading
  val balanceText = balanceResourceConsumer.data
    .filterNotNull()
    .map { SolTokenFormatter.format(it.lamports) }
    .onStart { emit("-") }

  val balanceLoadTimeText =
    combine(
      balanceResourceConsumer.data,
      balanceResourceConsumer.error
    ) { balance, error ->
      balance to error
    }.map { (balance, error) ->
      when {
        error != null -> errorMessageFactory.create(
          error,
          getString(R.string.home_error_loading_balance_try_again)
        )
        balance != null -> getString(
          R.string.home_balance_last_fetch_template,
          DateTimeFormatter.formatRelativeDateAndTime(application, balance.timestamp)
        )
        else -> getString(R.string.balance)
      }
    }
  private val transactionsResourceConsumer =
    ResourceFlowConsumer<List<Resource<TransactionOrSignature>>>(viewModelScope)
  val isLoadingTransactions = transactionsResourceConsumer.isLoading
  val transactionsError = transactionsResourceConsumer.error.map { error ->
    error?.let {
      errorMessageFactory.create(
        it,
        getString(R.string.home_error_loading_transactions_try_again)
      )
    }
  }
  val transactions = transactionsResourceConsumer.data
    .take(NUM_TRANSACTIONS_TO_DISPLAY)
    .map { transactionsWithState ->
      transactionsWithState?.map { transactionResource ->
        transactionViewStateFactory.createForList(transactionResource)
      }
    }

  private val _navigateToShareAddress = SimpleMutableEventFlow()
  val navigateToShareAddress = _navigateToShareAddress.asEventFlow()

  private val _navigateToRequestAmountFlow = SimpleMutableEventFlow()
  val navigateToRequestAmountFlow = _navigateToRequestAmountFlow.asEventFlow()

  private val _navigateToTransactionsList = SimpleMutableEventFlow()
  val navigateToTransactionsList = _navigateToTransactionsList.asEventFlow()

  private val _navigateToSettings = SimpleMutableEventFlow()
  val navigateToSettings = _navigateToSettings.asEventFlow()

  private val _navigateToTransactionDetails = MutableEventFlow<TransactionSignature>()
  val navigateToTransactionDetails = _navigateToTransactionDetails.asEventFlow()

  private val _showSendActionSheet = MutableEventFlow<List<SendActionSheetItem>>()
  val showSendActionSheet = _showSendActionSheet.asEventFlow()

  private val _showReceiveActionSheet = MutableEventFlow<List<ReceiveActionSheetItem>>()
  val showReceiveActionSheet = _showReceiveActionSheet.asEventFlow()

  private val _navigateToScanQr = SimpleMutableEventFlow()
  val navigateToScanQr = _navigateToScanQr.asEventFlow()

  private val _navigateToSendWithAddress = SimpleMutableEventFlow()
  val navigateToSendWithAddress = _navigateToSendWithAddress.asEventFlow()

  private val _navigateToSendWithNearby = SimpleMutableEventFlow()
  val navigateToSendWithNearby = _navigateToSendWithNearby.asEventFlow()

  private val _navigateToSendToAddress = MutableEventFlow<String>()
  val navigateToSendWithSolPayRequest = _navigateToSendToAddress.asEventFlow()

  private var hasBeenCreated = false

  fun onCreate() {
    reloadData(
      if (hasBeenCreated) {
        CacheStrategy.CACHE_IF_PRESENT
      } else {
        CacheStrategy.CACHE_AND_NETWORK
      }
    )

    hasBeenCreated = true
  }

  fun onSwipeToRefresh() {
    reloadData(CacheStrategy.NETWORK_ONLY)
  }

  fun onRetryTransactionLoadClicked() {
    reloadBalance(CacheStrategy.CACHE_IF_PRESENT)
    reloadRecentTransactions(CacheStrategy.NETWORK_ONLY)
  }

  fun onBalanceLabelClicked() {
    if (balanceResourceConsumer.error.value != null) {
      reloadBalance(CacheStrategy.NETWORK_ONLY)
      reloadRecentTransactions(CacheStrategy.CACHE_IF_PRESENT)
    }
  }

  fun onSettingsClicked() {
    _navigateToSettings.call()
  }

  fun onSendButtonClicked() {
    val addressOnClipboard = systemClipboard.currentContents()?.let {
      runCatching { PublicKey.fromBase58(it) }.getOrNull()
    }
    _showSendActionSheet.tryEmit(
      listOfNotNull(
        if (addressOnClipboard == null) {
          null
        } else {
          SendActionSheetItem(
            RichTextFormatter.expandTemplate(
              getApplication(),
              R.string.home_send_action_sheet_item_from_clipboard_template,
              publicKeyFormatter.abbreviate(addressOnClipboard)
            ),
            R.drawable.ic_baseline_content_copy_24,
            SendActionSheetItem.Type.PreselectedAddress(addressOnClipboard)
          )
        },
        SendActionSheetItem(
          getString(R.string.home_send_action_sheet_item_scan_qr),
          R.drawable.ic_baseline_qr_code_scanner_24,
          SendActionSheetItem.Type.ScanQr
        ),
        SendActionSheetItem(
          getString(R.string.home_send_action_sheet_item_enter_address),
          R.drawable.ic_baseline_keyboard_24,
          SendActionSheetItem.Type.EnterPublicAddress
        ),
        if (nfcManager.isNfAvailable()) {
          SendActionSheetItem(
            getString(R.string.home_send_action_sheet_item_nearby),
            R.drawable.ic_baseline_tap_and_play_24,
            SendActionSheetItem.Type.Nearby
          )
        } else {
          null
        },
      )
    )
  }

  fun onSendActionSheetItemClicked(type: SendActionSheetItem.Type) {
    when (type) {
      SendActionSheetItem.Type.ScanQr -> _navigateToScanQr.call()
      SendActionSheetItem.Type.EnterPublicAddress -> _navigateToSendWithAddress.call()
      SendActionSheetItem.Type.Nearby -> _navigateToSendWithNearby.call()
      is SendActionSheetItem.Type.PreselectedAddress -> {
        val request = SolPayTransferRequest(type.address)
        _navigateToSendToAddress.tryEmit(solPay.createUrl(request))
      }
    }
  }

  fun onReceiveActionSheetItemClicked(type: ReceiveActionSheetItem.Type) {
    when (type) {
      ReceiveActionSheetItem.Type.SHARE_ADDRESS -> _navigateToShareAddress.call()
      ReceiveActionSheetItem.Type.REQUEST_AMOUNT -> _navigateToRequestAmountFlow.call()
    }
  }

  fun onReceiveButtonClicked() {
    _showReceiveActionSheet.tryEmit(
      listOf(
        ReceiveActionSheetItem(
          getString(R.string.home_receive_action_sheet_item_share_address),
          R.drawable.ic_baseline_share_24,
          ReceiveActionSheetItem.Type.SHARE_ADDRESS
        ),
        ReceiveActionSheetItem(
          getString(R.string.home_receive_action_sheet_item_request_amount),
          R.drawable.ic_baseline_install_mobile_24,
          ReceiveActionSheetItem.Type.REQUEST_AMOUNT
        ),
      )
    )
  }

  fun onViewMoreTransactionsClicked() {
    _navigateToTransactionsList.call()
  }

  private fun reloadData(cacheStrategy: CacheStrategy) {
    reloadBalance(cacheStrategy)
    reloadRecentTransactions(cacheStrategy)
  }

  private fun reloadRecentTransactions(cacheStrategy: CacheStrategy) {
    transactionsResourceConsumer.collectFlow(
      solanaApiRepository.getTransactions(cacheStrategy, NUM_TRANSACTIONS_TO_DISPLAY)
    )
  }

  private fun reloadBalance(cacheStrategy: CacheStrategy) {
    balanceResourceConsumer.collectFlow(solanaApiRepository.getBalance(cacheStrategy))
  }

  fun onTransactionClicked(transaction: TransactionSignature) {
    _navigateToTransactionDetails.tryEmit(transaction)
  }
}