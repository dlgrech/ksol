package com.dgsd.ksol.collections

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

internal class BidirectionalMapTest {

    private val testData = mutableMapOf(
        "a" to 1,
        "b" to 2,
        "c" to 3
    )

    @TestFactory
    fun testFactory_setAndGet() = testData.map { (key, value) ->
        DynamicTest.dynamicTest("setAndGet (key = $key value = $value)") {
            val map = BidirectionalMap<String, Int>()
            map[key] = value

            Assertions.assertEquals(value, map.getFromKey(key))
            Assertions.assertEquals(key, map.getFromValue(value))
        }
    }

    @Test
    fun getFromKey_whenNotExisting_returnsNull() {
        val map = BidirectionalMap<String, Int>()
        Assertions.assertNull(map.getFromKey("a"))
    }

    @Test
    fun getFromValue_whenNotExisting_returnsNull() {
        val map = BidirectionalMap<String, Int>()
        Assertions.assertNull(map.getFromValue(1))
    }

    @Test
    fun remove_whenCalled_removesAllEntries() {
        val map = BidirectionalMap<String, Int>()
        map["a"] = 1

        map.clear()

        Assertions.assertNull(map.getFromKey("a"))
        Assertions.assertNull(map.getFromValue(1))
    }

    @Test
    fun removeKey_whenNotExisting_returnsNull() {
        val map = BidirectionalMap<String, Int>()
        Assertions.assertNull(map.removeKey("a"))
    }

    @Test
    fun removeValue_whenNotExisting_returnsNull() {
        val map = BidirectionalMap<String, Int>()
        Assertions.assertNull(map.removeValue(1))
    }

    @Test
    fun removeKey_whenExists_returnsValue() {
        val map = BidirectionalMap<String, Int>()
        map["a"] = 1

        val value = map.removeKey("a")
        Assertions.assertEquals(1, value)
        Assertions.assertNull(map.getFromKey("a"))
    }

    @Test
    fun removeValue_whenExists_returnsKey() {
        val map = BidirectionalMap<String, Int>()
        map["a"] = 1

        val key = map.removeValue(1)
        Assertions.assertEquals("a", key)
        Assertions.assertNull(map.getFromValue(1))
    }

    @Test
    fun clear_whenCalled_removesAllEntries() {
        val map = BidirectionalMap<String, Int>()
        map["a"] = 1

        map.clear()

        Assertions.assertNull(map.getFromKey("a"))
        Assertions.assertNull(map.getFromValue(1))
    }

}