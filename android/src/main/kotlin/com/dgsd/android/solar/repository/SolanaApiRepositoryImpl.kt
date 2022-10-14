package com.dgsd.android.solar.repository

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.dgsd.android.solar.cache.CacheStrategy
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.common.util.executeWithCache
import com.dgsd.android.solar.common.util.flatMapSuccess
import com.dgsd.android.solar.common.util.resourceFlowOf
import com.dgsd.android.solar.model.LamportsWithTimestamp
import com.dgsd.android.solar.model.TransactionOrSignature
import com.dgsd.android.solar.repository.cache.balance.BalanceCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache.TransactionSignaturesCacheKey
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.core.LocalTransactions
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.time.OffsetDateTime

private const val TRANSACTION_SIGNATURE_PAGE_SIZE = 8

internal class SolanaApiRepositoryImpl(
  private val coroutineScope: CoroutineScope,
  private val session: WalletSession,
  private val solanaApi: SolanaApi,
  private val balanceCache: BalanceCache,
  private val transactionCache: TransactionCache,
  private val transactionSignaturesCache: TransactionSignaturesCache,
) : SolanaApiRepository {

  private val solanaSubscription = solanaApi.createSubscription()

  init {
    ProcessLifecycleOwner.get().lifecycle.addObserver(
      object : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
          connectAndSubscribeToAccount()
        }

        override fun onStop(owner: LifecycleOwner) {
          solanaSubscription.disconnect()
        }
      }
    )
  }

  override fun getBalance(
    cacheStrategy: CacheStrategy,
    commitment: Commitment
  ): Flow<Resource<LamportsWithTimestamp>> {
    return getBalanceOfAccount(session.publicKey, cacheStrategy, commitment)
  }

  override fun getBalanceOfAccount(
    account: PublicKey,
    cacheStrategy: CacheStrategy,
    commitment: Commitment
  ): Flow<Resource<LamportsWithTimestamp>> {
    return executeWithCache(
      cacheKey = account,
      cacheStrategy = cacheStrategy,
      cache = balanceCache,
      networkFlowProvider = {
        resourceFlowOf {
          LamportsWithTimestamp(solanaApi.getBalance(account, commitment), OffsetDateTime.now())
        }
      }
    )
  }

  override fun getTransactions(
    cacheStrategy: CacheStrategy,
    beforeSignature: TransactionSignature?,
    commitment: Commitment,
  ): Flow<Resource<List<Resource<TransactionOrSignature>>>> {
    return getTransactionSignatures(
      cacheStrategy,
      beforeSignature,
      commitment
    ).flatMapSuccess { signatureList ->
      val transactionsFlow = signatureList.map { signatureInfo ->
        getTransaction(
          CacheStrategy.CACHE_IF_PRESENT,
          signatureInfo.signature
        ).map { transactionResource ->
          when (transactionResource) {
            is Resource.Error -> {
              Resource.Error(
                error = transactionResource.error,
                data = TransactionOrSignature(signatureInfo.signature)
              )
            }

            is Resource.Loading -> {
              Resource.Loading(
                data = transactionResource.data?.let { TransactionOrSignature(it) }
                  ?: TransactionOrSignature(signatureInfo.signature)
              )
            }

            is Resource.Success -> {
              Resource.Success(TransactionOrSignature(transactionResource.data))
            }
          }
        }
      }

      if (transactionsFlow.isEmpty()) {
        flowOf(Resource.Success(emptyList()))
      } else {
        combine(transactionsFlow) { transactionsArray ->
          transactionsArray.toList()
        }.map { transactionList ->
          Resource.Success(transactionList)
        }
      }
    }
  }

  override fun getTransaction(
    cacheStrategy: CacheStrategy,
    transactionSignature: TransactionSignature,
    commitment: Commitment
  ): Flow<Resource<Transaction>> {
    return executeWithCache(
      cacheKey = transactionSignature,
      cacheStrategy = cacheStrategy,
      cache = transactionCache,
      networkFlowProvider = {
        resourceFlowOf {
          checkNotNull(solanaApi.getTransaction(transactionSignature, commitment))
        }
      }
    )
  }

  override fun getRecentBlockhash(): Flow<Resource<RecentBlockhashResult>> {
    return resourceFlowOf {
      solanaApi.getRecentBlockhash(Commitment.FINALIZED)
    }
  }

  override fun send(
    privateKey: PrivateKey,
    recipient: PublicKey,
    lamports: Lamports,
    memo: String?
  ): Flow<Resource<TransactionSignature>> {
    return resourceFlowOf {
      val blockhash = PublicKey.fromBase58(
        solanaApi.getRecentBlockhash(Commitment.FINALIZED).blockhash
      )

      send(
        LocalTransactions.createTransferTransaction(
          sender = KeyPair(session.publicKey, privateKey),
          recipient = recipient,
          lamports = lamports,
          memo = memo,
          recentBlockhash = blockhash
        )
      )
    }
  }

  override fun signAndSend(
    privateKey: PrivateKey,
    localTransaction: LocalTransaction
  ): Flow<Resource<TransactionSignature>> {
    return resourceFlowOf {
      val keyPair = KeyPair(session.publicKey, privateKey)
      println("HERE: about to sign")
      val signedTransaction = LocalTransactions.sign(localTransaction, keyPair)
      println("HERE: SIGNED!")
      send(signedTransaction)
    }
  }

  override fun subscribeToUpdates(
    transactionSignature: TransactionSignature,
    commitment: Commitment,
  ): SubscriptionHandle<TransactionSignatureStatus> {
    return object : SubscriptionHandle<TransactionSignatureStatus> {
      override fun observe(): Flow<TransactionSignatureStatus> {
        ensureSubscriptionConnected()
        return solanaSubscription.signatureSubscribe(transactionSignature, commitment)
      }

      override fun stop() {
        solanaSubscription.signatureUnsubscribe(transactionSignature)
      }
    }
  }


  override fun close() {
    solanaSubscription.disconnect()
  }

  private fun connectAndSubscribeToAccount() {
    ensureSubscriptionConnected()

    solanaSubscription.accountSubscribe(
      session.publicKey,
      Commitment.FINALIZED
    ).onEach { accountInfo ->
      balanceCache.set(
        accountInfo.publicKey,
        LamportsWithTimestamp(accountInfo.lamports, OffsetDateTime.now())
      )
      getTransactionSignatures(
        cacheStrategy = CacheStrategy.NETWORK_ONLY,
        beforeSignature = null,
        commitment = Commitment.FINALIZED
      )
    }.launchIn(coroutineScope)
  }

  private fun ensureSubscriptionConnected() {
    if (!solanaSubscription.isConnected()) {
      solanaSubscription.connect()
    }
  }

  private fun getTransactionSignatures(
    cacheStrategy: CacheStrategy,
    beforeSignature: TransactionSignature?,
    commitment: Commitment
  ): Flow<Resource<List<TransactionSignatureInfo>>> {
    return executeWithCache(
      cacheKey = TransactionSignaturesCacheKey(session.publicKey, beforeSignature),
      cacheStrategy = cacheStrategy,
      cache = transactionSignaturesCache,
      networkFlowProvider = {
        resourceFlowOf {
          val result = solanaApi.getSignaturesForAddress(
            accountKey = session.publicKey,
            before = beforeSignature,
            limit = TRANSACTION_SIGNATURE_PAGE_SIZE,
            commitment = commitment
          )
          result
        }
      }
    )
  }

  private suspend fun send(signedTransaction: LocalTransaction): TransactionSignature {
    println("HERE: about to send")
    val signature = solanaApi.sendTransaction(signedTransaction)
    println("HERE: SENT: $signature")
    balanceCache.clear()
    transactionSignaturesCache.clear()
    return signature
  }
}