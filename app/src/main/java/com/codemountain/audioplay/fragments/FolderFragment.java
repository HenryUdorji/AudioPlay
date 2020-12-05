package com.codemountain.audioplay.fragments;/*
package com.codemountain.audioplay.fragments;

import android.app.AlertDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.codemountain.audioplay.adapters.FolderFragmentAdapter;
import com.codemountain.audioplay.adapters.SongsFragmentAdapter;
import com.codemountain.audioplay.loaders.FolderLoader;
import com.codemountain.audioplay.loaders.TrackLoader;
import com.codemountain.audioplay.model.Folder;
import com.codemountain.audioplay.service.AudioPlayService;
import com.codemountain.audioplay.utils.Extras;
import com.codemountain.audioplay.utils.Helper;

import java.io.File;
import java.util.List;

public class FolderFragment extends Fragment implements SearchView.OnQueryTextListener{
    private RecyclerView recyclerView;
    private FolderFragmentAdapter adapter;
    private FolderLoader folderLoader;
    private Helper helper;
    private File currentPath;

    public FolderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        String storagePath = Extras.getInstance().getFolderPath();
        if (storagePath != null) {
            currentPath = new File(storagePath);
        } else {
            currentPath = new File(Helper.getStoragePath());
        }

        folderLoader = new FolderLoader(getContext(), getCurrentPath());
        helper = new Helper(getContext());


        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    public File getCurrentPath() {
        return currentPath;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.folder_frag_menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.song_search));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search folder");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.default_folder) {
            setDefaultFolder();
            getActivity().recreate();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDefaultFolder() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Default Folder");
        dialog.setMessage("Select default folder");
        dialog.setPositiveButton("Select", (dialog1, which) -> {
            if (getCurrentPath() != null) {
                if (getCurrentPath().isDirectory()) {
                    try {
                        Log.e("FolderFragment", getCurrentPath().getAbsolutePath());
                        Extras.getInstance().saveFolderPath(getCurrentPath().getAbsolutePath());
                    } catch (Exception e) {
                        // ignored
                    }
                }
            }
        }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Folder> folderList = helper.filterFolder(getContext(), adapter.getFolderList(), newText);
        if (folderList.size() > 0) {
            adapter.setFilter(folderList);
            return true;
        } else {
            Toast.makeText(getContext(), "No data found...", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}*/
