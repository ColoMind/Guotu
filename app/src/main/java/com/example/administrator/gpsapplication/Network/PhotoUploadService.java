package com.example.administrator.gpsapplication.Network;

import android.database.Observable;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/29.
 * Retrofit2 需要一service发起请求
 */

public interface PhotoUploadService {

    @Multipart
    @POST("xxxxxx")
    Call<String> uploadPhotos(@Part List<MultipartBody.Part> partList);
}
