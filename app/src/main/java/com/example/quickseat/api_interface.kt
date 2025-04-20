package com.example.quickseat
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
private const val BASE_URL = "https://student-api-hazel.vercel.app"

class api_interface {

     interface ApiService {
        @GET("read_user/{userId}")
        fun authUser(@Path("userId") userId:Int): Call<com.example.quickseat.ApiResponse>
    }

    object RetrofitClient {
        val instance: ApiService
        init{
            val retrofit= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to Kotlin objects
                .build()
             instance= retrofit.create(ApiService::class.java)
        }
    }

}