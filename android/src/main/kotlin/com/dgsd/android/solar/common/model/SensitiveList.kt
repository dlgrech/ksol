package com.dgsd.android.solar.common.model

/**
 * Wrapper class that makes it more unlikely/inconvenient to log sensitive values
 */
data class SensitiveList<T>(val sensitiveValue: List<T>): List<T> by sensitiveValue {

    override fun toString(): String {
        return "[redacted]"
    }
}