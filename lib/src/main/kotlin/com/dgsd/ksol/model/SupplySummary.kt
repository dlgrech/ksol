package com.dgsd.ksol.model

data class SupplySummary(
    val circulating: Lamports,
    val nonCirculating: Lamports,
    val total: Lamports
)