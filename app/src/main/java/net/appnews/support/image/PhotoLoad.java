package net.appnews.support.image;

import android.widget.ImageView;

/**
 * Created by DongNguyen on 10/14/16.
 */

public class PhotoLoad {
    public String url;
    public int placeHolderId;
    public ImageView imageView;
    public float offset;
    public int showWidth;
    public int showHeight;
    public BitmapCallback callback;
    public ScaleType scaleType;
    public int id;
    public int type;
    public boolean isLocal;
    public boolean isFadeIn;

    public PhotoLoad(Builder builder) {
        url = builder.url;
        placeHolderId = builder.placeHolderId;
        imageView = builder.imageView;
        offset = builder.offset;
        showWidth = builder.showWidth;
        showHeight = builder.showHeight;
        callback = builder.callback;
        scaleType = builder.scaleType;
        id = builder.id;
        type = builder.type;
        isLocal = builder.isLocal;
        isFadeIn = builder.isFadeIn;
    }

    public static class Builder {
        public String url;
        public int placeHolderId;
        public ImageView imageView;
        public float offset;
        public int showWidth;
        public int showHeight;
        public BitmapCallback callback;
        public ScaleType scaleType = ScaleType.NOT_SCALE;
        public int id;
        public int type;
        public boolean isLocal;
        public boolean isFadeIn = true;

        public Builder() {}

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder isLocal(boolean isLocal) {
            this.isLocal = isLocal;
            return this;
        }

        public Builder isFadeIn(boolean isFadeIn) {
            this.isFadeIn = isFadeIn;
            return this;
        }

        public Builder placeHolderId(int placeHolderId) {
            this.placeHolderId = placeHolderId;
            return this;
        }

        public Builder into(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder offset(float offset) {
            this.offset = offset;
            return this;
        }

        public Builder width(int showWidth) {
            this.showWidth = showWidth;
            return this;
        }

        public Builder height(int showHeight) {
            this.showHeight = showHeight;
            return this;
        }

        public Builder callBack(BitmapCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder scaleType(ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public PhotoLoad build() {
            return new PhotoLoad(this);
        }
    }

    public enum ScaleType {SCALE, NOT_SCALE, SHOW_WITH_OFFSET};
}
