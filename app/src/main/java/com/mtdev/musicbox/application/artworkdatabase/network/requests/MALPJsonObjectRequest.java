

package com.mtdev.musicbox.application.artworkdatabase.network.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MALPJsonObjectRequest extends JsonObjectRequest{
    public MALPJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders(){
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", "Application MALP/1.0 (https://github.com/gateship-one/malp)");
        return headers;
    }
}
