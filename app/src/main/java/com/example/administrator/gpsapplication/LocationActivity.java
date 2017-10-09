package com.example.administrator.gpsapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gpsapplication.Camera.PhotoLoadAdapter;
import com.example.administrator.gpsapplication.Constant.Common;
import com.example.administrator.gpsapplication.Constant.ConstantVar;
import com.example.administrator.gpsapplication.Data.LocationContract;
import com.example.administrator.gpsapplication.Data.LocationHelper;
import com.example.administrator.gpsapplication.Login.LoginActivity;
import com.example.administrator.gpsapplication.Map.MapListActivity;
import com.example.administrator.gpsapplication.Network.PhotoUploadActivity;
import com.example.administrator.gpsapplication.Network.PhotoUploadHelper;
import com.example.administrator.gpsapplication.Permission.PermissionActivity;
import com.example.administrator.gpsapplication.Permission.PermissionsChecker;
import com.example.administrator.gpsapplication.Preference.SettingActivity;
import com.example.administrator.gpsapplication.Utils.CameraHelper;
import com.example.administrator.gpsapplication.Utils.MyDateUitl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;


public class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private LocationHelper mHelper;
    private ProgressDialog dialog;
    private boolean upLoadToServer;
    private PermissionsChecker mChecker;
    private Intent intentCarryData;
    private SharedPreferences sp;
    private String userName;
    private static ArrayList<String> selectedPhotos;
    private RecyclerView viewPhotoPreview;
    private PhotoLoadAdapter mAdapter;
    public static final int NUM_SPAN = 3;
    private PhotoLoadAdapter.photoItemClickListener mPhotoItemClickListener;
    private double latitude;
    private double longitude;
    private TextView dis_latitude;
    private TextView dis_longitude;
    static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        mActivity = LocationActivity.this;
        init();

    }

    public void init() {
        dis_latitude = findViewById(R.id.dis_latitude);
        dis_longitude = findViewById(R.id.dis_longitude);
        //init the buttons

        mChecker = new PermissionsChecker(this);
        upLoadToServer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_pref_upload), false);

        mPhotoItemClickListener = new PhotoLoadAdapter.photoItemClickListener() {
            @Override
            public void onPhotoClick(int index) {
                CameraHelper.previewPhoto(mActivity, selectedPhotos, index);
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
        selectedPhotos = null;
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @TargetApi(21)
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
            case R.id.action_map:
                Intent showMapList = new Intent(this, MapListActivity.class);
                showMapList.putExtra(getString(R.string.key_save_latitude), latitude);
                showMapList.putExtra(getString(R.string.key_save_longitude), longitude);
                startActivity(showMapList);
                break;
            case R.id.action_upload:
                Intent intentStartUpload = new Intent(this, PhotoUploadActivity.class);
                if (selectedPhotos == null) {
                    Toast.makeText(this, "请先选择要上传的照片", Toast.LENGTH_SHORT).show();
                } else {
                    intentStartUpload.putExtra(getString(R.string.key_save_photo_paths), selectedPhotos);
                    startActivity(intentStartUpload);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Common.LOCATION_ACTION)) return;

            //get latitude and longitude ,then display them
            latitude = intent.getDoubleExtra(Common.LOCATION_LATITUDE, 0);
            longitude = intent.getDoubleExtra(Common.LOCATION_LONGITUDE, 0);
            dis_latitude.setText(String.valueOf(latitude));
            dis_longitude.setText(String.valueOf(longitude));

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
        //values to save
        ContentValues values = new ContentValues();
        values.put(getString(R.string.key_save_latitude), latitude);
        values.put(getString(R.string.key_save_longitude), longitude);
        values.put(getString(R.string.key_save_date), MyDateUitl.getCurrentTime());
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
            CameraHelper.takePhoto(mActivity);
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


}
