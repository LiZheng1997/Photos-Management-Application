/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.List;

/**
 * <h1>A adapter for album items</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class AlbumsItemAdapter extends RecyclerView.Adapter<AlbumsItemAdapter.View_Holder> {
    static private Context context;
    private List<ImageElement> items;

    public AlbumsItemAdapter(List<ImageElement> imageElements){
        this.items = imageElements;
    }

    public void setImages(List<ImageElement> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_list_item_image,
                parent, false);
        View_Holder holder = new View_Holder(v);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {

        if (holder != null && items.get(position) != null) {

                new uploadAlbumsItem().execute(new HolderAndPosition(position, holder));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlbumImageActivity.class);
                    ImageElement image = new ImageElement(Parcel.obtain());
                    ImageElement temp = items.get(position);
                    image.setId(temp.getId());
                    image.setTitle(temp.getTitle());
                    image.setDate(temp.getDate());
                    image.setDescription(temp.getDescription());
                    image.setNailPath(temp.getNailPath());
                    image.setFilePath(temp.getFilePath());
                    image.setLatitude(temp.getLatitude());
                    image.setLongitude(temp.getLongitude());
                    // save object parameter
                    intent.putExtra("Image", image);
                    context.startActivity(intent);
                }
            });
        }
    }



    public class View_Holder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView imageView;
        Toolbar toolBar;
        CardView cardView;
        View_Holder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            imageView = itemView.findViewById(R.id.vertical_image_item);
            titleText = itemView.findViewById(R.id.vertical_image_name);
        }
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    // async task to upload photo
    private class uploadAlbumsItem extends AsyncTask<HolderAndPosition, Void,Bitmap> {
        HolderAndPosition holdAndPos;
        @Override
        protected Bitmap doInBackground(HolderAndPosition... holderAndPosition) {
            holdAndPos= holderAndPosition[0];
            Bitmap myBitmap = BitmapFactory.decodeFile(items.get(holdAndPos.position).getNailPath());
        return myBitmap;
        }
        @Override
        protected void onPostExecute (Bitmap bitmap){
            holdAndPos.holder.imageView.setImageBitmap(bitmap);
            holdAndPos.holder.titleText.setText(items.get(holdAndPos.position).getTitle());
        }
    }

    /**
     * <h1>Class encapsulates holder and position.</h1>
     */
    // create a class to store position and holder
    // so that can pass to async task
    private class HolderAndPosition{
        int position;
        View_Holder holder;
        public HolderAndPosition(int position, View_Holder holder) {
            this.position = position;
            this.holder = holder;
        }
    }

}