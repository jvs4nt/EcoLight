package org.ecolight.api

import org.ecolight.models.Device
import retrofit2.http.GET

interface ApiService {
    @GET("devices")
    suspend fun getDevices(): List<Device>
}