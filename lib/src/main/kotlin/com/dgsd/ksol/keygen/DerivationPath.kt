package com.dgsd.ksol.keygen

private const val BIP44_PURPOSE = 44
private const val SOLANA_COIN_TYPE = 501

/**
 * Represents a path in a Hierarchical Deterministic key, according to BIP44
 *
 * @see <a href="https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki">bip-0044</a>
 * @see <a href="https://www.w3.org/2016/04/blockchain-workshop/interest/robles.html">Hierarchical Deterministic Keys</a>
 */
internal data class DerivationPath private constructor(

    /**
     * Purpose is a constant set to 44' (or 0x8000002C) following the BIP43 recommendation. It indicates that the subtree of this node is used according to this specification
     */
    val purpose: Int,

    /**
     * A constant, for each type of crypto coin.
     */
    val coinType: Int,

    /**
     * Splits the key space into independent user identities.
     *
     * This can be used in the same fashion as a user having multiple bank accounts
     */
    val account: Int,

    /**
     * Constant 0 (for addresses that should be publicly accessible), or 1, for addresses that should be kept internal
     */
    val change: Int,
) {

    companion object {

        /**
         * Create a `DerivationPath` representing a Solana Bip44 address
         */
        fun solanaBip44(accountIndex: Int): DerivationPath {
            return DerivationPath(
                purpose = BIP44_PURPOSE,
                coinType = SOLANA_COIN_TYPE,
                account = accountIndex,
                change = 0,
            )
        }
    }
}
