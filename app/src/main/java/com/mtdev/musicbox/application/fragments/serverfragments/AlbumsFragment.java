
package com.mtdev.musicbox.application.fragments.serverfragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.AlbumsAdapter;
import com.mtdev.musicbox.application.artworkdatabase.ArtworkManager;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.listviewitems.AbsImageListViewItem;
import com.mtdev.musicbox.application.loaders.AlbumsLoader;
import com.mtdev.musicbox.application.utils.CoverBitmapLoader;
import com.mtdev.musicbox.application.utils.PreferenceHelper;
import com.mtdev.musicbox.application.utils.ScrollSpeedListener;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;

import java.util.List;

public class AlbumsFragment extends GenericMPDFragment<List<MPDAlbum>> implements AdapterView.OnItemClickListener, CoverBitmapLoader.CoverBitmapListener, ArtworkManager.onNewArtistImageListener {
    public final static String TAG = AlbumsFragment.class.getSimpleName();

    /**
     * Definition of bundled extras
     */
    public static final String BUNDLE_STRING_EXTRA_ARTISTNAME = "artistname";

    public static final String BUNDLE_STRING_EXTRA_ARTIST = "artist";

    public static final String BUNDLE_STRING_EXTRA_PATH = "album_path";

    public static final String BUNDLE_STRING_EXTRA_BITMAP = "bitmap";

    /**
     * GridView adapter object used for this GridView
     */
    private AlbumsAdapter mAlbumsAdapter;

    /**
     * Save the root GridView for later usage.
     */
    private AbsListView mAdapterView;

    /**
     * Save the last position here. Gets reused when the user returns to this view after selecting sme
     * albums.
     */
    private int mLastPosition = -1;

    private String mAlbumsPath;

    private MPDArtist mArtist;

    private AlbumSelectedCallback mAlbumSelectCallback;

    private FABFragmentCallback mFABCallback = null;

    private boolean mUseList = false;

    private boolean mUseArtistSort;

    private Bitmap mBitmap;

    private CoverBitmapLoader mBitmapLoader;

    private MPDAlbum.MPD_ALBUM_SORT_ORDER mSortOrder;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String libraryView = sharedPref.getString(getString(R.string.pref_library_view_key), getString(R.string.pref_library_view_default));

        if (libraryView.equals(getString(R.string.pref_library_view_list_key))) {
            mUseList = true;
        }

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

        mSortOrder = PreferenceHelper.getMPDAlbumSortOrder(sharedPref, getContext());

        mUseArtistSort = sharedPref.getBoolean(getString(R.string.pref_use_artist_sort_key), getResources().getBoolean(R.bool.pref_use_artist_sort_default));

        mAlbumsAdapter = new AlbumsAdapter(getActivity(), mAdapterView, mUseList);


        /* Check if an artistname was given in the extras */
        Bundle args = getArguments();
        if (null != args) {
            mAlbumsPath = args.getString(BUNDLE_STRING_EXTRA_PATH);
            mArtist = args.getParcelable(BUNDLE_STRING_EXTRA_ARTIST);
            mBitmap = args.getParcelable(BUNDLE_STRING_EXTRA_BITMAP);
        } else {
            mAlbumsPath = "";
            // Create dummy album
            mArtist = new MPDArtist("");
        }

        mAdapterView.setAdapter(mAlbumsAdapter);
        mAdapterView.setOnItemClickListener(this);


        mAdapterView.setOnScrollListener(new ScrollSpeedListener(mAlbumsAdapter, mAdapterView));

        // register for context menu
        registerForContextMenu(mAdapterView);


        setHasOptionsMenu(true);

        // get swipe layout
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        // set swipe colors
        mSwipeRefreshLayout.setColorSchemeColors(ThemeUtils.getThemeColor(getContext(), R.attr.colorAccent),
                ThemeUtils.getThemeColor(getContext(), R.attr.colorPrimary));
        // set swipe refresh listener
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        mBitmapLoader = new CoverBitmapLoader(getContext(), this);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setupToolbarAndStuff();


        ArtworkManager.getInstance(getContext()).registerOnNewArtistImageListener(this);
        ArtworkManager.getInstance(getContext()).registerOnNewAlbumImageListener(mAlbumsAdapter);
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
            mAlbumSelectCallback = (AlbumSelectedCallback) context;
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

    @Override
    public void onPause() {
        super.onPause();

        ArtworkManager.getInstance(getContext()).unregisterOnNewArtistImageListener(this);
        ArtworkManager.getInstance(getContext()).unregisterOnNewAlbumImageListener(mAlbumsAdapter);
    }


    /**
     * Create the context menu.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_album, menu);
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
            case R.id.fragment_albums_action_enqueue:
                enqueueAlbum(info.position);
                return true;
            case R.id.fragment_albums_action_play:
                playAlbum(info.position);
                return true;
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
        if (null != mArtist && !mArtist.getArtistName().equals("")) {
            menuInflater.inflate(R.menu.fragment_menu_albums, menu);

            // get tint color
            int tintColor = ThemeUtils.getThemeColor(getContext(), R.attr.malp_color_text_accent);

            Drawable drawable = menu.findItem(R.id.action_add_artist).getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, tintColor);
            menu.findItem(R.id.action_add_artist).setIcon(drawable);

            menu.findItem(R.id.action_reset_artwork).setVisible(true);
        }
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
        switch (item.getItemId()) {
            case R.id.action_reset_artwork:
                setupToolbarAndStuff();
                ArtworkManager.getInstance(getContext()).resetArtistImage(mArtist);
                return true;
            case R.id.action_add_artist:
                enqueueArtist();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * This method creates a new loader for this fragment.
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<List<MPDAlbum>> onCreateLoader(int id, Bundle args) {
        return new AlbumsLoader(getActivity(), mArtist == null ? "" : mArtist.getArtistName(), mAlbumsPath);
    }

    /**
     * Called when the loader finished loading its data.
     *
     * @param loader The used loader itself
     * @param data   Data of the loader
     */
    @Override
    public void onLoadFinished(Loader<List<MPDAlbum>> loader, List<MPDAlbum> data) {
        super.onLoadFinished(loader, data);
        // Set the actual data to the adapter.
        mAlbumsAdapter.swapModel(data);

        // Reset old scroll position
        if (mLastPosition >= 0) {
            mAdapterView.setSelection(mLastPosition);
            mLastPosition = -1;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Do not save the bitmap for later use (too big for binder)
        Bundle args = getArguments();
        if (args != null) {
            getArguments().remove(BUNDLE_STRING_EXTRA_BITMAP);
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * If a loader is reset the model data should be cleared.
     *
     * @param loader Loader that was resetted.
     */
    @Override
    public void onLoaderReset(Loader<List<MPDAlbum>> loader) {
        // Clear the model data of the adapter.
        mAlbumsAdapter.swapModel(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mLastPosition = position;

        MPDAlbum album = (MPDAlbum) mAlbumsAdapter.getItem(position);
        Bitmap bitmap = null;

        // Check if correct view type, to be safe
        if (view instanceof AbsImageListViewItem ) {
            bitmap = ((AbsImageListViewItem) view).getBitmap();
        }

        // If artist albums are shown set artist for the album (necessary for old MPD version, which don't
        // support group commands and therefore do not provide artist tags for albums)
        if (mArtist != null && !mArtist.getArtistName().isEmpty() && album.getArtistName().isEmpty()) {
            album.setArtistName(mArtist.getArtistName());
            album.setArtistSortName(mArtist.getArtistName());
        }

        // Check if the album already has an artist set. If not use the artist of the fragment
        mAlbumSelectCallback.onAlbumSelected(album, bitmap);
    }

    @Override
    public void receiveBitmap(final Bitmap bm, final CoverBitmapLoader.IMAGE_TYPE type) {
        if (type == CoverBitmapLoader.IMAGE_TYPE.ARTIST_IMAGE && null != mFABCallback && bm != null) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    mFABCallback.setupToolbar(mArtist.getArtistName(), false, false, true);
                    mFABCallback.setupToolbarImage(bm);
                    getArguments().putParcelable(BUNDLE_STRING_EXTRA_BITMAP, bm);
                });
            }
        }
    }

    private void setupToolbarAndStuff() {
        if (null != mFABCallback) {
            if (null != mArtist && !mArtist.getArtistName().isEmpty()) {
                mFABCallback.setupFAB(true, view -> {
                    if(mUseArtistSort) {
                        MPDQueryHandler.playArtistSort(mArtist.getArtistName(), mSortOrder);
                    } else {
                        MPDQueryHandler.playArtist(mArtist.getArtistName(), mSortOrder);
                    }
                });
                if (mBitmap == null) {
                    final View rootView = getView();
                    rootView.post(() -> {
                        int width = rootView.getWidth();
                        mBitmapLoader.getArtistImage(mArtist, true, width, width);
                    });
                    mFABCallback.setupToolbar(mArtist.getArtistName(), false, false, false);
                } else {
                    // Reuse image
                    mFABCallback.setupToolbar(mArtist.getArtistName(), false, false, true);
                    mFABCallback.setupToolbarImage(mBitmap);
                    final View rootView = getView();
                    rootView.post(() -> {
                        int width = rootView.getWidth();

                        // Image too small
                        if(mBitmap.getWidth() < width) {
                            mBitmapLoader.getArtistImage(mArtist, true, width, width);
                        }
                    });
                }
            } else if (null != mAlbumsPath && !mAlbumsPath.equals("")) {
                String lastPath = mAlbumsPath;
                String pathSplit[] = mAlbumsPath.split("/");
                if (pathSplit.length > 0) {
                    lastPath = pathSplit[pathSplit.length - 1];
                }
                mFABCallback.setupFAB(true, v -> MPDQueryHandler.playAlbumsInPath(mAlbumsPath));
                mFABCallback.setupToolbar(lastPath, false, false, false);
            } else {
                mFABCallback.setupFAB(false, null);
                mFABCallback.setupToolbar(getString(R.string.app_name), true, true, false);

            }
        }
    }

    /**
     * Callback for asynchronous image fetching
     * @param artist Artist for which a new image is received
     */
    @Override
    public void newArtistImage(MPDArtist artist) {
        if (artist.equals(mArtist)) {
            setupToolbarAndStuff();
        }
    }

    /**
     * Interface to implement for the activity containing this fragment
     */
    public interface AlbumSelectedCallback {
        void onAlbumSelected(MPDAlbum album, Bitmap bitmap);
    }


    /**
     * Enqueues the album selected by the user
     * @param index Index of the selected album
     */
    private void enqueueAlbum(int index) {
        MPDAlbum album = (MPDAlbum) mAlbumsAdapter.getItem(index);

        // If artist albums are shown set artist for the album (necessary for old MPD version, which don't
        // support group commands and therefore do not provide artist tags for albums)
        if (mArtist != null && !mArtist.getArtistName().isEmpty() && album.getArtistName().isEmpty()) {
            album.setArtistName(mArtist.getArtistName());
            album.setArtistSortName(mArtist.getArtistName());
        }

        MPDQueryHandler.addArtistAlbum(album.getName(), album.getArtistName(), album.getMBID());
    }

    /**
     * Plays the album selected by the user
     * @param index Index of the selected album
     */
    private void playAlbum(int index) {
        MPDAlbum album = (MPDAlbum) mAlbumsAdapter.getItem(index);

        // If artist albums are shown set artist for the album (necessary for old MPD version, which don't
        // support group commands and therefore do not provide artist tags for albums)
        if (mArtist != null && !mArtist.getArtistName().isEmpty() && album.getArtistName().isEmpty()) {
            album.setArtistName(mArtist.getArtistName());
            album.setArtistSortName(mArtist.getArtistName());
        }

        MPDQueryHandler.playArtistAlbum(album.getName(), album.getArtistName(), album.getMBID());
    }

    /**
     * Enqueues the artist that is currently shown (if the fragment is not shown for all albums)
     */
    private void enqueueArtist() {
        MPDQueryHandler.addArtist(mArtist.getArtistName(), mSortOrder);
    }

    public void applyFilter(String name) {
        mAlbumsAdapter.applyFilter(name);
    }

    public void removeFilter() {
        mAlbumsAdapter.removeFilter();
    }


}
