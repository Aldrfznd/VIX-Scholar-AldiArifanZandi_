package com.example.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.news.R
import com.example.news.db.ArticleDatabase
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {
lateinit var viewModel: NewsViewModel
override fun onCreate(savedInstanceState: Bundle?){
    super.onCreate(savedInstanceState)
    setContentView(R.id.layout.activity_news)

    val newsrepository = NewsRepository(ArticleDatabase(this))

    val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsrepository)
    viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

    bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}