/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;

import java.util.ArrayList;
import java.util.List;


/**
 * <h1>Adapter of RecyclerView in AlbumFragment</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.View_Holder> {
    static private Context context;
    private List<ImageOnDate> groups = new ArrayList<>();
    private  List<ImageElement> items = new ArrayList<>();
    AlbumsItemAdapter albumsItemAdapter;

    public AlbumAdapter(List<ImageOnDate> groups){
        this.groups = groups;
    }

    public AlbumAdapter() {
    }

    public void setImages(List<ImageOnDate> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_show_albums_inner_content,
                parent, false);
        View_Holder holder = new View_Holder(v);
        context = parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(View_Holder holder,  int position) {

            if (groups.get(position) != null) {
                List<ImageElement> list = new ArrayList<>();
                //get the list of photos in same date
                list = groups.get(position).getImageElements();
                    albumsItemAdapter.setImages(list);
                    holder.dateText.setText(groups.get(position).getImageElements().get(0).getDate());
            }
    }

    ImageOnDate getItem(int id) {
        return groups.get(id);
    }

    /**
     * <h1>Class encapsulates holder and position.</h1>
     */
    public class View_Holder extends RecyclerView.ViewHolder {
        TextView dateText;
        RecyclerView innerRecyclerView;
        View_Holder(View itemView) {
            super(itemView);
            albumsItemAdapter = new AlbumsItemAdapter(items);
            innerRecyclerView = itemView.findViewById(R.id.innerRecyclerView);
            innerRecyclerView.setLayoutManager(new GridLayoutManager(ShowPhotosActivity.getActivity(),3));
            dateText = itemView.findViewById(R.id.albumDate);
            innerRecyclerView.setAdapter(albumsItemAdapter);
        }
    }

    @Override
    public int getItemCount(){

        return groups.size();
    }

    /**
     *Get List of ImageElement
     * @return  List<ImageElement> list of ImageElement
     */
    public List<ImageOnDate> getItems() {
        return groups;
    }

}
