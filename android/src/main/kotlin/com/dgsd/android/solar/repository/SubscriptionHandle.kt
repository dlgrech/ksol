package com.dgsd.android.solar.repository

import kotlinx.coroutines.flow.Flow

interface SubscriptionHandle<T> {

  fun observe(): Flow<T>

  fun stop()
}