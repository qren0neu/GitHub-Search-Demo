package com.qiren.github_search_demo.repo

import com.qiren.github_search_demo.data.GitHubUser
import com.qiren.github_search_demo.service.GitHubApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubRepository {
    
    suspend fun getUser(username: String): Result<GitHubUser> = withContext(Dispatchers.IO) {
        try {
            val response = GitHubApi.service.getUser(username)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserFollowers(username: String): Result<List<GitHubUser>> = withContext(Dispatchers.IO) {
        try {
            val response = GitHubApi.service.getUserFollowers(username)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch followers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserFollowing(username: String): Result<List<GitHubUser>> = withContext(Dispatchers.IO) {
        try {
            val response = GitHubApi.service.getUserFollowing(username)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch following"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
