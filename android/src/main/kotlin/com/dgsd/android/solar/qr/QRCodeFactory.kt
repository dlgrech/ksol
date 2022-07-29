package com.dgsd.android.solar.qr

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QRCodeFactory {

  private const val QR_CODE_SIZE = 768

  suspend fun createQR(
    contents: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
  ): Bitmap = withContext(dispatcher) {
    BarcodeEncoder().encodeBitmap(
      contents,
      BarcodeFormat.QR_CODE,
      QR_CODE_SIZE,
      QR_CODE_SIZE,
      mapOf(
        EncodeHintType.MARGIN to 1
      )
    )
  }
}