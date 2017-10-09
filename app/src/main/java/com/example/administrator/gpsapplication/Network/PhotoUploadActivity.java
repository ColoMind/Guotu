package com.example.administrator.gpsapplication.Network;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.administrator.gpsapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoUploadActivity extends AppCompatActivity {
    public static final String TOKEN = "token";
    PhotoUploadAdapter mAdapter;
    RecyclerView photoUploadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);
        photoUploadList = findViewById(R.id.photo_remain_view);
        ArrayList<String> photoPaths = getIntent().getStringArrayListExtra(getString(R.string.key_save_photo_paths));
        mAdapter = new PhotoUploadAdapter(photoPaths, this);
        photoUploadList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        photoUploadList.setAdapter(mAdapter);
        photoUploadList.setHasFixedSize(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.upload_multi_photo) {
            //TODO upLoadImages Here
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void setModeMulti() {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            PhotoUploadAdapter.PhotoMessageViewHolder holder
                    = (PhotoUploadAdapter.PhotoMessageViewHolder) photoUploadList.getChildViewHolder(photoUploadList.getChildAt(i));

            //此处需要让所有的布局都更改，但由于RecyclerView循环利用了holder,导致第5个后的holder为空指针
        }
    }*/

    //上传多张图片
    private void upLoadImages() {
        List<String> pathList = mAdapter.getSelectedPhotos();//获取多张待上传图片的地址列表
        Log.i("SizeOfList", String.valueOf(pathList.size()));
        String token = "ASDDSKKK19990SDDDSS";//用户token
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
                .addFormDataPart(TOKEN, token);//ParamKey.TOKEN 自定义参数key常量类，即参数名
        //多张图片
        for (int i = 0; i < pathList.size(); i++) {
            File file = new File(pathList.get(i));//filePath 图片地址
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("imgfile" + i, file.getName(), imageBody);//"imgfile"+i 后台接收图片流的参数名
        }

        List<MultipartBody.Part> parts = builder.build().parts();
        PhotoUploadHelper.uploadPhotos(parts).enqueue(new Callback<String>() {//返回结果
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("UploadPhotoSuc", response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("UploadPhotoFail", t.toString());
            }
        });
    }

}
