package com.dgsd.ksol.jsonrpc.types

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#transaction-structure">json-rpc API</a>
 */
@JsonClass(generateAdapter = true)
internal data class TransactionResponse(

    /**
     * Defines the content of the transaction
     */
    @Json(name = "message") val message: Message,

    /**
     * A list of base-58 encoded signatures applied to the transaction. This list is always non-empty
     */
    @Json(name = "signatures") val signatures: List<String>,
) {

    @JsonClass(generateAdapter = true)
    internal data class Message(

        /**
         * List of base-58 encoded public keys used by the transaction, including by the instructions and for signatures
         */
        @Json(name = "accountKeys") val accountKeys: List<String>,

        /**
         * A base-58 encoded hash of a recent block in the ledger used to prevent transaction duplication and to give
         * transactions lifetimes
         */
        @Json(name = "recentBlockhash") val recentBlockhash: String,

        /**
         * Details the account types and signatures required by the transaction
         */
        @Json(name = "header") val header: Header,

        /**
         * List of program instructions that will be executed in sequence and committed in one atomic transaction
         * if all succeed
         */
        @Json(name = "instructions") val instructions: List<Instruction>,
    ) {

        @JsonClass(generateAdapter = true)
        internal data class Header(

            /**
             *The total number of signatures required to make the transaction valid
             */
            @Json(name = "numRequiredSignatures") val numRequiredSignatures: Int,

            /**
             * The last `numReadonlySignedAccounts` of the signed keys are read-only accounts
             */
            @Json(name = "numReadonlySignedAccounts") val numReadonlySignedAccounts: Int,

            /**
             *The last numReadonlyUnsignedAccounts of the unsigned keys are read-only accounts
             */
            @Json(name = "numReadonlyUnsignedAccounts") val numReadonlyUnsignedAccounts: Int,
        )

        @JsonClass(generateAdapter = true)
        internal data class Instruction(

            /**
             * Index into the `message.accountKeys` array indicating the program account that executes this instruction
             */
            @Json(name = "programIdIndex") val programIdIndex: Int,

            /**
             * The program input data encoded in a base-58 string
             */
            @Json(name = "data") val inputData: String,

            /**
             * List of ordered indices into the `message.accountKeys` array indicating which accounts to pass to
             * the program
             */
            @Json(name = "accounts") val accounts: List<Int>,
        )
    }
}