

package com.mtdev.musicbox.application.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.CurrentPlaylistAdapter;
import com.mtdev.musicbox.application.utils.ScrollSpeedListener;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDCommandHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;

import java.util.Calendar;

import static com.mtdev.musicbox.application.activities.MainActivity.previous;

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
        SkipSong(position);
    }
    private void SkipSong(int position) {
        if(previous == null){
            //at least 20 minutes difference
            MPDCommandHandler.playSongIndex(position);
            previous = Calendar.getInstance();
            Toast.makeText(getContext(),"Song skipped , your next skip will be available in 15 minutes .",Toast.LENGTH_LONG).show();
        }else{
            Calendar now = Calendar.getInstance();
            long diff = now.getTimeInMillis() - previous.getTimeInMillis();
            if(diff >= 15 * 60 * 1000)
            {
                //at least 20 minutes difference
                MPDCommandHandler.playSongIndex(position);
                previous = Calendar.getInstance();
                Toast.makeText(getContext(),"Song skipped , your next skip will be available in 15 minutes .",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getContext(),"You already skipped a song, your next skip will be available in 15 minutes .",Toast.LENGTH_LONG).show();
            }
        }
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
