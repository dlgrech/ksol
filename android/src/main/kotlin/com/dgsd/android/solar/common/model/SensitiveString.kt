package com.dgsd.android.solar.common.model

/**
 * Wrapper class that makes it more unlikely/inconvenient to log sensitive values
 */
data class SensitiveString(val sensitiveValue: String) {

    override fun toString(): String {
        return "[redacted]"
    }
}