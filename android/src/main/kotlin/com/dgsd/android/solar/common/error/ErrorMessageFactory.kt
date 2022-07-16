package com.dgsd.android.solar.common.error

import android.content.Context
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.model.UserFacingException

/**
 * For creating common error messages shown throughout the app
 */
class ErrorMessageFactory(
    private val context: Context,
) {

    fun create(error: Throwable): CharSequence {
        return when(error) {
            is UserFacingException -> error.userVisibleMessage.ifEmpty { createDefault() }
            else -> createDefault()
        }
    }

    fun createDefault(): CharSequence {
        return context.getString(R.string.error_message_generic)
    }
}