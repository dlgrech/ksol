package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.core.model.Commitment
import com.dgsd.ksol.core.model.TransactionSignature
import com.dgsd.ksol.core.model.TransactionSignatureStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private val MAX_WAIT_TIME_MS = TimeUnit.SECONDS.toMillis(7)

class SendConfirmationViewModel(
  application: Application,
  transactionSignature: TransactionSignature,
  solanaApiRepository: SolanaApiRepository,
) : AndroidViewModel(application) {

  private val _statusText =
    MutableStateFlow<CharSequence>(getString(R.string.send_confirm_status_processed))
  val statusText = _statusText.asStateFlow()

  private val _isCloseButtonVisible = MutableStateFlow(false)
  val isCloseButtonVisible = _isCloseButtonVisible.asStateFlow()

  private val subscriptionHandle =
    solanaApiRepository.subscribeToUpdates(transactionSignature, Commitment.CONFIRMED)
  private var subscriptionJob: Job? = null
  private var takingTooLongJob: Job? = null

  fun onStart() {
    subscriptionJob = onEach(subscriptionHandle.observe()) { update ->
      takingTooLongJob?.cancel()

      val isFinalized =
        (update as? TransactionSignatureStatus.Confirmed)?.commitment == Commitment.CONFIRMED
      _statusText.value =
        if (isFinalized) {
          RichTextFormatter.coloredText(
            getApplication<Application>().getColor(R.color.positive_text_color),
            getString(R.string.send_confirm_status_finalized)
          )
        } else {
          RichTextFormatter.coloredText(
            getApplication<Application>().getColorAttr(R.attr.colorError),
            (update as? TransactionSignatureStatus.Confirmed)
              ?.errorMessage
              ?.takeIf { it.isNotEmpty() }
              ?: getString(R.string.send_confirm_error_getting_state)
          )
        }

      _isCloseButtonVisible.value = true
    }

    takingTooLongJob = viewModelScope.launch {
      delay(MAX_WAIT_TIME_MS)
      subscriptionJob?.cancel()

      _isCloseButtonVisible.value = true
      _statusText.value = getString(R.string.send_confirm_status_taking_too_long)
    }
  }

  fun onStop() {
    subscriptionJob?.cancel()
    subscriptionHandle.stop()
  }
}