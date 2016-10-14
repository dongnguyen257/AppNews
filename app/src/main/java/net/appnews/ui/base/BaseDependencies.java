package net.appnews.ui.base;

import net.appnews.BuildConfig;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class BaseDependencies {
    private static int DEFAULT_TIMEOUT = 30;

    protected HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    protected OkHttpClient provideOkHttpClientDefault(HttpLoggingInterceptor interceptor) {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(interceptor);
        okBuilder.addInterceptor(chain -> {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            HashMap<String, String> headers = getHeaders();
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }
            return chain.proceed(builder.build());
        });

        int timeout = getTimeOut();
        okBuilder.connectTimeout(timeout, SECONDS);
        okBuilder.readTimeout(timeout, SECONDS);
        okBuilder.writeTimeout(timeout, SECONDS);

        OkHttpClient okHttpClient = okBuilder.build();

        return okHttpClient;
    }

    protected HashMap<String, String> getHeaders() {
        return null;
    }

    protected int getTimeOut() {
        return DEFAULT_TIMEOUT;
    }
}
