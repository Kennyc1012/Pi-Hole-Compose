package com.kennyc.api.pi_hole.response

import com.google.gson.annotations.SerializedName

data class SummaryResponse(
    @SerializedName("domains_being_blocked")
    val blockedDomainsCount: String,

    @SerializedName("dns_queries_today")
    val dnsQueryCount: String,

    @SerializedName("ads_blocked_today")
    val adsBlockedCount: String,

    @SerializedName("ads_percentage_today")
    val adsPercentageBlocked: Double,

    @SerializedName("unique_clients")
    val clientCount: Int,

    @SerializedName("status")
    val status: String
)
