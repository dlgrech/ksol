package com.dgsd.android.solar.common.ui

import android.content.Context
import com.dgsd.android.solar.R
import com.dgsd.android.solar.model.NativePrograms
import com.dgsd.android.solar.model.TransactionViewState
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.model.*
import com.dgsd.ksol.programs.system.SystemProgram
import com.dgsd.ksol.programs.system.SystemProgramInstruction
import com.dgsd.ksol.programs.system.SystemProgramInstructionData

class TransactionViewStateFactory(
  private val context: Context,
  private val publicKeyFormatter: PublicKeyFormatter,
  private val session: WalletSession,
) {

  fun createForList(transaction: Transaction): TransactionViewState {
    val displayPublicKey = extractDisplayAccount(transaction)
    val displayAccountText = if (displayPublicKey == null) {
      context.getString(R.string.unknown)
    } else {
      publicKeyFormatter.format(displayPublicKey)
    }

    val dateText = transaction.blockTime?.let { blockTime ->
      DateTimeFormatter.formatRelativeDateAndTime(context, blockTime)
    }

    val amount = extractAmount(transaction)

    val formattedAmount = SolTokenFormatter.format(amount)
    val transactionDirection = getTransactionDirection(transaction)

    val amountText = when(transactionDirection) {
      TransactionViewState.Direction.INCOMING -> "+ $formattedAmount"
      TransactionViewState.Direction.OUTGOING -> "- $formattedAmount"
      TransactionViewState.Direction.NONE -> formattedAmount
    }

    return TransactionViewState(
      transactionSignature = transaction.id,
      direction = transactionDirection,
      displayAccountText = displayAccountText,
      amountText = amountText,
      dateText = dateText
    )
  }

  private fun getTransactionDirection(
    transaction: Transaction
  ): TransactionViewState.Direction {
    val balanceDifference = transaction.sessionAccountBalance()?.balanceDifference() ?: 0
    return if (balanceDifference < 0L) {
      TransactionViewState.Direction.OUTGOING
    } else if (balanceDifference > 0L) {
      TransactionViewState.Direction.INCOMING
    } else {
      TransactionViewState.Direction.NONE
    }
  }

  private fun extractAmount(transaction: Transaction): Lamports {
    return if (transaction.isSystemProgramTransfer()) {
      checkNotNull(transaction.getSystemProgramInstruction()?.lamports)
    } else {
      transaction.sessionAccountBalance()?.balanceDifference() ?: 0
    }
  }

  private fun extractDisplayAccount(transaction: Transaction): PublicKey? {
    return extractDisplayAccount(transaction.message.accountKeys)
  }

  private fun extractDisplayAccount(accounts: List<TransactionAccountMetadata>): PublicKey? {
    val otherAccounts = accounts.filterNot { it.publicKey == session.publicKey }
    return when (otherAccounts.size) {
      0 -> null
      1 -> otherAccounts.single().publicKey
      else -> {
        extractDisplayAccount(accounts.filterNot { NativePrograms.isNativeProgram(it.publicKey) })
      }
    }
  }

  private fun Transaction.getSystemProgramInstruction(): SystemProgramInstructionData? {
    return runCatching {
      val singleInstruction = message.instructions.singleOrNull()
      if (singleInstruction?.programAccount != SystemProgram.PROGRAM_ID) {
        null
      } else {
        SystemProgram.decodeInstruction(singleInstruction.inputData)
      }
    }.getOrNull()
  }

  private fun Transaction.isSystemProgramTransfer(): Boolean {
    return when (getSystemProgramInstruction()?.instruction) {
      SystemProgramInstruction.TRANSFER,
      SystemProgramInstruction.TRANSFER_WITH_SEED -> true
      else -> false
    }
  }

  private fun Transaction.sessionAccountBalance(): TransactionMetadata.Balance? {
    return metadata.accountBalances.firstOrNull { it.accountKey == session.publicKey }
  }

  private fun TransactionMetadata.Balance.balanceDifference(): Lamports {
    return balanceAfter - balanceBefore
  }
}