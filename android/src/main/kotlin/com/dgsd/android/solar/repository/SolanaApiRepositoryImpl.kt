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
import com.dgsd.ksol.LocalTransactions
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import java.time.OffsetDateTime

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
    limit: Int,
    beforeSignature: TransactionSignature?,
    commitment: Commitment,
  ): Flow<Resource<List<Resource<TransactionOrSignature>>>> {
    return getTransactionSignatures(
      cacheStrategy,
      limit,
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

      combine(transactionsFlow) { transactionsArray ->
        transactionsArray.toList()
      }.map { transactionList ->
        Resource.Success(transactionList)
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
    lamports: Lamports
  ): Flow<Resource<TransactionSignature>> {
    return resourceFlowOf {
      val blockhash = PublicKey.fromBase58(
        solanaApi.getRecentBlockhash(Commitment.FINALIZED).blockhash
      )

      solanaApi.sendTransaction(
        LocalTransactions.createTransferTransaction(
          sender = KeyPair(session.publicKey, privateKey),
          recipient = recipient,
          lamports = lamports,
          recentBlockhash = blockhash
        )
      )
    }
  }

  override fun close() {
    solanaSubscription.disconnect()
  }

  private fun connectAndSubscribeToAccount() {
    if (!solanaSubscription.isConnected()) {
      solanaSubscription.connect()
    }

    solanaSubscription.accountSubscribe(
      session.publicKey,
      Commitment.FINALIZED
    ).onEach { accountInfo ->
      balanceCache.set(
        accountInfo.publicKey,
        LamportsWithTimestamp(accountInfo.lamports, OffsetDateTime.now())
      )
      transactionSignaturesCache.clear()
    }.launchIn(coroutineScope)
  }

  private fun getTransactionSignatures(
    cacheStrategy: CacheStrategy,
    limit: Int,
    beforeSignature: TransactionSignature?,
    commitment: Commitment
  ): Flow<Resource<List<TransactionSignatureInfo>>> {
    return executeWithCache(
      cacheKey = TransactionSignaturesCacheKey(
        session.publicKey,
        limit,
        beforeSignature,
      ),
      cacheStrategy = cacheStrategy,
      cache = transactionSignaturesCache,
      networkFlowProvider = {
        resourceFlowOf {
          val result = solanaApi.getSignaturesForAddress(
            accountKey = session.publicKey,
            before = beforeSignature,
            limit = limit,
            commitment = commitment
          )
          println("HERE: got result: ${result.map { it.signature }}")
          result
        }
      }
    )
  }
}