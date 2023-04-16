package com.alexplas.weathernew.data.api

import com.alexplas.weathernew.utils.Constant.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class QueryParameterAddInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()

        val request = chain.request().newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}