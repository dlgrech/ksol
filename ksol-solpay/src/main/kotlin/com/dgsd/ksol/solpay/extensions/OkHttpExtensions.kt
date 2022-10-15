package com.dgsd.ksol.solpay.extensions

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Allows a [Call] object to suspend until complete in a Coroutine
 */
internal suspend inline fun Call.await(): Response {
  return suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        if (!continuation.isCancelled) {
          continuation.resumeWithException(e)
        }
      }

      override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
      }
    })

    continuation.invokeOnCancellation {
      try {
        cancel()
      } catch (_: Throwable) {
        // Ignored..
      }
    }
  }
}
