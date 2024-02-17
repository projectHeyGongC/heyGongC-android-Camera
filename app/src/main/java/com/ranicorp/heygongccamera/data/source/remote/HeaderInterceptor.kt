package com.ranicorp.heygongccamera.data.source.remote

import com.ranicorp.heygongccamera.data.source.local.LocalDataStoreManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val localDataStoreManager: LocalDataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.request().headers["Auth"] == "false") {
            val newRequest = chain.request().newBuilder().removeHeader("Auth").build()
            return chain.proceed(newRequest)
        }

        val token = runBlocking {
            "Bearer " + localDataStoreManager.getAccessToken()
        }
        val newRequest = chain.request().newBuilder().addHeader("Authorization", token).build()
        return chain.proceed(newRequest)
    }
}