package com.movierr.movit.Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Class to handle all the connection stuff (Volley networking).
 */
public class Connection {
    private Context context;
    private String url;
    private JSONObject object;

    public Connection(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    /**
     * @return The JSONObject representing the response
     *         from the url supplied in the constructor.
     *         Return Null if any error occurred.
     */
    public JSONObject getResponseObject() {
        RequestQueue q = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        setJsonReturnObject(object);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // do nothing... yet.
                        // if returned obj from this outer method is null,
                        // consider it an error.
                    }
                });
        q.add(request);
        return this.object;
    }

    /**
     * Sets the this.object to our response object.
     * @param object The response JSON object.
     */
    private void setJsonReturnObject(JSONObject object) {
        this.object = object;
    }
}
