package com.dgsd.ksol.keygen

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DerivationPathTests {

    @Test
    fun solanaBip44_whenInvoked_createsExpectedPath() {
        val path = DerivationPath.solanaBip44(1337)

        Assertions.assertEquals(44, path.purpose)
        Assertions.assertEquals(501, path.coinType)
        Assertions.assertEquals(1337, path.account)
        Assertions.assertEquals(0, path.change)
    }
}