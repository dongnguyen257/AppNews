package net.appnews.data.entities;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by DongNguyen on 10/15/16.
 */
@Parcel
public class NewsItem extends BaseResponse{
    public int count;
    public String next;
    public String previous;
    public List<Results> results;

    @Parcel
    public static class Results{
        public int id;
        public String url;
        public int category;
        public String title;
        public String slug;
        public String content;
        public List<String> images;
        public String video;
        public String created_at;
    }
}
