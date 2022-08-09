package com.dgsd.android.solar.extensions

fun ByteArray.append(other: ByteArray): ByteArray {
  val combined = copyOf(size + other.size)
  other.copyInto(combined, size)
  return combined
}