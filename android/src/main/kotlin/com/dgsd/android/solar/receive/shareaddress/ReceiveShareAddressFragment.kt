package com.dgsd.android.solar.receive.shareaddress

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.modalsheet.extensions.showModelFromErrorMessage
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.extensions.showSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveShareAddressFragment : Fragment(R.layout.frag_receive_share_address) {

  private val viewModel: ReceiveShareAddressViewModel by viewModel()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.requireViewById<Toolbar>(R.id.toolbar)
    val qrCode = view.requireViewById<ImageView>(R.id.qr_code)
    val walletAddress = view.requireViewById<TextView>(R.id.wallet_address)
    val copyButton = view.requireViewById<View>(R.id.copy_button)
    val shareButton = view.requireViewById<View>(R.id.share_button)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    copyButton.setOnClickListener {
      viewModel.onCopyAddressClicked()
    }

    shareButton.setOnClickListener {
      viewModel.onShareAddressClicked()
    }

    walletAddress.setOnClickListener {
      viewModel.onCopyAddressClicked()
    }

    onEach(viewModel.qrCodeBitmap) {
      qrCode.setImageBitmap(it)
    }

    onEach(viewModel.walletAddressText) {
      walletAddress.text = it
    }

    onEach(viewModel.showError) {
      showModelFromErrorMessage(it)
    }

    onEach(viewModel.showSuccessMessage) {
      showSnackbar(it)
    }

    onEach(viewModel.showSystemShare) { textToShare ->
      ShareCompat.IntentBuilder(requireContext())
        .setText(textToShare)
        .setType("text/plain")
        .startChooser()
    }

    onEach(viewModel.showSystemShareForImage) { imageUri ->
      ShareCompat.IntentBuilder(requireContext())
        .setStream(imageUri)
        .setType("image/png")
        .startChooser()
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  companion object {

    fun newInstance(): ReceiveShareAddressFragment {
      return ReceiveShareAddressFragment()
    }
  }
}