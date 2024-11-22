package org.ecolight.api

import org.ecolight.models.Device
import org.ecolight.models.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("devices")
    suspend fun getDevices(): List<Device>

    @POST("api/usuarios")
    fun registrarUsuario(@Body usuario: Usuario): Call<Void>
}
