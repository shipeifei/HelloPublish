package com.mmy.remotecontrol.http;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @创建者 lucas
 * @创建时间 2018/11/16 0016 10:30
 * @描述 TODO
 */
public interface ApiManager {

    /**
     * 添加遥控，获取设备类型列表
     * api待定，参数待定
     */
    @POST("getdevicelist.asp")
    @FormUrlEncoded
    Call<RequestBody> getTypeList(@Field("mac") String mac);
}
