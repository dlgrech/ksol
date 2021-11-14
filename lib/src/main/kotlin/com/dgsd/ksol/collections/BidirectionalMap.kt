package com.dgsd.ksol.collections

/**
 * Map-like data structure to hold key/value pairs that have a 1:1 correspondence
 */
internal class BidirectionalMap<K, V> {
    private val keysToValue = mutableMapOf<K, V>()
    private val valuesToKeys = mutableMapOf<V, K>()

    operator fun set(key: K, value: V) {
        keysToValue[key] = value
        valuesToKeys[value] = key
    }

    fun getFromKey(key: K): V? {
        return keysToValue[key]
    }

    fun getFromValue(value: V): K? {
        return valuesToKeys[value]
    }

    fun removeKey(key: K): V? {
        val value = keysToValue.remove(key)
        if (value != null) {
            valuesToKeys.remove(value)
        }

        return value
    }

    fun removeValue(value: V): K? {
        val key = valuesToKeys.remove(value)
        if (key != null) {
            keysToValue.remove(key)
        }

        return key
    }

    fun clear() {
        keysToValue.clear()
        valuesToKeys.clear()
    }
}