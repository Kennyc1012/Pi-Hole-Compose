package com.kennyc.api.pi_hole.di

import com.kennyc.api.pi_hole.PiholeApi
import com.kennyc.api.pi_hole.interceptor.AuthInterceptor
import com.kennyc.api.pi_hole.interceptor.ErrorInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
object PiholeApiModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesOkHttp(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ErrorInterceptor())
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesApi(okHttpClient: OkHttpClient): PiholeApi =
        Retrofit.Builder()
            .baseUrl("http://pi.hole/admin/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PiholeApi::class.java)

    @Provides
    @JvmStatic
    fun providesLogInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // TODO
            level = when (true) {
                true -> HttpLoggingInterceptor.Level.BODY
                else -> HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @JvmStatic
    fun providesAuthInterceptor(@Named("auth") auth: String): AuthInterceptor =
        AuthInterceptor(auth)
}