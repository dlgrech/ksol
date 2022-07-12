package com.dgsd.android.solar.common.ui

import com.dgsd.ksol.model.LAMPORTS_IN_SOL
import com.dgsd.ksol.model.Lamports
import java.text.NumberFormat

object SolTokenFormatter {

    private val numberFormatter = NumberFormat.getNumberInstance().apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        minimumIntegerDigits = 1
    }

    fun format(lamports: Lamports): CharSequence {
        return numberFormatter.format(
            lamports.toBigDecimal().divide(LAMPORTS_IN_SOL.toBigDecimal())
        )
    }
}