package com.dgsd.android.solar.extensions

import android.widget.ImageView
import coil.load
import coil.request.ImageRequest
import org.koin.java.KoinJavaComponent.getKoin

fun ImageView.setUrl(url: String, builder: ImageRequest.Builder.() -> Unit = {}) {
  load(
    data = url,
    imageLoader = getKoin().get(),
    builder = builder
  )
}