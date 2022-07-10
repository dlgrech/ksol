package com.dgsd.android.solar.di

import com.dgsd.android.solar.cluster.manager.ClusterManager
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
                factory {
                    SolanaApi(
                        cluster = get<ClusterManager>().activeCluster.value,
                        okHttpClient = get()
                    )
                }

                scoped {
                    get<SessionManager>().activeSession.value as WalletSession
                }
            }
        }
    }
}