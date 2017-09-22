package com.example.administrator.gpsapplication;

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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gpsapplication.Data.LocationContract;
import com.example.administrator.gpsapplication.Data.LocationHelper;
import com.example.administrator.gpsapplication.Login.LoginActivity;
import com.example.administrator.gpsapplication.Permission.PermissionActivity;
import com.example.administrator.gpsapplication.Permission.PermissionsChecker;
import com.example.administrator.gpsapplication.Preference.SettingActivity;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.ImageConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;


public class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private LocationHelper mHelper;
    private TextView location_display;
    private ProgressDialog dialog;
    private boolean upLoadToServer;
    private PermissionsChecker mChecker;
    private Intent intentCarryData;
    private SharedPreferences sp;
    private String userName;
    private CameraHelper mCameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    public void init() {
        location_display = findViewById(R.id.text);
        mChecker = new PermissionsChecker(this);
        mCameraHelper = new CameraHelper();

        boolean test = mChecker.lacksPermissions(Common.PERMISSIONS_LOCATION);

        upLoadToServer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_pref_upload), false);

        //getUsername
        sp = getSharedPreferences(LoginActivity.PREF_NAME, 0);
        userName = sp.getString(getString(R.string.key_save_username), null);

        if (test) {
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

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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
                mCameraHelper = new CameraHelper();
                mCameraHelper.takePhoto();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == Common.REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
            finish();
        }
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> selectedPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                PhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(0)
                        .setShowDeleteButton(false)
                        .start(LocationActivity.this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*outState.putBoolean(getString(R.string.key_pref_upload), upLoadToServer);*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_pref_upload))) {
            upLoadToServer = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, false);
        }
        storeData();
    }

    public class CameraHelper {
        private PermissionsChecker mChecker;

        public CameraHelper() {
            mChecker = new PermissionsChecker(LocationActivity.this);
        }

        private void cameraActive() {
            PhotoPicker.builder()
                    .setPhotoCount(9)
                    .setShowCamera(true)
                    .setShowGif(true)
                    .setPreviewEnabled(false)
                    .start(LocationActivity.this, PhotoPicker.REQUEST_CODE);
        }

        public void takePhoto() {
            if (mChecker.lacksPermissions(Common.PERMISSIONS_CAMERA)) {
                PermissionActivity.startActivityForResult(LocationActivity.this, Common.REQUEST_CODE, Common.PERMISSIONS_CAMERA);
            } else {
                cameraActive();
            }
        }

    }
}
