package com.casecode.data.remote

import com.casecode.domain.model.PointOfSaleResponse
import retrofit2.http.GET

interface ApiService {
    @GET("test")
    suspend fun getCustomers(): PointOfSaleResponse
}