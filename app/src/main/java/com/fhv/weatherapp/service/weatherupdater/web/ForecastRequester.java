package com.fhv.weatherapp.service.weatherupdater.web;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastRequester {
    private static final String TAG = "ForecastRequester";
    //    private static final String URL = "https://api.darksky.net/forecast/285d2f7ba2b4c6a886a3356737900fb9/{latitude},{longitude}?units=si&lang={locale}";
    private static final String URL = "https://api.darksky.net/forecast/285d2f7ba2b4c6a886a3356737900fb9/{latitude},{longitude}?units=si";
    private final Context context;

    public ForecastRequester(final Context context) {
        this.context = context;
    }

    public String request() throws ForecastRequestException {
        final String latitude = "51.750000";    //fixme - get it from somewhere!
        final String longitude = "19.466670";
        final String locale = context.getResources().getConfiguration().locale.getCountry().toLowerCase();
        final String replacedUrl = URL
                .replace("{latitude}", latitude)
                .replace("{longitude}", longitude);
//                .replace("{locale}", locale);
        Log.d(TAG, "Update weather http request url:");
        Log.d(TAG, replacedUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(replacedUrl)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (Exception e) {
            throw new ForecastRequestException(e);
        }

    }
}
