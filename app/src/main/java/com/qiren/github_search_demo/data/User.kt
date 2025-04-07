package com.qiren.github_search_demo.data

data class GitHubUser(
    val login: String, // Username
    val avatar_url: String, // Avatar URL
    val html_url: String, // Profile URL
    val name: String? = null, // Full name
    val bio: String? = null, // User bio/description
    val followers: Int = 0, // Number of followers
    val following: Int = 0 // Number of users they follow
)