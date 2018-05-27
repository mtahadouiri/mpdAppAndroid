

package com.mtdev.musicbox.application.loaders;


import android.content.Context;
import android.support.v4.content.Loader;

import com.mtdev.musicbox.mpdservice.handlers.responsehandler.MPDResponseOutputList;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;

import java.lang.ref.WeakReference;
import java.util.List;


public class OutputsLoader extends Loader<List<MPDOutput>> {

    private OutputResponseHandler mOutputResponseHandler;


    public OutputsLoader(Context context) {
        super(context);
        mOutputResponseHandler = new OutputResponseHandler(this);
    }


    private static class OutputResponseHandler extends MPDResponseOutputList {
        private WeakReference<OutputsLoader> mOutputsLoader;

        private OutputResponseHandler(OutputsLoader loader) {
            mOutputsLoader = new WeakReference<>(loader);
        }

        @Override
        public void handleOutputs(List<MPDOutput> outputList) {
            OutputsLoader loader = mOutputsLoader.get();

            if (loader != null) {
               loader.deliverResult(outputList);
            }
        }
    }


    @Override
    public void onStartLoading() {
        forceLoad();
    }

    @Override
    public void onStopLoading() {

    }

    @Override
    public void onForceLoad() {
        MPDQueryHandler.getOutputs(mOutputResponseHandler);
    }
}
