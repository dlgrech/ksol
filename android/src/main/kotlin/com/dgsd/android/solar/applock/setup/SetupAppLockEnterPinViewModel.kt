package com.dgsd.android.solar.applock.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.model.AppLockConstants
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetupAppLockEnterPinViewModel(
  application: Application,
) : AndroidViewModel(application) {

  private val inputtedCode = MutableStateFlow(SensitiveString(""))

  private val _showAsError = MutableStateFlow(false)
  val showAsError = _showAsError.asStateFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val _continueWithCode = MutableEventFlow<SensitiveString>()
  val continueWithCode = _continueWithCode.asEventFlow()

  fun onCodeChanged(code: String) {
    inputtedCode.value = SensitiveString(code)
    _showAsError.value = false
  }

  fun onNextClicked() {
    val code = inputtedCode.value
    if (code.sensitiveValue.length < AppLockConstants.MIN_APP_LOCK_CODE_LENGTH) {
      _showAsError.value = true
      _showError.tryEmit(
        getString(
          R.string.setup_app_lock_error_too_short,
          AppLockConstants.MIN_APP_LOCK_CODE_LENGTH
        )
      )
    } else {
      _continueWithCode.tryEmit(code)
    }
  }
}