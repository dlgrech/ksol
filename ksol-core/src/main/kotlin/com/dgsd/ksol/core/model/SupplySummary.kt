package com.dgsd.ksol.core.model

data class SupplySummary(
    val circulating: Lamports,
    val nonCirculating: Lamports,
    val total: Lamports
)