package net.appnews.data.entities;

import com.google.gson.Gson;

/**
 * Created by DongNguyen on 11/4/16.
 */

public class BaseResponse {
    public String toFormResponse() {
        return new Gson().toJson(this);
    }
}
