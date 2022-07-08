package com.dgsd.android.solar.common.error

import android.content.Context
import com.dgsd.android.solar.R

/**
 * For creating common error messages shown throughout the app
 */
class ErrorMessageFactory(
    private val context: Context,
) {

    fun create(error: Throwable): CharSequence {
        return createDefault()
    }

    fun createDefault(): CharSequence {
        return context.getString(R.string.error_message_generic)
    }
}