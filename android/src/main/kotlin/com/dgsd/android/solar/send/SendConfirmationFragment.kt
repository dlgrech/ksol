package com.dgsd.android.solar.send

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.util.SwallowBackpressLifecycleObserver
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SendConfirmationFragment : Fragment(R.layout.frag_send_confirmation) {

  private val coordinator by parentViewModel<SendCoordinator>()
  private val viewModel by viewModel<SendConfirmationViewModel> {
    parametersOf(checkNotNull(coordinator.transactionSignature))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SwallowBackpressLifecycleObserver.attach(this)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val statusMessage = view.requireViewById<TextView>(R.id.status_message)
    val closeButton = view.requireViewById<View>(R.id.close)

    closeButton.setOnClickListener {
      coordinator.onCloseFlowClicked()
    }

    onEach(viewModel.statusText) {
      statusMessage.text = it
    }
  }

  override fun onStart() {
    super.onStart()
    viewModel.onStart()
  }

  override fun onStop() {
    viewModel.onStop()
    super.onStop()
  }
}