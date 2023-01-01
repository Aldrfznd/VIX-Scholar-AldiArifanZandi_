package com.example.news.repository

import com.example.news.db.ArticleDatabase
import com.example.news.fragments.api.RetrotifInstance
import com.example.news.models.Article
import retrofit2.http.Query

class NewRepository(val db: ArticleDatabase) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrotifInstance.api.searchForNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrotifInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article:Article) = db.getArticleDao().upsert(article)

    fun getSavedNews()= db.getArticleDao().getAllArticle()

    suspend fun deleteArticle(article: Article)= db.getArticleDao().deteleArticle(article)
}