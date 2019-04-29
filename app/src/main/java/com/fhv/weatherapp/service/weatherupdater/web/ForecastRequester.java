package com.fhv.weatherapp.service.weatherupdater.web;

import android.content.Context;
import android.util.Log;

import com.fhv.weatherapp.model.CurrentLocation;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastRequester {
    private static final String TAG = "ForecastRequester";
    private static final String URL = "https://api.darksky.net/forecast/285d2f7ba2b4c6a886a3356737900fb9/{latitude},{longitude}?units=si&lang={locale}";
    private static final String URL_NO_LOCALE = "https://api.darksky.net/forecast/285d2f7ba2b4c6a886a3356737900fb9/{latitude},{longitude}?units=si";
    private final Context context;

    public ForecastRequester(final Context context) {
        this.context = context;
    }

    public String request(CurrentLocation location) throws ForecastRequestException {
        final String latitude = String.valueOf(location.getLat());
        final String longitude = String.valueOf(location.getLng());
        final String locale = context.getResources().getConfiguration().locale.getLanguage().toLowerCase();
        final String replacedUrl = URL
                .replace("{latitude}", latitude)
                .replace("{longitude}", longitude)
                .replace("{locale}", locale);
        Log.d(TAG, "Update weather http request url:");
        Log.d(TAG, replacedUrl);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(replacedUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "Response code " + response.code());
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                final String resp = response.body().string();
                Log.w(TAG, "Request was not successful");
                Log.w(TAG, resp);

                if (didNotRecogniseLocale(resp)) {
                    return retryWithoutLocale(latitude, longitude);
                } else {
                    throw new ForecastRequestException(resp);
                }
            }
        } catch (Exception e) {
            throw new ForecastRequestException(e);
        }
    }

    private boolean didNotRecogniseLocale(final String response) {
        return response.contains("An invalid lang parameter was provided.");
    }

    private String retryWithoutLocale(final String latitude, final String longitude) throws ForecastRequestException {
        Log.d(TAG, "Retrying without locale");
        final String replacedNoLocale = URL_NO_LOCALE
                .replace("{latitude}", latitude)
                .replace("{longitude}", longitude);
        Log.d(TAG, "Update weather http request url:");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(replacedNoLocale)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "Request status " + response.code() + " " + response.message());
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new ForecastRequestException(response.body().string());
            }
        } catch (Exception e) {
            throw new ForecastRequestException(e);
        }
    }
}
