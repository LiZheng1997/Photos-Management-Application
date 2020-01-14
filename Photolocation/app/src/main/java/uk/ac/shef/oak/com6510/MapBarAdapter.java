package uk.ac.shef.oak.com6510;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
/**
 * <h1>Adapter of RecyclerView in bar on the bottom of ShowMapsActivity</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */

public class MapBarAdapter extends  RecyclerView.Adapter<MapBarAdapter.View_Holder>  {
    static private Context context;
    private static List<ImageElement> items = new ArrayList<>();
    static Double latitude;
    static Double longitude;


    public void setImages(List<ImageElement> items){
        this.items = items;
        notifyDataSetChanged();
    }
    @Override
    public MapBarAdapter.View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_list_item_image,
                parent, false);
        MapBarAdapter.View_Holder holder = new MapBarAdapter.View_Holder(v);
        context= parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(final MapBarAdapter.View_Holder holder, final int position) {

        if (holder != null && items.get(position) != null) {
            if (items.get(position).getNailPath() != null) {
                // upload thumb nail of photo
                new MapBarAdapter.UploadSingleImageTask().execute(new HolderAndPosition(position, holder));
                Log.i("MAP UPLOAD IMAGE","EXECUTE");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Click");
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });

            // add long click listener
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (ShowMapsActivity.getActivity() != null){
                        ShowMapsActivity.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                if (ShowMapsActivity.getMap() != null){
                                    // if latitude and longitude are available
                                    // move camera to the location of photo
                                    if ((items.get(position).getLatitude() <= 90.00) && (-90.00 <= items.get(position).getLatitude()) && (items.get(position).getLongitude() <= 180.00) && (-180.00 <= items.get(position).getLongitude())){
                                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
                                        ShowMapsActivity.getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(items.get(position).getLatitude(), items.get(position).getLongitude())));
                                        ShowMapsActivity.getMap().animateCamera(zoom);
                                    }
                                }
                            }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    return true;
                }
            });
        }
    }


    static ImageElement getItem(int id) {
        return items.get(id);
    }


    public class View_Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View_Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.horizontal_image_item);
        }
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    // async task for uploading thumb nail files
    private class UploadSingleImageTask extends AsyncTask<MapBarAdapter.HolderAndPosition, Void,Bitmap> {
        MapBarAdapter.HolderAndPosition holdAndPos;
        @Override
        protected Bitmap doInBackground(MapBarAdapter.HolderAndPosition... holderAndPosition) {
            holdAndPos= holderAndPosition[0];
            Bitmap myBitmap = BitmapFactory.decodeFile(items.get(holdAndPos.position).getNailPath());
            return myBitmap;
        }
        @Override
        protected void onPostExecute (Bitmap bitmap){
            holdAndPos.holder.imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * <h1>Class encapsulates holder and position.</h1>
     */
    // create a class to store position and holder
    // so that can pass to async task
    private class HolderAndPosition{
        int position;
        MapBarAdapter.View_Holder holder;
        public HolderAndPosition(int position, MapBarAdapter.View_Holder holder) {
            this.position = position;
            this.holder = holder;
        }
    }

    public static List<ImageElement> getItems() {
        return items;
    }


}