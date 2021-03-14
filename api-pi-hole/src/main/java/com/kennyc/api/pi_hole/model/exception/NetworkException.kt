package com.kennyc.api.pi_hole.model.exception

import okio.IOException

data class NetworkException(val code: Int) : IOException()
