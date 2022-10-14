package com.dgsd.android.solar.di

import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.common.ui.TransactionViewStateFactory
import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.di.util.scopedWithClose
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.repository.SolanaApiRepositoryImpl
import com.dgsd.android.solar.repository.cache.balance.BalanceCache
import com.dgsd.android.solar.repository.cache.balance.BalanceMemoryCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionInMemoryCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesCache
import com.dgsd.android.solar.repository.cache.transactions.TransactionSignaturesInMemoryCache
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.Session
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import com.dgsd.ksol.solpay.SolPay
import kotlinx.coroutines.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.scopedOf
import org.koin.dsl.module
import org.koin.dsl.onClose

internal object SessionScopedModule {

  fun create(): Module {
    return module {
      scope<Session> {
        scopedWithClose<SolanaApi> {
          SolanaApi(
            cluster = get<ClusterManager>().activeCluster.value,
            okHttpClient = get()
          )
        }

        scopedWithClose<SolPay> {
          SolPay(
            okHttpClient = get(),
            solanaApi = getScoped(),
          )
        }

        scopedWithClose<BalanceCache> {
          BalanceMemoryCache()
        }

        scopedWithClose<TransactionCache> {
          TransactionInMemoryCache()
        }

        scopedWithClose<TransactionSignaturesCache> {
          TransactionSignaturesInMemoryCache()
        }

        scopedWithClose<SolanaApiRepository> {
          SolanaApiRepositoryImpl(
            coroutineScope = getScoped(),
            session = getScoped(),
            solanaApi = getScoped(),
            balanceCache = getScoped(),
            transactionCache = getScoped(),
            transactionSignaturesCache = getScoped(),
          )
        }

        scoped<CoroutineScope> {
          GlobalScope + Dispatchers.Main.immediate
        } onClose { it?.cancel() }

        scopedOf(::TransactionViewStateFactory)

        scopedWithClose {
          get<SessionManager>().activeSession.value as WalletSession
        }
      }
    }
  }
}