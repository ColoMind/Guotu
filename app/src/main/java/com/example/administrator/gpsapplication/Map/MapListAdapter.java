package com.example.administrator.gpsapplication.Map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.gpsapplication.Constant.ConstantVar;
import com.example.administrator.gpsapplication.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/26.
 */

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.MapListViewHolder> {
    private String[] mapListTitles = new String[]{"xxx", "ddd", "fff", "www", "eee", "qqq", "jjj", "kkk", "ooo"};

    public MapItemClickListener mListener;

    public MapListAdapter(MapItemClickListener listener) {
        super();
        mListener = listener;
    }

    @Override
    public MapListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.map_list_item, parent, false);
        return new MapListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MapListViewHolder holder, int position) {
        holder.map_list_title.setText(mapListTitles[position]);
        holder.map_list_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapListTitles.length;
    }

    public class MapListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView map_list_title;

        public MapListViewHolder(View itemView) {
            super(itemView);
            map_list_title = itemView.findViewById(R.id.map_list_title);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(getAdapterPosition());
        }
    }

    public interface MapItemClickListener {
        void onItemClick(int index);
    }
}
