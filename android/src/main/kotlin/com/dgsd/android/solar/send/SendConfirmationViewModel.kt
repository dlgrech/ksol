package com.dgsd.android.solar.send

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.TransactionSignature
import com.dgsd.ksol.model.TransactionSignatureStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SendConfirmationViewModel(
  application: Application,
  transactionSignature: TransactionSignature,
  solanaApiRepository: SolanaApiRepository,
) : AndroidViewModel(application) {

  private val _statusText =
    MutableStateFlow<CharSequence>(getString(R.string.send_confirm_status_processed))
  val statusText = _statusText.asStateFlow()

  private val subscriptionHandle = solanaApiRepository.subscribeToUpdates(transactionSignature)
  private var subscriptionJob: Job? = null

  fun onStart() {
    subscriptionJob = onEach(subscriptionHandle.observe()) { update ->
      val isFinalized =
        (update as? TransactionSignatureStatus.Confirmed)?.commitment == Commitment.FINALIZED
      if (isFinalized) {
        _statusText.value =
          RichTextFormatter.coloredText(
            getApplication<Application>().getColor(R.color.positive_text_color),
            getString(R.string.send_confirm_status_finalized)
          )
      } else {
        _statusText.value =
          RichTextFormatter.coloredText(
            getApplication<Application>().getColorAttr(R.attr.colorError),
            (update as? TransactionSignatureStatus.Confirmed)
              ?.errorMessage
              ?.takeIf { it.isNotEmpty() }
              ?: getString(R.string.send_confirm_error_getting_state)
          )
      }
    }
  }

  fun onStop() {
    subscriptionJob?.cancel()
    subscriptionHandle.stop()
  }
}