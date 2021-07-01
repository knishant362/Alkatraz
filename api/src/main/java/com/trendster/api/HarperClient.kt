package com.trendster.api

import com.trendster.api.services.HarperAPI
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HarperClient {

    private val content = "application/json"

    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("Authorization", auth)
                return@Interceptor chain.proceed(builder.build())
            }
        )
    }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val apiService = retrofit.create(HarperAPI::class.java)
}
