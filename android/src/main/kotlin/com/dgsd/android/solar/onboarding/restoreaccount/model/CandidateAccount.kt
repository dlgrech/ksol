package com.dgsd.android.solar.onboarding.restoreaccount.model

import com.dgsd.ksol.model.KeyPair
import com.dgsd.ksol.model.Lamports

sealed interface CandidateAccount {

  val accountIndex: Int

  data class Empty(
    override val accountIndex: Int,
  ): CandidateAccount

  data class Loading(
    override val accountIndex: Int,
    val keyPair: KeyPair
  ) : CandidateAccount

  data class Error(
    override val accountIndex: Int,
    val keyPair: KeyPair,
    val errorMessage: CharSequence,
  ) : CandidateAccount

  data class AccountWithBalance(
    override val accountIndex: Int,
    val keyPair: KeyPair,
    val lamports: Lamports
  ) : CandidateAccount
}