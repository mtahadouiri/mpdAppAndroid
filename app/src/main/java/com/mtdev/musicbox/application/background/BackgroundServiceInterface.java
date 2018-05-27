

package com.mtdev.musicbox.application.background;

import android.os.Message;
import android.os.RemoteException;

import java.lang.ref.WeakReference;

/**
 * Interface to handle requests over the {@link android.content.ServiceConnection}.
 * Start/stop of stream playback is handled over an extra handler to ensure handling
 * in the right thread. Otherwise it will fail.
 */
public class BackgroundServiceInterface extends IBackgroundService.Stub {
    WeakReference<BackgroundService> mService;

    public BackgroundServiceInterface(BackgroundService service) {
        mService = new WeakReference<>(service);
    }

    @Override
    public void stopStreamingPlayback() throws RemoteException {
        Message msg = mService.get().getHandler().obtainMessage();
        msg.obj = new BackgroundServiceHandler.HandlerAction(BackgroundServiceHandler.HANDLER_ACTION_TYPE.ACTION_STOP_STREAMING);
        mService.get().getHandler().sendMessage(msg);
    }

    @Override
    public void startStreamingPlayback() throws RemoteException {
        Message msg = mService.get().getHandler().obtainMessage();
        msg.obj = new BackgroundServiceHandler.HandlerAction(BackgroundServiceHandler.HANDLER_ACTION_TYPE.ACTION_START_STREAMING);
        mService.get().getHandler().sendMessage(msg);
    }

    @Override
    public int getStreamingStatus() throws RemoteException {
        return mService.get().getStreamingStatus();
    }
}
