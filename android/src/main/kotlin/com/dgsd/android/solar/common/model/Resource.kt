package com.dgsd.android.solar.common.model

/**
 * Represents a generic Loading/Error/Success state
 */
sealed interface Resource<T> {

  data class Loading<T>(val data: T? = null) : Resource<T>

  data class Error<T>(val error: Throwable, val data: T? = null) : Resource<T>

  data class Success<T>(val data: T) : Resource<T>

  fun dataOrNull(): T? {
    return when (this) {
      is Error -> this.data
      is Loading -> this.data
      is Success -> this.data
    }
  }
}