/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
/**
 * <h1>Background location tracking service</h1>
 *
 * @author  Haobin Yuan
 * @version 1.0
 */
public class LocationService extends IntentService {
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    public LocationService(String name) {
        super(name);
    }

    public LocationService() {
        super("Location Intent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locResults = LocationResult.extractResult(intent);
            if (locResults != null) {
                for (Location location : locResults.getLocations()) {
                    if (location == null) continue;
                    Log.i("New Location", location.toString());
                    if (ShowPhotosActivity.getActivity() != null) {
                        ShowPhotosActivity.setLastLocation(locResults.getLastLocation());

                    }
                }
            }
        }
    }
}

