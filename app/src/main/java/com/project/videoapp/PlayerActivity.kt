@file:Suppress("DEPRECATION")

package com.project.videoapp

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerActivity : AppCompatActivity() {
    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val videoUrl = intent.getStringExtra("videoUrl")
        val channel = intent.getStringExtra("channel")
        val title = intent.getStringExtra("title")
        intent.getIntExtra("views", 0)
        val description = intent.getStringExtra("description")

        val channelTV = findViewById<TextView>(R.id.channel)
        val titleTV = findViewById<TextView>(R.id.title)
        val descriptionTV = findViewById<TextView>(R.id.description)

        channelTV.text = channel
        titleTV.text = title
        descriptionTV.text = description

        playerView = findViewById(R.id.exo)
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        videoUrl?.let {
            val videoUri = Uri.parse(it)
            val mediaSource = buildMediaSource(videoUri)
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"))
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}