package com.codemountain.audioplay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.interfaces.OnItemClickListener;
import com.codemountain.audioplay.model.Artist;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragmentAdapter extends RecyclerView.Adapter<ArtistFragmentAdapter.ArtistViewHolder> {

    Context context;
    private List<Artist> artistList;
    private OnItemClickListener listener;
    Helper helper;
    private int lastPos = -1;

    public ArtistFragmentAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
        helper = new Helper(context);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_artist_layout, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artists = getItem(position);

        holder.singleArtistSongCount.setText(artists.getTrackCount() + " Song(s)");
        holder.singleArtistName.setText(artists.getName());
        Glide.with(context).load(ArtWork.albumArtUri(artists.getId()))
                .placeholder(R.drawable.default_song_album_art)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.singleArtistImage);


        //Animate recyclerview
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPos) ? R.anim.down_from_left_side : R.anim.up_from_right_side);
        holder.itemView.startAnimation(animation);
        lastPos = position;

    }

    @Override
    public int getItemCount() {
        return null != artistList ? artistList.size() : 0;
    }

    public Artist getItem(int position) {
        if (artistList == null || artistList.size() < 0 || artistList.size() == 0) {
            return null;
        }
        if (position < artistList.size() && position >= 0) {
            return artistList.get(position);
        } else {
            return null;
        }

    }

    class ArtistViewHolder extends RecyclerView.ViewHolder{

        private ImageView singleArtistImage;
        private TextView singleArtistName, singleArtistSongCount;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            singleArtistImage = itemView.findViewById(R.id.singleArtistImage);
            singleArtistName = itemView.findViewById(R.id.singleArtistName);
            singleArtistSongCount = itemView.findViewById(R.id.singleArtistSongCount);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setFilter(List<Artist> listArtist) {
        artistList = new ArrayList<>();
        artistList.addAll(listArtist);
        notifyDataSetChanged();
    }

}
