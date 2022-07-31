package com.dgsd.ksol.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer

class ShortVecEncodingTests {

  @Test
  fun encodeLength() {
    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0),
      byteArrayOf(0)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(5),
      byteArrayOf(5)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0x7f),
      byteArrayOf(0x7f)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0x80),
      byteArrayOf(0x80.toByte(), 0x01)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0xFF),
      byteArrayOf(0xFF.toByte(), 0x01)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0x100),
      byteArrayOf(0x80.toByte(), 0x02)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0x7FFF),
      byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x01)
    )

    Assertions.assertArrayEquals(
      ShortVecEncoding.encodeLength(0x200000),
      byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01)
    )
  }

  @Test
  fun decodeLength() {
    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf())),
      0
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(5))),
      5
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0x7f))),
      0x7f
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0x80.toByte(), 0x01))),
      0x80
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0xff.toByte(), 0x01))),
      0xff
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0x80.toByte(), 0x02))),
      0x100
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0xff.toByte(), 0xff.toByte(), 0x01))),
      0x7fff
    )

    Assertions.assertEquals(
      ShortVecEncoding.decodeLength(ByteBuffer.wrap(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x80.toByte(), 0x01))),
      0x200000
    )
  }
}