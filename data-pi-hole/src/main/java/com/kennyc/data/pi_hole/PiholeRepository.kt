package com.kennyc.data.pi_hole

import com.kennyc.api.pi_hole.PiholeApi
import com.kennyc.api.pi_hole.response.SummaryResponse
import com.kennyc.data.pi_hole.model.PiholeSummary
import com.kennyc.data.pi_hole.model.PiholeSystemStatus
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode

class PiholeRepository(private val api: PiholeApi) {

    suspend fun getSummary(): PiholeSummary = api.getSummary().toData()

    suspend fun getSystemStatus(): PiholeSystemStatus {
        val document = Jsoup.connect("http://pi.hole/admin").get()
        val statusElement = document.getElementById("status")
        var isActive: Boolean = false
        statusElement.childNodes().forEach {
            if (it is TextNode) {
                isActive = STATUS_ACTIVE == it.text().trim()
            }
        }

        val tempElement = document.getElementById("rawtemp")
        var temp: Double = 0.00

        tempElement.childNodes().forEach {
            if (it is TextNode) {
                temp = it.text().toDouble()
            }
        }

        val loadElement = document.select("span:contains(Load:)")
        val loadPercentages = FloatArray(3)

        loadElement.first().childNodes().forEach {
            if (it is TextNode) {
                // The text will originally look like "Load: 05 02 10
                val loads = it.text().substring(7).split(" ")
                require(loads.size == 3)
                loads.forEachIndexed { index, s ->
                    loadPercentages[index] = s.toFloat()
                }
            }
        }

        val memoryElement = document.select("span:contains(Memory)")
        var memory: Double = 0.0

        memoryElement.first().childNodes().forEach {
            if (it is TextNode) {
                // Text will look like Memory usage: 13.8%
                memory = it.text().substring(15).let { text ->
                    // Remove the %
                    text.substring(0, text.length - 2).toDouble()
                }
            }
        }

        return PiholeSystemStatus(temp, loadPercentages, memory, isActive)
    }

    suspend fun enable(): Boolean {
        val response = api.enable()

        // Errors will return an empty JSON array
        return when {
            response.isJsonArray -> false
            response.isJsonObject -> response.asJsonObject.get("status").asString == STATUS_ENABLED
            else -> false
        }
    }

    suspend fun disablePihole(durationInSeconds: Int?): Boolean {
        val response = durationInSeconds?.let { api.disable(it) } ?: api.disableIndefinitely()

        // Errors will return an empty JSON array
        return when {
            response.isJsonArray -> false
            response.isJsonObject -> response.asJsonObject.get("status").asString == STATUS_DISABLED
            else -> false
        }
    }

    private fun SummaryResponse.toData(): PiholeSummary {
        return PiholeSummary(
            blockedDomainsCount,
            dnsQueryCount,
            adsBlockedCount,
            adsPercentageBlocked,
            clientCount,
            status
        )
    }
}

private const val STATUS_ACTIVE = "Active"
private const val STATUS_ENABLED = "enabled"
private const val STATUS_DISABLED = "disabled"