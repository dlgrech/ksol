package com.dgsd.ksol.keygen

import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.PrivateKey
import com.dgsd.ksol.model.PublicKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

private val WORDS = listOf(
    "sentence",
    "ugly",
    "section",
    "antenna",
    "motion",
    "bind",
    "adapt",
    "vault",
    "increase",
    "milk",
    "lawn",
    "humor",
)

class KeyFactoryTests {

    @Test
    fun createKeyPairFromPrivateKey_returnsExpectedResult() = runBlocking {
        val privateKey = PrivateKey.fromBase58(
            "5ysPKzei6U5b1KTRs7XjwUL7335E8L1eta531oQkXP63Wf1jkavEyov1zyNX928hHhNkfEpVptACSfWPZtbzgeoa"
        )

        val keyPair = KeyFactory.createKeyPairFromPrivateKey(privateKey)

        Assertions.assertEquals(privateKey, keyPair.privateKey)
        Assertions.assertEquals(
            PublicKey.fromBase58("HYvJjCgo4yoyxJD8oanc18vsi4aqEMwtz2wkrj26kH7e"),
            keyPair.publicKey
        )
    }

    @Test
    fun createSeedFromMnemonic_withoutPassphrase_returnsExpectedResult() = runBlocking {
        val seed = KeyFactory.createSeedFromMnemonic(WORDS)
        val expected = PrivateKey.fromBase58(
            "usDk6nuQLHq8U1DXrhAxmQM1Ra5iazcZ2tdAQs4NoccABGCgDt2mezEYqBGLdziMd44u1bdj7WQ8en7TaoAAsTb"
        ).key

        Assertions.assertArrayEquals(expected, seed)
    }

    @Test
    fun createSeedFromMnemonic_withPassphrase_returnsExpectedResult() = runBlocking {
        val seed = KeyFactory.createSeedFromMnemonic(WORDS, "password")
        val expected = PrivateKey.fromBase58(
            "65eMnGdwXHBRFu2f89m5cALXYCofdTHBGAaZ8qGA97d8ZwNhjXAy2iwSGZ9RCJnXE5qtDMr49Gnf5fXzUDfN9DeQ"
        ).key

        Assertions.assertArrayEquals(expected, seed)
    }

    @TestFactory
    fun testFactory_createKeyPairFromMnemonic() = listOf(
        Triple(0,
            "EUbPPkMVnHLzSzLQrZtTvaUSP9QQx2c1cCWoPyfGbWm3",
            "2259sJ4Hv8wiEvki391UCkwubGpLJuVUk7ccrar8povQdK3LrZAGtJQ8V2FADXXycLYFJpabLygzwb1w8bvsV3hP"),
        Triple(1,
            "GJweZLe1BxuAcqU4q81kzJRuhGBtkiaged73AXSVLx4P",
            "cAM9E1nBCoiA1uPDii4oE2JAtJ9gdGvNr2g7RrmimCEJujPQMhp23z5EvbwwoELQmp3kTbPm58n1oQE9pPoCQaB"),
        Triple(2,
            "6bFopPshfo3StpRrovh4pabmsRf17h75SB6xsqFiTAnF",
            "4XRPzPhR8hUPXciHhQVAu4ywAPAYrUSzjXpvfozjptfxqbLfLKPJqLPDj5EcgoR1MfFdEQDMrAJNM7XZEm12pooR"),
        Triple(3,
            "Hgi2HrZxr7gpGp5LA3eTXTcRiCqWMAF7PVtfu4UL2nZK",
            "5hAn3tDVwZqLWwbizU9TiLJvd8rx6sWqKBLj5Vj8yiiyRUcVCENvN7syT94GZfUkPo7Ydbz9SQpkYBBnuBGBeAZ"),
        Triple(4,
            "HHMzAdd3YNjvU8SBbi77pf1CBAHAVZ2xoGhVpdriquzD",
            "2nCPxrj3KRUTQPkqvxs3L2cskDnuasjZaMDBczLxy28ntXFM4rEPi3vpiG8AtGBDji1nSjTkMMoSdvuxDuDWcVk1"),
        Triple(5,
            "Gs4P2XVCwKEKLyhj5qaxGYqTfp16tJUDT5qhVmCJNLjz",
            "5RGX2n3TEHoi1Ra3DvHQmezkGSBp4EAQgX1SPStcNhrg23CtjKgGDWiwZ3yVofi6FxsDe4iYJqizC3Jgn9xKzEuW"),
        Triple(6,
            "6Mk1JbQp2eFcwQjtRBr7TtuSpr3ocK1Ke6YpRn4dD6Wa",
            "33TMM39LTfdniseeuCotgiLGE6pBXDHrSuBXSgSe3aPQhAHCTAyjbLuq19TJH8tPLjfmhKLY2oALJ7skrVgEPDyg"),
        Triple(7,
            "C2KrX5RjuXy7CqcBCKHua9eJxqdiJ4cb3tbQ3WmfMaUX",
            "4DzVVASUM19mZrk8W2XwEXt4tHDRKcQWsUx3qvf58dp9v6EWuD6VJSpnubVmrrPp6vLFwNvvuTdr3HPx24c1nsz7"),
        Triple(8,
            "5RexpHu5cHhLmNH9Kr22q7Kpybg1RRV41ukN1zh1vDK9",
            "3k23Nba9QxHEFS3DWjpQj3zTFqPV3aT7tg8LKWhDD8aCkBWLZ8mUBtCGRdryjWqi7htvogjaXGBB3sgy1GJdiMGP"),
        Triple(9,
            "CL2s8d6T8tULpX6vLRHLdhF5omU9SKj3N5PGpygZYjh7",
            "HhvXvokRemjtCgC8qSAAgfZ8BHXvsSwwCUuWKeDeLtAQ61HHVhdL32bPFRJw4ZCSCpTTFtwKkiKNwfgafkzneER"),
        Triple(10,
            "DB7tgWejnGXydW9tvyr9atsBhmB6CDz326KG2nnm96HN",
            "4s69nsCdwTKWFWrrriLkc3cptfH8niYh7axq3r5odKK9rayd8A64Zie2JfVcg8QHAm7XHN5WwdzHYEnZKxWQcxyN"),
    ).map { (accountIndex, publicKey, privateKey) ->
        DynamicTest.dynamicTest("Create key pair for account #$accountIndex") {
            runBlocking {
                val keyPair = KeyFactory.createKeyPairFromMnemonic(WORDS, "password", accountIndex)

                Assertions.assertEquals(publicKey, keyPair.publicKey.toBase58String())
                Assertions.assertEquals(privateKey, keyPair.privateKey.toBase58String())
            }
        }
    }
}