package com.codemountain.audioplay.adapters;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.interfaces.OnItemClickListener;
import com.codemountain.audioplay.model.Playlist;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragmentAdapter extends RecyclerView.Adapter<PlaylistFragmentAdapter.PlaylistViewHolder> {

    Context context;
    private List<Playlist> playlistList;
    private OnItemClickListener listener;
    Helper helper;
    Playlist playlist;
    private int lastPos = -1;

    public PlaylistFragmentAdapter(Context context, List<Playlist> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
        helper = new Helper(context);
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_playlist_layout, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        playlist = getItem(position);

        holder.singlePlaylistName.setText(playlist.getName());
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getId());
        int songCount = Helper.getSongCount(context.getContentResolver(), uri);
        holder.singlePlaylistSongCount.setText(songCount + " " + context.getString(R.string.songs));

        //Animate recyclerview
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPos) ? R.anim.down_from_left_side : R.anim.up_from_right_side);
        holder.itemView.startAnimation(animation);
        lastPos = position;

    }

    @Override
    public int getItemCount() {
        return null != playlistList ? playlistList.size() : 0;
    }


    public Playlist getItem(int position) {
        if (playlistList == null || playlistList.size() < 0 || playlistList.size() == 0) {
            return null;
        }
        if (position < playlistList.size() && position >= 0) {
            return playlistList.get(position);
        } else {
            return null;
        }

    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder{

        private ImageView singlePlaylistImage;
        private TextView singlePlaylistSongCount, singlePlaylistName;
        private ImageButton morePlaylistBtn;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            singlePlaylistImage = itemView.findViewById(R.id.singlePlaylistImage);
            singlePlaylistSongCount = itemView.findViewById(R.id.singlePlaylistSongCount);
            singlePlaylistName = itemView.findViewById(R.id.singlePlaylistName);
            morePlaylistBtn = itemView.findViewById(R.id.morePlaylistBtn);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                }
            });

            morePlaylistBtn.setOnClickListener(v ->
                    showMenu(v, playlist));
        }
    }

    private void showMenu(View view, Playlist playlist) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.playlist_frag_pop_up_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_playlist_delete:
                    Helper.deletePlaylistDialog(context, playlist.getName(), () ->
                            ((MainActivity)context).recreate());
                    break;

                case R.id.action_playlist_rename:
                    Helper.showRenameDialog(context, () ->
                            ((MainActivity)context).recreate(), playlist.getId());
                    break;

            }
            return false;
        });
        popup.show();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setFilter(List<Playlist> playlists) {
        playlistList = new ArrayList<>();
        playlistList.addAll(playlists);
        notifyDataSetChanged();
    }

}
