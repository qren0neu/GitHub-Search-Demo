package com.qiren.github_search_demo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiren.github_search_demo.data.GitHubUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MockGitHubViewModel : GithubSearchViewModel() {

    private val mockUsers = listOf(
        GitHubUser(
            login = "david",
            avatar_url = "https://avatars.githubusercontent.com/u/110271091",
            html_url = "https://github.com/qren0neu",
            name = "David Ren",
            bio = "Full-stack developer with passion for clean code",
            followers = 120,
            following = 75
        )
    )

    override fun searchUser(username: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            // Simulate API delay
            kotlinx.coroutines.delay(800)
            
            val user = mockUsers.find { it.login.equals(username, ignoreCase = true) }
            _userProfile.value = user
            _userNotFound.value = user == null
            _isLoading.value = false
        }
    }
}

open class GithubSearchViewModel : ViewModel() {
    protected val _searchResults = MutableStateFlow<List<GitHubUser>>(emptyList())
    val searchResults: StateFlow<List<GitHubUser>> = _searchResults
    
    // For individual user profile display
    protected val _userProfile = MutableStateFlow<GitHubUser?>(null)
    val userProfile: StateFlow<GitHubUser?> = _userProfile
    
    // To track when a user isn't found
    protected val _userNotFound = MutableStateFlow(false)
    val userNotFound: StateFlow<Boolean> = _userNotFound
    
    // Loading state
    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // For search list (original function)
    open fun searchUsers(query: String) {
        viewModelScope.launch {
            // In a real app, you would call a repository or API service here
            _userNotFound.value = false
            _userProfile.value = null
            
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            }
        }
    }
    
    // For specific user profile search
    open fun searchUser(username: String) {
        _isLoading.value = true
        
        viewModelScope.launch {
            // In a real implementation, this would call an API service
            // to fetch the specific user's details
            _userProfile.value = null
            _userNotFound.value = true
            _isLoading.value = false
        }
    }
    
    fun clearUserProfile() {
        _userProfile.value = null
        _userNotFound.value = false
    }
}