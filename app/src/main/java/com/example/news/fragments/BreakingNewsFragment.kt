package com.example.news.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.news.R
import com.example.news.adapters.NewsAdapters
import com.example.news.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.news.util.Resources
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import java.util.Observer

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapters

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_ArticleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { respons ->
            when (respons) {
                is Resources.Success -> {
                    hideProgressBar()
                    respons.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResult / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if (isLastPage) {
                            rvBreakingNews.setPadding(0, 0, 0, 0,)
                        }
                    }
                }
                is Resources.Error -> {
                    hideProgressBar()
                    respons.message?.let { message ->
                        Toast.makeText(activity, "An Error Accured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resources.Loading ->{
                    showingProgressBar()
                }
            }
        })
    }
    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = false
    }

    private fun showingProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAsLastItem = firstVisibleItemPosition + visibleItemCount >=totalItemCount
            val isNotBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaganite = isNotLoadingAndNotLastPage && isAsLastItem && isNotBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaganite){
                viewModel.getBreakingNews("us")
                    isScrolling = false
            }
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapters()
        rvBreakingNews.apply {
            adapter = newsAdapter

            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}
