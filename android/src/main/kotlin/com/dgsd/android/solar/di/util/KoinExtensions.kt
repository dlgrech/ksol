package com.dgsd.android.solar.di.util

import android.content.ComponentCallbacks
import com.dgsd.android.solar.session.manager.SessionManager
import com.dgsd.android.solar.session.model.Session
import org.koin.android.ext.android.get
import org.koin.core.definition.Definition
import org.koin.core.module.KoinDefinition
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.dsl.onClose
import org.koin.java.KoinJavaComponent.getKoin
import java.io.Closeable

/**
 * Returns an object of type [T] scoped to the currently active [Session]
 */
inline fun <reified T: Any> Scope.getScoped(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return getCurrentSessionScope(get()).get(qualifier, parameters)
}

inline fun <reified T> ScopeDSL.scopedWithClose(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T> {
    return scoped(qualifier, definition) onClose { if (it is Closeable) it.close() }
}

inline fun <reified T : Any> ComponentCallbacks.injectScoped(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    noinline parameters: ParametersDefinition? = null,
) = lazy(mode) {
    getCurrentSessionScope(get()).get<T>(qualifier, parameters)
}

fun getCurrentSessionScope(sessionManager: SessionManager): Scope {
    val currentSession = sessionManager.activeSession.value
    return getKoin().getOrCreateScope<Session>(currentSession.sessionId)
}