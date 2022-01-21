package com.sumi.contactbook.rest

import com.sumi.contactbook.model.login.LoginResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("Login")
    suspend fun loginUser(@Header ("Content-Type") contentType: String, @Body body: RequestBody): LoginResponse
}