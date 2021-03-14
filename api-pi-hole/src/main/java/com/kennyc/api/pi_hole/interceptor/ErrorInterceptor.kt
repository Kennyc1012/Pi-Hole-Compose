package com.kennyc.api.pi_hole.interceptor
import com.kennyc.api.pi_hole.model.exception.NetworkException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response = chain.proceed(request)

        if (response.code != HttpURLConnection.HTTP_OK) {
            throw NetworkException(response.code)
        }

        return response
    }
}