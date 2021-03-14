package com.kennyc.data.pi_hole.model

data class PiholeSystemStatus(
    val temp: Double,
    val load: FloatArray,
    val memoryUsage: Double,
    val isActive: Boolean
)
