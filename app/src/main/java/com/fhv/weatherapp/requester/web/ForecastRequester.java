package com.fhv.weatherapp.requester.web;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ForecastRequester {
    private static final String TAG = "ForecastRequester";
    private static final String URL = "https://api.darksky.net/forecast/285d2f7ba2b4c6a886a3356737900fb9/{latitude},{longitude}?units=si&lang={locale}";
    private final Context context;
    private final RequestQueue queue;

    public ForecastRequester(final Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    public void request(final VolleyCallback callback) {
        final String latitude = "51.750000";    //fixme - get it from somewhere!
        final String longitude = "19.466670";
        final String locale = context.getResources().getConfiguration().locale.getCountry().toLowerCase();
        final String replacedUrl = URL
                .replace("{latitude}", latitude)
                .replace("{longitude}", longitude)
                .replace("{locale}", locale);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                replacedUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }
}
