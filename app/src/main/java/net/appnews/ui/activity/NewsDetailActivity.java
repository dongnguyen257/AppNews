package net.appnews.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import net.appnews.Constants;
import net.appnews.R;
import net.appnews.data.entities.NewsItem;
import net.appnews.support.image.ImageWorker;
import net.appnews.ui.base.BaseActivity;
import net.appnews.ui.media.YoutubeHelper;
import net.appnews.utils.Utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

public class NewsDetailActivity extends BaseActivity implements NewsDetailPresent.View, View.OnClickListener{

    @BindView(R.id.scrollNews)
    ScrollView scrollNews;
    @BindView(R.id.wvContent)
    WebView wvContent;
    @BindView(R.id.llNewsDetail)
    LinearLayout llNewsDetail;
    @BindView(R.id.llListImages)
    LinearLayout llListImages;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDatePost)
    TextView tvDatePost;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private NewsItem.Results newsDetail;
    private NewsDetailPresent mNewsDetailPresent;
    private ArrayList<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_news_detail);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNewsDetailPresent = new NewsDetailPresent();
        mNewsDetailPresent.bindView(this);
        mNewsDetailPresent.getNewsDetail(getIntent());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onVideoComplete(int position, String id) {

    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        if(!isFullscreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void showDataIntentUi(NewsItem.Results newsDetail) {
        setTitle(newsDetail.title);
        viewList = new ArrayList<>();
//        if (newsDetail.image != null){
//            Picasso.with(this).load("https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg")
//                    .fit().centerCrop().priority(Picasso.Priority.LOW).into(ivNews);
//        }
//        setViewImages();
        tvTitle.setText(Html.fromHtml(newsDetail.title));
        tvDatePost.setText(Utils.getTimeZone(newsDetail.created_at));
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.loadDataWithBaseURL(null, newsDetail.content, "text/html", "utf-8", null);

        if (!newsDetail.video.isEmpty()){
            setViewYoutube(splitYoutube(newsDetail.video));
        }else {
            scrollNews.smoothScrollTo(0, 0);
        }
    }


    public void setViewYoutube(String endPoint){
        View videoView = YoutubeHelper.getInstance(this).getYoutubeView(endPoint, this.getSupportFragmentManager(), Constants.API_YOUTUBE, this);
        llNewsDetail.addView(videoView);
        scrollNews.smoothScrollTo(0, 0);
    }

    public void setViewImages(){
        for (int i = 0; i < 3; i++){
            View imageViewContainer = LayoutInflater.from(this).inflate(R.layout.detail_images, null);
            ImageView img = (ImageView) imageViewContainer.findViewById(R.id.detailNewsImage);
            ProgressBar progressBarImage = (ProgressBar) imageViewContainer.findViewById(R.id.progressBarImage);
            ImageWorker.displayImage(this, img, progressBarImage, "https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg");
            viewList.add(imageViewContainer);
            llListImages.addView(imageViewContainer);
        }
    }

    public static String splitYoutube(String ytUrl) {
        String vId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if(matcher.find()){
            return matcher.group();
        }
        return vId;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onClick(View view) {

    }
}
