package com.dgsd.ksol

import com.dgsd.ksol.model.*
import okhttp3.OkHttpClient

/**
 * Create a default implementation of [SolanaApi] using the given [Cluster]
 */
fun SolanaApi(
    cluster: Cluster,
    okHttpClient: OkHttpClient = OkHttpClient(),
): SolanaApi {
    return SolanaApiImpl(cluster, okHttpClient)
}

/**
 * API for interacting with the Solana blockchain, using the JSON-RPC API
 *
 * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api">json-rpc API</a>
 */
interface SolanaApi {

    /**
     * Returns all information associated with the account of provided Pubkey
     *
     * @param accountKey Pubkey of account to query, as base-58 encoded string
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getaccountinfo">json-rpc API</a>
     */
    suspend fun getAccountInfo(
        accountKey: PublicKey,
        commitment: Commitment = Commitment.FINALIZED,
    ): AccountInfo?

    /**
     * Returns the balance of the account of provided Pubkey
     *
     * @param accountKey Pubkey of account to query, as base-58 encoded string
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getbalance">json-rpc API</a>
     */
    suspend fun getBalance(
        accountKey: PublicKey,
        commitment: Commitment = Commitment.FINALIZED,
    ): Lamports

    /**
     * Returns the current block height of the node
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getblockheight">json-rpc API</a>
     */
    suspend fun getBlockHeight(
        commitment: Commitment = Commitment.FINALIZED,
    ): Long

    /**
     * Returns the estimated production time of a block.
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getblocktime">json-rpc API</a>
     *
     * @return estimated production time, as Unix timestamp (seconds since the Unix epoch), or {@code null} if the
     * timestamp is not available for this block
     */
    suspend fun getBlockTime(
        blockSlotNumber: Long,
    ): Long?

    /**
     * Returns the 20 largest accounts, by lamport balance
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getlargestaccounts">json-rpc API</a>
     */
    suspend fun getLargestAccounts(
        circulatingStatus: AccountCirculatingStatus? = null,
        commitment: Commitment = Commitment.FINALIZED,
    ): List<AccountBalance>

    /**
     * Returns minimum balance required to make account rent exempt.
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getminimumbalanceforrentexemption">json-rpc API</a>
     */
    suspend fun getMinimumBalanceForRentExemption(
        accountDataLength: Long,
        commitment: Commitment = Commitment.FINALIZED,
    ): Lamports

    /**
     * Returns a recent block hash from the ledger, and a fee schedule that can be used to compute the cost of submitting a transaction using it.
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getrecentblockhash">json-rpc API</a>
     */
    suspend fun getRecentBlockhash(
        commitment: Commitment = Commitment.FINALIZED,
    ): RecentBlockhashResult

    /**
     * Returns all accounts owned by the provided program Pubkey
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getprogramaccounts">json-rpc API</a>
     */
    suspend fun getProgramAccounts(
        programKey: PublicKey,
        commitment: Commitment = Commitment.FINALIZED,
    ): List<AccountInfo>

    /**
     * Returns information about the current supply.
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#getsupply">json-rpc API</a>
     */
    suspend fun getSupply(
        commitment: Commitment = Commitment.FINALIZED,
    ): SupplySummary

    /**
     * Returns the current Transaction count from the ledger
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#gettransactioncount">json-rpc API</a>
     */
    suspend fun getTransactionCount(
        commitment: Commitment = Commitment.FINALIZED,
    ): Long

    /**
     * Requests an airdrop of lamports to a Pubkey
     *
     * @see <a href="https://docs.solana.com/developing/clients/jsonrpc-api#requestairdrop">json-rpc API</a>
     */
    suspend fun requestAirdrop(
        accountKey: PublicKey,
        amount: Lamports,
        commitment: Commitment = Commitment.FINALIZED,
    ): TransactionSignature
}