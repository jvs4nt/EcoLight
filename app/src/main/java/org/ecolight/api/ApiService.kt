package org.ecolight.api

import org.ecolight.models.Device
import org.ecolight.models.Meta
import org.ecolight.models.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("devices")
    suspend fun getDevices(): List<Device>

    @POST("api/usuarios")
    fun registrarUsuario(@Body usuario: Usuario): Call<Void>

    @GET("api/metas")
    fun getUserMeta(@Query("usuarioEmail") email: String): Call<Meta>

    @POST("api/metas")
    fun registrarMeta(@Body meta: Meta): Call<Void>
}
