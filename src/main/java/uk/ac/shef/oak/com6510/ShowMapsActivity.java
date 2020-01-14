package uk.ac.shef.oak.com6510;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
/**
 * <h1>The activity to show photos on map.</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class ShowMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private MyViewModel myViewModel;
    private MapBarAdapter mapBarAdapter;
    private RecyclerView mRecyclerView;
    private static Activity activity;
    private Toolbar topNavigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        setActivity(this);
        ShowPhotosActivity.getProgressDialog().dismiss();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.show_integral_map);
        mapFragment.getMapAsync(this);
        mRecyclerView =  findViewById(R.id.map_recycler_view);
        initToolBar();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mapBarAdapter = new MapBarAdapter();
        mRecyclerView.setAdapter(mapBarAdapter);



        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        myViewModel.getAll().observe(this, new Observer<List<ImageElement>>() {
            @Override
            public void onChanged(@Nullable List<ImageElement> imageElements) {
                if (imageElements.size() != 0) {
                    //set toast tip
                    ArrayList<Integer> tempList= Util.getNumberAndLocation(imageElements);
                    int numberOfLocation = tempList.get(1) == 0 ? 1 : tempList.get(1);
                    String tips = String.format("%s photos in %s locations.", String.valueOf(tempList.get(0)), numberOfLocation);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 20);
                    toast.makeText(ShowMapsActivity.this, tips, Toast.LENGTH_LONG).show();


                    //notify to change UI
                    mapBarAdapter.setImages(imageElements);
                    //add marker on map
                    for (ImageElement image : imageElements) {
                        Double latitude = image.getLatitude();
                        Double longitude = image.getLongitude();
                        if ((latitude <= 90.00) && (-90.00 <= latitude) && (longitude <= 180.00) && (-180 <= longitude)){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                    .title(image.getTitle()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(2));}
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // if location permission is not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // user cannot locate their own location
            mMap.setMyLocationEnabled(false);
            return;
        } else {
            // user can locate their own location
            mMap.setMyLocationEnabled(true);
        }
        ShowPhotosActivity.getProgressDialog().dismiss();
    }
    /**
     *Get this activity
     * @return   Activity this activity
     */
    public static Activity getActivity() {
        return activity;
    }

    /**
     *Get this activity
     * @param activity set activity
     */
    public static void setActivity(Activity activity) {
        ShowMapsActivity.activity = activity;
    }

    public static GoogleMap getMap() {
        return mMap;
    }

    /**
     *Initialize tool bar
     */
    private void initToolBar() {
        topNavigationBar = findViewById(R.id.show_map_toolbar);
        topNavigationBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMapsActivity.this.finish();
                ShowPhotosActivity.getProgressDialog().dismiss();
            }
        });
    }
}
