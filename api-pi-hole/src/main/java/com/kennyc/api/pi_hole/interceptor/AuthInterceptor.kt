package com.kennyc.api.pi_hole.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val auth: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        return if (url.contains("disable") || url.contains("enable")) {
            val httpUrl = request.url.newBuilder().addQueryParameter("auth", auth).build()
            request.newBuilder().url(httpUrl).build().let { chain.proceed(it) }
        } else {
            chain.proceed(request)
        }
    }
}