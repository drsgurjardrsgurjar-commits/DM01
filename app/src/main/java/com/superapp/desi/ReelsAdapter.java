package com.superapp.desi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.ReelsViewHolder> {

    private Context context;
    private List<String> videoIds;

    public ReelsAdapter(Context context, List<String> videoIds) {
        this.context = context;
        this.videoIds = videoIds;
    }

    @NonNull
    @Override
    public ReelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reel, parent, false);
        return new ReelsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelsViewHolder holder, int position) {
        String videoId = videoIds.get(position);
        holder.playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoIds.size();
    }

    static class ReelsViewHolder extends RecyclerView.ViewHolder {
        YouTubePlayerView playerView;

        public ReelsViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.youtube_player_view);
        }
    }
          }

