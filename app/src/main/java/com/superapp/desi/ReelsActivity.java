package com.superapp.desi;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {

    static class ReelModel {
        String url;
        String title;
        String desc;
        ReelModel(String url, String title, String desc) {
            this.url = url;
            this.title = title;
            this.desc = desc;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reels);

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        ViewPager2 viewPager = findViewById(R.id.viewPagerReels);

        List<ReelModel> reelList = new ArrayList<>();
        // वर्किंग टेस्ट वीडियो लिंक्स (MP4)
        reelList.add(new ReelModel(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "Desi Swag Reel 🔥",
            "#SuperApp #Trending #Reels"
        ));
        reelList.add(new ReelModel(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "Cinema Highlight 🎬",
            "#Action #DesiCinema #Viral"
        ));
        reelList.add(new ReelModel(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
            "Comedy Tadka 😂",
            "#Funny #DesiComedy"
        ));

        viewPager.setAdapter(new RecyclerView.Adapter<ReelViewHolder>() {
            @NonNull
            @Override
            public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reel, parent, false);
                return new ReelViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
                ReelModel item = reelList.get(position);
                holder.title.setText(item.title);
                holder.desc.setText(item.desc);
                holder.progressBar.setVisibility(View.VISIBLE);

                holder.videoView.setVideoURI(Uri.parse(item.url));
                holder.videoView.setOnPreparedListener(mp -> {
                    holder.progressBar.setVisibility(View.GONE);
                    mp.setLooping(true);
                    holder.videoView.start();
                });
            }

            @Override
            public int getItemCount() { return reelList.size(); }
        });
    }

    static class ReelViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView title, desc;
        ProgressBar progressBar;

        ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.tvReelTitle);
            desc = itemView.findViewById(R.id.tvReelDesc);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
                    }
