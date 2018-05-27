

package com.mtdev.musicbox.application.fragments.serverfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.ArtistsAdapter;
import com.mtdev.musicbox.application.artworkdatabase.ArtworkManager;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.listviewitems.AbsImageListViewItem;
import com.mtdev.musicbox.application.loaders.ArtistsLoader;
import com.mtdev.musicbox.application.utils.PreferenceHelper;
import com.mtdev.musicbox.application.utils.ScrollSpeedListener;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import java.util.List;

public class ArtistsFragment extends GenericMPDFragment<List<MPDArtist>> implements AdapterView.OnItemClickListener {
    public final static String TAG = ArtistsFragment.class.getSimpleName();
    /**
     * GridView adapter object used for this GridView
     */
    private ArtistsAdapter mArtistAdapter;

    /**
     * Save the root GridView for later usage.
     */
    private AbsListView mAdapterView;

    /**
     * Save the last position here. Gets reused when the user returns to this view after selecting sme
     * albums.
     */
    private int mLastPosition = -1;


    private ArtistSelectedCallback mSelectedCallback;


    private FABFragmentCallback mFABCallback = null;

    private boolean mUseList = false;

    private MPDAlbum.MPD_ALBUM_SORT_ORDER mAlbumSortOrder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String libraryView = sharedPref.getString(getString(R.string.pref_library_view_key), getString(R.string.pref_library_view_default));

        if (libraryView.equals(getString(R.string.pref_library_view_list_key))) {
            mUseList = true;
        }

        mAlbumSortOrder = PreferenceHelper.getMPDAlbumSortOrder(sharedPref, getContext());

        View rootView;
        // get gridview
        if (mUseList) {
            rootView = inflater.inflate(R.layout.listview_layout_refreshable, container, false);
            mAdapterView = (ListView) rootView.findViewById(R.id.main_listview);
        } else {
            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_gridview, container, false);
            mAdapterView = (GridView) rootView.findViewById(R.id.grid_refresh_gridview);
        }

        mArtistAdapter = new ArtistsAdapter(getActivity(), mAdapterView, mUseList);

        mAdapterView.setAdapter(mArtistAdapter);
        mAdapterView.setOnItemClickListener(this);

        mAdapterView.setOnScrollListener(new ScrollSpeedListener(mArtistAdapter, mAdapterView));

        // register for context menu
        registerForContextMenu(mAdapterView);


        // get swipe layout
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        // set swipe colors
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(getContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(getContext(), R.attr.colorPrimary));
        // set swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


        if (null != mFABCallback) {
            mFABCallback.setupFAB(false, null);
            mFABCallback.setupToolbar(getString(R.string.app_name), true, true, false);
        }
        ArtworkManager.getInstance(getContext().getApplicationContext()).registerOnNewArtistImageListener(mArtistAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();

        ArtworkManager.getInstance(getContext().getApplicationContext()).unregisterOnNewArtistImageListener(mArtistAdapter);
    }

    /**
     * Called when the fragment is first attached to its context.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mSelectedCallback = (ArtistSelectedCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArtistSelectedListener");
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mFABCallback = (FABFragmentCallback) context;
        } catch (ClassCastException e) {
            mFABCallback = null;
        }
    }

    /**
     * This method creates a new loader for this fragment.
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<List<MPDArtist>> onCreateLoader(int id, Bundle args) {
        // Read albumartists/artists preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useAlbumArtists = sharedPref.getBoolean(getString(R.string.pref_use_album_artists_key), getResources().getBoolean(R.bool.pref_use_album_artists_default));
        boolean useArtistSort = sharedPref.getBoolean(getString(R.string.pref_use_artist_sort_key), getResources().getBoolean(R.bool.pref_use_artist_sort_default));
        return new ArtistsLoader(getActivity(), useAlbumArtists, useArtistSort);
    }

    /**
     * Called when the loader finished loading its data.
     *
     * @param loader The used loader itself
     * @param data   Data of the loader
     */
    @Override
    public void onLoadFinished(Loader<List<MPDArtist>> loader, List<MPDArtist> data) {
        super.onLoadFinished(loader, data);
        // Set the actual data to the adapter.
        mArtistAdapter.swapModel(data);

        // Reset old scroll position
        if (mLastPosition >= 0) {
            mAdapterView.setSelection(mLastPosition);
            mLastPosition = -1;
        }
    }

    /**
     * If a loader is reset the model data should be cleared.
     *
     * @param loader Loader that was resetted.
     */
    @Override
    public void onLoaderReset(Loader<List<MPDArtist>> loader) {
        // Clear the model data of the adapter.
        mArtistAdapter.swapModel(null);
    }

    /**
     * Create the context menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_artist, menu);
    }

    /**
     * Hook called when an menu item in the context menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the hook was consumed here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info == null) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.fragment_artist_action_enqueue:
                enqueueArtist(info.position);
                return true;
            case R.id.fragment_artist_action_play:
                playArtist(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLastPosition = position;

        MPDArtist artist = (MPDArtist) mArtistAdapter.getItem(position);

        Bitmap bitmap = null;

        // Check if correct view type, to be safe
        if (view instanceof AbsImageListViewItem) {
            bitmap = ((AbsImageListViewItem) view).getBitmap();
        }

        mSelectedCallback.onArtistSelected(artist, bitmap);
    }

    public interface ArtistSelectedCallback {
        void onArtistSelected(MPDArtist artistname, Bitmap bitmap);
    }


    private void enqueueArtist(int index) {
        MPDArtist artist = (MPDArtist) mArtistAdapter.getItem(index);

        MPDQueryHandler.addArtist(artist.getArtistName(), mAlbumSortOrder);
    }

    private void playArtist(int index) {
        MPDArtist artist = (MPDArtist) mArtistAdapter.getItem(index);

        MPDQueryHandler.playArtist(artist.getArtistName(), mAlbumSortOrder);
    }

    public void applyFilter(String name) {
        mArtistAdapter.applyFilter(name);
    }

    public void removeFilter() {
        mArtistAdapter.removeFilter();
    }

}
