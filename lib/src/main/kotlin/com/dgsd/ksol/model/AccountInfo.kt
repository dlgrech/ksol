package com.dgsd.ksol.model

data class AccountInfo(

    /**
     * Base-58 encoded Pubkey of the program this account has been assigned to
     */
    val ownerHash: String,

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
    val accountData: String
)