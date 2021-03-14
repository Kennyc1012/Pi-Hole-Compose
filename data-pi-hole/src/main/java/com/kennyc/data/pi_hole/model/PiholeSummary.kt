package com.kennyc.data.pi_hole.model

data class PiholeSummary(
    val blockedDomainsCount: String,

    val dnsQueryCount: String,

    val adsBlockedCount: String,

    val adsPercentageBlocked: Double,

    val clientCount:Int,

    val status: String
)
