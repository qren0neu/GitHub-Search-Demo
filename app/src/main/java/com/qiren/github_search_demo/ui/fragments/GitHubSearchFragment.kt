package com.qiren.github_search_demo.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.qiren.github_search_demo.ui.components.SearchBar
import com.qiren.github_search_demo.ui.components.UserListEmptyView
import com.qiren.github_search_demo.ui.components.UserNotFoundView
import com.qiren.github_search_demo.ui.components.UserProfileView
import com.qiren.github_search_demo.viewmodels.GithubSearchViewModel

class GithubSearchFragment : Fragment() {

    private val viewModel: GithubSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    GithubUserProfileScreen(viewModel, this@GithubSearchFragment)
                }
            }
        }
    }
}

@Preview
@Composable
fun GithubUserProfileScreenPreview() {
//    val viewModel = MockGitHubViewModel()
    val viewModel = GithubSearchViewModel()
    MaterialTheme {
        GithubUserProfileScreen(viewModel)
    }
}

@Composable
fun GithubUserProfileScreen(
    viewModel: GithubSearchViewModel,
    fragment: Fragment? = null
) {
    val searchQuery = viewModel.searchQuery
    val userProfile by viewModel.userProfile.collectAsState()
    val userNotFound by viewModel.userNotFound.collectAsState()
    val userListEmpty by viewModel.userListEmpty.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val following by viewModel.following.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val focusManager = LocalFocusManager.current
    val canGoBack by viewModel.canGoBack.collectAsState()

    fun openUrl(url: String) {
        if (null != fragment) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            fragment.startActivity(intent)
        }
    }

    fun clickFollowers(username: String) {
        viewModel.clearUserProfile()
        searchQuery.value = "#follower@${username}"
        viewModel.searchUser("#follower@${username}")
    }

    fun clickFollowing(username: String) {
        viewModel.clearUserProfile()
        searchQuery.value = "#following@${username}"
        viewModel.searchUser("#following@${username}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            searchQuery = searchQuery.value,
            onQueryChanged = { searchQuery.value = it },
            onSearch = {
                focusManager.clearFocus()
                viewModel.searchUser(searchQuery.value)
            },
            modifier = Modifier.fillMaxWidth(),
            canGoBack = canGoBack,
            popHistory = viewModel::popSearchHistory
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                userNotFound -> {
                    UserNotFoundView(
                        username = searchQuery.value,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                userListEmpty -> {
                    UserListEmptyView(modifier = Modifier.align(Alignment.Center))
                }

                userProfile != null -> {
                    UserProfileView(
                        user = userProfile!!,
                        modifier = Modifier.fillMaxWidth(),
                        onFollowersClick = { clickFollowers(userProfile!!.login) },
                        onFollowingClick = { clickFollowing(userProfile!!.login) },
                        onOpenUrl = { openUrl(it) }
                    )
                }

                following.isNotEmpty() || followers.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (following.isNotEmpty()) {
                            items(following) { user ->
                                UserProfileView(
                                    user = user,
                                    modifier = Modifier.fillMaxWidth(),
                                    onFollowersClick = { clickFollowers(user.login) },
                                    onFollowingClick = { clickFollowing(user.login) },
                                    onOpenUrl = { openUrl(it) }
                                )
                            }
                        }
                        if (followers.isNotEmpty()) {
                            items(followers) { user ->
                                UserProfileView(
                                    user = user,
                                    modifier = Modifier.fillMaxWidth(),
                                    onFollowersClick = { clickFollowers(user.login) },
                                    onFollowingClick = { clickFollowing(user.login) },
                                    onOpenUrl = { openUrl(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
