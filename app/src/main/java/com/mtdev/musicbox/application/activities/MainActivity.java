/*
 *  Copyright (C) 2018 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://github.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mtdev.musicbox.application.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mtdev.musicbox.Client.Entities.AllMusicFolders;
import com.mtdev.musicbox.Client.Entities.LocalTrack;
import com.mtdev.musicbox.Client.Entities.Playlist;
import com.mtdev.musicbox.Client.Entities.UnifiedTrack;
import com.mtdev.musicbox.Client.Fragments.NewPlaylistFragment;
import com.mtdev.musicbox.Client.Fragments.PlayList;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.CurrentPlaylistAdapter;
import com.mtdev.musicbox.application.callbacks.AddPathToPlaylist;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.callbacks.PlaylistCallback;
import com.mtdev.musicbox.application.callbacks.ProfileManageCallbacks;
import com.mtdev.musicbox.application.fragments.ArtworkSettingsFragment;
import com.mtdev.musicbox.application.fragments.EditProfileFragment;
import com.mtdev.musicbox.application.fragments.InformationSettingsFragment;
import com.mtdev.musicbox.application.fragments.ProfilesFragment;
import com.mtdev.musicbox.application.fragments.SettingsFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.AlbumTracksFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.AlbumsFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.ArtistsFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.ChoosePlaylistDialog;
import com.mtdev.musicbox.application.fragments.serverfragments.FilesFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.MyMusicTabsFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.PlaylistTracksFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.SavedPlaylistsFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.SearchFragment;
import com.mtdev.musicbox.application.fragments.serverfragments.SongDetailsDialog;
import com.mtdev.musicbox.application.utils.Album;
import com.mtdev.musicbox.application.utils.AllPlaylists;
import com.mtdev.musicbox.application.utils.Artist;
import com.mtdev.musicbox.application.utils.MusicFolder;
import com.mtdev.musicbox.application.utils.PlayListsHorizontalAdapter;
import com.mtdev.musicbox.application.utils.Settings;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.application.views.CurrentPlaylistView;
import com.mtdev.musicbox.application.views.NowPlayingView;
import com.mtdev.musicbox.mpdservice.ConnectionManager;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDStateMonitoringHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDException;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDArtist;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDCurrentStatus;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDProfileManager;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDServerProfile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.mtdev.musicbox.AppConfig.HOST;

public class MainActivity extends GenericActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlbumsFragment.AlbumSelectedCallback, ArtistsFragment.ArtistSelectedCallback,
        ProfileManageCallbacks, PlaylistCallback,
        NowPlayingView.NowPlayingDragStatusReceiver, FilesFragment.FilesCallback,
        FABFragmentCallback, SettingsFragment.OnArtworkSettingsRequestedCallback,NewPlaylistFragment.NewPlaylistFragmentCallbackListener {


    private static final String TAG = "MainActivity";

    public final static String MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW = "org.malp.requestedview";
    public final static String MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW_NOWPLAYINGVIEW = "org.malp.requestedview.nowplaying";

    private final static String MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_DRAG_STATUS = "MainActivity.NowPlayingDragStatus";
    private final static String MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_VIEW_SWITCHER_CURRENT_VIEW = "MainActivity.NowPlayingViewSwitcherCurrentView";
    public static int themeColor = Color.parseColor("#B24242");
    public static List<LocalTrack> finalSelectedTracks = new ArrayList<>();
    public static List<LocalTrack> localTrackList = new ArrayList<>();
    public static List<Album> albums = new ArrayList<>();
    public static List<Album> finalAlbums = new ArrayList<>();
    public static List<Artist> artists = new ArrayList<>();
    public static List<Artist> finalArtists = new ArrayList<>();
    public static AllPlaylists allPlaylists = new AllPlaylists();
    public static AllMusicFolders allMusicFolders;

    private DRAG_STATUS mNowPlayingDragStatus;
    private DRAG_STATUS mSavedNowPlayingDragStatus = null;

    private ActionBarDrawerToggle mDrawerToggle;

    private VIEW_SWITCHER_STATUS mNowPlayingViewSwitcherStatus;
    private VIEW_SWITCHER_STATUS mSavedNowPlayingViewSwitcherStatus;

    private boolean mHeaderImageActive;

    private boolean mUseArtistSort;

    private FloatingActionButton mFAB;

    public SharedPreferences mPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static Gson gson;

    public static PlayListsHorizontalAdapter pAdapter;
    private Settings settings;
    public RecyclerView playlistsRecycler;
    private TextView playlistNothingText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore drag state
        if (savedInstanceState != null) {
            mSavedNowPlayingDragStatus = DRAG_STATUS.values()[savedInstanceState.getInt(MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_DRAG_STATUS)];
            mSavedNowPlayingViewSwitcherStatus = VIEW_SWITCHER_STATUS.values()[savedInstanceState.getInt(MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_VIEW_SWITCHER_CURRENT_VIEW)];
        }


        setContentView(R.layout.activity_main);
        mPrefs = getPreferences(MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        gson = new Gson();
        // restore elevation behaviour as pre 24 support lib
        AppBarLayout layout = findViewById(R.id.appbar);
        layout.setStateListAnimator(null);
        ViewCompat.setElevation(layout, 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable back navigation
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (allMusicFolders == null) {
            allMusicFolders = new AllMusicFolders();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }

        int navId = getDefaultViewID();

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setCheckedItem(navId);
        }


        mFAB = findViewById(R.id.andrompd_play_button);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mUseArtistSort = sharedPref.getBoolean(getString(R.string.pref_use_artist_sort_key), getResources().getBoolean(R.bool.pref_use_artist_sort_default));



        registerForContextMenu(findViewById(R.id.main_listview));

        if (MPDProfileManager.getInstance(this).getProfiles().size() == 0) {
            navId = R.id.nav_library;

            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.welcome_dialog_title));
            builder.setMessage(getResources().getString(R.string.welcome_dialog_text));


            builder.setPositiveButton(R.string.dialog_action_ok, (dialog, id) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();*/
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            
            Fragment fragment = null;

            if (navId == R.id.nav_library) {
                fragment = new MyMusicTabsFragment();
                MyMusicTabsFragment.DEFAULTTAB defaultTab = getDefaultTab();
                Bundle args = new Bundle();
                args.putInt(MyMusicTabsFragment.MY_MUSIC_REQUESTED_TAB, defaultTab.ordinal());

                fragment.setArguments(args);
            } else if (navId == R.id.nav_saved_playlists) {
                fragment = new SavedPlaylistsFragment();
            } else if (navId == R.id.nav_files) {
                fragment = new FilesFragment();
            } else if (navId == R.id.nav_profiles) {
                fragment = new ProfilesFragment();
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }

        getLocalSongs();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
            NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
            if (nowPlayingView != null) {
                View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
                coordinatorLayout.setVisibility(View.VISIBLE);
                nowPlayingView.minimize();
            }
        } else {
            super.onBackPressed();

            // enable navigation bar when backstack empty
            if (fragmentManager.getBackStackEntryCount() == 0) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    onBackPressed();
                } else {
                    // back stack empty so enable navigation drawer

                    mDrawerToggle.setDrawerIndicatorEnabled(true);

                    if (mDrawerToggle.onOptionsItemSelected(item)) {
                        return true;
                    }
                }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.main_listview && mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
            int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu_current_playlist_track, menu);

            // Check if the menu is created for the currently playing song. If this is the case, do not show play as next item.
            MPDCurrentStatus status = MPDStateMonitoringHandler.getHandler().getLastStatus();
            if (status != null && position == status.getCurrentSongIndex()) {
                menu.findItem(R.id.action_song_play_next).setVisible(false);
            }


            CurrentPlaylistView currentPlaylistView = findViewById(R.id.now_playing_playlist);
            if (currentPlaylistView.getItemViewType(position) == CurrentPlaylistAdapter.VIEW_TYPES.TYPE_SECTION_TRACK_ITEM) {
                menu.findItem(R.id.action_remove_album).setVisible(true);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (info == null) {
            return super.onContextItemSelected(item);
        }

        CurrentPlaylistView currentPlaylistView = findViewById(R.id.now_playing_playlist);

        if (currentPlaylistView != null && mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {

            MPDTrack track = (MPDTrack) currentPlaylistView.getItem(info.position);

            switch (item.getItemId()) {

                case R.id.action_song_play_next:
                    MPDQueryHandler.playIndexAsNext(info.position);
                    return true;
                case R.id.action_add_to_saved_playlist:
                    // open dialog in order to save the current playlist as a playlist in the mediastore
                    ChoosePlaylistDialog choosePlaylistDialog = new ChoosePlaylistDialog();
                    Bundle args = new Bundle();
                    args.putBoolean(ChoosePlaylistDialog.EXTRA_SHOW_NEW_ENTRY, true);
                    choosePlaylistDialog.setCallback(new AddPathToPlaylist(track, this));
                    choosePlaylistDialog.setArguments(args);
                    choosePlaylistDialog.show(getSupportFragmentManager(), "ChoosePlaylistDialog");
                    return true;
                case R.id.action_remove_song:
                    MPDQueryHandler.removeSongFromCurrentPlaylist(info.position);
                    return true;
                case R.id.action_remove_album:
                    currentPlaylistView.removeAlbumFrom(info.position);
                    return true;
                case R.id.action_show_artist:
                    if (mUseArtistSort) {
                        onArtistSelected(new MPDArtist(track.getTrackArtistSort()), null);
                    } else {
                        onArtistSelected(new MPDArtist(track.getTrackArtist()), null);
                    }
                    return true;
                case R.id.action_show_album:
                    MPDAlbum tmpAlbum = new MPDAlbum(track.getTrackAlbum());
                    // Set album artist
                    if (!track.getTrackAlbumArtist().isEmpty()) {
                        tmpAlbum.setArtistName(track.getTrackAlbumArtist());
                    } else {
                        tmpAlbum.setArtistName(track.getTrackArtist());
                    }

                    // Set albumartistsort
                    if (!track.getTrackAlbumArtistSort().isEmpty()) {
                        tmpAlbum.setArtistSortName(track.getTrackAlbumArtistSort());
                    } else {
                        tmpAlbum.setArtistSortName(track.getTrackArtistSort());
                    }

                    tmpAlbum.setMBID(track.getTrackAlbumMBID());
                    onAlbumSelected(tmpAlbum, null);
                    return true;
                case R.id.action_show_details:
                    // Open song details dialog
                    SongDetailsDialog songDetailsDialog = new SongDetailsDialog();
                    Bundle songArgs = new Bundle();
                    songArgs.putParcelable(SongDetailsDialog.EXTRA_FILE, track);
                    songDetailsDialog.setArguments(songArgs);
                    songDetailsDialog.show(getSupportFragmentManager(), "SongDetails");
                    return true;
            }
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
        coordinatorLayout.setVisibility(View.VISIBLE);

        NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
        if (nowPlayingView != null) {
            nowPlayingView.minimize();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        // clear backstack
        fragmentManager.popBackStackImmediate("", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Fragment fragment = null;
        String fragmentTag = "";

        if (id == R.id.nav_library) {
            // Handle the camera action
            fragment = new MyMusicTabsFragment();
            fragmentTag = MyMusicTabsFragment.TAG;
        } else if (id == R.id.nav_saved_playlists) {
            fragment = new SavedPlaylistsFragment();
            fragmentTag = SavedPlaylistsFragment.TAG;
        } else if (id == R.id.nav_files) {
            fragment = new FilesFragment();
            fragmentTag = FilesFragment.TAG;

            Bundle args = new Bundle();
            args.putString(FilesFragment.EXTRA_FILENAME, "");

        } else if (id == R.id.nav_search) {
            fragment = new SearchFragment();
            fragmentTag = SearchFragment.TAG;
        } else if (id == R.id.nav_profiles) {
            fragment = new ProfilesFragment();
            fragmentTag = ProfilesFragment.TAG;
        } else if (id == R.id.nav_app_settings) {
            fragment = new SettingsFragment();
            fragmentTag = SettingsFragment.TAG;
        } else if (id == R.id.nav_server_properties) {
            /*fragment = new ServerPropertiesFragment();
            fragmentTag = ServerPropertiesFragment.TAG;*/
            fragment = new PlayList();
            fragmentTag = PlayList.TAG;
            Log.d(fragmentTag,"Opened");
        } else if (id == R.id.nav_information) {
            fragment = new InformationSettingsFragment();
            fragmentTag = InformationSettingsFragment.class.getSimpleName();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        // Do the actual fragment transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction().addToBackStack(null);
        transaction.replace(R.id.fragment_container, fragment, fragmentTag);
        transaction.commit();

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
        if (nowPlayingView != null) {


            nowPlayingView.registerDragStatusReceiver(this);

            /*
             * Check if the activity got an extra in its intend to show the nowplayingview directly.
             * If yes then pre set the dragoffset of the draggable helper.
             */
            Intent resumeIntent = getIntent();
            if (resumeIntent != null && resumeIntent.getExtras() != null && resumeIntent.getExtras().getString(MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW) != null &&
                    resumeIntent.getExtras().getString(MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW).equals(MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW_NOWPLAYINGVIEW)) {
                nowPlayingView.setDragOffset(0.0f);
                getIntent().removeExtra(MAINACTIVITY_INTENT_EXTRA_REQUESTEDVIEW);
            } else {
                // set drag status
                if (mSavedNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
                    nowPlayingView.setDragOffset(0.0f);
                } else if (mSavedNowPlayingDragStatus == DRAG_STATUS.DRAGGED_DOWN) {
                    nowPlayingView.setDragOffset(1.0f);
                }
                mSavedNowPlayingDragStatus = null;

                // set view switcher status
                if (mSavedNowPlayingViewSwitcherStatus != null) {
                    nowPlayingView.setViewSwitcherStatus(mSavedNowPlayingViewSwitcherStatus);
                    mNowPlayingViewSwitcherStatus = mSavedNowPlayingViewSwitcherStatus;
                }
                mSavedNowPlayingViewSwitcherStatus = null;
            }
            nowPlayingView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
        if (nowPlayingView != null) {
            nowPlayingView.registerDragStatusReceiver(null);

            nowPlayingView.onPause();
        }


    }

    @Override
    protected void onConnected() {
        setNavbarHeader(ConnectionManager.getInstance(getApplicationContext()).getProfileName());
    }

    @Override
    protected void onDisconnected() {
        setNavbarHeader(getString(R.string.app_name_nice));
    }

    @Override
    protected void onMPDError(MPDException.MPDServerException e) {
        View layout = findViewById(R.id.drawer_layout);
        if (layout != null) {
            String errorText = getString(R.string.snackbar_mpd_server_error_format,e.getErrorCode(), e.getCommandOffset(), e.getServerMessage());
            Snackbar sb = Snackbar.make(layout, errorText, Snackbar.LENGTH_LONG);

            // style the snackbar text
            TextView sbText = sb.getView().findViewById(android.support.design.R.id.snackbar_text);
            sbText.setTextColor(ThemeUtils.getThemeColor(this, R.attr.malp_color_text_accent));
            sb.show();
        }
    }

    @Override
    protected void onMPDConnectionError(MPDException.MPDConnectionException e) {
        View layout = findViewById(R.id.drawer_layout);
        if (layout != null) {
            String errorText = getString(R.string.snackbar_mpd_connection_error_format,e.getError());

            Snackbar sb = Snackbar.make(layout, errorText, Snackbar.LENGTH_LONG);

            // style the snackbar text
            TextView sbText = sb.getView().findViewById(android.support.design.R.id.snackbar_text);
            sbText.setTextColor(ThemeUtils.getThemeColor(this, R.attr.malp_color_text_accent));
            sb.show();
        }
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // save drag status of the nowplayingview
        savedInstanceState.putInt(MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_DRAG_STATUS, mNowPlayingDragStatus.ordinal());

        // save the cover/playlist view status of the nowplayingview
        savedInstanceState.putInt(MAINACTIVITY_SAVED_INSTANCE_NOW_PLAYING_VIEW_SWITCHER_CURRENT_VIEW, mNowPlayingViewSwitcherStatus.ordinal());
    }

    @Override
    public void onAlbumSelected(MPDAlbum album, Bitmap bitmap) {

        if (mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
            NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
            if (nowPlayingView != null) {
                View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
                coordinatorLayout.setVisibility(View.VISIBLE);
                nowPlayingView.minimize();
            }
        }

        // Create fragment and give it an argument for the selected article
        AlbumTracksFragment newFragment = new AlbumTracksFragment();
        Bundle args = new Bundle();
        args.putParcelable(AlbumTracksFragment.BUNDLE_STRING_EXTRA_ALBUM, album);
        if (bitmap != null) {
            args.putParcelable(AlbumTracksFragment.BUNDLE_STRING_EXTRA_BITMAP, bitmap);
        }

        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        newFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        newFragment.setExitTransition(new Slide(Gravity.TOP));
        transaction.replace(R.id.fragment_container, newFragment, AlbumTracksFragment.TAG);
        transaction.addToBackStack("AlbumTracksFragment");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_library);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onArtistSelected(MPDArtist artist, Bitmap bitmap) {
        if (mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
            NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
            if (nowPlayingView != null) {
                View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
                coordinatorLayout.setVisibility(View.VISIBLE);
                nowPlayingView.minimize();
            }
        }

        // Create fragment and give it an argument for the selected article
        AlbumsFragment newFragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putString(AlbumsFragment.BUNDLE_STRING_EXTRA_ARTISTNAME, artist.getArtistName());
        args.putParcelable(AlbumsFragment.BUNDLE_STRING_EXTRA_ARTIST, artist);

        // Transfer the bitmap to the next fragment
        if (bitmap != null) {
            args.putParcelable(AlbumsFragment.BUNDLE_STRING_EXTRA_BITMAP, bitmap);
        }

        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
        newFragment.setExitTransition(new Slide(Gravity.TOP));
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment, AlbumsFragment.TAG);
        transaction.addToBackStack("ArtistAlbumsFragment");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_library);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onStatusChanged(DRAG_STATUS status) {
        mNowPlayingDragStatus = status;
        if (status == DRAG_STATUS.DRAGGED_UP) {
            View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
            coordinatorLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDragPositionChanged(float pos) {
        if(mHeaderImageActive) {
            // Get the primary color of the active theme from the helper.
            int newColor = ThemeUtils.getThemeColor(this, R.attr.colorPrimaryDark);

            // Calculate the offset depending on the floating point position (0.0-1.0 of the view)
            // Shift by 24 bit to set it as the A from ARGB and set all remaining 24 bits to 1 to
            int alphaOffset = (((255 - (int) (255.0 * pos)) << 24) | 0xFFFFFF);
            // and with this mask to set the new alpha value.
            newColor &= (alphaOffset);
            getWindow().setStatusBarColor(newColor);
        }
    }

    @Override
    public void onSwitchedViews(VIEW_SWITCHER_STATUS view) {
        mNowPlayingViewSwitcherStatus = view;
    }

    @Override
    public void onStartDrag() {
        View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
        coordinatorLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void editProfile(MPDServerProfile profile) {
        if (null == profile) {
            profile = new MPDServerProfile(getString(R.string.fragment_profile_default_name), true);
            ConnectionManager.getInstance(getApplicationContext()
            ).addProfile(profile, this);
        }

        // Create fragment and give it an argument for the selected article
        EditProfileFragment newFragment = new EditProfileFragment();
        Bundle args = new Bundle();
        if (null != profile) {
            args.putParcelable(EditProfileFragment.EXTRA_PROFILE, profile);
        }


        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        newFragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment, EditProfileFragment.TAG);
        transaction.addToBackStack("EditProfileFragment");


        // Commit the transaction
        transaction.commit();
    }



    @Override
    public void openPlaylist(String name) {
        // Create fragment and give it an argument for the selected article
        PlaylistTracksFragment newFragment = new PlaylistTracksFragment();
        Bundle args = new Bundle();
        args.putString(PlaylistTracksFragment.EXTRA_PLAYLIST_NAME, name);


        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        newFragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("PlaylistTracksFragment");

        // Commit the transaction
        transaction.commit();

    }


    @Override
    public void setupFAB(boolean active, View.OnClickListener listener) {
        mFAB = findViewById(R.id.andrompd_play_button);
        if (null == mFAB) {
            return;
        }
        if (active) {
            mFAB.show();
        } else {
            mFAB.hide();
        }
        mFAB.setOnClickListener(listener);
    }

    @Override
    public void setupToolbar(String title, boolean scrollingEnabled, boolean drawerIndicatorEnabled, boolean showImage) {
        // set drawer state
        mDrawerToggle.setDrawerIndicatorEnabled(drawerIndicatorEnabled);

        RelativeLayout collapsingImageLayout = findViewById(R.id.appbar_image_layout);

        ImageView collapsingImage = findViewById(R.id.collapsing_image);

        if (collapsingImage != null) {
            if (showImage) {
                collapsingImageLayout.setVisibility(View.VISIBLE);
                mHeaderImageActive = true;

                // Get the primary color of the active theme from the helper.
                int newColor = ThemeUtils.getThemeColor(this, R.attr.colorPrimaryDark);

                // Calculate the offset depending on the floating point position (0.0-1.0 of the view)
                // Shift by 24 bit to set it as the A from ARGB and set all remaining 24 bits to 1 to
                int alphaOffset = (((255 - (int) (255.0 * (mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP  ? 0.0 : 1.0 ))) << 24) | 0xFFFFFF);
                // and with this mask to set the new alpha value.
                newColor &= (alphaOffset);
                getWindow().setStatusBarColor(newColor);
            } else {
                collapsingImageLayout.setVisibility(View.GONE);
                mHeaderImageActive = false;

                // Get the primary color of the active theme from the helper.
                getWindow().setStatusBarColor(ThemeUtils.getThemeColor(this, R.attr.colorPrimaryDark));
            }
        } else {
            // If in portrait mode (no collapsing image exists), the status bar also needs dark coloring
            mHeaderImageActive = false;

            // Get the primary color of the active theme from the helper.
            getWindow().setStatusBarColor(ThemeUtils.getThemeColor(this, R.attr.colorPrimaryDark));
        }
        // set scrolling behaviour
        CollapsingToolbarLayout toolbar = findViewById(R.id.collapsing_toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.height = -1;

        if (scrollingEnabled && !showImage) {
            toolbar.setTitleEnabled(false);
            setTitle(title);

            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL + AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED + AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else if (!scrollingEnabled && showImage && collapsingImage != null) {
            toolbar.setTitleEnabled(true);
            toolbar.setTitle(title);


            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED + AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        } else {
            toolbar.setTitleEnabled(false);
            setTitle(title);
            params.setScrollFlags(0);
        }
    }

    public void setupToolbarImage(Bitmap bm) {
        ImageView collapsingImage = findViewById(R.id.collapsing_image);
        if (collapsingImage != null) {
            collapsingImage.setImageBitmap(bm);
            
            // FIXME DIRTY HACK: Manually fix the toolbar size to the screen width
            CollapsingToolbarLayout toolbar = findViewById(R.id.collapsing_toolbar);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

            params.height = getWindow().getDecorView().getMeasuredWidth();

            // Always expand the toolbar to show the complete image
            AppBarLayout appbar = findViewById(R.id.appbar);
            appbar.setExpanded(true,false);
        }
    }

    @Override
    public void onPlaylistSelected(int position) {

    }


    @Override
    public void openPath(String path) {
        // Create fragment and give it an argument for the selected directory
        FilesFragment newFragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(FilesFragment.EXTRA_FILENAME, path);

        newFragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

        newFragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        newFragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));

        transaction.addToBackStack("FilesFragment" + path);
        transaction.replace(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void showAlbumsForPath(String path) {
        if (mNowPlayingDragStatus == DRAG_STATUS.DRAGGED_UP) {
            NowPlayingView nowPlayingView = findViewById(R.id.now_playing_layout);
            if (nowPlayingView != null) {
                View coordinatorLayout = findViewById(R.id.main_coordinator_layout);
                coordinatorLayout.setVisibility(View.VISIBLE);
                nowPlayingView.minimize();
            }
        }
        // Create fragment and give it an argument for the selected article
        AlbumsFragment newFragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putString(AlbumsFragment.BUNDLE_STRING_EXTRA_PATH, path);


        newFragment.setArguments(args);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        newFragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        newFragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));
        // Replace whatever is in the fragment_container view with this
        // fragment,
        // and add the transaction to the back stack so the user can navigate
        // back
        transaction.replace(R.id.fragment_container, newFragment, AlbumsFragment.TAG);
        transaction.addToBackStack("DirectoryAlbumsFragment");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_library);

        // Commit the transaction
        transaction.commit();
    }

    public void setNavbarHeader(String text) {
        TextView header = findViewById(R.id.navdrawer_header_text);
        if (header == null) {
            return;
        }

        if (text == null) {
            header.setText("");
        }
        header.setText(text);
    }

    @Override
    public void openArtworkSettings() {
        // Create fragment and give it an argument for the selected directory
        ArtworkSettingsFragment newFragment = new ArtworkSettingsFragment();


        FragmentManager fragmentManager = getSupportFragmentManager();

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

        newFragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        newFragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));

        transaction.addToBackStack("ArtworkSettingsFragment");
        transaction.replace(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    private MyMusicTabsFragment.DEFAULTTAB getDefaultTab() {
        // Read default view preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultView = sharedPref.getString(getString(R.string.pref_start_view_key), getString(R.string.pref_view_default));

        // the default tab for mymusic
        MyMusicTabsFragment.DEFAULTTAB defaultTab = MyMusicTabsFragment.DEFAULTTAB.ALBUMS;

        if (defaultView.equals(getString(R.string.pref_view_my_music_artists_key))) {
            defaultTab = MyMusicTabsFragment.DEFAULTTAB.ARTISTS;
        } else if (defaultView.equals(getString(R.string.pref_view_my_music_albums_key))) {
            defaultTab = MyMusicTabsFragment.DEFAULTTAB.ALBUMS;
        }

        return defaultTab;
    }

    private int getDefaultViewID() {
        // Read default view preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultView = sharedPref.getString(getString(R.string.pref_start_view_key), getString(R.string.pref_view_default));

        // the nav resource id to mark the right item in the nav drawer
        int navId = -1;

        if (defaultView.equals(getString(R.string.pref_view_my_music_artists_key))) {
            navId = R.id.nav_library;
        } else if (defaultView.equals(getString(R.string.pref_view_my_music_albums_key))) {
            navId = R.id.nav_library;
        } else if (defaultView.equals(getString(R.string.pref_view_playlists_key))) {
            navId = R.id.nav_saved_playlists;
        } else if (defaultView.equals(getString(R.string.pref_view_files_key))) {
            navId = R.id.nav_files;
        }

        return navId;
    }

    @Override
    public void onCancel() {
        finalSelectedTracks.clear();
    }

    @Override
    public void onDone() {
        Log.d("onDone",finalSelectedTracks.size()+" selected");
        if (finalSelectedTracks.size() == 0) {
            finalSelectedTracks.clear();
            onBackPressed();
        } else {
            newPlaylistNameDialog();
        }
    }

    public void newPlaylistNameDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_image_dialog);

        TextView titleText = (TextView) dialog.findViewById(R.id.dialog_title);
        titleText.setText("Playlist Name");
        Button btn = (Button) dialog.findViewById(R.id.save_image_btn);
        final EditText newName = (EditText) dialog.findViewById(R.id.save_image_filename_text);

        CheckBox cb = (CheckBox) dialog.findViewById(R.id.text_checkbox);
        cb.setVisibility(GONE);

        btn.setBackgroundColor(themeColor);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNameRepeat = false;
                if (newName.getText().toString().trim().equals("")) {
                    newName.setError("Enter Playlist Name!");
                } else {
                    for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
                        if (newName.getText().toString().equals(allPlaylists.getPlaylists().get(i).getPlaylistName())) {
                            isNameRepeat = true;
                            newName.setError("Playlist with same name exists!");
                            break;
                        }
                    }
                    if (!isNameRepeat) {
                        UnifiedTrack ut;
                        Playlist pl = new Playlist(newName.getText().toString());
                        for (int i = 0; i < finalSelectedTracks.size(); i++) {
                            ut = new UnifiedTrack(true, finalSelectedTracks.get(i), null);
                            pl.getSongList().add(ut);
                        }
                        allPlaylists.addPlaylist(pl);
                        finalSelectedTracks.clear();
                        if (pAdapter != null) {
                            pAdapter.notifyDataSetChanged();
                            if (allPlaylists.getPlaylists().size() > 0) {
                                playlistsRecycler.setVisibility(View.VISIBLE);
                                playlistNothingText.setVisibility(View.INVISIBLE);
                            }
                        }
                        /*PlayList plFrag = (PlayList) fragMan.findFragmentByTag("allPlaylists");
                        if (plFrag != null) {
                            plFrag.dataChanged();
                        }*/
                        //new SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        dialog.dismiss();
                        try {
                            transferPlaylist(pl.getSongList());
                        } catch (IOException e) {
                            Log.d("error",e.getMessage());
                        }
                        onBackPressed();
                    }
                }
            }
        });

        dialog.show();

    }

    public static class SavePlaylists extends AsyncTask<Void, Void, Void> {

        private boolean isSavePLaylistsRunning;

        @Override
        protected Void doInBackground(Void... params) {
            if (!isSavePLaylistsRunning) {
                isSavePLaylistsRunning = true;
                try {
                    String json2 = gson.toJson(allPlaylists);
                    prefsEditor.putString("allPlaylists", json2);
                } catch (Exception e) {

                }
                isSavePLaylistsRunning = false;
            }
            return null;
        }
    }


    public void transferPlaylist(final List<UnifiedTrack> playList) throws IOException {
        final Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    Log.d("Connecting to server","...");
                    for (UnifiedTrack t :playList) {
                        Socket sock = new Socket(HOST, 13267);
                        OutputStream os = sock.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        Log.d("Path",t.getLocalTrack().getPath());
                        // sendfile
                        File myFile = new File(t.getLocalTrack().getPath());
                        byte[] mybytearray = new byte[(int) myFile.length()];

                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        DataInputStream dis = new DataInputStream(bis);
                        dis.readFully(mybytearray, 0, mybytearray.length);


                        // os.write(mybytearray, 0, mybytearray.length);

                        // os.flush();


                        dos.writeUTF(myFile.getName());
                        dos.writeLong(mybytearray.length);
                        dos.write(mybytearray, 0, mybytearray.length);
                        dos.flush();
                        sock.close();
                    }

                } catch (Exception e) {
                    Log.e("Exception","error in transfer");
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    private void getLocalSongs() {

        localTrackList.clear();
        //recentlyAddedTrackList.clear();
        //finalLocalSearchResultList.clear();
        //finalRecentlyAddedTrackSearchResultList.clear();
        albums.clear();
        finalAlbums.clear();
        artists.clear();
        finalArtists.clear();

        ContentResolver musicResolver = this.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, MediaStore.MediaColumns.DATE_ADDED + " DESC");

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            int pathColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbum = musicCursor.getString(albumColumn);
                String path = musicCursor.getString(pathColumn);
                long duration = musicCursor.getLong(durationColumn);
                if (duration > 10000) {
                    LocalTrack lt = new LocalTrack(thisId, thisTitle, thisArtist, thisAlbum, path, duration);
                    localTrackList.add(lt);
                    /*finalLocalSearchResultList.add(lt);
                    if (recentlyAddedTrackList.size() <= 50) {
                        recentlyAddedTrackList.add(lt);
                        finalRecentlyAddedTrackSearchResultList.add(lt);
                    }*/

                    int pos;
                    if (thisAlbum != null) {
                        pos = checkAlbum(thisAlbum);
                        if (pos != -1) {
                            albums.get(pos).getAlbumSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Album ab = new Album(thisAlbum, llt);
                            albums.add(ab);
                        }
                        if (pos != -1) {
                            finalAlbums.get(pos).getAlbumSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Album ab = new Album(thisAlbum, llt);
                            finalAlbums.add(ab);
                        }
                    }

                    if (thisArtist != null) {
                        pos = checkArtist(thisArtist);
                        if (pos != -1) {
                            artists.get(pos).getArtistSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Artist ab = new Artist(thisArtist, llt);
                            artists.add(ab);
                        }
                        if (pos != -1) {
                            finalArtists.get(pos).getArtistSongs().add(lt);
                        } else {
                            List<LocalTrack> llt = new ArrayList<>();
                            llt.add(lt);
                            Artist ab = new Artist(thisArtist, llt);
                            finalArtists.add(ab);
                        }
                    }

                    File f = new File(path);
                    String dirName = f.getParentFile().getName();
                    if (getFolder(dirName) == null) {
                        MusicFolder mf = new MusicFolder(dirName);
                        mf.getLocalTracks().add(lt);
                        //allMusicFolders.getMusicFolders().add(mf);
                    } else {
                        getFolder(dirName).getLocalTracks().add(lt);
                    }
                }

            }
            while (musicCursor.moveToNext());
        }

        if (musicCursor != null)
            musicCursor.close();

       /* System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        try {
            if (localTrackList.size() > 0) {
                Collections.sort(localTrackList, new LocalMusicComparator());
                Collections.sort(finalLocalSearchResultList, new LocalMusicComparator());
            }
            if (albums.size() > 0) {
                Collections.sort(albums, new AlbumComparator());
                Collections.sort(finalAlbums, new AlbumComparator());
            }
            if (artists.size() > 0) {
                Collections.sort(artists, new ArtistComparator());
                Collections.sort(finalArtists, new ArtistComparator());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        List<UnifiedTrack> tmp = new ArrayList<>();
        boolean queueCurrentIndexCollision = false;
        int indexCorrection = 0;

        tmp.clear();
        List<UnifiedTrack> temp = new ArrayList<>();
        List<Playlist> tmpPL = new ArrayList<>();

        for (int i = 0; i < allPlaylists.getPlaylists().size(); i++) {
            Playlist pl = allPlaylists.getPlaylists().get(i);
            for (int j = 0; j < pl.getSongList().size(); j++) {
                UnifiedTrack ut = pl.getSongList().get(j);
                if (ut.getType()) {
                    if (!checkTrack(ut.getLocalTrack())) {
                        temp.add(ut);
                    }
                }
            }
            for (int j = 0; j < temp.size(); j++) {
                pl.getSongList().remove(temp.get(j));
            }
            temp.clear();
            if (pl.getSongList().size() == 0) {
                tmpPL.add(pl);
            }
        }
        for (int i = 0; i < tmpPL.size(); i++) {
            allPlaylists.getPlaylists().remove(tmpPL.get(i));
        }
        tmpPL.clear();
    }
    public boolean checkTrack(LocalTrack lt) {
        for (int i = 0; i < localTrackList.size(); i++) {
            LocalTrack localTrack = localTrackList.get(i);
            if (localTrack.getTitle().equals(lt.getTitle())) {
                return true;
            }
        }
        return false;
    }

    public int checkAlbum(String album) {
        for (int i = 0; i < albums.size(); i++) {
            Album ab = albums.get(i);
            if (ab.getName().equals(album)) {
                return i;
            }
        }
        return -1;
    }

    public int checkArtist(String artist) {
        for (int i = 0; i < artists.size(); i++) {
            Artist at = artists.get(i);
            if (at.getName().equals(artist)) {
                return i;
            }
        }
        return -1;
    }

    public MusicFolder getFolder(String folderName) {
        MusicFolder mf = null;
        for (int i = 0; i < allMusicFolders.getMusicFolders().size(); i++) {
            MusicFolder mf1 = allMusicFolders.getMusicFolders().get(i);
            if (mf1.getFolderName().equals(folderName)) {
                mf = mf1;
                break;
            }
        }
        return mf;
    }
}
