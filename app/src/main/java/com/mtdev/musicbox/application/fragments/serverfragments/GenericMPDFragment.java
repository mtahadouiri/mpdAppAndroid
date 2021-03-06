

package com.mtdev.musicbox.application.fragments.serverfragments;


import android.app.Activity;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;

import com.mtdev.musicbox.mpdservice.handlers.MPDConnectionStateChangeHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDInterface;

import java.lang.ref.WeakReference;

public abstract class GenericMPDFragment<T extends Object> extends DialogFragment implements LoaderManager.LoaderCallbacks<T> {
    private static final String TAG = GenericMPDFragment.class.getSimpleName();

    protected ConnectionStateListener mConnectionStateListener;

    protected SwipeRefreshLayout mSwipeRefreshLayout = null;

    protected GenericMPDFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContent();
        Activity activity = getActivity();
        if (activity != null) {
            mConnectionStateListener = new ConnectionStateListener(this, activity.getMainLooper());
            MPDInterface.mInstance.addMPDConnectionStateChangeListener(mConnectionStateListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        synchronized (this) {
            getLoaderManager().destroyLoader(0);
            MPDInterface.mInstance.removeMPDConnectionStateChangeListener(mConnectionStateListener);
            mConnectionStateListener = null;
        }
    }


    protected void refreshContent() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }
        if ( !isDetached()) {
            getLoaderManager().restartLoader(0, getArguments(), this);
        }
    }


    private static class ConnectionStateListener extends MPDConnectionStateChangeHandler {
        private WeakReference<GenericMPDFragment> pFragment;

        public ConnectionStateListener(GenericMPDFragment fragment, Looper looper) {
            super(looper);
            pFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onConnected() {
            pFragment.get().refreshContent();
        }

        @Override
        public void onDisconnected() {
            GenericMPDFragment fragment = pFragment.get();
            if(fragment == null) {
                    return;
            }
            synchronized (fragment) {
                if (!fragment.isDetached()) {
                    if(fragment.getLoaderManager().hasRunningLoaders()) {
                        fragment.getLoaderManager().destroyLoader(0);
                        fragment.finishedLoading();
                    }
                }
            }
        }
    }

    private void finishedLoading() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
        }
    }

    /**
     * Called when the loader finished loading its data.
     * <p/>
     * The refresh indicator will be stopped if a refreshlayout exists.
     *
     * @param loader The used loader itself
     * @param model  Data of the loader
     */
    @Override
    public void onLoadFinished(Loader<T> loader, T model) {
        finishedLoading();
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        finishedLoading();
    }

    /**
     * Method to apply a filter to the view model of the fragment.
     * <p/>
     * This method must be overridden by the subclass.
     */
    public void applyFilter(String filter) {
        throw new IllegalStateException("filterView hasn't been implemented in the subclass");
    }

    /**
     * Method to remove a previous set filter.
     * <p/>
     * This method must be overridden by the subclass.
     */
    public void removeFilter() {
        throw new IllegalStateException("removeFilter hasn't been implemented in the subclass");
    }
}
