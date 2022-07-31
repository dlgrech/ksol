package com.dgsd.ksol

import com.dgsd.ksol.factory.AccountInfoFactory
import com.dgsd.ksol.factory.CommitmentFactory
import com.dgsd.ksol.factory.TransactionFactory
import com.dgsd.ksol.jsonrpc.RpcRequestFactory
import com.dgsd.ksol.jsonrpc.SolanaJsonRpcConstants
import com.dgsd.ksol.jsonrpc.networking.RpcError
import com.dgsd.ksol.jsonrpc.networking.RpcException
import com.dgsd.ksol.jsonrpc.networking.RpcIOException
import com.dgsd.ksol.jsonrpc.networking.util.await
import com.dgsd.ksol.jsonrpc.types.*
import com.dgsd.ksol.model.*
import com.dgsd.ksol.serialization.LocalTransactionSerialization
import com.dgsd.ksol.utils.EncodingUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.time.Duration

private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

private val SUBSCRIPTION_WEB_SOCKET_PING_INTERVAL = Duration.ofSeconds(30)

/**
 * Internal implementation of [SolanaApi] interface
 */
internal class SolanaApiImpl(
    private val cluster: Cluster,
    private val okHttpClient: OkHttpClient,
) : SolanaApi {

    private val moshiJson: Moshi = Moshi.Builder().build()

    private val requestJsonAdapter = moshiJson.adapter(RpcRequest::class.java)

    override fun createSubscription(): SolanaSubscription {
        return SolanaSubscriptionImpl(
            cluster,
            okHttpClient.newBuilder()
                .pingInterval(SUBSCRIPTION_WEB_SOCKET_PING_INTERVAL)
                .build()
        )
    }

    override suspend fun getAccountInfo(accountKey: PublicKey, commitment: Commitment): AccountInfo? {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_ACCOUNT_INFO,
            accountKey.toBase58String(),
            GetAccountInfoRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                encoding = SolanaJsonRpcConstants.Encodings.BASE64
            )
        )

        val response = executeRequest<GetAccountInfoResponseBody>(request)

        return AccountInfoFactory.create(accountKey, response.value)
    }

    override suspend fun getBalance(accountKey: PublicKey, commitment: Commitment): Lamports {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_BALANCE,
            accountKey.toBase58String(),
            commitment.toRequestBody()
        )

        val response = executeRequest<GetBalanceResponseBody>(request)

        return response.value
    }

    override suspend fun getBlockTime(blockSlotNumber: Long): Long? {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_BLOCK_TIME,
            blockSlotNumber
        )

        return executeRequest(request)
    }

    override suspend fun getBlockHeight(commitment: Commitment): Long {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_BLOCK_HEIGHT,
            commitment.toRequestBody()
        )

        return executeRequest(request)
    }

    override suspend fun getLargestAccounts(
        circulatingStatus: AccountCirculatingStatus?,
        commitment: Commitment,
    ): List<AccountBalance> {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_LARGEST_ACCOUNTS,
            GetLargestAccountsRequestBody(
                CommitmentFactory.toRpcValue(commitment),
                when (circulatingStatus) {
                    null -> null
                    AccountCirculatingStatus.CIRCULATING -> GetLargestAccountsRequestBody.FILTER_CIRCULATING
                    AccountCirculatingStatus.NON_CIRCULATING -> GetLargestAccountsRequestBody.FILTER_NON_CIRCULATING
                }
            )
        )

        val response = executeRequest<GetLargestAccountsResponseBody>(request)

        return response.value.map {
            AccountBalance(PublicKey.fromBase58(it.address), it.lamports)
        }
    }

    override suspend fun getMinimumBalanceForRentExemption(
        accountDataLength: Long,
        commitment: Commitment,
    ): Lamports {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_MINIMUM_BALANCE_FOR_RENT_EXEMPTION,
            accountDataLength,
            commitment.toRequestBody(),
        )

        return executeRequest(request)
    }

    override suspend fun getMultipleAccounts(
        accountKeys: List<PublicKey>,
        commitment: Commitment,
    ): Map<PublicKey, AccountInfo?> {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_MULTIPLE_ACCOUNTS,
            accountKeys.map { it.toBase58String() },
            GetMultipleAccountsRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                encoding = SolanaJsonRpcConstants.Encodings.BASE64
            )
        )

        val response = executeRequest<GetMultipleAccountsResponseBody>(request)

        return accountKeys.zip(response.value) { key, accountInfoResponse ->
            key to AccountInfoFactory.create(key, accountInfoResponse)
        }.toMap()
    }

    override suspend fun getProgramAccounts(programKey: PublicKey, commitment: Commitment): List<AccountInfo> {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_PROGRAM_ACCOUNTS,
            programKey.toBase58String(),
            GetProgramAccountsRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                encoding = SolanaJsonRpcConstants.Encodings.BASE64,
                withContext = true
            )
        )

        val response = executeRequest<GetProgramAccountsResponseBody>(request)

        return response.values.mapNotNull {
            AccountInfoFactory.create(PublicKey.fromBase58(it.pubKey), it.account)
        }
    }

    override suspend fun getRecentBlockhash(commitment: Commitment): RecentBlockhashResult {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_RECENT_BLOCKHASH,
            commitment.toRequestBody()
        )

        val response = executeRequest<RecentBlockhashResponseBody>(request)

        return RecentBlockhashResult(
            response.value.blockhash,
            response.value.feeCalculator.value
        )
    }

    override suspend fun getSignaturesForAddress(
        accountKey: PublicKey,
        limit: Int,
        before: TransactionSignature?,
        until: TransactionSignature?,
        commitment: Commitment,
    ): List<TransactionSignatureInfo> {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_SIGNATURES_FOR_ADDRESS,
            accountKey.toBase58String(),
            GetSignaturesForAddressRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                limit = limit,
                beforeTransactionSignature = before,
                untilTransactionSignature = until,
            )
        )

        val response = executeRequestAsList<GetSignaturesForAddressResponseBody>(request)

        return response.map {
            TransactionSignatureInfo(
                signature = it.signature,
                slot = it.slot,
                memo = it.memo,
                blockTime = it.blockTime,
                errorMessage = it.error?.message
            )
        }
    }

    override suspend fun getSignatureStatuses(
        transactionSignatures: List<String>,
        searchTransactionHistory: Boolean,
    ): List<TransactionSignatureStatus> {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_SIGNATURE_STATUSES,
            transactionSignatures,
            GetSignatureStatusesRequestBody(searchTransactionHistory)
        )

        val response = executeRequest<GetSignatureStatusesResponseBody>(request)

        return response.value.zip(transactionSignatures) { responseValue, signature ->
            if (responseValue == null) {
                TransactionSignatureStatus.UnknownTransaction(signature)
            } else {
                TransactionSignatureStatus.Confirmed(
                    signature = signature,
                    slot = responseValue.slot,
                    errorMessage = responseValue.error?.message,
                    commitment = CommitmentFactory.fromRpcValue(responseValue.confirmationStatus)
                )
            }
        }
    }

    override suspend fun getSupply(commitment: Commitment): SupplySummary {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_SUPPLY,
            GetSupplyRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                excludeNonCirculatingAccountsList = true
            )
        )

        val response = executeRequest<GetSupplyResponseBody>(request)

        return SupplySummary(
            circulating = response.value.circulating,
            nonCirculating = response.value.nonCirculating,
            total = response.value.total,
        )
    }

    override suspend fun getTransaction(
        transactionSignature: TransactionSignature,
        commitment: Commitment,
    ): Transaction? {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_TRANSACTION,
            transactionSignature,
            GetTransactionRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                encoding = SolanaJsonRpcConstants.Encodings.JSON
            )
        )

        val response = executeRequest<GetTransactionResponseBody?>(request)

        return TransactionFactory.create(response)
    }

    override suspend fun getTransactionCount(commitment: Commitment): Long {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.GET_TRANSACTION_COUNT,
            commitment.toRequestBody()
        )

        return executeRequest(request)
    }

    override suspend fun requestAirdrop(
        accountKey: PublicKey,
        amount: Lamports,
        commitment: Commitment,
    ): TransactionSignature {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.REQUEST_AIRDROP,
            accountKey.toBase58String(),
            amount,
            commitment.toRequestBody(),
        )

        return executeRequest(request)
    }

    override suspend fun sendTransaction(transaction: LocalTransaction): TransactionSignature {
        val request = RpcRequestFactory.create(
            SolanaJsonRpcConstants.Methods.SEND_TRANSACTION,
            EncodingUtils.encodeBase64(LocalTransactionSerialization.serialize(transaction)),
            SendTransactionRequestBody(SolanaJsonRpcConstants.Encodings.BASE64)
        )

        return executeRequest(request)
    }

    private suspend inline fun <reified T> executeRequestAsList(rpcRequest: RpcRequest): List<T> {
        val responseParser = moshiJson.adapter<RpcResponse<List<Any>>>(
            Types.newParameterizedType(RpcResponse::class.java, List::class.java)
        )
        val responseItemParser = moshiJson.adapter(T::class.java)

        return executeRequest(rpcRequest, responseParser).mapNotNull(responseItemParser::fromJsonValue)
    }

    private suspend inline fun <reified T> executeRequest(rpcRequest: RpcRequest): T {
        val responseParser = moshiJson.adapter<RpcResponse<T>>(
            Types.newParameterizedType(RpcResponse::class.java, T::class.java)
        )

        return executeRequest(rpcRequest, responseParser)
    }

    private suspend inline fun <reified T> executeRequest(
        rpcRequest: RpcRequest,
        responseParser: JsonAdapter<RpcResponse<T>>,
    ): T {
        try {
            val httpRequest = rpcRequest.asHttpRequest()
            val httpResponse = okHttpClient.newCall(httpRequest).await()

            val responseJson = checkNotNull(httpResponse.body).string()

            val parsedResponse = responseParser.fromJson(responseJson)

            if (parsedResponse?.error != null) {
                throw RpcError(parsedResponse.error.code, parsedResponse.error.message)
            } else if (parsedResponse?.result == null && null !is T) {
                throw RpcException("Missing result body when executing request: ${rpcRequest.methodName}")
            } else {
                return parsedResponse?.result as T
            }
        } catch (e: RpcError) {
            throw e
        } catch (e: RpcException) {
            throw e
        } catch (e: IOException) {
            throw RpcIOException("Error executing request: ${rpcRequest.methodName}", e)
        } catch (e: Throwable) {
            throw RpcException("Error executing request: ${rpcRequest.methodName}", e)
        }
    }

    private fun Commitment.toRequestBody(): CommitmentConfigRequestBody {
        return CommitmentConfigRequestBody(CommitmentFactory.toRpcValue(this))
    }

    private fun RpcRequest.asHttpRequest(): Request {
        return Request.Builder()
            .url(cluster.rpcUrl)
            .post(requestJsonAdapter.toJson(this).toRequestBody(MEDIA_TYPE_JSON))
            .build()
    }
}