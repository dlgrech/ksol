package com.dgsd.android.solar.di.util

import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.Session
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Returns an object of type [T] scoped to the currently active [Session]
 */
inline fun <reified T: Any> Koin.getScoped(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val sessionManager = get<SessionManager>()
    val currentSession = sessionManager.activeSession.value

    val scope = getOrCreateScope<Session>(currentSession.sessionId)
    return scope.get(qualifier, parameters)
}
