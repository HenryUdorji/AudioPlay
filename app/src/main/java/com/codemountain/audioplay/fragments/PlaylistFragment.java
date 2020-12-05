package com.codemountain.audioplay.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.PlaylistDetailsActivity;
import com.codemountain.audioplay.adapters.PlaylistFragmentAdapter;
import com.codemountain.audioplay.interfaces.RefreshPlaylist;
import com.codemountain.audioplay.loaders.PlaylistLoaders;
import com.codemountain.audioplay.loaders.SortOrder;
import com.codemountain.audioplay.model.Playlist;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlaylistFragment extends Fragment implements SearchView.OnQueryTextListener{
    private RecyclerView recyclerView;
    private FloatingActionButton createNewPlaylist;
    private PlaylistLoaders playlistLoaders;
    private PlaylistFragmentAdapter adapter;
    private Helper helper;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        playlistLoaders = new PlaylistLoaders(getActivity());
        playlistLoaders.setSortOrder(Extras.getInstance().getPlaylistSort());
        helper = new Helper(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        createNewPlaylist = view.findViewById(R.id.createPlaylistFab);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        createNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }
                Helper.showCreatePlaylistDialog(getActivity(), new RefreshPlaylist() {
                    @Override
                    public void refresh() {
                        this.refresh();
                    }
                });
            }
        });

        new LoadPlaylistData().execute("");
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadPlaylistData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            adapter = new PlaylistFragmentAdapter(getContext(), playlistLoaders.loadInBackground());
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.setOnItemClickListener((view, position) -> {
                if (getActivity() == null) {
                    return;
                }
                long ID = adapter.getItem(position).getId();
                String name = adapter.getItem(position).getName();
                Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", ID);
                int songCount = Helper.getSongCount(getActivity().getContentResolver(), uri);

                startActivity(new Intent(getActivity(), PlaylistDetailsActivity.class)
                        .putExtra("PLAYLIST_ID", ID)
                        .putExtra("PLAYLIST_NAME", name)
                        .putExtra("SONG_COUNT", songCount));
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.playlist_frag_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.playlist_search));
        searchView.setQueryHint("Search Playlist");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setPlaylistSortOrder(SortOrder.PlaylistSortOrder.PLAYLIST_A_Z);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_za:
                extras.setPlaylistSortOrder(SortOrder.PlaylistSortOrder.PLAYLIST_Z_A);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_date_added:
                extras.setPlaylistSortOrder(SortOrder.PlaylistSortOrder.PLAYLIST_DATE_ADDED);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_date_modified:
                extras.setPlaylistSortOrder(SortOrder.PlaylistSortOrder.PLAYLIST_DATE_MODIFIED);
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
        final List<Playlist> filterList = Helper.filterPlaylist(playlistLoaders.loadInBackground(), newText);
        if (filterList.size() > 0) {
            adapter.setFilter(filterList);
            return true;
        } else {
            Toast.makeText(getContext(), "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}