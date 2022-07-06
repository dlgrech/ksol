package com.dgsd.android.solar.extensions

import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

fun Fragment.setContent(
    content: @Composable () -> Unit,
): View {
    return ComposeView(requireContext()).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
        )
        setContent(content)
    }
}