package com.dgsd.ksol.keygen

import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.PrivateKey
import com.dgsd.ksol.model.PublicKey
import com.dgsd.ksol.utils.reverseBytes
import com.dgsd.ksol.utils.toByteArray
import com.iwebpp.crypto.TweetNaclFast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.MnemonicCode
import java.security.SecureRandom

private val SOLANA_CURVE_SEED = "ed25519 seed".toByteArray()

/**
 * Helper for generating public/private key pairs
 */
object KeyFactory {

    /**
     * Create a [KeyPair] from a private key
     */
    suspend fun createKeyPairFromPrivateKey(
        privateKey: PrivateKey,
    ): KeyPair = withContext(Dispatchers.IO) {
        val keyPair = TweetNaclFast.Signature.keyPair_fromSecretKey(privateKey.key)

        KeyPair(
            PublicKey(keyPair.publicKey),
            PrivateKey(keyPair.secretKey),
        )
    }

    /**
     * @param words list of words that represent a seed phrase
     * @param passPhrase an additional "password" that will be used to generate the seed
     */
    suspend fun createSeedFromMnemonic(
        words: List<String>,
        passPhrase: String = "",
    ): ByteArray = withContext(Dispatchers.IO) {
        MnemonicCode.toSeed(words, passPhrase)
    }

    /**
     * @param words list of words that represent a seed phrase
     * @param passPhrase an additional "password" that will be used to generate the seed
     * @param accountIndex The index of the account to use in the derivation path
     *
     * @see DerivationPath.account
     */
    suspend fun createKeyPairFromMnemonic(
        words: List<String>,
        passPhrase: String = "",
        accountIndex: Int = 0,
    ): KeyPair = withContext(Dispatchers.IO) {
        val seed = createSeedFromMnemonic(words, passPhrase)
        val derivationPath = DerivationPath.solanaBip44(accountIndex)

        createKeyPair(seed, derivationPath)
    }

    suspend fun createKeyPairFromSeed(
        seed: ByteArray,
    ): KeyPair = withContext(Dispatchers.IO) {
        val keyPair = TweetNaclFast.Signature.keyPair_fromSeed(seed)
        KeyPair(
            PublicKey(keyPair.publicKey),
            PrivateKey(keyPair.secretKey),
        )
    }

    /**
     * Create a new mnemonic word list, which can be used to generate a public/private key pair
     *
     * @see createSeedFromMnemonic
     */
    suspend fun createMnemonic(
        phraseLength: MnemonicPhraseLength = MnemonicPhraseLength.TWENTY_FOUR,
    ): List<String> {
        val entropy = ByteArray(phraseLength.byteLength)
        SecureRandom().nextBytes(entropy)
        return MnemonicCode.INSTANCE.toMnemonic(entropy)
    }

    /**
     * Get the complete list of words that are used as the basis for mnenmonic code generation
     */
    suspend fun getValidMnemonicWords(): List<String> {
        return MnemonicCode.INSTANCE.wordList
    }

    private suspend fun createKeyPair(
        seed: ByteArray,
        derivationPath: DerivationPath,
    ): KeyPair {
        val masterAddress = createAddressFromSeed(seed)
        val purposeAddress = createChildAddress(masterAddress, derivationPath.purpose)
        val coinTypeAddress = createChildAddress(purposeAddress, derivationPath.coinType)
        val accountTypeAddress = createChildAddress(coinTypeAddress, derivationPath.account)
        val changeAddress = createChildAddress(accountTypeAddress, derivationPath.change)

        return createKeyPairFromSeed(changeAddress.secretKey)
    }

    private fun createAddressFromSeed(seed: ByteArray): DerivationAddress {
        val sha512 = HDUtils.hmacSha512(SOLANA_CURVE_SEED, seed)
        return sha512.toDerivationAddress()
    }

    private fun createChildAddress(parent: DerivationAddress, childIndex: Int): DerivationAddress {
        return createChildAddress(parent, ChildIndex.Hardened(childIndex))
    }

    private fun createChildAddress(parent: DerivationAddress, childIndex: ChildIndex): DerivationAddress {
        // See rust implementation: https://github.com/jpopesculian/ed25519-dalek-bip32/blob/main/src/lib.rs#L82-L104

        val sha512 = HDUtils.hmacSha512(
            parent.chainCode,
            byteArrayOf(
                0,
                *parent.secretKey,
                *childIndex.toBits().reverseBytes().toByteArray(),
            )
        )

        return sha512.toDerivationAddress()
    }

    private fun ByteArray.toDerivationAddress(): DerivationAddress {
        check(this.size == 64) {
            "Expected 64-bytes, but got ${this.size}"
        }

        val privateKey = copyOfRange(0, 32)
        val chainCode = copyOfRange(32, 64)

        return DerivationAddress(privateKey, chainCode)
    }
}