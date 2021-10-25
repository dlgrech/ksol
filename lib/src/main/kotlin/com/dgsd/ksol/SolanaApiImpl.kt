package com.dgsd.ksol

import com.dgsd.ksol.jsonrpc.RpcRequestFactory
import com.dgsd.ksol.jsonrpc.SolanaJsonRpcConstants
import com.dgsd.ksol.jsonrpc.networking.RpcError
import com.dgsd.ksol.jsonrpc.networking.RpcException
import com.dgsd.ksol.jsonrpc.networking.RpcIOException
import com.dgsd.ksol.jsonrpc.networking.util.await
import com.dgsd.ksol.jsonrpc.types.CommitmentConfigRequestBody
import com.dgsd.ksol.jsonrpc.types.RecentBlockhashResponseBody
import com.dgsd.ksol.jsonrpc.types.RpcRequest
import com.dgsd.ksol.jsonrpc.types.RpcResponse
import com.dgsd.ksol.model.Cluster
import com.dgsd.ksol.model.Commitment
import com.dgsd.ksol.model.RecentBlockhashResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

/**
 * Internal implementation of [SolanaApi] interface
 */
internal class SolanaApiImpl(
    private val cluster: Cluster,
    private val okHttpClient: OkHttpClient,
) : SolanaApi {

    private val moshiJson: Moshi = Moshi.Builder().build()

    private val requestJsonAdapter = moshiJson.adapter(RpcRequest::class.java)

    override suspend fun getRecentBlockhash(commitment: Commitment): RecentBlockhashResult {
        val responseBody = executeRequest<RecentBlockhashResponseBody>(
            RpcRequestFactory.create(
                SolanaJsonRpcConstants.Methods.GET_RECENT_BLOCKHASH,
                CommitmentConfigRequestBody(
                    when (commitment) {
                        Commitment.FINALIZED -> CommitmentConfigRequestBody.FINALIZED
                        Commitment.CONFIRMED -> CommitmentConfigRequestBody.CONFIRMED
                        Commitment.PROCESSED -> CommitmentConfigRequestBody.PROCESSED
                    }
                )
            )
        )

        return RecentBlockhashResult(
            responseBody.value.blockhash,
            responseBody.value.feeCalculator.value
        )
    }

    private suspend inline fun <reified T> executeRequest(rpcRequest: RpcRequest): T {
        try {
            val httpRequest = rpcRequest.asHttpRequest()

            val httpResponse = okHttpClient.newCall(httpRequest).await()
            val responseJson = checkNotNull(httpResponse.body).string()

            val responseParser = moshiJson.adapter<RpcResponse<T>>(
                Types.newParameterizedType(RpcResponse::class.java, T::class.java)
            )

            val response = responseParser.fromJson(responseJson)
            if (response?.error != null) {
                throw RpcError(response.error.code, response.error.message)
            } else if (response?.result == null) {
                throw RpcException("Missing result body when executing request: ${rpcRequest.methodName}")
            } else {
                return response.result
            }
        } catch (e: IOException) {
            throw RpcIOException("Error executing request: ${rpcRequest.methodName}", e)
        } catch (e: Throwable) {
            throw RpcException("Error executing request: ${rpcRequest.methodName}", e)
        }
    }

    private fun RpcRequest.asHttpRequest(): Request {
        return Request.Builder()
            .url(cluster.endpoint)
            .post(requestJsonAdapter.toJson(this).toRequestBody(MEDIA_TYPE_JSON))
            .build()
    }
}