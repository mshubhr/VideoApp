package com.project.videoapp

import android.content.Context
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

public class VideoAdapter(
    var context: Context,
    var videos: List<videos>,
    var listener: OnItemClickListener
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    var filteredVideos = ArrayList(videos)

    interface OnItemClickListener {
        fun onItemClick(video: videos)
    }

    fun filter(query: String) {
        filteredVideos.clear()
        if (TextUtils.isEmpty(query)) {
            filteredVideos.addAll(videos)
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            for (video in videos) {
                if (video.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredVideos.add(video)
                }
            }
        }
        notifyDataSetChanged()
    }

    @NonNull
    @Override
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_video, parent, false)
        return ViewHolder(view)
    }

    @Override
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.bind(filteredVideos[position], listener)
    }

    @Override
    override fun getItemCount(): Int = filteredVideos.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.findViewById(R.id.viewt)
        var deTextView: TextView = itemView.findViewById(R.id.viewd)
        var likesTextView: TextView = itemView.findViewById(R.id.textlikes)
        var channelTextView: TextView = itemView.findViewById(R.id.viewch)
        var thumbnailIV: ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            itemView.setOnClickListener {
                var position = adapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(filteredVideos[position])
                }
            }
        }

        fun bind(video: videos, listener: OnItemClickListener) {
            val thumbnail = ThumbnailUtils.createVideoThumbnail(
                video.videourl,
                MediaStore.Images.Thumbnails.MINI_KIND
            )
            if (thumbnail != null) {
                thumbnailIV.setImageBitmap(thumbnail)
            } else {
                thumbnailIV.setImageResource(R.drawable.thumbnail)
            }
            titleTextView.text = video.title
            likesTextView.text = "likes : ${video.likes}"
            channelTextView.text = video.channelname
            deTextView.text = video.description
        }
    }
}