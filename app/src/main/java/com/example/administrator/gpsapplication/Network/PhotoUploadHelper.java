package com.example.administrator.gpsapplication.Network;


import android.database.Observable;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/9/29.
 */

public class PhotoUploadHelper {
    public static final String BASE_URL = null;


    public static Call<String> uploadPhotos(List<MultipartBody.Part> partList) {
        //Retrofit 默认的超时为10秒
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PhotoUploadService service = mRetrofit.create(PhotoUploadService.class);
        return service.uploadPhotos(partList);
    }
}
