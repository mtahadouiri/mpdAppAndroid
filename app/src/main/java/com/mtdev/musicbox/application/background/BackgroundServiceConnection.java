

package com.mtdev.musicbox.application.background;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * {@link ServiceConnection} class to allow connection between the UI and the BackgroundService.
 * This is necessary for control of the background service and query of the status.
 */
public class BackgroundServiceConnection implements ServiceConnection {

    private IBackgroundService mBackgroundService;

    /**
     * Context used for binding to the service
     */
    private Context mContext;

    private OnConnectionStatusChangedListener mListener;

    public BackgroundServiceConnection(Context context, OnConnectionStatusChangedListener listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * This initiates the connection to the PlaybackService by binding to it
     */
    public void openConnection() {
        Intent serviceStartIntent = new Intent(mContext, BackgroundService.class);
        mContext.bindService(serviceStartIntent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnects the connection by unbinding from the service (not needed anymore)
     */
    public void closeConnection() {
        mContext.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBackgroundService = IBackgroundService.Stub.asInterface(service);
        if(null != mListener) {
            mListener.onConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBackgroundService = null;
        if(null != mListener) {
            mListener.onDisconnected();
        }
    }

    public synchronized IBackgroundService getService() throws RemoteException {
        if (null != mBackgroundService) {
            return mBackgroundService;
        } else {
            throw new RemoteException();
        }
    }

    public interface OnConnectionStatusChangedListener {
        void onConnected();
        void onDisconnected();
    }
}
