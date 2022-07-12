package com.dgsd.android.solar.di

import com.dgsd.android.solar.cluster.manager.ClusterManager
import com.dgsd.android.solar.di.util.getScoped
import com.dgsd.android.solar.repository.SolanaApiRepository
import com.dgsd.android.solar.repository.SolanaApiRepositoryImpl
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.Session
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.SolanaApi
import org.koin.core.module.Module
import org.koin.dsl.module

internal object SessionScopedModule {

    fun create(): Module {
        return module {
            scope<Session> {
                scoped<SolanaApiRepository> {
                    val solanaApi = SolanaApi(
                        cluster = get<ClusterManager>().activeCluster.value,
                        okHttpClient = get()
                    )

                    SolanaApiRepositoryImpl(
                        session = getScoped(),
                        solanaApi = solanaApi,
                    )
                }

                scoped {
                    get<SessionManager>().activeSession.value as WalletSession
                }
            }
        }
    }
}