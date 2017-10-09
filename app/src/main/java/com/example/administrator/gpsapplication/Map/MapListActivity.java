package com.example.administrator.gpsapplication.Map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.gpsapplication.Constant.ConstantVar;
import com.example.administrator.gpsapplication.R;

public class MapListActivity extends AppCompatActivity {
    private String[] mapUrlLists = new String[]{
            ConstantVar.DZZHMAPURL,
            ConstantVar.XZQHMAPURL,
            ConstantVar.TDLYXZURL,
            ConstantVar.TDGHURL,
            ConstantVar.TDJBNTURL,
            ConstantVar.BPDKTURL,
            ConstantVar.CSGHTURL,
            ConstantVar.KCFBTURL,
            ConstantVar.IMAGEURL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        intiView();
    }


    public void intiView() {
        RecyclerView mapListView = findViewById(R.id.map_list_pre);
        mapListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mapListView.setHasFixedSize(true);
        MapListAdapter mAdapter = new MapListAdapter(new MapListAdapter.MapItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Intent intent = new Intent(MapListActivity.this, MapPreviewActivity.class);
                intent.putExtra(getString(R.string.key_save_map_url), mapUrlLists[index]);
                startActivity(intent);
            }
        });
        mapListView.setAdapter(mAdapter);
    }
}
