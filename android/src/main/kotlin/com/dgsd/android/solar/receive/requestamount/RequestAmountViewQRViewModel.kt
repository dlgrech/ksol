package com.dgsd.android.solar.receive.requestamount

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.qr.QRCodeFactory
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.Lamports
import com.dgsd.ksol.model.SOL_IN_LAMPORTS
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransferRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class RequestAmountViewQRViewModel(
  application: Application,
  private val session: WalletSession,
  private val solPay: SolPay,
  private val errorMessageFactory: ErrorMessageFactory,
  private val lamports: Lamports,
  private val message: String?
) : AndroidViewModel(application) {

  private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
  val qrCodeBitmap = _qrCodeBitmap.asStateFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  fun onCreate() {
    viewModelScope.launch {
      runCatching {
        val url = solPay.createUrl(
          SolPayTransferRequest(
            recipient = session.publicKey,
            amount = SOL_IN_LAMPORTS * BigDecimal.valueOf(lamports),
            message = message,
          )
        )

        QRCodeFactory.createQR(url)
      }.onSuccess {
        _qrCodeBitmap.value = it
      }.onFailure {
        _showError.tryEmit(errorMessageFactory.create(it))
      }
    }
  }

  fun onShareClicked() {
    // Coming soon
  }

}