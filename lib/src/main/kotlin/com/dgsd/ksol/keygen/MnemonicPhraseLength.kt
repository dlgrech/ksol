package com.dgsd.ksol.keygen

enum class MnemonicPhraseLength(
    val byteLength: Int,
    val wordCount: Int,
) {

    TWELVE(byteLength = 16, wordCount = 12),

    TWENTY_FOUR(byteLength = 32, wordCount = 24)
}