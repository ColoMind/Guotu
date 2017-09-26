package com.example.administrator.gpsapplication.Constant;

import android.Manifest;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public class Common {
    //Location
    public static final String LOCATION = "location";
    public static final String LOCATION_LATITUDE = "latitude";
    public static final String LOCATION_LONGITUDE = "longitude";
    public static final String LOCATION_ACTION = "locationAction";

    //Meter
    public static final int FIFTY_METER = 50;
    public static final int TWENTY_METER = 20;

    //Time
    public static final int FIVE_SECONDS = 5 * 1000;
    public static final int ONE_MINUTE = FIVE_SECONDS * 12;

    //location permissions needed
    public static final String[] PERMISSIONS_LOCATION = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    //camera permissions needed
    public static final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    public static final int REQUEST_CODE = 0;

    //Variable about camera
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mms";
    public static File photo_file = new File(path);
    public static final int REQUEST_CAMERA_CODE = 10;
    public static final int REQUEST_PREVIEW_CODE = 20;

}
