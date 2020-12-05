package com.codemountain.audioplay.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.adapters.SongsFragmentAdapter;
import com.codemountain.audioplay.loaders.SortOrder;
import com.codemountain.audioplay.loaders.TrackLoader;
import com.codemountain.audioplay.model.Song;
import com.codemountain.audioplay.service.AudioPlayService;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;

import java.util.List;


public class SongsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    public static SongsFragmentAdapter songsFragmentAdapter;
    private TrackLoader trackLoader;
    private Helper helper;
    private AudioPlayService audioPlayService;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        trackLoader = new TrackLoader(getContext());
        trackLoader.setSortOrder(Extras.getInstance().getSongSortOrder());
        helper = new Helper(getContext());
        audioPlayService = new AudioPlayService();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.recyclerview_item_animation));
        recyclerView.setLayoutManager(layoutManager);

        new LoadSongData().execute("");
        return view;
    }


    /**
     * This class loads the songs in background using AsyncTask
     * in order to avoid UI lagging
     */
    @SuppressLint("StaticFieldLeak")
    public class LoadSongData extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null){
                songsFragmentAdapter = new SongsFragmentAdapter(getContext(), trackLoader.loadInBackground());
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
                songsFragmentAdapter.setOnItemClickListener((view, position) -> {
                    ((MainActivity) getActivity()).onSongSelected(songsFragmentAdapter.getSongsList(), position);
                    Extras.getInstance().saveSeekServices(0);
                    //NotificationHandler.buildNotification(audioPlayService, "");
                });
                recyclerView.setAdapter(songsFragmentAdapter);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.songs_frag_menu, menu);
        menu.findItem(R.id.equalizerBtn).setVisible(false);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.song_search));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search song");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Extras extras = Extras.getInstance();
        if (getActivity() == null){
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_za:
                extras.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_year:
                extras.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_duration:
                extras.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_date:
                extras.setSongSortOrder(SortOrder.SongSortOrder.SONG_DATE);
                getActivity().recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Song> filterList = Helper.filterSong(trackLoader.loadInBackground(), newText);
        if (filterList.size() > 0) {
            songsFragmentAdapter.setFilter(filterList);
            return true;
        } else {
            Toast.makeText(getContext(), "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}