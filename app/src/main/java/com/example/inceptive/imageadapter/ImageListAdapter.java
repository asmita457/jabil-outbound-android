package com.example.inceptive.imageadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

class ImageListAdapter extends BaseAdapter {

    byte[] boxImage;
    private Context context;
    private  int layout;
    private ArrayList<ImageModel> imagesList;

    public ImageListAdapter(Context context, int layout, ArrayList<ImageModel> imagesList)
    {
           this.context=context;
           this.layout=layout;
           this.imagesList=imagesList;
    }

    public void notifyDataSetChanged() {
    }

    @Override
    public int getCount() {

        return imagesList.size();

    }

    @Override
    public Object getItem(int position) {
        return imagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder{
        ImageView imageView;
//        TextView txtName;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.imageView = (ImageView) row.findViewById(R.id.imgBox);
            row.setTag(holder);
        } else
            {
            holder = (ViewHolder) row.getTag();
            }
        ImageModel imag = imagesList.get(position);
         boxImage = imag.getImage();
         Bitmap bitmp=ByteArrayToBitmap(boxImage);
        holder.imageView.setImageBitmap(bitmp);
        return row;
    }
    public Bitmap ByteArrayToBitmap(byte[] byteArray)
    {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
}
