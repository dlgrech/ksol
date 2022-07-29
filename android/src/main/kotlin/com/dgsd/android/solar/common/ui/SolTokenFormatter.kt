package com.dgsd.android.solar.common.ui

import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.Lamports
import java.text.NumberFormat

object SolTokenFormatter {

    private const val SOL_SYMBOL = "◎"

    private val numberFormatter = NumberFormat.getNumberInstance().apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        minimumIntegerDigits = 1
    }

    private val fullNumberFormatter = NumberFormat.getNumberInstance().apply {
        maximumFractionDigits = 9
        minimumFractionDigits = 9
        minimumIntegerDigits = 1
    }

    fun format(lamports: Lamports): CharSequence {
        val formattedNumber =  numberFormatter.format(
            lamports.toBigDecimal().divide(LAMPORTS_IN_SOL)
        )

        return "$SOL_SYMBOL$formattedNumber"
    }

    fun formatLong(lamports: Lamports): CharSequence {
        val formattedNumber =  fullNumberFormatter.format(
            lamports.toBigDecimal().divide(LAMPORTS_IN_SOL)
        )

        return "$SOL_SYMBOL$formattedNumber"
    }
}