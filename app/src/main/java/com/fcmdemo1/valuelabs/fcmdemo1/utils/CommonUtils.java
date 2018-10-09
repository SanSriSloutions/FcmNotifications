package com.fcmdemo1.valuelabs.fcmdemo1.utils;

import com.fcmdemo1.valuelabs.fcmdemo1.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class CommonUtils {
    public static void addStethoNetworkInterceptor(OkHttpClient.Builder okHttpBuilder) {
        if (BuildConfig.DEBUG) {
            try {
                Object interceptor = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor")
                        .newInstance();
                okHttpBuilder.addNetworkInterceptor((Interceptor) interceptor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
