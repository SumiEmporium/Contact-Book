package com.sumi.contactbook.rest

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val CONNECTION_TIME_OUT = 20L

class RetrofitBuilder {
    companion object{
        private val mClient: OkHttpClient
            get() {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY

                val httpBuilder = OkHttpClient.Builder()
                httpBuilder
                    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(interceptor)

                return httpBuilder.build()

            }

        private fun getRetrofitInstance(url: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().setLenient().create()
                    )
                )
                .client(mClient)
                .build()
        }


        suspend fun getApiService(url: String): ApiService {
            return withContext(Dispatchers.IO) {
                getRetrofitInstance(url).create(ApiService::class.java)
            }

        }
    }

}