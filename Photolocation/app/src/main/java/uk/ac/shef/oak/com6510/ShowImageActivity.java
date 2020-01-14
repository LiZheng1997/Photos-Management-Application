package uk.ac.shef.oak.com6510;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * <h1>ShowImageActivity</h1>
 * The activity to show full photo.
 * <p>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class ShowImageActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static Activity activity;
    private Toolbar topBar;
    private DrawerLayout mDrawerLayout;
    private GoogleMap mMap;
    static int position  = -1;
    ImageElement element;
    Double latitude;
    Double longitude;
    private MyViewModel myViewModel;
    private ImageView imageView;
    private boolean isClick = true;
    SupportMapFragment mapFragment;
    String updateTitle;
    String updateDesc;
    int id;
    static TextView titleText;
    TextView dateText;
    static TextView descText;
    EditInfoDialog editInfoDialog;
    public static ArrayList<String> editInfo;
    View view;
    android.support.v4.app.FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    /**
     * Static method to return current activity.
     * @return Activity current activity.
     */
    public static Activity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_image);
        activity = this;
        view = getWindow().getDecorView();
        topBar = (Toolbar) findViewById(R.id.show_singleImage_toolbar);
        mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        initToolBar();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.show_mini_map);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();

        if(b != null) {
            position = b.getInt("position");
            if (position != -1) {
                imageView = findViewById(R.id.image);
                titleText = findViewById(R.id.navigation_title);
                dateText = findViewById(R.id.navigation_date);
                descText = findViewById(R.id.navigation_description);

                    element = PhotosAdapter.getItems().get(position);
                    if (element.getFilePath() != null) {
                        // show original bitmap from file
                        Bitmap myBitmap = BitmapFactory.decodeFile(element.getFilePath());
                        // show information of photo
                        imageView.setImageBitmap(myBitmap);
                        titleText.setText("Title: \n" + element.getTitle());
                        dateText.setText("Date: \n" + element.getDate());
                        descText.setText("Description: \n" + element.getDescription());
                        latitude = element.getLatitude();
                        longitude = element.getLongitude();
                        id = element.getId();
                    }

            }
        }

        // click to show/hide status bar
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Click");
                if (isClick){
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    topBar.setVisibility(View.INVISIBLE);
                    isClick = !isClick;
                }
                else {topBar.setVisibility(View.VISIBLE);
                    view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    isClick = !isClick;
                }

            }
        });


        // click to show/hide tool bar
        View view;
        view = topBar.getRootView();
        if(view.performClick()){
            topBar.hideOverflowMenu();
        }

        topBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int menuItemId = item.getItemId();
                if(menuItemId == R.id.information){
                    mDrawerLayout.openDrawer(GravityCompat.END);
                }
                else if(menuItemId == R.id.delete){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ShowImageActivity.this);
                    dialog.setTitle("Notice");
                    dialog.setIcon(android.R.drawable.ic_delete);
                    dialog.setMessage("Do you want to delete this photo?");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // delete photo in database
                            myViewModel.deleteImage(element);
                            ShowImageActivity.this.finish();
                        }
                    });

                    // click on cancel, nothing change
                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
                // click on edit button
                else if(menuItemId == R.id.editInformation){
                    // pop up edit dialog
                    editInfoDialog = new EditInfoDialog(element, myViewModel);
                    editInfoDialog.showDialog(ShowImageActivity.this);
                }
                return true;
            }
        });
    }

    /**
     * Initialize tool bar.
     */
    private void initToolBar(){
        topBar.inflateMenu(R.menu.top_navigation);
        // click BACK button
        topBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageActivity.this.finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // if latitude and longitude are available, add marked in map
        if ((latitude <= 90.00) && (-90.00<=latitude)&& (longitude <= 180.00) && (-180<=longitude)) {
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        }else{
            // if latitude and longitude are unavailable, do not show map
            fragmentTransaction.remove(mapFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}