package com.dgsd.android.solar.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.DateTimeFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.ResourceFlowConsumer
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import com.dgsd.android.solar.model.TransactionInfo
import com.dgsd.android.solar.nfc.NfcManager
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.Lamports
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

private const val NUM_TRANSACTIONS_TO_DISPLAY = 5

class HomeViewModel(
  application: Application,
  private val solanaApiRepository: SolanaApiRepository,
  private val nfcManager: NfcManager,
) : AndroidViewModel(application) {

  private val balanceResourceConsumer = ResourceFlowConsumer<Lamports>(viewModelScope)
  val isLoadingBalance = balanceResourceConsumer.isLoading
  val balanceText =
    balanceResourceConsumer.data.filterNotNull().map { SolTokenFormatter.format(it) }
  val balanceLoadTimeText =
    balanceResourceConsumer.data.filterNotNull().map {
      getString(
        R.string.home_balance_last_fetch_template,
        DateTimeFormatter.formatRelativeDateAndTime(
          application,
          OffsetDateTime.now()
        )
      )
    }
  private val transactionsResourceConsumer =
    ResourceFlowConsumer<List<TransactionInfo>>(viewModelScope)
  val isLoadingTransactionSignatures = balanceResourceConsumer.isLoading
  val transactions = transactionsResourceConsumer.data

  private val _navigateToReceiveFlow = SimpleMutableEventFlow()
  val navigateToReceiveFlow = _navigateToReceiveFlow.asEventFlow()

  private val _navigateToSettings = SimpleMutableEventFlow()
  val navigateToSettings = _navigateToSettings.asEventFlow()

  private val _showSendActionSheet = MutableEventFlow<List<SendActionSheetItem>>()
  val showSendActionSheet = _showSendActionSheet.asEventFlow()

  fun onCreate() {
    reloadData()
  }

  fun onSwipeToRefresh() {
    reloadData()
  }

  fun onSettingsClicked() {
    _navigateToSettings.call()
  }

  fun onSendButtonClicked() {
    _showSendActionSheet.tryEmit(
      listOfNotNull(
        SendActionSheetItem(
          getString(R.string.home_send_action_sheet_item_scan_qr),
          R.drawable.ic_baseline_qr_code_scanner_24,
          SendActionSheetItem.Type.SCAN_QR
        ),
        SendActionSheetItem(
          getString(R.string.home_send_action_sheet_item_enter_address),
          R.drawable.ic_baseline_keyboard_24,
          SendActionSheetItem.Type.ENTER_PUBLIC_ADDRESS
        ),
        SendActionSheetItem(
          getString(R.string.home_send_action_sheet_item_send_to_previous),
          R.drawable.ic_baseline_history_24,
          SendActionSheetItem.Type.HISTORICAL_ADDRESS
        ),
        if (nfcManager.isNfAvailable()) {
          SendActionSheetItem(
            getString(R.string.home_send_action_sheet_item_tap_other_user),
            R.drawable.ic_baseline_tap_and_play_24,
            SendActionSheetItem.Type.NFC
          )
        } else {
          null
        },
      )
    )
  }

  fun onSendActionSheetItemClicked(type: SendActionSheetItem.Type) {
    when (type) {
      SendActionSheetItem.Type.SCAN_QR -> Unit
      SendActionSheetItem.Type.ENTER_PUBLIC_ADDRESS -> Unit
      SendActionSheetItem.Type.HISTORICAL_ADDRESS -> Unit
      SendActionSheetItem.Type.NFC -> Unit
    }
  }

  fun onReceiveButtonClicked() {
    _navigateToReceiveFlow.call()
  }

  private fun reloadData() {
    balanceResourceConsumer.collectFlow(solanaApiRepository.getBalance())
    transactionsResourceConsumer.collectFlow(
      solanaApiRepository.getTransactions(NUM_TRANSACTIONS_TO_DISPLAY)
    )
  }
}