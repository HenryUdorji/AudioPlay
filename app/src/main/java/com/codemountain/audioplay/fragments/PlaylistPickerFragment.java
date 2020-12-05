package com.codemountain.audioplay.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.AlbumsDetailActivity;
import com.codemountain.audioplay.activities.ArtistDetailActivity;
import com.codemountain.audioplay.activities.MusicPlayerActivity;
import com.codemountain.audioplay.activities.PlaylistDetailsActivity;
import com.codemountain.audioplay.adapters.PlaylistFragmentAdapter;
import com.codemountain.audioplay.loaders.PlaylistLoaders;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.codemountain.audioplay.fragments.SongsFragment.songsFragmentAdapter;

public class PlaylistPickerFragment extends Fragment {

    private static final String TAG = "PlaylistPickerFragment";
    private FloatingActionButton createNewPlaylist;
    private RecyclerView recyclerView;
    private PlaylistFragmentAdapter adapter;
    private ImageButton closeImageBtn;
    private PlaylistLoaders playlistLoaders;
    private Helper helper;
    private int getPosition;

    public PlaylistPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        playlistLoaders = new PlaylistLoaders(getContext());
        playlistLoaders.setSortOrder(Extras.getInstance().getPlaylistSort());
        helper = new Helper(getContext());
        Log.d(TAG, "onCreate: PlaylistPicker created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist_picker, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null){
            getPosition = bundle.getInt("POSITION");
        }

        closeImageBtn = view.findViewById(R.id.closeBtn);
        createNewPlaylist = view.findViewById(R.id.createPlaylistFab);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);


        createNewPlaylist.setOnClickListener(onClick);
        closeImageBtn.setOnClickListener(onClick);

        new LoadPlaylistData().execute("");
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadPlaylistData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null){
                adapter = new PlaylistFragmentAdapter(getContext(), playlistLoaders.loadInBackground());
            }
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (getActivity() != null) {
                adapter.setOnItemClickListener((view, position) -> {
                    if (getActivity() == AlbumsDetailActivity.getInstance()){
                        Song song = AlbumsDetailActivity.getInstance().albumDetailsAdapter.getItem(getPosition);
                        Log.d(TAG, "Album position: " + song.getTitle());
                        Helper.addSongToPlaylist(getActivity(), adapter.getItem(position).getId(), song.getId());
                    }
                    else if (getActivity() == ArtistDetailActivity.getInstance()){
                        Song song = ArtistDetailActivity.getInstance().artistDetailsAdapter.getItem(getPosition);
                        Log.d(TAG, "Artist position: " + song.getTitle());
                        Helper.addSongToPlaylist(getActivity(), adapter.getItem(position).getId(), song.getId());
                    }
                    else if (getActivity() == MusicPlayerActivity.getInstance()){
                        Song song = MusicPlayerActivity.getInstance().getAudioPlayService().getCurrentSong();
                        Log.d(TAG, "Music Player position: " + song.getTitle());
                        Helper.addSongToPlaylist(getActivity(), adapter.getItem(position).getId(), song.getId());
                    }
                    else if (getActivity() == PlaylistDetailsActivity.getInstance()){
                        Song song = PlaylistDetailsActivity.getInstance().playlistDetailsAdapter.getItem(getPosition);
                        Log.d(TAG, "Playlist position: " + song.getTitle());
                        Helper.addSongToPlaylist(getActivity(), adapter.getItem(position).getId(), song.getId());
                    }
                    else {
                        Song song = songsFragmentAdapter.getItem(getPosition);
                        Log.d(TAG, "Adapter position: " + song.getTitle());
                        Helper.addSongToPlaylist(getActivity(), adapter.getItem(position).getId(), song.getId());
                    }

                });
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private View.OnClickListener onClick = v -> {
        switch (v.getId()){
            case R.id.createPlaylistFab:
                if (getActivity() == null){
                    return;
                }
                Helper.showCreatePlaylistDialog(getActivity(), () ->
                        getActivity().recreate());
                break;

            case R.id.closeBtn:
                getActivity().onBackPressed();
                break;
        }
    };

}