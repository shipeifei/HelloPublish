package com.mmy.remotecontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @创建者 lucas
 * @创建时间 2018/11/16 0016 10:22
 * @描述 TODO
 */
public class Constant {
    private static String BASE_URL_KEY = "base_url_key";
    private static String baseUrl = "http://cir.huilink.com.cn/";
//    private static String baseUrl = "http://www.huilink.com.cn/dk2018/";

    public static String getBaseUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("control", Context.MODE_PRIVATE);
        String url = sharedPreferences.getString(BASE_URL_KEY, "");
        if (!TextUtils.isEmpty(url)) {
            baseUrl = url;
        }
        return baseUrl;
    }

    public static void setBaseUrl(Context context, String newUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("control", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BASE_URL_KEY, newUrl);
        editor.apply();

    }
}
