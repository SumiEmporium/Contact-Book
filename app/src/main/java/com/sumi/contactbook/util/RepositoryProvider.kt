package com.sumi.contactbook.util

import com.sumi.contactbook.rest.RestRepository
import com.sumi.contactbook.rest.RetrofitBuilder

class RepositoryProvider {
    companion object{
        suspend fun getRepository(): RestRepository{
            return RestRepository(apiService = RetrofitBuilder.getApiService("http://52.76.178.223:5060/api/"))
        }
    }
}