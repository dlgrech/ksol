package com.dgsd.android.solar.common.ui

import android.content.Context
import android.text.TextUtils
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.model.Resource
import com.dgsd.android.solar.model.NativePrograms
import com.dgsd.android.solar.model.TransactionOrSignature
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

  fun createForList(resource: Resource<TransactionOrSignature>): TransactionViewState {
    return when (resource) {
      is Resource.Error -> TransactionViewState.Error(resource.data?.signatureOrThrow())
      is Resource.Loading -> TransactionViewState.Loading(resource.data?.signatureOrThrow())
      is Resource.Success -> createForList(resource.data.transactionOrThrow())
    }
  }

  private fun createForList(transaction: Transaction): TransactionViewState {
    val displayPublicKey = extractDisplayAccount(transaction)
    val displayAccountText = if (displayPublicKey == null) {
      context.getString(R.string.unknown)
    } else {
      publicKeyFormatter.format(displayPublicKey)
    }

    val transactionDirection = getTransactionDirection(transaction)

    val formattedDate = transaction.blockTime?.let { blockTime ->
      DateTimeFormatter.formatRelativeDateAndTime(context, blockTime)
    }
    val dateText = when(transactionDirection) {
      TransactionViewState.Transaction.Direction.INCOMING -> {
        TextUtils.concat(
          context.getString(R.string.received),
          " ",
          formattedDate
        )
      }
      TransactionViewState.Transaction.Direction.OUTGOING -> {
        TextUtils.concat(
          context.getString(R.string.sent),
          " ",
          formattedDate
        )
      }
      TransactionViewState.Transaction.Direction.NONE -> formattedDate
    }

    val formattedAmount = SolTokenFormatter.format(extractAmount(transaction))

    val amountText = when(transactionDirection) {
      TransactionViewState.Transaction.Direction.INCOMING -> "+ $formattedAmount"
      TransactionViewState.Transaction.Direction.OUTGOING -> "- $formattedAmount"
      TransactionViewState.Transaction.Direction.NONE -> formattedAmount
    }

    return TransactionViewState.Transaction(
      transactionSignature = transaction.id,
      direction = transactionDirection,
      displayAccountText = displayAccountText,
      amountText = amountText,
      dateText = dateText
    )
  }

  private fun getTransactionDirection(
    transaction: Transaction
  ): TransactionViewState.Transaction.Direction {
    val balanceDifference = transaction.sessionAccountBalance()?.balanceDifference() ?: 0
    return if (balanceDifference < 0L) {
      TransactionViewState.Transaction.Direction.OUTGOING
    } else if (balanceDifference > 0L) {
      TransactionViewState.Transaction.Direction.INCOMING
    } else {
      TransactionViewState.Transaction.Direction.NONE
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