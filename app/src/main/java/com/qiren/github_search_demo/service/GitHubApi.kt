package com.qiren.github_search_demo.service

import com.qiren.github_search_demo.data.GitHubUser
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

// This token is only for temporary use and will be deleted afterwards
// todo: delete it
val API_CLIENT_SECRET = ""

interface GitHubApiService {
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
        @Header("Authorization") token: String = if (API_CLIENT_SECRET.isNotEmpty()) "Bearer $API_CLIENT_SECRET" else ""
    ): Response<GitHubUser>
    
    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") username: String,
        @Header("Authorization") token: String = if (API_CLIENT_SECRET.isNotEmpty()) "Bearer $API_CLIENT_SECRET" else ""
    ): Response<List<GitHubUser>>
    
    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") username: String,
        @Header("Authorization") token: String = if (API_CLIENT_SECRET.isNotEmpty()) "Bearer $API_CLIENT_SECRET" else ""
    ): Response<List<GitHubUser>>
}

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val service: GitHubApiService = retrofit.create(GitHubApiService::class.java)
}

