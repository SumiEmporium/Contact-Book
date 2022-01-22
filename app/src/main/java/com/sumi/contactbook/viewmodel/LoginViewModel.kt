package com.sumi.contactbook.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sumi.contactbook.model.login.LoginResponse
import com.sumi.contactbook.rest.Resource
import com.sumi.contactbook.rest.RestRepository
import com.sumi.contactbook.util.singleArgViewModelFactory
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: RestRepository) : ViewModel() {

    var userData: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()

    companion object {
        val FACTORY = singleArgViewModelFactory(::LoginViewModel)
    }

    fun loginUser(userName: String, password: String) {
        viewModelScope.launch {
            userData.postValue(Resource.loading(data = null))
            try {
                val response = repository.loginUser(
                    userName,
                    password
                )

                val status = response.responseCode
                when (status) {
                    200 -> {
                        userData.postValue(Resource.success(response))
                    }
                    else -> {
                        userData.postValue(
                            Resource.error(
                                data = null,
                                message = "Error Occurred!"
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                userData.postValue(
                    Resource.error(
                        data = null,
                        message = e.message ?: "Error Occurred!"
                    )
                )
            }
        }
    }
}