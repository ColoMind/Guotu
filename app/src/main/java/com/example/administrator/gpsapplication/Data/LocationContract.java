package com.example.administrator.gpsapplication.Data;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LocationContract {

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

        //The column needs to stored in table
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_USERNAME = "username";
    }

}
