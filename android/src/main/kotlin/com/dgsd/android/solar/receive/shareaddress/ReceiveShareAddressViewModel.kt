package com.dgsd.android.solar.receive.shareaddress

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.clipboard.SystemClipboard
import com.dgsd.android.solar.common.error.ErrorMessageFactory
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.qr.QRCodeFactory
import com.dgsd.android.solar.session.model.WalletSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReceiveShareAddressViewModel(
  application: Application,
  private val session: WalletSession,
  private val errorMessageFactory: ErrorMessageFactory,
  private val systemClipboard: SystemClipboard,
  publicKeyFormatter: PublicKeyFormatter,
) : AndroidViewModel(application) {

  val walletAddressText = MutableStateFlow(publicKeyFormatter.format(session.publicKey))

  private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
  val qrCodeBitmap = _qrCodeBitmap.asStateFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val _showSuccessMessage = MutableEventFlow<CharSequence>()
  val showSuccessMessage = _showSuccessMessage.asEventFlow()

  private val _showSystemShare = MutableEventFlow<String>()
  val showSystemShare = _showSystemShare.asEventFlow()

  fun onCreate() {
    viewModelScope.launch {
      runCatching {
        QRCodeFactory.createQR(session.publicKey.toBase58String())
      }.onSuccess {
        _qrCodeBitmap.value = it
      }.onFailure {
        _showError.tryEmit(errorMessageFactory.create(it))
      }
    }
  }

  fun onCopyAddressClicked() {
    systemClipboard.copy(session.publicKey.toBase58String())
    _showSuccessMessage.tryEmit(getString(R.string.account_key_copied_to_clipboard))
  }

  fun onShareAddressClicked() {
    _showSystemShare.tryEmit(session.publicKey.toBase58String())
  }

  override fun onCleared() {
    _qrCodeBitmap.value?.recycle()
    _qrCodeBitmap.value = null
  }
}