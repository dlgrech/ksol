package com.dgsd.android.solar.di.util

import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.Session
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

/**
 * Returns an object of type [T] scoped to the currently active [Session]
 */
inline fun <reified T: Any> Scope.getScoped(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val sessionManager = get<SessionManager>()
    val currentSession = sessionManager.activeSession.value

    val scope = getKoin().getOrCreateScope<Session>(currentSession.sessionId)
    return scope.get(qualifier, parameters)
}
