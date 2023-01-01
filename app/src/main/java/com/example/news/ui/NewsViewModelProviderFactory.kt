package com.example.news.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.news.repository.NewRepository

class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
): ViewModelProvider.Factory{
        override fun <T :ViewModel?> create(modelClass: Class<T>): T{
            return NewsViewModel(app, newsRepository) as T
    }
}