

package com.mtdev.musicbox.application.fragments.serverfragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.OutputAdapter;
import com.mtdev.musicbox.application.loaders.OutputsLoader;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDCommandHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;

import java.util.List;

public class OutputsFragment extends GenericMPDFragment<List<MPDOutput>> implements  AbsListView.OnItemClickListener{
    public final static String TAG = OutputsFragment.class.getSimpleName();
    /**
     * Main ListView of this fragment
     */
    private ListView mListView;

    private OutputAdapter mAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.listview_layout, container, false);

        // Get the main ListView of this fragment
        mListView = rootView.findViewById(R.id.main_listview);


        // Create the needed adapter for the ListView
        mAdapter = new OutputAdapter(getActivity());

        // Combine the two to a happy couple
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        setHasOptionsMenu(true);

        // Return the ready inflated and configured fragment view.
        return rootView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MPDOutput output = (MPDOutput)mAdapter.getItem(position);
        MPDCommandHandler.toggleOutput(output.getID());
        mAdapter.setOutputActive(position,!output.getOutputState());
    }

    @Override
    public Loader<List<MPDOutput>> onCreateLoader(int id, Bundle args) {
        return new OutputsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<MPDOutput>> loader, List<MPDOutput> data) {
        mAdapter.swapModel(data);
    }

    @Override
    public void onLoaderReset(Loader<List<MPDOutput>> loader) {
        mAdapter.swapModel(null);
    }

}
