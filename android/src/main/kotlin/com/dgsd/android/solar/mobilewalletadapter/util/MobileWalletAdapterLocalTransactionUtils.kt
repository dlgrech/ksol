package com.dgsd.android.solar.mobilewalletadapter.util

import android.content.Context
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.PublicKeyFormatter
import com.dgsd.android.solar.common.ui.RichTextFormatter
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.extensions.extractBestDisplayRecipient
import com.dgsd.android.solar.extensions.getMemoMessage
import com.dgsd.android.solar.extensions.getSystemProgramInstruction
import com.dgsd.android.solar.session.model.WalletSession
import com.dgsd.ksol.core.model.LocalTransaction
import com.dgsd.ksol.core.programs.system.SystemProgramInstruction

fun createTransactionSummaryString(
  context: Context,
  session: WalletSession,
  transaction: LocalTransaction,
  publicKeyFormatter: PublicKeyFormatter,
): CharSequence {
  val activeWallet = session.publicKey
  val recipient = transaction.message.extractBestDisplayRecipient(activeWallet)

  val systemProgramInfo = transaction.message.getSystemProgramInstruction()
  if (systemProgramInfo != null) {
    if (systemProgramInfo.instruction == SystemProgramInstruction.TRANSFER ||
      systemProgramInfo.instruction == SystemProgramInstruction.TRANSFER_WITH_SEED
    ) {
      return RichTextFormatter.expandTemplate(
        context,
        R.string.mobile_wallet_adapter_sign_transaction_summary_transfer_template,
        RichTextFormatter.bold(SolTokenFormatter.format(systemProgramInfo.lamports))
      )
    } else {
      return RichTextFormatter.expandTemplate(
        context,
        R.string.mobile_wallet_adapter_sign_transaction_summary_transaction_template,
        RichTextFormatter.bold(SolTokenFormatter.format(systemProgramInfo.lamports))
      )
    }
  }

  val memoMessage = transaction.message.getMemoMessage()
  if (memoMessage != null) {
    return RichTextFormatter.expandTemplate(
      context,
      R.string.mobile_wallet_adapter_sign_transaction_summary_memo_template,
      RichTextFormatter.bold(
        context.getString(R.string.mobile_wallet_adapter_sign_transaction_summary_create_memo)
      ),
      memoMessage
    )
  }

  if (recipient == null) {
    return context.getString(R.string.mobile_wallet_adapter_sign_transaction_unknown)
  } else {
    return RichTextFormatter.expandTemplate(
      context,
      R.string.mobile_wallet_adapter_sign_transaction_to_template,
      publicKeyFormatter.format(recipient)
    )
  }
}