package com.dgsd.android.solar.applock.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dgsd.android.solar.R
import com.dgsd.android.solar.applock.manager.AppLockManager
import com.dgsd.android.solar.common.model.SensitiveString
import com.dgsd.android.solar.extensions.getString
import com.dgsd.android.solar.flow.MutableEventFlow
import com.dgsd.android.solar.flow.SimpleMutableEventFlow
import com.dgsd.android.solar.flow.asEventFlow
import com.dgsd.android.solar.flow.call
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetupAppLockConfirmPinViewModel(
  application: Application,
  private val appLockManager: AppLockManager,
  private val originalPin: SensitiveString,
) : AndroidViewModel(application) {

  private val inputtedCode = MutableStateFlow(SensitiveString(""))

  private val _showAsError = MutableStateFlow(false)
  val showAsError = _showAsError.asStateFlow()

  private val _showError = MutableEventFlow<CharSequence>()
  val showError = _showError.asEventFlow()

  private val _continueWithCode = SimpleMutableEventFlow()
  val continueWithCode = _continueWithCode.asEventFlow()

  fun onCodeChanged(code: String) {
    inputtedCode.value = SensitiveString(code)
    _showAsError.value = false
  }

  fun onConfirmClicked() {
    val code = inputtedCode.value
    if (code != originalPin) {
      _showAsError.value = true
      _showError.tryEmit(
        getString(R.string.setup_app_lock_error_not_matching)
      )
    } else {
      appLockManager.updateCode(code)
      _continueWithCode.call()
    }
  }
}