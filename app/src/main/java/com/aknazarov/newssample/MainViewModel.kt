package com.aknazarov.newssample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _rssChannel = MutableLiveData<Channel>()
    val rssChannel: LiveData<Channel>
        get() = _rssChannel


    fun fetchFeed(parser: Parser, onLoaded: (() -> Unit)?) {
        viewModelScope.launch {
            try {
                val channel = parser.getChannel(URL)
                _rssChannel.postValue(channel)
                onLoaded?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                _rssChannel.postValue(Channel(null, null, null, null, null, null, mutableListOf()))
            }
        }
    }

    companion object {
        private const val URL = "https://www.androidauthority.com/feed"
    }
}