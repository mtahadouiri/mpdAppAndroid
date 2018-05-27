

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.mtdev.musicbox.mpdservice.profilemanagement.MPDProfileManager;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDServerProfile;

import java.util.List;

public class ProfilesLoader extends AsyncTaskLoader<List<MPDServerProfile>> {
    public ProfilesLoader(Context context) {
        super(context);
    }

    @Override
    public List<MPDServerProfile> loadInBackground() {
        return MPDProfileManager.getInstance(getContext()).getProfiles();
    }

    /**
     * Start loading the data.
     * A previous load dataset will be ignored
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Stop the loader and cancel the current task.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
