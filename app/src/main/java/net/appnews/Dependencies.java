package net.appnews;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.appnews.data.ServerAPI;
import net.appnews.ui.base.BaseDependencies;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class Dependencies extends BaseDependencies{
    private static Dependencies sInstance;
    public static ServerAPI serverAPI;

    public static Dependencies getsInstance() {
        if (sInstance == null)
            sInstance = new Dependencies();
        return sInstance;
    }

    public void init() {
        if (serverAPI == null) {
            OkHttpClient okHttpClient = provideOkHttpClientDefault(provideHttpLoggingInterceptor());
            serverAPI = provideRestApi(okHttpClient);
        }
    }

    private ServerAPI provideRestApi(@NonNull OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
        final Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://sky-red-api.herokuapp.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        return builder.build().create(ServerAPI.class);
    }

    @Override
    protected HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer 4lYD4MHCsIjr7HVGXMjcB7xubqiIqw");
        headers.put("Accept", "*/*");
        return headers;
    }
}
