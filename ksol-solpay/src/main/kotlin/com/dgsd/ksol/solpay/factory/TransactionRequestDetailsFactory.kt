package com.dgsd.ksol.solpay.factory

import com.dgsd.ksol.solpay.api.TransactionRequestGetDetailsResponse
import com.dgsd.ksol.solpay.model.SolPayTransactionRequestDetails

internal object TransactionRequestDetailsFactory {

  fun create(response: TransactionRequestGetDetailsResponse): SolPayTransactionRequestDetails {
    val iconUrl = response.iconUrl
    if (iconUrl != null) {
      check(iconUrl.startsWith("http")) {
        "icon was not an absolute http or https url"
      }
    }

    return SolPayTransactionRequestDetails(
      label = response.label,
      iconUrl = iconUrl
    )
  }
}