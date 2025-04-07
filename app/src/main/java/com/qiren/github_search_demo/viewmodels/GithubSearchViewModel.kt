package com.qiren.github_search_demo.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiren.github_search_demo.data.GitHubUser
import com.qiren.github_search_demo.repo.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Stack

// Search history item to store in the stack
data class SearchHistoryItem(
    val query: String,
    val isFollowersQuery: Boolean = false,
    val isFollowingQuery: Boolean = false
)

//class MockGitHubViewModel : GithubSearchViewModel() {
//
//    private val mockUsers = listOf(
//        GitHubUser(
//            login = "david",
//            avatar_url = "https://avatars.githubusercontent.com/u/110271091",
//            html_url = "https://github.com/qren0neu",
//            name = "David Ren",
//            bio = "Full-stack developer with passion for clean code",
//            followers = 120,
//            following = 75
//        )
//    )
//
//    override fun searchUser(username: String, saveHistory: Boolean) {
//        // load
//        _isLoading.value = true
//
//        // Add to search history
//        addToSearchHistory(username)
//
//        viewModelScope.launch {
//            // Simulate API delay
//            kotlinx.coroutines.delay(800)
//
//            val user = mockUsers.find { it.login.equals(username, ignoreCase = true) }
//            _userProfile.value = user
//            _userNotFound.value = user == null
//            _isLoading.value = false
//        }
//    }
//}

open class GithubSearchViewModel : ViewModel() {
    private val repository = GitHubRepository()

    // Search history stack
    val searchHistory = Stack<SearchHistoryItem>()
    val searchQuery = mutableStateOf("")
    
    // Current position in search history
    protected val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack

    // For individual user profile display
    protected val _userProfile = MutableStateFlow<GitHubUser?>(null)
    val userProfile: StateFlow<GitHubUser?> = _userProfile
    
    // To track when a user isn't found
    protected val _userNotFound = MutableStateFlow(false)
    val userNotFound: StateFlow<Boolean> = _userNotFound

    protected val _userListEmpty = MutableStateFlow(false)
    val userListEmpty: StateFlow<Boolean> = _userListEmpty
    
    // Loading state
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // For followers display
    private val _followers = MutableStateFlow<List<GitHubUser>>(emptyList())
    val followers: StateFlow<List<GitHubUser>> = _followers
    
    // For following display
    private val _following = MutableStateFlow<List<GitHubUser>>(emptyList())
    val following: StateFlow<List<GitHubUser>> = _following
    
    // For specific user profile search
    open fun searchUser(username: String, saveHistory: Boolean = true) {
        // clean
        clearUserProfile()
        clearFollowersAndFollowing()

        // Check if it's a special format query
        when {
            username.startsWith("#follower@") -> {
                val targetUsername = username.substringAfter("@")
                if (saveHistory) addToSearchHistory(username, isFollowersQuery = true)
                fetchUserFollowers(targetUsername)
                return
            }
            username.startsWith("#following@") -> {
                val targetUsername = username.substringAfter("@")
                if (saveHistory) addToSearchHistory(username, isFollowingQuery = true)
                fetchUserFollowing(targetUsername)
                return
            }
        }
        
        // Regular username search
        _isLoading.value = true
        _userNotFound.value = false
        
        // Add to search history
        if (saveHistory) addToSearchHistory(username)

        viewModelScope.launch {
            repository.getUser(username).fold(
                onSuccess = { user ->
                    _userProfile.value = user
                    _userNotFound.value = false
                    // Clear any previous followers/following data
                    _followers.value = emptyList()
                    _following.value = emptyList()
                },
                onFailure = {
                    _userProfile.value = null
                    _userNotFound.value = true
                }
            )
            _isLoading.value = false
        }
    }
    
    fun fetchUserFollowers(username: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            repository.getUserFollowers(username).fold(
                onSuccess = { followersList ->
                    _followers.value = followersList
                    _userListEmpty.value = followersList.isEmpty()
                },
                onFailure = {
                    _followers.value = emptyList()
                    _userListEmpty.value = true
                }
            )
            _isLoading.value = false
        }
    }
    
    fun fetchUserFollowing(username: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            repository.getUserFollowing(username).fold(
                onSuccess = { followingList ->
                    _following.value = followingList
                    _userListEmpty.value = followingList.isEmpty()
                },
                onFailure = {
                    _following.value = emptyList()
                    _userListEmpty.value = true
                }
            )
            _isLoading.value = false
        }
    }
    
    fun clearUserProfile() {
        _userProfile.value = null
        _userNotFound.value = false
    }
    
    fun clearFollowersAndFollowing() {
        _followers.value = emptyList()
        _following.value = emptyList()
        _userListEmpty.value = false
    }
    
    // Add to search history
    protected fun addToSearchHistory(query: String, isFollowersQuery: Boolean = false, isFollowingQuery: Boolean = false) {
        val historyItem = SearchHistoryItem(query, isFollowersQuery, isFollowingQuery)
        searchHistory.push(historyItem)
        updateCanGoBack()
    }
    
    // Go back to previous search
    fun popSearchHistory() {
        if (searchHistory.size > 1) {
            // Remove current search
            searchHistory.pop()
            // Get previous search
            val previousSearch = searchHistory.pop()
            searchQuery.value = previousSearch.query
            searchUser(previousSearch.query, saveHistory = false)
        }
        
        updateCanGoBack()
    }
    
    // Update the canGoBack state
    private fun updateCanGoBack() {
        _canGoBack.value = searchHistory.size > 1
    }
}