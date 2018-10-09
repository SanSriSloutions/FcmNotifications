package com.fcmdemo1.valuelabs.fcmdemo1.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcmdemo1.valuelabs.fcmdemo1.utils.CommonUtils;
import com.fcmdemo1.valuelabs.fcmdemo1.utils.Constants;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Singleton class for creating the instance of  Retrofit
 * <p>
 * Created by Krishna.Vasamsetti on 05/30/2018.
 */
public class RetrofitManager {
    public static final int SESSION_EXPIRED = 401;
    public static final int TOKEN_INVALID = 403;

    private static Retrofit retrofit = null;
    private static final String TAG = RetrofitManager.class.getSimpleName();

    /**
     * This method is used to get the retrofit client object.
     *
     * @return which returns the retrofit client object.
     */
    public static Retrofit getClient() {
        return retrofit;
    }

    /**
     * This method is used to create the Retrofit client object.
     *
     * @param context context of the application
     */
    public static void createClient(final Context context) {
        if (retrofit == null) {
            retrofit = getRetrofitCustomClient(context, true);
        }
    }

    /**
     * This method is used to get the retrofit client object.
     *
     * @param context      context of the object. Note: <b>Don't use activity context</b>
     * @param isLogEnabled debug logs enabling flag.
     * @return Which returns the retrofit client object.
     */
    /*public static Retrofit getClient(final Context context, boolean isLogEnabled) {
        return getRetrofitCustomClient(context, isLogEnabled);
    }*/

    /**
     * This method is used to create the retrofit client reference.
     *
     * @param context      context of the application
     * @param isLogEnabled debug logs enabling flag
     * @return which returns the retrofit client object.
     */
    private static Retrofit getRetrofitCustomClient(final Context context, boolean isLogEnabled) {

        if (context instanceof Activity) {
            throw new RuntimeException(" Please use Context reference instead of Activity reference");
        }

        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.cache(new Cache(httpCacheDirectory, 10 * 1024 * 1024)); // 10 MB
        okHttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                if (!chain.request().url().encodedPath().contains("TermsAndConditions")) {
                    builder.header("AuthenticationToken", "")
                            .header("Content-type", "application/json")
                            .header("UserId", "111")
                            .header("AppSource", "android_" + "1.0")
                            .header("MobileLanguage",  "1");
                }
                Request request = builder.build();
                okhttp3.Response response = chain.proceed(request);


                //TODO: For handing the session expired case here

                if (response.code() == SESSION_EXPIRED) {
                    Toast.makeText(context,"Session Expired",Toast.LENGTH_LONG).show();
                    /*if (PrefUtils.isRememberedMe(context)) {
                        return getResponseOnSessionExpiry(context, chain, request);
                    } else {

                    }*/
                    return response;
                }

                //TODO: For handing the invalid token case here
                if (response.code() == TOKEN_INVALID) {
                   // PrefUtils.setLogin(context, false);
                    //goToLogin(context, true);
                    return response;
                }

                response = processEveryRequest(context, response);
                return response;
            }
        });

        if (isLogEnabled) {
        //    if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpBuilder.addInterceptor(logging);

                CommonUtils.addStethoNetworkInterceptor(okHttpBuilder);
          //  }
        }
        okHttpBuilder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpBuilder.readTimeout(180, TimeUnit.SECONDS);
        okHttpBuilder.writeTimeout(830 * 1000, TimeUnit.MILLISECONDS);

        ObjectMapper mapper = getObjectMapper();
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpBuilder.build())
                .build();
    }

    /**
     * This method is used to update the server time on every API request.
     *
     * @param context  context of the application for updating the serverTime on preference.
     * @param response response of the request.
     * @return which process the request and updates the server time and re-build of the response will be returned.
     */
    private static Response processEveryRequest(Context context, Response response) {
        if (response != null) {
            try {
                if (response.body() != null) {
                    String rawResponse = response.body().string();

                    try {
                        isRunningOnMainThread();
                        JSONObject rawObject = new JSONObject(rawResponse);
                        if (rawObject.has("StatusResponse")) {
                            JSONObject statusResponse = rawObject.getJSONObject("StatusResponse");
                            long serverTime = statusResponse.getLong("ServerTime");
                            Log.d(TAG, "serverTimeOnRequest: " + serverTime);
                           // PrefUtils.setServerTime(context, serverTime);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Re-create the response before returning it because body can be read only once
                    return response.newBuilder().body(ResponseBody.create(response.body().contentType(), rawResponse)).build();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private static boolean isRunningOnMainThread() {
        boolean isInMainThread = Looper.getMainLooper() == Looper.myLooper();
        Log.d(TAG, "isRunningOnMainThread: " + (isInMainThread ? " MainThread" : "WorkerThread"));
        return isInMainThread;
    }

    @NonNull
    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
