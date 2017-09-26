package com.example.administrator.gpsapplication;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gpsapplication.Camera.PhotoLoadAdapter;
import com.example.administrator.gpsapplication.Constant.Common;
import com.example.administrator.gpsapplication.Constant.ConstantVar;
import com.example.administrator.gpsapplication.Data.LocationContract;
import com.example.administrator.gpsapplication.Data.LocationHelper;
import com.example.administrator.gpsapplication.Login.LoginActivity;
import com.example.administrator.gpsapplication.Permission.PermissionActivity;
import com.example.administrator.gpsapplication.Permission.PermissionsChecker;
import com.example.administrator.gpsapplication.Preference.SettingActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;


public class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private LocationHelper mHelper;
    private TextView location_display;
    private ProgressDialog dialog;
    private boolean upLoadToServer;
    private PermissionsChecker mChecker;
    private Intent intentCarryData;
    private SharedPreferences sp;
    private String userName;
    private ArrayList<String> selectedPhotos;
    private RecyclerView viewPhotoPreview;
    private PhotoLoadAdapter mAdapter;
    private CameraHelper mCameraHelper;
    private Button openMap;
    public static final int NUM_SPAN = 3;
    private WebView viewDisplayMap;
    private PhotoLoadAdapter.photoItemClickListener mPhotoItemClickListener;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        init();

    }

    public void init() {
        location_display = findViewById(R.id.text);
        location_display.setVisibility(View.VISIBLE);

        //init the buttons
        openMap = findViewById(R.id.open_map);
        openMap.setOnClickListener(this);

        mChecker = new PermissionsChecker(this);
        mCameraHelper = new CameraHelper();

        upLoadToServer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_pref_upload), false);

        mPhotoItemClickListener = new PhotoLoadAdapter.photoItemClickListener() {
            @Override
            public void onPhotoClick(int index) {
                mCameraHelper.previewPhoto(index);
            }
        };

        //init the recyclerview
        mAdapter = new PhotoLoadAdapter(mPhotoItemClickListener);
        viewPhotoPreview = findViewById(R.id.view_photo_preview);
        viewPhotoPreview.setLayoutManager(new GridLayoutManager(this, NUM_SPAN));
        viewPhotoPreview.setHasFixedSize(true);
        viewPhotoPreview.setAdapter(mAdapter);

        //getUsername
        sp = getSharedPreferences(LoginActivity.PREF_NAME, 0);
        userName = sp.getString(getString(R.string.key_save_username), null);

        if (mChecker.lacksPermissions(Common.PERMISSIONS_LOCATION)) {
            PermissionActivity.startActivityForResult(LocationActivity.this, Common.REQUEST_CODE, Common.PERMISSIONS_LOCATION);
        }

        //regist broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(Common.LOCATION_ACTION);
        this.registerReceiver(new LocationBroadcastReceiver(), filter);

        //start service
        Intent intent = new Intent();
        intent.setClass(this, LocationService.class);

        startService(intent);

        //waiting for response
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在定位...");
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(true);
        }
        if (selectedPhotos != null) {
            location_display.setVisibility(View.INVISIBLE);
        }
    }

    @TargetApi(21)
    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        if (viewDisplayMap != null) {
            viewDisplayMap.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            viewDisplayMap.clearHistory();
            ((ViewGroup) viewDisplayMap.getParent()).removeView(viewDisplayMap);
            viewDisplayMap.destroy();
            viewDisplayMap = null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intentStartSetting = new Intent(this, SettingActivity.class);
                startActivity(intentStartSetting);
                break;
            case R.id.action_camera:
                checkCameraPermissions();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(21)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_map:
                setContentView(R.layout.activity_webview);
                viewDisplayMap = findViewById(R.id.web_view);
                setWebSetting(viewDisplayMap);
                viewDisplayMap.loadUrl(ConstantVar.LOGINURL);
                viewDisplayMap.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        view.loadUrl(request.getUrl().toString());
                        return true;
                    }
                });
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData();
                startActivity(intent);*/
                break;
            default:
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (webSettings != null) {
            webSettings.setJavaScriptEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewDisplayMap != null) {
            viewDisplayMap.onPause();
        }
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;

            //get latitude and longitude ,then display them
            double latitude = intent.getDoubleExtra(Common.LOCATION_LATITUDE, 0);
            double longitude = intent.getDoubleExtra(Common.LOCATION_LONGITUDE, 0);
            location_display.append("latitude is " + String.valueOf(latitude) + "\n" + "longitude is " + String.valueOf(longitude));

            //put data into intent
            intentCarryData = new Intent();
            intentCarryData.putExtra(Common.LOCATION_LONGITUDE, longitude);
            intentCarryData.putExtra(Common.LOCATION_LATITUDE, latitude);

            //save data;
            storeData();

            dialog.dismiss();
            LocationActivity.this.unregisterReceiver(this);
        }
    }

    public boolean storeLocationInLocal(double latitude, double longitude, String username) {
        mHelper = new LocationHelper(this);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        //get system current time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String formatDate = formatter.format(curDate);

        //values to save
        ContentValues values = new ContentValues();
        values.put(getString(R.string.key_save_latitude), latitude);
        values.put(getString(R.string.key_save_longitude), longitude);
        values.put(getString(R.string.key_save_date), formatDate);
        values.put(getString(R.string.key_save_username), username);
        long id = db.insert(LocationContract.LocationEntry.TABLE_NAME, null, values);
        return (id > 0);
    }

    public void showStoreResult(boolean isSucceed) {
        if (isSucceed) {
            Toast.makeText(LocationActivity.this, "Save Location Succeed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LocationActivity.this, "Save Location Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeData() {
        if (upLoadToServer) {

        } else {
            showStoreResult(
                    storeLocationInLocal(
                            intentCarryData.getDoubleExtra(Common.LOCATION_LATITUDE, 0),
                            intentCarryData.getDoubleExtra(Common.LOCATION_LONGITUDE, 0),
                            userName
                    )
            );
        }
    }

    public void checkCameraPermissions() {
        if (mChecker.lacksPermissions(Common.PERMISSIONS_CAMERA)) {
            PermissionActivity.startActivityForResult(LocationActivity.this, Common.REQUEST_CAMERA_CODE, Common.PERMISSIONS_CAMERA);
        } else {
            mCameraHelper.takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == Common.REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
            finish();
        }

        if (resultCode == RESULT_OK && requestCode == Common.REQUEST_CAMERA_CODE) {
            checkCameraPermissions();
        }

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                selectedPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mAdapter.setPaths(selectedPhotos);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_pref_upload))) {
            upLoadToServer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, false);
        }
        storeData();
    }

    public class CameraHelper {

        public void takePhoto() {
            PhotoPicker.builder()
                    .setPhotoCount(9)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(false)
                    .start(LocationActivity.this, PhotoPicker.REQUEST_CODE);
        }

        public void previewPhoto(int index) {
            PhotoPreview.builder()
                    .setPhotos(selectedPhotos)
                    .setCurrentItem(index)
                    .setShowDeleteButton(false)
                    .start(LocationActivity.this); //点击完成按钮后去的位置
        }
    }

    public void setWebSetting(WebView webView) {
        //声明WebSettings子类
        webSettings = webView.getSettings();

        // 如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

        // 缩放操作
        webSettings.setSupportZoom(true);//支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false);//隐藏原生的缩放控件

        // 其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }
}
