package com.dgsd.android.solar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.dgsd.android.solar.deeplink.SolarDeeplinkingFactory
import com.dgsd.android.solar.di.util.injectScoped
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.solpay.SolPay
import com.dgsd.ksol.solpay.model.SolPayTransferRequest

/**
 * [Activity] that responds to [Intent.ACTION_PROCESS_TEXT] and opens a link to send
 */
class ProcessTextActivity : Activity() {

  private val solPay by injectScoped<SolPay>(mode = LazyThreadSafetyMode.NONE)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (intent?.action == Intent.ACTION_PROCESS_TEXT) {
      val selectedText = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT).orEmpty()

      runCatching {
        PublicKey.fromBase58(selectedText)
      }.onFailure {
        Toast.makeText(
          this,
          getString(R.string.error_process_text_invalid_address),
          Toast.LENGTH_SHORT
        ).show()
      }.onSuccess {
        runCatching {
          startActivity(
            SolarDeeplinkingFactory.createSolPayRequestIntent(
              this,
              solPay.createUrl(SolPayTransferRequest(recipient = it))
            )
          )
        }
      }
    }

    finish()
  }
}