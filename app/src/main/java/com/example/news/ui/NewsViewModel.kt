package com.example.news.ui

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.MutableLiveData
import androidx.room.Query
import com.example.news.models.Article
import com.example.news.models.NewsResponse
import com.example.news.repository.NewRepository
import com.example.news.util.Resources
import retrofit2.Response
import java.io.IOException
import javax.xml.transform.Source

class NewsViewModel {
    app: Application,
    val newRepository: NewRepository
    ): AndroidViewModel(app){

        val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
        var breakingNewsPage = 1
        var breakingNewsResponse: NewsResponse? = null

        val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
        var searchNewsPage =1
        var searchNewsResponse: NewsResponse?= null

        init{
            getBreakingNews("us")
        }

        fun getBreakingNews(countryCode: String)= viewModelScope.launch{
            safeBreakingNewsCall(countryCode)
        }

        fun searchNews(searchQuery: String) = viewModelScope.launch{
            safeSearchNewsCall(searchQuery)
        }

        private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
            if(response.isSuccessful
                response.body()?.let { resultResponse ->
                    breakingNewsPage++
                    if (breakingNewsResponse == null) {
                        breakingNewsResponse == resultResponse
                    } else {
                        val oldArticle = breakingNewsResponse?.articles
                        val newArticle = resultResponse.articles
                        oldArticle?.addAll(newArticle)
                    }
                }
            return Resource.Error(response.message())
        }

        private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
            if(response.isSuccessful)
                response.body()?.let { resultResponse ->
                    searchNewsPage++
                    if(searchNewsResponse == null) {
                        searchNewsResponse == resultResponse
                    }
                    else{
                        val oldArticle = searchNewsResponse?.articles
                        val newArticle = resultResponse.articles
                        oldArticle?.addAll(newArticle)
                    }
                    return Resource.Success(searchNewsResponse ?: resultResponse)
                }
            }
        return Resource.Error(response.message())
    }

    fun saveArticle(article:Article) = viewModelScope.launch{
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch{
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeSearchNewsCall(searchQuerry: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuerry, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }
            catch(t: Throwable){
                when(t){
                    is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                    else -> searchNews.postValue(Resource.Error("COnversion Error"))
                }
            }
        }

        private suspend fun saveBreakingNewsCall(countryCode: String){
            breakingNews.postValue(Resource.Loading())
            try{
                if(hasInternetConnection()){
                    val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                    breakingNews.postValue(Resource.Error("No Internet Connection"))
                }
            }
            catch(t: Throwable){
                when(t){
                    is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                    else -> searchNews.postValue(Resource.Error("COnversion Error"))
                }
            }
    }
    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsAplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE ->true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}