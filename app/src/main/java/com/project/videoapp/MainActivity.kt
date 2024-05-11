@file:OptIn(DelicateCoroutinesApi::class)

package com.project.videoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val supabaseClient = createSupabaseClient(
    supabaseUrl = "https://axojtjqqnamuwrdhpiob.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF4b2p0anFxbmFtdXdyZGhwaW9iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUyMzYzMzIsImV4cCI6MjAzMDgxMjMzMn0.Uhwm8JV2xdjJ-aJwRbOlifkuj0MBvffnKgawgI_5MvI"
) {
    install(Auth)
    install(Postgrest)
}

class MainActivity : AppCompatActivity(), VideoAdapter.OnItemClickListener {

    private val videos = mutableListOf<videos>()
    private lateinit var adapter: VideoAdapter
    private lateinit var searchView: SearchView
    private val filteredVideos = mutableListOf<videos>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainRV = findViewById<RecyclerView>(R.id.recycler)
        searchView = findViewById(R.id.search)

        adapter = VideoAdapter(this, videos, this)
        mainRV.layoutManager = LinearLayoutManager(this)
        mainRV.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                Log.e("SearchView", "Query submitted: $query")
                filterVideos(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("SearchView", "Query changed: $newText")
                newText?.let {
                    filterVideos(it)
                }
                return true
            }
        })
        adapter.notifyDataSetChanged()
        fetchVideosFromSupabase()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterVideos(query: String) {
        filteredVideos.clear()
        if (query.isEmpty()) {
            Log.e("MainActivity", "Query is empty")
            filteredVideos.addAll(videos)
        } else {
            val lowerCaseQuery = query.trim().lowercase()
            videos.forEach { video ->
                Log.e("MainActivity", "Video title: ${video.title}")
                if (video.title.lowercase().contains(lowerCaseQuery)) {
                    filteredVideos.add(video)
                }
            }
            Log.e("MainActivity", lowerCaseQuery)
        }
        adapter.videos = filteredVideos
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(video: videos) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("videoUrl", video.videourl)
        intent.putExtra("thumbnails", video.thumbnails)
        intent.putExtra("title", video.title)
        intent.putExtra("channel", video.channelname)
        intent.putExtra("comments", video.comments)
        intent.putExtra("views", video.views)
        intent.putExtra("likes", video.likes)
        intent.putExtra("description", video.description)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchVideosFromSupabase() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = supabaseClient.from("videos").select()
                val data = response.decodeList<videos>()
                videos.addAll(data)
                Log.e("MainActivity", "Fetched ${data.size} videos")
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching videos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("MainActivity", "Error fetching videos: ${e.message}", e)
            }
        }
    }
}
