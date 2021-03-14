package com.kennyc.api.pi_hole

import com.google.gson.JsonElement
import com.kennyc.api.pi_hole.response.SummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PiholeApi {

    @GET("api.php?summary")
    suspend fun getSummary(): SummaryResponse

    @GET("api.php")
    suspend fun disable(@Query("disable") durationInSeconds: Int): JsonElement

    @GET("api.php?disable")
    suspend fun disableIndefinitely(): JsonElement

    @GET("api.php?enable")
    suspend fun enable(): JsonElement
}