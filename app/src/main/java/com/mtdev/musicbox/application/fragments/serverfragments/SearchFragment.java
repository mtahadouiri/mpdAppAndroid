

package com.mtdev.musicbox.application.fragments.serverfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.FileAdapter;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.loaders.SearchResultLoader;
import com.mtdev.musicbox.application.utils.PreferenceHelper;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDCommands;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.util.List;

public class SearchFragment extends GenericMPDFragment<List<MPDFileEntry>> implements AdapterView.OnItemClickListener, View.OnFocusChangeListener {
    public static final String TAG = SearchFragment.class.getSimpleName();

    /**
     * Adapter used by the ListView
     */
    private FileAdapter mFileAdapter;

    /**
     * Main ListView of this fragment
     */
    private ListView mListView;

    /**
     * Name of the playlist to load
     */
    private String mPath;

    private FABFragmentCallback mFABCallback = null;

    private Spinner mSelectSpinner;

    private SearchView mSearchView;

    private String mSearchText;

    private MPDCommands.MPD_SEARCH_TYPE mSearchType;

    private MPDAlbum.MPD_ALBUM_SORT_ORDER mAlbumSortOrder;

    private PreferenceHelper.LIBRARY_TRACK_CLICK_ACTION mClickAction;

    /**
     * Hack variable to save the position of a opened context menu because menu info is null for
     * submenus.
     */
    private int mContextMenuPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_server_search, container, false);

        // Get the main ListView of this fragment
        mListView = rootView.findViewById(R.id.main_listview);

        // Create the needed adapter for the ListView
        mFileAdapter = new FileAdapter(getActivity(), false, true);

        // Combine the two to a happy couple
        mListView.setAdapter(mFileAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        mSelectSpinner = rootView.findViewById(R.id.search_criteria);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.server_search_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSelectSpinner.setAdapter(adapter);
        mSelectSpinner.setOnItemSelectedListener(new SpinnerSelectListener());

        mSearchView = rootView.findViewById(R.id.search_text);
        mSearchView.setOnQueryTextListener(new SearchViewQueryListener());
        mSearchView.setOnFocusChangeListener(this);


        // get swipe layout
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        // set swipe colors
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(getContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(getContext(), R.attr.colorPrimary));
        // set swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        setHasOptionsMenu(true);

        // Get album sort order
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mAlbumSortOrder = PreferenceHelper.getMPDAlbumSortOrder(sharedPref, getContext());
        mClickAction = PreferenceHelper.getClickAction(sharedPref, getContext());

        // Return the ready inflated and configured fragment view.
        return rootView;
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
            mFABCallback = (FABFragmentCallback) context;
            showFAB(false);
        } catch (ClassCastException e) {
            mFABCallback = null;
        }
    }

    /**
     * Starts the loader to make sure the data is up-to-date after resuming the fragment (from background)
     */
    @Override
    public void onResume() {
        super.onResume();

        if (null != mFABCallback) {
            mFABCallback.setupFAB(true, new FABOnClickListener());
            mFABCallback.setupToolbar(getResources().getString(R.string.action_search), false, true, false);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String searchTypeSetting = sharedPref.getString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_default));

        if (searchTypeSetting.equals(getString(R.string.pref_search_type_track_key))) {
            mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_TRACK;
            mSelectSpinner.setSelection(0);
        } else if (searchTypeSetting.equals(getString(R.string.pref_search_type_album_key))) {
            mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ALBUM;
            mSelectSpinner.setSelection(1);
        } else if (searchTypeSetting.equals(getString(R.string.pref_search_type_artist_key))) {
            mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ARTIST;
            mSelectSpinner.setSelection(2);
        } else if (searchTypeSetting.equals(getString(R.string.pref_search_type_file_key))) {
            mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_FILE;
            mSelectSpinner.setSelection(3);
        } else if (searchTypeSetting.equals(getString(R.string.pref_search_type_any_key))) {
            mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ANY;
            mSelectSpinner.setSelection(4);
        }
    }

    @Override
    public Loader<List<MPDFileEntry>> onCreateLoader(int id, Bundle args) {
        return new SearchResultLoader(getActivity(), mSearchText, mSearchType);
    }

    @Override
    public void onLoadFinished(Loader<List<MPDFileEntry>> loader, List<MPDFileEntry> data) {
        super.onLoadFinished(loader, data);
        mFileAdapter.swapModel(data);
        if (null != data && !data.isEmpty()) {
            showFAB(true);
        } else {
            showFAB(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MPDFileEntry>> loader) {
        super.onLoaderReset(loader);
        mFileAdapter.swapModel(null);
    }

    /**
     * Create the context menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_search_track, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getView();
        if (null != view) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
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

        int position;
        if (info == null) {
            if (mContextMenuPosition == -1 ) {
                return super.onContextItemSelected(item);
            }
            position = mContextMenuPosition;
            mContextMenuPosition = -1;
        } else {
            position = info.position;
        }


        MPDTrack track = (MPDTrack) mFileAdapter.getItem(position);

        mListView.requestFocus();

        switch (item.getItemId()) {
            case R.id.action_song_play:
                MPDQueryHandler.playSong(track.getPath());
                return true;
            case R.id.action_song_play_next:
                MPDQueryHandler.playSongNext(track.getPath());
                return true;
            case R.id.action_show_details: {
                // Open song details dialog
                SongDetailsDialog songDetailsDialog = new SongDetailsDialog();
                Bundle args = new Bundle();
                args.putParcelable(SongDetailsDialog.EXTRA_FILE, (MPDTrack) mFileAdapter.getItem(position));
                songDetailsDialog.setArguments(args);
                songDetailsDialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "SongDetails");
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Initialize the options menu.
     * Be sure to call {@link #setHasOptionsMenu} before.
     *
     * @param menu         The container for the custom options menu.
     * @param menuInflater The inflater to instantiate the layout.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.fragment_menu_search_tracks, menu);

        // get tint color
        int tintColor = ThemeUtils.getThemeColor(getContext(), R.attr.malp_color_text_accent);

        Drawable drawable = menu.findItem(R.id.action_add_search_result).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, tintColor);
        menu.findItem(R.id.action_add_search_result).setIcon(drawable);

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    /**
     * Hook called when an menu item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return True if the hook was consumed here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG,"onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.action_add_search_result:
                MPDQueryHandler.searchAddFiles(mSearchText, mSearchType);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mClickAction) {
            case ACTION_SHOW_DETAILS: {
                // Open song details dialog
                SongDetailsDialog songDetailsDialog = new SongDetailsDialog();
                Bundle args = new Bundle();
                args.putParcelable(SongDetailsDialog.EXTRA_FILE, (MPDTrack) mFileAdapter.getItem(position));
                songDetailsDialog.setArguments(args);
                songDetailsDialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "SongDetails");
                return;
            }
            case ACTION_PLAY_SONG: {
                MPDTrack track = (MPDTrack) mFileAdapter.getItem(position);

                MPDQueryHandler.playSong(track.getPath());
                return;
            }
            case ACTION_PLAY_SONG_NEXT: {
                MPDTrack track = (MPDTrack) mFileAdapter.getItem(position);

                MPDQueryHandler.playSongNext(track.getPath());
                return;
            }
        }
    }

    private void showFAB(boolean active) {
        if (null != mFABCallback) {
            mFABCallback.setupFAB(active, active ? new FABOnClickListener() : null);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(mSearchView) && !hasFocus) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private class FABOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MPDQueryHandler.searchPlayFiles(mSearchText, mSearchType);
        }
    }

    private class SpinnerSelectListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            switch (position) {
                case 0:
                    mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_TRACK;
                    prefEditor.putString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_track_key));
                    break;
                case 1:
                    mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ALBUM;
                    prefEditor.putString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_album_key));
                    break;
                case 2:
                    mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ARTIST;
                    prefEditor.putString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_artist_key));
                    break;
                case 3:
                    mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_FILE;
                    prefEditor.putString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_file_key));
                    break;
                case 4:
                    mSearchType = MPDCommands.MPD_SEARCH_TYPE.MPD_SEARCH_ANY;
                    prefEditor.putString(getString(R.string.pref_search_type_key), getString(R.string.pref_search_type_any_key));
                    break;
            }

            // Write settings values
            prefEditor.apply();

            mSearchView.setActivated(true);
            mSearchView.requestFocus();

            // Open the keyboard again
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class SearchViewQueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            mSearchText = query;
            refreshContent();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

}
