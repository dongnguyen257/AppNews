package net.appnews.data.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DongNguyen on 11/3/16.
 */
@Table(name = "news")
public class NewsDto extends Model implements Serializable {
    @Column(name = "news_id")
    public int news_id;
    @Column(name = "url")
    public String url;
    @Column(name = "category")
    public int category;
    @Column(name = "title")
    public String title;
    @Column(name = "slug")
    public String slug;
    @Column(name = "content")
    public String content;
    @Column(name = "images")
    public List<String> images;
    @Column(name = "video")
    public String video;
    @Column(name = "created_at")
    public String created_at;

    public NewsDto(){
    }

    public static List<NewsDto> all() {
        return new Select().from(NewsDto.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(NewsDto.class).execute();
    }

    public static NewsDto getById(int id) {
        return new Select().from(NewsDto.class).where("news_id=?", id).executeSingle();
    }

    public static List<NewsDto> getCategoryById(int id) {
        return new Select().from(NewsDto.class).where("category=?", id).executeSingle();
    }
}
