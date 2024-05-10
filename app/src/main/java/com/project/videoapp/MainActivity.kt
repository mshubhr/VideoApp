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

        val initialvideos = listOf(
            videos(14, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/12839997-uhd_3840_2160_30fps.mp4?t=2024-05-09T17%3A20%3A51.980Z", "Udaipur", "Fatehsagar lake", "Crowd at sunset", 169, 100, 25),
            videos(15, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/20799636-uhd_3840_2160_30fps.mp4?t=2024-05-10T07%3A08%3A06.045Z", "New Year", "Fireworks", "Celebrating New Year", 200, 20, 30),
            videos(16, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/1860079-uhd_2560_1440_25fps.mp4?t=2024-05-10T07%3A14%3A35.987Z", "New York", "Daily routine", "Traffic on the daily roads", 180, 69, 27),
            videos(17, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/1543760-hd_1920_1080_25fps.mp4?t=2024-05-10T07%3A13%3A18.339Z", "Sports", "Kids boxing", "Boxing practise with coach", 240, 159, 24),
            videos(18, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/2099568-hd_1920_1080_30fps.mp4?t=2024-05-10T07%3A18%3A43.979Z", "Adventure", "Car in dessert", "Safari in white dessert", 300, 210, 21),
            videos(19, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/3044805-uhd_3840_2160_25fps.mp4?t=2024-05-10T07%3A20%3A19.797Z", "People", "Girl & phone", "A girl at shopping center", 380, 30, 30),
            videos(20, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/3129576-uhd_3840_2160_30fps.mp4?t=2024-05-10T07%3A21%3A56.442Z", "Technology", "Cyber security", "Spider technology in cyber security", 420, 258, 35),
            videos(21, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/3134599-hd_1920_1080_24fps.mp4", "Works", "Laptop for music", "Music composer works with laptop", 384, 34, 30),
            videos(22, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/3281050-hd_1920_1080_18fps.mp4", "Aesthetics", "Ice-cream", "Aesthetics view of the ice cream", 410, 32, 30),
            videos(23, "https://axojtjqqnamuwrdhpiob.supabase.co/storage/v1/object/public/videos/855564-hd_1920_1080_24fps.mp4", "Udaipur", "City Palace", "Foreigners are at city palace", 369, 36, 30),
        )

        videos.addAll(initialvideos)

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
