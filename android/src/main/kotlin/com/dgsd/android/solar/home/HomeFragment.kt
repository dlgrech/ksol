package com.dgsd.android.solar.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.R
import com.dgsd.android.solar.extensions.blur
import com.dgsd.android.solar.extensions.onEach
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val BOTTOM_SHEET_SCRIM_MAX_ALPHA = 0.7f

class HomeFragment : Fragment(R.layout.frag_home) {

  private val viewModel: HomeViewModel by viewModel()

  private val backPressCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {
      val containerView = view?.findViewById<View>(R.id.send_receive_bottom_sheet)
      if (containerView != null) {
        BottomSheetBehavior.from(containerView).state = BottomSheetBehavior.STATE_COLLAPSED
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val contentScrim = view.findViewById<View>(R.id.content_scrim)
    val contentContainer = view.findViewById<View>(R.id.content_container)
    val sendReceiveBottomSheetContainer = view.findViewById<View>(R.id.send_receive_bottom_sheet)
    val receiveButton = view.findViewById<View>(R.id.send)
    val sendButton = view.findViewById<View>(R.id.receive)
    val sendReceiveBottomSheet = BottomSheetBehavior.from(sendReceiveBottomSheetContainer)
    val balanceText = view.findViewById<TextView>(R.id.balance)

    sendReceiveBottomSheetContainer.setOnClickListener {
      if (sendReceiveBottomSheet.state != BottomSheetBehavior.STATE_COLLAPSED) {
        sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
      } else {
        sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
      }
    }

    sendButton.setOnClickListener {
      sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    receiveButton.setOnClickListener {
      sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    contentScrim.setOnClickListener {
      sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    sendReceiveBottomSheet.addBottomSheetCallback(
      object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            backPressCallback.isEnabled = false
            contentScrim.isEnabled = false
            sendReceiveBottomSheet.isDraggable = false
          } else {
            backPressCallback.isEnabled = true
            contentScrim.isEnabled = true
            sendReceiveBottomSheet.isDraggable = true
          }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
          val percentageOpened = slideOffset.coerceIn(0f, 1f)
          contentScrim.alpha = percentageOpened * BOTTOM_SHEET_SCRIM_MAX_ALPHA
          contentContainer.blur(percentageOpened)
        }
      }
    )

    sendReceiveBottomSheet.setPeekHeight(
      resources.getDimensionPixelSize(R.dimen.home_send_receive_peek_size),
      false
    )
    sendReceiveBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    sendReceiveBottomSheet.isDraggable = false

    onEach(viewModel.balanceText) {
      balanceText.text = it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  override fun onStart() {
    super.onStart()
    requireActivity().onBackPressedDispatcher.addCallback(backPressCallback)
  }

  override fun onStop() {
    backPressCallback.remove()
    super.onStop()
  }

  companion object {

    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }
}