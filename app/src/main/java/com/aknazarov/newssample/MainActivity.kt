package com.aknazarov.newssample

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.prof.rssparser.Parser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: FeedAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val parser = Parser.Builder()
            .context(this)
            .build()

        val llManager = LinearLayoutManager(this)
        feed_recyclerview.layoutManager = llManager

        swipe_refresh.setOnRefreshListener {
            viewModel.fetchFeed(parser) { swipe_refresh.isRefreshing = false }
        }

        viewModel.rssChannel.observe(this, Observer { channel ->
            if (channel != null) {
                if (channel.title != null) {
                    title = channel.title
                }
                adapter = FeedAdapter(channel.articles)
                feed_recyclerview.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.fetchFeed(parser) { setIsLoading(false) }
        setIsLoading(true)
    }

    private fun setIsLoading(isLoading: Boolean) {
        content.visibility = if (isLoading) View.GONE else View.VISIBLE
        progress_bar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}