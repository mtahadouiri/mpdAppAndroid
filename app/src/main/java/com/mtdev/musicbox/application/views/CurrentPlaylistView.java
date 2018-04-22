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

package com.mtdev.musicbox.application.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.CurrentPlaylistAdapter;
import com.mtdev.musicbox.application.utils.ScrollSpeedListener;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDCommandHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.application.adapters.CurrentPlaylistAdapter;

public class CurrentPlaylistView extends LinearLayout implements AdapterView.OnItemClickListener {
    Context mContext;


    /**
     * Adapter used by the ListView
     */
    private CurrentPlaylistAdapter mPlaylistAdapter;

    public CurrentPlaylistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Inflate the layout for this fragment
        LayoutInflater.from(context).inflate(R.layout.listview_layout, this, true);

        // Get the main ListView of this fragment
        ListView listView = this.findViewById(R.id.main_listview);

        // Create the needed adapter for the ListView
        mPlaylistAdapter = new CurrentPlaylistAdapter(getContext(), listView);

        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new ScrollSpeedListener(mPlaylistAdapter, listView));

        // Return the ready inflated and configured fragment view.
        mContext = context;
    }

    /**
     * Play the selected track.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MPDCommandHandler.playSongIndex(position);
    }

    public void onResume() {
        mPlaylistAdapter.onResume();
    }

    public void onPause() {
        mPlaylistAdapter.onPause();
    }

    public MPDFileEntry getItem(int position) {
        if (null != mPlaylistAdapter ) {
            return (MPDFileEntry)mPlaylistAdapter.getItem(position);
        }
        return null;
    }

    public CurrentPlaylistAdapter.VIEW_TYPES getItemViewType(int position) {
        return CurrentPlaylistAdapter.VIEW_TYPES.values()[mPlaylistAdapter.getItemViewType(position)];
    }

    public void removeAlbumFrom(int position) {
        mPlaylistAdapter.removeAlbumFrom(position);
    }

    /**
     * Triggers a jump to the currently playing song. Not animated.
     */
    public void jumpToCurrentSong() {
        mPlaylistAdapter.jumpToCurrent();
    }

}
