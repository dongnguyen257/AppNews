package net.appnews.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DongNguyen on 10/15/16.
 */

public class NewsItem {
    @SerializedName("news_id")
    public String newsId;
    @SerializedName("image_url")
    public String imageUrl;
    @SerializedName("title")
    public String title;
    @SerializedName("contents")
    public String contents;
    @SerializedName("youtube_url")
    public String youtubeUrl;
}
