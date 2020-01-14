package uk.ac.shef.oak.com6510;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a14290.photolocation.R;

import uk.ac.shef.oak.com6510.database.ImageElement;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
/**
 * <h1>The main activity to show all photos.</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class ShowPhotosActivity extends AppCompatActivity {
    private static final String TAG = "ShowPhotosActivity";
    private TextView mTextMessage;
    private static Activity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private PendingIntent mLocationPendingIntent;
    private LocationRequest mLocationRequest;
    private Intent intent;
    private static final int ACCESS_FINE_LOCATION = 123;
    private static Location lastLocation;
    private static final int WRITE_EXTERNAL_STORAGE = 321;
    private static final int ACCESS_CAMERA = 666;
    private MyViewModel myViewModel;
    static ProgressDialog progressDialog = null;
    private ViewPager viewPagerPhotos;
    private int numberOfColumns = 2;
    Bundle savedInstanceState1;
    private static boolean storageIsGranted = false;
    private boolean isCamera = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState1 = savedInstanceState;
        setContentView(R.layout.activity_show_photo);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        activity= this;
        setActivity(activity);

        // initial tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Photos"),0,true);
        tabLayout.addTab(tabLayout.newTab().setText("Albums"),1);
        tabLayout.addTab(tabLayout.newTab().setText("Discover"),2);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // initial viewpager
        viewPagerPhotos = (ViewPager) findViewById(R.id.viewpager);
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPagerPhotos.setAdapter(viewPagerAdapter);
        viewPagerPhotos.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(mOnTabSelectedListener);

        // set main page
        viewPagerPhotos.setCurrentItem(0);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // initial location service parameters
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();
        startLocationUpdates(getApplicationContext());


        // initial EasyImage
        initEasyImage();

         myViewModel.getAll().observe(ShowPhotosActivity.this, new Observer<List<ImageElement>>() {
                @Override
                public void onChanged(@Nullable List<ImageElement> images) {
                    viewPagerPhotos.setCurrentItem(0);
                }
         });
    }

    // activity destroy, stop location service
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mFusedLocationClient.removeLocationUpdates(mLocationPendingIntent);
    }
    /**
     * Set location of taken photos
     * @param location current location
     */
    public static void setLastLocation(Location location){
        lastLocation = location;
    }

    /**
     *initialize EasyImage
     */
    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage Library")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {

                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null){
                        photoFile.delete();}
                }
            }
        });
    }

    /**
     *Handle the returned photos
     * @param  returnedPhotos list of returned photos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
            // photos are taken, pop up dialog to edit information
            TakePhotoDialog dialog = new TakePhotoDialog(returnedPhotos, myViewModel, lastLocation);
            dialog.showDialog(ShowPhotosActivity.getActivity());
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
    public static void setActivity(Activity activity){ShowPhotosActivity.activity = activity;}

    /**
     *Start location update service
     * @param  context context
     */
    //start location service
    private void startLocationUpdates(Context context) {
        intent = new Intent(context, LocationService.class);

        mLocationPendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            final Task<Void> locationTask = mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationPendingIntent);
            if (locationTask != null) {
                locationTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            Log.w("MapsActivity", ((ApiException) e).getStatusMessage());
                        } else {
                            Log.w("MapsActivity", e.getMessage());
                        }
                    }
                });

                locationTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("MapsActivity", "restarting gps successful!");
                    }
                });
            }
        }
    }

    // resume activity, restart location service
    @Override
    protected void onResume() {
        super.onResume();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(8000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setActivity(this);
    }

    /**
     *Check location permission
     */
    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // if location permission is denied, ask for it
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, ACCESS_FINE_LOCATION);
                return;
            }
        }
    }

    /**
     *Check camera permission
     */
    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // if camera permission is denied, ask for it
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, ACCESS_CAMERA);
                return;
            }
        }
    }

    /**
     *Check external permission
     */
    private void checkExternalPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            ShowPhotosActivity.storageIsGranted = true;
            // if write external storage permission is denied, ask for it
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates(getApplicationContext());
                } else {
                    // start activity of settings
                    Toast.makeText(this, "Grant location permission in setting", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case WRITE_EXTERNAL_STORAGE:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShowPhotosActivity.storageIsGranted = true;
                } else {
                    Toast.makeText(this, "Grant storage permission in settings", Toast.LENGTH_SHORT).show();
                    setStorageIsGranted(false);
                }
                return;
            }

            case ACCESS_CAMERA:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCamera = true;
                } else {
                    // start activity of settings
                    Toast.makeText(this, "Grant camera permission in settings", Toast.LENGTH_SHORT).show();
                    isCamera = false;
                }
                return;
            }
        }
    }

    /**
     *Set progressDialog
     * @param progressDialog progressDialog
     */
    public static void  setProgressDialog(ProgressDialog progressDialog){
        ShowPhotosActivity.progressDialog =  progressDialog;
    }

    /**
     *Get progressDialog
     * @return ProgressDialog
     */
    public static ProgressDialog getProgressDialog(){
        return progressDialog;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //click on search, start activity for searching
                case R.id.navigation_search:
                    Intent search_intent = new Intent(ShowPhotosActivity.this,SearchPhotoActivity.class);
                    startActivity(search_intent);
                    return true;
                //click on search, start activity for showing photos on map
                case R.id.navigation_map:
                    Intent map_intent = new Intent(ShowPhotosActivity.this,ShowMapsActivity.class);
                    startActivity(map_intent);
                    progressDialog = new ProgressDialog(ShowPhotosActivity.this);
                    setProgressDialog(progressDialog);
                    progressDialog.setTitle("Completing the map");
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    return true;

                //click on search, open gallery
                case R.id.navigation_gallery:
                    EasyImage.openGallery(getActivity(), 0);
                    checkExternalPermission();
                    return true;

                //click on search, open camera to take photos
                case R.id.navigation_camera:

                    PackageManager pm = getPackageManager();
                    // check if the device has camera or not
                    if (checkCameraHardware(getApplicationContext())) {
                        //if there is camera, user can take photos
                        EasyImage.openCamera(getActivity(), 0);
                        checkExternalPermission();
                    }
                    return true;

            }
            return false;
        }
    };

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener(){
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPagerPhotos.setCurrentItem(tab.getPosition());
            switch (tab.getPosition()) {

                // slide to photos fragment
                case 0:
                    viewPagerPhotos.setCurrentItem(0);
                    break;

                // slide to album fragment
                // notify adapter to change gallery when data change
                case 1:
                    RecyclerView albumsRecyclerView =  findViewById(R.id.albums_grid_recycler_view);
                    albumsRecyclerView.setLayoutManager(new LinearLayoutManager(ShowPhotosActivity.getActivity().getApplicationContext()));
                    final AlbumAdapter albumAdapter = new AlbumAdapter();
                    albumsRecyclerView.setAdapter(albumAdapter);
                    myViewModel = ViewModelProviders.of(ShowPhotosActivity.this).get(MyViewModel.class);
                    myViewModel.getAll().observe(ShowPhotosActivity.this, new Observer<List<ImageElement>>() {
                        @Override
                        public void onChanged(@Nullable List<ImageElement> imageElements) {
                            List<ImageOnDate> emptyList = new ArrayList<>();
                            // if data are empty, clear gallery
                            if (imageElements.size() == 0 || imageElements == null){
                                albumAdapter.setImages(emptyList);
                            }else{
                                // group and display photos
                                int numberOfUniqueDate = Util.getNumberOfUniqueDate(imageElements);
                                ArrayList<String> uniqueDateList = Util.getUniqueDateList(imageElements);
                                List<ImageOnDate> imageOnDates = new ArrayList<>();
                                // group photos in different date
                                for (int i = 0; i<numberOfUniqueDate; i++){
                                    String specificDate = uniqueDateList.get(i);
                                    ImageOnDate imageOnDate = new ImageOnDate(Util.getListOnDate(imageElements, specificDate));
                                    imageOnDates.add(imageOnDate);
                                }
                                List<ImageOnDate> groups = imageOnDates;

                                // notify adapter to response
                                albumAdapter.setImages(groups);
                            }
                        }
                    });

                    break;

                case 2:
                    viewPagerPhotos.setCurrentItem(2);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }

    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // make this comment to make sure the app would not crash if users deny permissions
        // while app is running
         super.onSaveInstanceState(outState);
    }

    /**
     *Check camera hardware
     * @return  boolean
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     *Get indicator of storage permission
     * @return  boolean
     */
    public static boolean getStorageIsGranted(){
        return storageIsGranted;
    }

    /**
     *Set indicator of storage permission
     * @param  storageIsGranted
     */
    public static void setStorageIsGranted(boolean storageIsGranted){
        ShowPhotosActivity.storageIsGranted = storageIsGranted;
    }


}
