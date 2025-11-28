package com.pikosplash.hydroballs.keopge.data.repo

import android.util.Log
import com.pikosplash.hydroballs.keopge.domain.model.PikoSplashEntity
import com.pikosplash.hydroballs.keopge.domain.model.PikoSplashParam
import com.pikosplash.hydroballs.keopge.presentation.app.PikoSplashApplication.Companion.PIKO_SPLASH_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface PikoSplashApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun pikoSplashGetClient(
        @Body jsonString: JsonObject,
    ): Call<PikoSplashEntity>
}


private const val PIKO_SPLASH_MAIN = "https://pikosplash.com/"
class PikoSplashRepository {

    suspend fun pikoSplashGetClient(
        pikoSplashParam: PikoSplashParam,
        pikoSplashConversion: MutableMap<String, Any>?
    ): PikoSplashEntity? {
        val gson = Gson()
        val api = pikoSplashGetApi(PIKO_SPLASH_MAIN, null)

        val pikoSplashJsonObject = gson.toJsonTree(pikoSplashParam).asJsonObject
        pikoSplashConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            pikoSplashJsonObject.add(key, element)
        }
        return try {
            val pikoSplashRequest: Call<PikoSplashEntity> = api.pikoSplashGetClient(
                jsonString = pikoSplashJsonObject,
            )
            val pikoSplashResult = pikoSplashRequest.awaitResponse()
            Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: Result code: ${pikoSplashResult.code()}")
            if (pikoSplashResult.code() == 200) {
                Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: Get request success")
                Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: Code = ${pikoSplashResult.code()}")
                Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: ${pikoSplashResult.body()}")
                pikoSplashResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(PIKO_SPLASH_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun pikoSplashGetApi(url: String, client: OkHttpClient?) : PikoSplashApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
