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
import com.codemountain.audioplay.model.Album;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragmentAdapter extends RecyclerView.Adapter<AlbumFragmentAdapter.AlbumsViewHolder> {

    Context context;
    private List<Album> albumList;
    private OnItemClickListener listener;
    Helper helper;
    private int lastPos = -1;

    public AlbumFragmentAdapter(Context context, List<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
        helper = new Helper(context);
    }

    @NonNull
    @Override
    public AlbumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_album_layout, parent, false);
        return new AlbumsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsViewHolder holder, int position) {
        Album albums = getItem(position);

        holder.singleAlbumName.setText(albums.getAlbumName());
        holder.singleAlbumArtistName.setText(albums.getArtistName());
        Glide.with(context).load(ArtWork.albumArtUri(albums.getId()))
                .placeholder(R.drawable.default_song_album_art)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.singleAlbumImage);

        //Animate recyclerview
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPos) ? R.anim.down_from_left_side : R.anim.up_from_right_side);
        holder.itemView.startAnimation(animation);
        lastPos = position;
    }

    @Override
    public int getItemCount() {
        return null != albumList ? albumList.size() : 0;
    }


    public Album getItem(int position) {
        if (albumList == null || albumList.size() < 0 || albumList.size() == 0) {
            return null;
        }
        if (position < albumList.size() && position >= 0) {
            return albumList.get(position);
        } else {
            return null;
        }

    }

    class AlbumsViewHolder extends RecyclerView.ViewHolder{

        private ImageView singleAlbumImage;
        private TextView singleAlbumArtistName, singleAlbumName;

        public AlbumsViewHolder(@NonNull View itemView) {
            super(itemView);
            singleAlbumImage = itemView.findViewById(R.id.singleAlbumImage);
            singleAlbumArtistName = itemView.findViewById(R.id.singleAlbumArtistName);
            singleAlbumName = itemView.findViewById(R.id.singleAlbumName);

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

    public void setFilter(List<Album> listAlbums) {
        albumList = new ArrayList<>();
        albumList.addAll(listAlbums);
        notifyDataSetChanged();
    }

}
