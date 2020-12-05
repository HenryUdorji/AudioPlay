package com.codemountain.audioplay.adapters;/*
package com.codemountain.audioplay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.interfaces.OnItemClickListener;
import com.codemountain.audioplay.model.Folder;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class FolderFragmentAdapter extends RecyclerView.Adapter<FolderFragmentAdapter.SongsViewHolder> {

    Context context;
    private List<Folder> folderList;
    private List<Song> songList = new ArrayList<>();
    private OnItemClickListener listener;

    public FolderFragmentAdapter(Context context, List<Folder> folderList) {
        this.context = context;
        this.folderList = folderList;
    }

    @NonNull
    @Override
    public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_layout, parent, false);
        return new SongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
        Folder folder = getItem(position);

        songList = folder.getSongList();
        if (songList.size() > 0) {
            String currentPath = folder.getFile().getAbsolutePath();
            int index = Helper.getIndex(currentPath, songList);
            if (index > -1) {
                Song song = songList.get(index);
                holder.folderNameText.setText(song.getTitle());
                //holder.extraParam.setText(song.getArtist());
            }}

        holder.folderNameText.setText(folder.getFile().getName());
        //holder.folderPathText.setText(Helper.calculateDuration(folder.getDuration()));
        holder.totalSongText.setText(folder.getFileCount() + "Song(s)");
    }

    public List<Folder> getFolderList() {
        return folderList;
    }

    public Folder getItem(int position) {
        if (folderList == null || folderList.size() < 0 || folderList.size() == 0) {
            return null;
        }
        if (position < folderList.size() && position >= 0) {
            return folderList.get(position);
        } else {
            return null;
        }
    }

    public List<Song> getSongList() {
        return songList;
    }


    @Override
    public int getItemCount() {
        return null != folderList ? folderList.size() : 0;
    }

    class SongsViewHolder extends RecyclerView.ViewHolder{

        private TextView folderNameText, folderPathText, totalSongText;

        public SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            folderNameText = itemView.findViewById(R.id.nameText);
            folderPathText = itemView.findViewById(R.id.durationText);
            totalSongText = itemView.findViewById(R.id.artistNameText);

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

    public void setFilter(List<Folder> folderList) {
        this.folderList = new ArrayList<>();
        this.folderList.addAll(folderList);
        notifyDataSetChanged();
    }

}
*/
