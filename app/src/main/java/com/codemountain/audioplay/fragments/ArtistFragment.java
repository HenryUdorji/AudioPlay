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
import com.codemountain.audioplay.activities.ArtistDetailActivity;
import com.codemountain.audioplay.adapters.ArtistFragmentAdapter;
import com.codemountain.audioplay.loaders.ArtistLoader;
import com.codemountain.audioplay.loaders.SortOrder;
import com.codemountain.audioplay.model.Artist;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;

import java.util.List;


public class ArtistFragment extends Fragment implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ArtistFragmentAdapter adapter;
    private ArtistLoader artistLoader;
    private Helper helper;


    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        artistLoader = new ArtistLoader(getContext());
        artistLoader.setSortOrder(Extras.getInstance().getArtistSortOrder());
        helper = new Helper(getContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        new LoadArtistData().execute("");
        return view;
    }

    /**
     * This class loads the artist in background using AsyncTask
     * in order to avoid UI lagging
     */
    public class LoadArtistData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            if (getActivity() != null){
                adapter = new ArtistFragmentAdapter(getContext(), artistLoader.loadInBackground());
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
                        Artist artist = adapter.getItem(position);
                        long artistId = artist.getId();

                        startActivity(new Intent(getContext(), ArtistDetailActivity.class)
                                .putExtra("ARTIST_ID", artistId));
                        getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.artist_frag_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.artist_search));;
        searchView.setQueryHint("Search Artist");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_za:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_number_of_songs:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                getActivity().recreate();
                break;

            case R.id.menu_sort_by_number_of_albums:
                extras.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
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
        final List<Artist> filterList = Helper.filterArtist(artistLoader.loadInBackground(), newText);
        if (filterList.size() > 0) {
            adapter.setFilter(filterList);
            return true;
        } else {
            Toast.makeText(getContext(), "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}