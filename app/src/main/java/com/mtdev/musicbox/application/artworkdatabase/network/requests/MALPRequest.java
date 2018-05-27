

package com.mtdev.musicbox.application.artworkdatabase.network.requests;


import com.android.volley.Request;
import com.android.volley.Response;

import com.mtdev.musicbox.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public abstract class MALPRequest<T> extends Request<T> {


    public MALPRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    public Map<String, String> getHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", "Application MALP/" + BuildConfig.VERSION_NAME + " (https://github.com/gateship-one/malp)");
        return headers;
    }
}
