

package com.mtdev.musicbox.application.fragments.serverfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.FileAdapter;
import com.mtdev.musicbox.application.callbacks.OnSaveDialogListener;
import com.mtdev.musicbox.application.loaders.PlaylistsLoader;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDPlaylist;

import java.util.List;

public class ChoosePlaylistDialog extends GenericMPDFragment<List<MPDFileEntry>> {

    public static final String EXTRA_SHOW_NEW_ENTRY = "show_newentry";

    /**
     * Listener to save the bookmark
     */
    OnSaveDialogListener mSaveCallback;

    /**
     * Adapter used for the ListView
     */
    private FileAdapter mPlaylistsListViewAdapter;

    private boolean mShowNewEntry;


    public void setCallback(OnSaveDialogListener callback) {
        mSaveCallback = callback;
    }




    /**
     * This method creates a new loader for this fragment.
     *
     * @param id   The id of the loader
     * @param args Optional arguments
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<MPDFileEntry>> onCreateLoader(int id, Bundle args) {
        return new PlaylistsLoader(getActivity(),mShowNewEntry);
    }

    /**
     * Called when the loader finished loading its data.
     *
     * @param loader The used loader itself
     * @param data   Data of the loader
     */
    @Override
    public void onLoadFinished(Loader<List<MPDFileEntry>> loader, List<MPDFileEntry> data) {
        mPlaylistsListViewAdapter.swapModel(data);
    }

    /**
     * If a loader is reset the model data should be cleared.
     *
     * @param loader Loader that was resetted.
     */
    @Override
    public void onLoaderReset(Loader<List<MPDFileEntry>> loader) {
        mPlaylistsListViewAdapter.swapModel(null);
    }

    /**
     * Create the dialog to choose to override an existing bookmark or to create a new bookmark.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        if ( null != args ) {
            mShowNewEntry = args.getBoolean(EXTRA_SHOW_NEW_ENTRY);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mPlaylistsListViewAdapter = new FileAdapter(getActivity(), false, false);

        builder.setTitle(getString(R.string.dialog_choose_playlist)).setAdapter(mPlaylistsListViewAdapter, (dialog, which) -> {
            if ( null == mSaveCallback ) {
                return;
            }
            if (which == 0) {
                // open save dialog to create a new playlist
                mSaveCallback.onCreateNewObject();
            } else {
                // override existing playlist
                MPDPlaylist playlist = (MPDPlaylist) mPlaylistsListViewAdapter.getItem(which);
                String objectTitle = playlist.getPath();
                mSaveCallback.onSaveObject(objectTitle);
            }
        }).setNegativeButton(R.string.dialog_action_cancel, (dialog, id) -> {
            // User cancelled the dialog don't save object
            getDialog().cancel();
        });

        // Prepare loader ( start new one or reuse old )
        getLoaderManager().initLoader(0, getArguments(), this);

        // set divider
        AlertDialog dlg = builder.create();
        dlg.getListView().setDivider(new ColorDrawable(ThemeUtils.getThemeColor(getContext(),R.attr.malp_color_background_selected)));
        dlg.getListView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen.list_divider_size));

        return dlg;
    }
}
