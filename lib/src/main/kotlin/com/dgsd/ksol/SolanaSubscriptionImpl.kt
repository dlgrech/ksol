package com.dgsd.ksol

import com.dgsd.ksol.factory.AccountInfoFactory
import com.dgsd.ksol.factory.CommitmentFactory
import com.dgsd.ksol.flow.MutableEventFlow
import com.dgsd.ksol.jsonrpc.RpcRequestFactory
import com.dgsd.ksol.jsonrpc.SolanaJsonRpcConstants
import com.dgsd.ksol.jsonrpc.types.*
import com.dgsd.ksol.model.*
import com.dgsd.ksol.utils.fromJsonOrNull
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import okhttp3.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val WS_CLOSE_REASON_NORMAL = 1000

/**
 * Internal implementation of [SolanaSubscription] interface
 */
internal class SolanaSubscriptionImpl(
    private val cluster: Cluster,
    private val okHttpClient: OkHttpClient,
) : SolanaSubscription {

    private val subscriptionRegistry = SubscriptionRegistry()
    private val accountKeyToFlowMap =
        mutableMapOf<PublicKey, MutableEventFlow<AccountInfo>>()
    private val transactionSignatureToFlowMap =
        mutableMapOf<TransactionSignature, MutableEventFlow<TransactionSignatureStatus>>()

    private val moshi = Moshi.Builder().build()
    private val requestJsonAdapter = moshi.adapter(RpcRequest::class.java)

    private val socketLock = ReentrantLock()
    private var activeWebSocket: WebSocket? = null

    override fun connect() {
        socketLock.withLock {
            check(activeWebSocket == null) {
                "Web socket already started!"
            }

            val request = Request.Builder().url(cluster.webSocketUrl).build()
            activeWebSocket = okHttpClient.newWebSocket(request, SubscriptionWebSocketListener())
        }
    }

    override fun disconnect() {
        socketLock.withLock {
            activeWebSocket?.close(code = WS_CLOSE_REASON_NORMAL, reason = null)
            cleanup()
        }
    }

    override fun isConnected(): Boolean {
        return socketLock.withLock { activeWebSocket != null }
    }

    override fun accountSubscribe(
        accountKey: PublicKey,
        commitment: Commitment,
    ): Flow<AccountInfo> {
        val existingSubscriptionId = subscriptionRegistry.getSubscriptionIdFromAccount(accountKey)
        if (existingSubscriptionId == null) {
            val request = createAccountSubscribeRequest(accountKey, commitment)
            subscriptionRegistry.onAccountSubscribe(request.id, accountKey, commitment)
            sendRequest(request)
        }

        return accountKeyToFlowMap.getOrPut(accountKey) { MutableEventFlow() }
    }

    override fun accountUnsubscribe(accountKey: PublicKey) {
        val existingSubscriptionId = subscriptionRegistry.getSubscriptionIdFromAccount(accountKey)
        if (existingSubscriptionId != null) {
            val request = createAccountUnsubscribeRequest(existingSubscriptionId)
            sendRequest(request)
            subscriptionRegistry.clearAccountSubscription(accountKey)
        }

        accountKeyToFlowMap.remove(accountKey)
    }

    override fun signatureSubscribe(
        signature: TransactionSignature,
        commitment: Commitment,
    ): Flow<TransactionSignatureStatus> {
        val existingSubscriptionId = subscriptionRegistry.getSubscriptionIdFromSignature(signature)
        if (existingSubscriptionId == null) {
            val request = createTransactionSubscribeRequest(signature, commitment)
            subscriptionRegistry.onSignatureSubscribe(request.id, signature, commitment)
            sendRequest(request)
        }

        return transactionSignatureToFlowMap.getOrPut(signature) { MutableEventFlow() }
    }

    override fun signatureUnsubscribe(signature: TransactionSignature) {
        val existingSubscriptionId = subscriptionRegistry.getSubscriptionIdFromSignature(signature)
        if (existingSubscriptionId != null) {
            val request = createTransactionUnsubscribeRequest(existingSubscriptionId)
            sendRequest(request)
            subscriptionRegistry.clearSignatureSubscription(signature)
        }

        transactionSignatureToFlowMap.remove(signature)
    }

    private fun createAccountSubscribeRequest(accountKey: PublicKey, commitment: Commitment): RpcRequest {
        return RpcRequestFactory.create(
            SolanaJsonRpcConstants.Subscriptions.ACCOUNT_SUBSCRIBE,
            accountKey.toBase58String(),
            GetAccountInfoRequestBody(
                commitment = CommitmentFactory.toRpcValue(commitment),
                encoding = SolanaJsonRpcConstants.Encodings.BASE64
            )
        )
    }

    private fun createAccountUnsubscribeRequest(subscriptionId: Long): RpcRequest {
        return RpcRequestFactory.create(
            SolanaJsonRpcConstants.Subscriptions.ACCOUNT_UNSUBSCRIBE,
            subscriptionId
        )
    }

    private fun createTransactionSubscribeRequest(
        transactionSignature: TransactionSignature,
        commitment: Commitment,
    ): RpcRequest {
        return RpcRequestFactory.create(
            SolanaJsonRpcConstants.Subscriptions.SIGNATURE_SUBSCRIBE,
            transactionSignature,
            CommitmentConfigRequestBody(CommitmentFactory.toRpcValue(commitment))
        )
    }

    private fun createTransactionUnsubscribeRequest(subscriptionId: Long): RpcRequest {
        return RpcRequestFactory.create(
            SolanaJsonRpcConstants.Subscriptions.SIGNATURE_UNSUBSCRIBE,
            subscriptionId
        )
    }

    private fun sendRequest(request: RpcRequest) {
        val socket = checkNotNull(activeWebSocket) {
            "No web socket found. Need to call start() yet"
        }

        socket.send(requestJsonAdapter.toJson(request))
    }

    private fun cleanup() {
        socketLock.withLock {
            activeWebSocket = null
            subscriptionRegistry.clear()
        }
    }

    private fun onSubscriptionConfirmation(confirmation: RpcSubscriptionConfirmation) {
        subscriptionRegistry.onSubscriptionConfirmation(confirmation.id, confirmation.result)
    }

    private fun onAccountNotification(notification: RpcSubscriptionNotification<AccountInfoResponse>) {
        val accountKey = subscriptionRegistry.getAccountKeyFromSubscriptionId(notification.params.subscriptionId)

        if (accountKey != null) {
            val accountInfo = AccountInfoFactory.create(accountKey, notification.params.result.value)
            if (accountInfo != null) {
                accountKeyToFlowMap[accountKey]?.tryEmit(accountInfo)
            }
        }
    }

    private fun onSignatureNotification(notification: RpcSubscriptionNotification<SignatureNotificationResponseBody>) {
        val transactionSignature =
            subscriptionRegistry.getSignatureFromSubscriptionId(notification.params.subscriptionId)
        val commitment = subscriptionRegistry.getCommitmentFromSubscriptionId(notification.params.subscriptionId)
        if (transactionSignature != null && commitment != null) {
            val transactionSignatureStatus = TransactionSignatureStatus.Confirmed(
                signature = transactionSignature,
                slot = notification.params.result.context.slot,
                errorMessage = notification.params.result.value.error?.message,
                commitment = commitment
            )

            transactionSignatureToFlowMap[transactionSignature]?.tryEmit(transactionSignatureStatus)
        }
    }

    private inner class SubscriptionWebSocketListener : WebSocketListener() {

        private val subscriptionConfirmationJsonAdapter = moshi.adapter(RpcSubscriptionConfirmation::class.java)
        private val accountNotificationJsonAdapter = moshi.adapter<RpcSubscriptionNotification<AccountInfoResponse>>(
            Types.newParameterizedType(RpcSubscriptionNotification::class.java, AccountInfoResponse::class.java)
        )
        private val signatureNotificationJsonAdapter =
            moshi.adapter<RpcSubscriptionNotification<SignatureNotificationResponseBody>>(
                Types.newParameterizedType(
                    RpcSubscriptionNotification::class.java,
                    SignatureNotificationResponseBody::class.java
                )
            )

        override fun onMessage(webSocket: WebSocket, text: String) {
            val confirmation = subscriptionConfirmationJsonAdapter.fromJsonOrNull(text)
            if (confirmation != null) {
                onSubscriptionConfirmation(confirmation)
                return
            }

            val accountNotification = accountNotificationJsonAdapter.fromJsonOrNull(text)
            if (accountNotification != null) {
                onAccountNotification(accountNotification)
                return
            }

            val signatureNotification = signatureNotificationJsonAdapter.fromJsonOrNull(text)
            if (signatureNotification != null) {
                onSignatureNotification(signatureNotification)
                return
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            t.printStackTrace()
            cleanup()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            cleanup()
        }
    }
}