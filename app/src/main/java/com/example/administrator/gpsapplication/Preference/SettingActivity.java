package com.example.administrator.gpsapplication.Preference;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.gpsapplication.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        judgeSdk();
    }

    public void judgeSdk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingFragment())
                    .commit();
        }
        else {
            setContentView(R.layout.activity_setting);
        }
    }
}
