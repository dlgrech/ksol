package com.dgsd.android.solar.common.ui

import com.dgsd.ksol.core.model.LAMPORTS_IN_SOL
import org.junit.Assert
import org.junit.Test

class SolTokenFormatterTest {

    @Test
    fun format_withRoundNumber_returnsExpectedValue() {
        val input = 1 * LAMPORTS_IN_SOL

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1.00", output)
    }

    @Test
    fun format_withLessThanOneSol_returnsExpectedValue() {
        val input = 990000000L

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("0.99", output)
    }

    @Test
    fun format_withLargeNumber_returnsExpectedValue() {
        val input = 1234 * LAMPORTS_IN_SOL

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1,234.00", output)
    }

    @Test
    fun format_withNegative_returnsExpectedValue() {
        val input = -1234 * LAMPORTS_IN_SOL

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("-1,234.00", output)
    }

    @Test
    fun format_withHalf_returnsExpectedValue() {
        val input = (1.5 * LAMPORTS_IN_SOL.longValueExact()).toLong()

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1.50", output)
    }

    @Test
    fun format_withRoundedValue_returnsExpectedValue() {
        val input = 1000000001L

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1.00", output)
    }

    @Test
    fun format_withFraction_returnsExpectedValue() {
        val input = 1230000000L

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1.23", output)
    }

    @Test
    fun format_withRoundedFraction_returnsExpectedValue() {
        val input = 1239000000L

        val output = SolTokenFormatter.format(input)

        Assert.assertEquals("1.24", output)
    }
}