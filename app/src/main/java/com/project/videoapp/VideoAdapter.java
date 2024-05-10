package com.project.videoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    public List<videos> videos;
    public List<videos> filteredVideos;
    private Context context;
    private static OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(videos video);
    }

    public VideoAdapter(Context context, List<videos> videos, OnItemClickListener listener) {
        this.context = context;
        this.videos = videos;
        this.filteredVideos = new ArrayList<>(videos);
        this.listener = listener;
    }

    public void filter(String query) {
        filteredVideos.clear();
        if (TextUtils.isEmpty(query)) {
            filteredVideos.addAll(videos);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (videos video : videos) {
                if (video.getTitle().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredVideos.add(video);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, likesTextView, viewsTextView, commentsTextView, channelTextView;
        ImageView thumbnailIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.viewt);
            likesTextView = itemView.findViewById(R.id.textlikes);
            viewsTextView = itemView.findViewById(R.id.viewsc);
            commentsTextView = itemView.findViewById(R.id.textcomments);
            channelTextView = itemView.findViewById(R.id.viewch);
            thumbnailIV = itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(videos.get(position));
                    }

                }
            });
        }

        public void bind(final videos video, final OnItemClickListener listener) {
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(video.getVideourl(), MediaStore.Images.Thumbnails.MINI_KIND);
            if (thumbnail != null) {
                thumbnailIV.setImageBitmap(thumbnail);
            } else {
                thumbnailIV.setImageResource(R.drawable.thumbnail);
            }
            titleTextView.setText(video.getTitle());
            likesTextView.setText("Likes : "+String.valueOf(video.getLikes()));
            viewsTextView.setText("Views : "+String.valueOf(video.getViews()));
            commentsTextView.setText("Comments : "+String.valueOf(video.getComments()));
            channelTextView.setText(video.getChannelname());
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(videos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


}
