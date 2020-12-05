package com.codemountain.audioplay.fragments;

import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codemountain.audioplay.R;
import com.codemountain.audioplay.activities.AlbumsDetailActivity;
import com.codemountain.audioplay.adapters.AlbumFragmentAdapter;
import com.codemountain.audioplay.loaders.AlbumLoader;
import com.codemountain.audioplay.loaders.SortOrder;
import com.codemountain.audioplay.model.Album;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;

import java.util.List;

public class AlbumFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private AlbumFragmentAdapter adapter;
    private AlbumLoader albumLoader;
    private Helper helper;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        albumLoader = new AlbumLoader(getContext());
        albumLoader.setSortOrder(Extras.getInstance().getAlbumSortOrder());
        helper = new Helper(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        new LoadAlbumData().execute("");
        return view;
    }

    /**
     * This class loads the albums in background using AsyncTask
     * in order to avoid UI lagging
     */
    public class LoadAlbumData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null){
                adapter = new AlbumFragmentAdapter(getActivity(), albumLoader.loadInBackground());
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
                    if (position < adapter.getItemCount() && position >= 0) {
                        Album album = adapter.getItem(position);
                        long albumID = album.getId();

                        startActivity(new Intent(getContext(), AlbumsDetailActivity.class)
                                .putExtra("ALBUM_ID", albumID));
                        getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }

                });
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.album_frag_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.album_search));
        searchView.setQueryHint("Search Albums");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_za:
                extras.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_Z_A);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_year:
                extras.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_artist:
                extras.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                getActivity().recreate();
                break;
            case R.id.menu_sort_by_number_of_songs:
                extras.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS);
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
        final List<Album> filterList = Helper.filterAlbum(albumLoader.loadInBackground(), newText);
        if (filterList.size() > 0) {
            adapter.setFilter(filterList);
            return true;
        } else {
            Toast.makeText(getContext(), "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}