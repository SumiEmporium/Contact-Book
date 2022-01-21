package com.sumi.contactbook.rest

import com.google.gson.Gson
import com.sumi.contactbook.model.login.LoginModel
import com.sumi.contactbook.model.login.LoginResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class RestRepository(private val apiService: ApiService) {

    suspend fun loginUser(
        userName: String,
        password: String
    ): LoginResponse {
        val registerModel = LoginModel(userName, password)
        val gson = Gson()
        val params = gson.toJson(registerModel)

        val JSON = "text/plain".toMediaType()
        val body = JSONObject(params).toString().toRequestBody(JSON)
        val result = apiService.loginUser("application/json", body)
        return result
    }
}