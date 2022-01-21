package com.sumi.contactbook.model.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("responseCode")
    val responseCode: Int?,
    @SerializedName("responseObj")
    val responseObj: ResponseObj?
) {
    data class ResponseObj(
        @SerializedName("token")
        val token: String?,
        @SerializedName("userName")
        val userName: String?
    )
}