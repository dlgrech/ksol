package com.dgsd.ksol.model

data class AccountInfo(

    /**
     * The `PublicKey` of the account
     */
    val publicKey: PublicKey,

    /**
     * `PublicKey` of the program this account has been assigned to
     */
    val ownerHash: PublicKey,

    /**
     * Number of lamports assigned to this account
     */
    val lamports: Lamports,

    /**
     * Indicates if the account contains a program (and is strictly read-only)
     */
    val isExecutable: Boolean,

    /**
     * The epoch at which this account will next owe rent
     */
    val rentEpoch: Long,

    /**
     * Base64-encoding of the account data
     */
    val accountData: String,
)