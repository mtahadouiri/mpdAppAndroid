

package com.mtdev.musicbox.application.background;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BackgroundServiceHandler extends Handler {
    private static final String TAG = BackgroundServiceHandler.class.getSimpleName();

    public enum HANDLER_ACTION_TYPE {
        ACTION_START_STREAMING,
        ACTION_STOP_STREAMING
    }

    BackgroundService mService;

    public BackgroundServiceHandler(BackgroundService service) {
        mService = service;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Log.v(TAG,"Handle message: " + msg);

        HANDLER_ACTION_TYPE action = ((HandlerAction)msg.obj).getType();
        switch (action) {
            case ACTION_START_STREAMING:
                mService.startStreamingPlayback();
                break;
            case ACTION_STOP_STREAMING:
                mService.stopStreamingPlayback();
                break;
            default:
                return;
        }
    }

    public static class HandlerAction {
        private HANDLER_ACTION_TYPE mType;
        public HandlerAction(HANDLER_ACTION_TYPE type) {
            mType = type;
        }

        public HANDLER_ACTION_TYPE getType() {
            return mType;
        }
    }
}
