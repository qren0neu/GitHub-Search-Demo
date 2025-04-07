package com.qiren.github_search_demo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.qiren.github_search_demo.ui.fragments.GithubSearchFragment

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GithubSearchFragment())
                .commit()
        }
    }
}