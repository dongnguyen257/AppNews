package net.appnews.data;

import net.appnews.data.entities.DeviceToken;
import net.appnews.data.entities.NewsItem;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by DongNguyen on 10/14/16.
 */

public interface ServerAPI {
    @GET("/api/v1/news/")
    Observable<NewsItem> getNews();

    @GET("/api/v1/news/")
    Observable<NewsItem> getMoreNews(@Query("page") int page);

    @GET("/api/v1/categories/{category_id}/news/")
    Observable<NewsItem> getCategoryNews(@Path("category_id") int category_id);

    @GET("/api/v1/categories/{category_id}/news/")
    Observable<NewsItem> getCategoryNewsCategory(@Path("category_id") int category_id, @Query("page") int page);

    @GET("/api/v1/users/{id}/")
    Observable<NewsItem> getDetailNews(@Path("id") int id);

    @POST("/device/gcm/")
    @FormUrlEncoded
    Observable<DeviceToken> postDeviceToken(@Field("name") String name,
                                            @Field("registration_id") String token, @Field("active") boolean active);
}
