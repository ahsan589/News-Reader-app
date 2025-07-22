package com.example.exercise.data



import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("posts")
    suspend fun getNews(): List<NewsItem>

    @GET("posts/{id}")
    suspend fun getNewsItem(@Path("id") id: Int): NewsItem
}

object ApiClient {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val retrofitService: ApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}