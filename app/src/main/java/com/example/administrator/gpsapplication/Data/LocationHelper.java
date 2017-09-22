package com.example.administrator.gpsapplication.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LocationHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DB_NAME = "location.db";

    public static final String CREATE_TABLE = "CREATE TABLE " + LocationContract.LocationEntry.TABLE_NAME
            + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LocationContract.LocationEntry.COLUMN_DATE + " TEXT NOT NULL, "
            + LocationContract.LocationEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
            + LocationContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, "
            + LocationContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL" + ")";

    public LocationHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationContract.LocationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
