package com.example.inceptive.imageadapter.Activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.inceptive.imageadapter.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter
{
    ArrayList pictureList = new ArrayList<>();
    Context context;
    String imageurl;

    public Integer[] mThumbIds = {
            R.drawable.camera, R.drawable.camerapic,
            R.drawable.jabillogoo, R.drawable.camera, R.drawable.camerapic,
            R.drawable.jabillogoo, R.drawable.camera, R.drawable.camerapic,
            R.drawable.jabillogoo, R.drawable.camera, R.drawable.camerapic,
            R.drawable.jabillogoo, R.drawable.camera, R.drawable.camerapic,
            R.drawable.jabillogoo
    };


    public CustomAdapter(Context applicationContext, ArrayList pictureList)
    {
        this.context = applicationContext;
        this.pictureList = pictureList;
    }

    @Override
    public int getCount()
    {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
        return imageView;
    }

}
