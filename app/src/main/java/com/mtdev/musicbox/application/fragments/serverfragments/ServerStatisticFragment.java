

package com.mtdev.musicbox.application.fragments.serverfragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.utils.FormatHelper;
import com.mtdev.musicbox.mpdservice.handlers.MPDStatusChangeHandler;
import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseServerStatistics;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDStateMonitoringHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDCapabilities;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDInterface;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDCurrentStatus;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDStatistics;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDTrack;

import java.lang.ref.WeakReference;

public class ServerStatisticFragment extends Fragment {
    public final static String TAG = ServerStatisticFragment.class.getSimpleName();

    private TextView mArtistCount;
    private TextView mAlbumsCount;
    private TextView mSongsCount;

    private TextView mUptime;
    private TextView mPlaytime;
    private TextView mLastUpdate;
    private TextView mDBLength;

    private TextView mDBUpdating;

    private TextView mServerFeatures;

    private ServerStatusHandler mServerStatusHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_server_statistic, container, false);

        mArtistCount = rootView.findViewById(R.id.server_statistic_artist_count);
        mAlbumsCount = rootView.findViewById(R.id.server_statistic_albums_count);
        mSongsCount = rootView.findViewById(R.id.server_statistic_songs_count);

        mUptime = rootView.findViewById(R.id.server_statistic_server_uptime);
        mPlaytime = rootView.findViewById(R.id.server_statistic_server_playtime);
        mLastUpdate = rootView.findViewById(R.id.server_statistic_db_update);
        mDBLength = rootView.findViewById(R.id.server_statistic_db_playtime);

        mDBUpdating = rootView.findViewById(R.id.server_statistic_updateing_db);

        mServerFeatures = rootView.findViewById(R.id.server_statistic_malp_server_information);

        rootView.findViewById(R.id.server_statistic_update_db_btn).setOnClickListener(new DBUpdateBtnListener());

        mServerStatusHandler = new ServerStatusHandler(this);

        // Return the ready inflated and configured fragment view.
        return rootView;
    }

    /**
     * Attaches callbacks
     */
    @Override
    public void onResume() {
        super.onResume();

        MPDQueryHandler.getStatistics(new StatisticResponseHandler());

        MPDStateMonitoringHandler.getHandler().registerStatusListener(mServerStatusHandler);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();

        MPDStateMonitoringHandler.getHandler().unregisterStatusListener(mServerStatusHandler);
    }

    private synchronized void showDatabaseUpdating(final boolean show) {
        Activity activity = getActivity();
        if (null == activity) {
            return;
        }
        activity.runOnUiThread(() -> {
            if (show) {
                mDBUpdating.setVisibility(View.VISIBLE);
            } else {
                mDBUpdating.setVisibility(View.GONE);
            }
        });

    }


    private class StatisticResponseHandler extends MPDResponseServerStatistics {

        @Override
        public void handleStatistic(MPDStatistics statistics) {
            mArtistCount.setText(String.valueOf(statistics.getArtistsCount()));
            mAlbumsCount.setText(String.valueOf(statistics.getAlbumCount()));
            mSongsCount.setText(String.valueOf(statistics.getSongCount()));

            // Context could be null already because of asynchronous back call
            Context context = getContext();
            if (context != null) {
                mUptime.setText(FormatHelper.formatTracktimeFromSWithDays(statistics.getServerUptime(), context));
                mPlaytime.setText(FormatHelper.formatTracktimeFromSWithDays(statistics.getPlayDuration(), context));
                mDBLength.setText(FormatHelper.formatTracktimeFromSWithDays(statistics.getAllSongDuration(), context));
            }

            mLastUpdate.setText(FormatHelper.formatTimeStampToString(statistics.getLastDBUpdate() * 1000));

            MPDCapabilities capabilities = MPDInterface.mInstance.getServerCapabilities();
            if (null != capabilities) {
                mServerFeatures.setText(capabilities.getServerFeatures());
            }
        }
    }

    private class DBUpdateBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Update the whole database => no path
            MPDQueryHandler.updateDatabase("");
        }
    }

    private static class ServerStatusHandler extends MPDStatusChangeHandler {
        WeakReference<ServerStatisticFragment> mFragment;

        public ServerStatusHandler(ServerStatisticFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }


        @Override
        protected void onNewStatusReady(MPDCurrentStatus status) {
            mFragment.get().showDatabaseUpdating(status.getUpdateDBJob() >= 0);
        }

        @Override
        protected void onNewTrackReady(MPDTrack track) {

        }
    }
}
