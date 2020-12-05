package com.codemountain.audioplay.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.PlaylistDetailsActivity;
import com.codemountain.audioplay.fragments.PlaylistPickerFragment;
import com.codemountain.audioplay.interfaces.OnItemClickListener;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.ArtWork;
import com.codemountain.audioplay.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class PlaylistDetailsAdapter extends RecyclerView.Adapter<PlaylistDetailsAdapter.PlaylistSongViewHolder> {

    private static final String TAG = "PlaylistSongRecyclerAda";
    private Context context;
    private List<Song> songList;
    private OnItemClickListener listener;

    //BottomSheet widgets
    private ImageView bottomImage;
    private TextView bottomSongTitle, bottomSongArtist;
    private LinearLayout addToPlaylist, delete, playNext, setAsRingTone, shareSong, addToQueue;
    Dialog bottomSheetDialog;
    private int lastPos = -1;
    private int selectedPosition = -1;

    public PlaylistDetailsAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public PlaylistSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_song_layout, parent, false);
        return new PlaylistSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongViewHolder holder, int position) {
        Song song = getItem(position);
        int duration = Integer.parseInt(String.valueOf(song.getDuration())) / 1000;

        holder.nameText.setText(song.getTitle());
        //holder.durationText.setText(Helper.calculateDuration(song.getDuration()));
        holder.artistNameText.setText(song.getArtist());
        Log.d(TAG, "onBindViewHolder: " + formattedTime(duration));

        //set highlight
        //holder.itemView.setSelected(selectedPosition == position);
        if (selectedPosition == position) {
            holder.equalizerView.setVisibility(View.VISIBLE);
            holder.equalizerView.animateBars();

        }
        else {
            //holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.equalizerView.setVisibility(View.GONE);
            holder.equalizerView.stopBars();
        }

        //Animate recyclerview
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPos) ? R.anim.up_from_bottom : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPos = position;
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public Song getItem(int position) {
        if (songList == null || songList.size() < 0 || songList.size() == 0) {
            return null;
        }
        if (position < songList.size() && position >= 0) {
            return songList.get(position);
        } else {
            return null;
        }

    }

    class PlaylistSongViewHolder extends RecyclerView.ViewHolder{
        private final EqualizerView equalizerView;
        private TextView nameText, durationText, artistNameText;
        private ImageButton moreBtn;

        public PlaylistSongViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            durationText = itemView.findViewById(R.id.durationText);
            moreBtn = itemView.findViewById(R.id.singleMoreBtn);
            artistNameText = itemView.findViewById(R.id.artistNameText);
            equalizerView = itemView.findViewById(R.id.miniEqualizer);

            equalizerView.setVisibility(View.GONE);


            durationText.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.itemClick(v, position);
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    notifyItemChanged(selectedPosition);
                }
            });

            moreBtn.setOnClickListener(v -> {
                openBottomSheetDialog(getAdapterPosition(), v);
            });
        }
    }

    public List<Song> getSongsList() {
        return songList;
    }

    private void openBottomSheetDialog(int position, View view) {
        Song song = getItem(position);
        context = view.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheet = inflater.inflate(R.layout.bottom_sheet_layout, null);

        bottomImage = bottomSheet.findViewById(R.id.bottomImage);
        bottomSongTitle = bottomSheet.findViewById(R.id.dialogSongTitle);
        bottomSongArtist = bottomSheet.findViewById(R.id.dialogArtistName);
        addToPlaylist = bottomSheet.findViewById(R.id.addToPlaylist);
        playNext = bottomSheet.findViewById(R.id.playNext);
        setAsRingTone = bottomSheet.findViewById(R.id.setAsRingTone);
        delete = bottomSheet.findViewById(R.id.delete);
        shareSong = bottomSheet.findViewById(R.id.shareSong);
        addToQueue = bottomSheet.findViewById(R.id.addToQueue);


        bottomSheetDialog = new Dialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        bottomSongTitle.setText(song.getTitle());
        bottomSongArtist.setText(song.getArtist());
        Glide.with(context).load(ArtWork.albumArtUri(song.getAlbumId()))
                .placeholder(R.drawable.default_song_album_art)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(bottomImage);


        addToPlaylist.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("POSITION", position);
            PlaylistPickerFragment playlistPickerFragment = new PlaylistPickerFragment();
            playlistPickerFragment.setArguments(bundle);
            FragmentManager manager = ((PlaylistDetailsActivity)context).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.container, playlistPickerFragment, "playList");
            transaction.addToBackStack("songs");
            transaction.commit();
            bottomSheetDialog.dismiss();
        });
        setAsRingTone.setOnClickListener(v -> {
            String path = song.getmSongPath();
            Helper.setRingTone(context, path);
            bottomSheetDialog.dismiss();
        });
        delete.setOnClickListener(v -> {
            long playlistID = PlaylistDetailsActivity.playlistID;
            long audioID = song.getId();
            Helper.deletePlaylistTrack(context, playlistID, audioID);

            bottomSheetDialog.dismiss();
        });
        shareSong.setOnClickListener(v ->
                Helper.shareMusic(song.getId(), context)
        );
        addToQueue.setOnClickListener(v -> {
            ((PlaylistDetailsActivity) context).addToQueue(song);
            Toast.makeText(context, R.string.song_added_to_queue, Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        playNext.setOnClickListener(v -> {
            ((PlaylistDetailsActivity)context).setAsNextTrack(song);
            Toast.makeText(context, R.string.song_would_be_played_next, Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    private String formattedTime(int currentPosition) {
        String finalTimerString = "";
        String secondsString = "";
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        secondsString  = minutes + ":" + seconds;
        finalTimerString = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return finalTimerString;
        }
        else {
            return secondsString;
        }
    }

    public void setFilter(List<Song> songs) {
        songList = new ArrayList<>();
        songList.addAll(songs);
        notifyDataSetChanged();
    }
}
