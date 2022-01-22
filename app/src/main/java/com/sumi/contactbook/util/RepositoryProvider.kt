package com.sumi.contactbook.util

import com.sumi.contactbook.rest.RestRepository
import com.sumi.contactbook.rest.RetrofitBuilder

class RepositoryProvider {
    companion object{
        suspend fun getRepository(): RestRepository{
            return RestRepository(apiService = RetrofitBuilder.getApiService(BASE_URL))
        }
    }
}