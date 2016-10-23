package net.appnews.ui.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import net.appnews.R;

/**
 * Created by DongNguyen on 10/19/16.
 */

public class YoutubeHelper {
    static YoutubeHelper _self = null;
    private Context context;
    int videoCountGlobal = 0;
    public static YoutubeHelper getInstance(Context context) {
        if (_self == null) {
            _self = new YoutubeHelper();
        }
        _self.context = context;
        return _self;
    }
    int fID = 0;

    public int findUnusedId(View view) {
        while (view.findViewById(++fID) != null) ;
        return fID;
    }
    public View getYoutubeView(final String videoId, android.support.v4.app.FragmentManager fragmentManager,String apiKey,OnCompleteListener onCompleteListener) {
        View videoView = LayoutInflater.from(context)
                .inflate(R.layout.video_view, null);

        /**
         * Framelayout
         */
        View youtubeFragment = videoView.findViewById(R.id.youtubeplayerFrame);

        ViewGroup.LayoutParams youtubeFragmentLayoutParams = youtubeFragment.getLayoutParams();

        /**
         * resizing video to 3:2 ratio, assuming youtube always scales video to 3:2
         */
        youtubeFragmentLayoutParams.height = (GeneralHelper.getInstance().getScreenSize(context).x * 2/3);

        youtubeFragment.setLayoutParams(youtubeFragmentLayoutParams);

        int frameId = findUnusedId(videoView);

        youtubeFragment.setId(frameId);
        youtubeFragment.setTag("YOUTUBE" + videoCountGlobal);
        YouTubePlayerSupportFragment ypsf = new YouTubePlayerSupportFragment();
        // VIDEO_ID=contentList.get(j).fileName;


        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        // ft.addToBackStack("YOUTUBE"+videoCountGlobal);
        ft.replace(frameId, ypsf);
        ft.commit();
        InitYoutubeVideo iytv = new InitYoutubeVideo(videoCountGlobal, videoId, videoView, ypsf,onCompleteListener);
        iytv.init(apiKey);
        videoCountGlobal++;
        return videoView;
    }
}
