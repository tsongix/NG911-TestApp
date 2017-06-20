package com.android.albert.ng911bluetoothtester;

/**
 *
 * Created by Albert on 2/11/2016.TBC
 */

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;

public class LocationHelper {

    private Context context;
    LocationManager mLocationManager;

    /**
     * Criteria parameter will select best provider service based on:
     *      - Accuracy      - Power consumption
     *      - Response      - Monetary cost
     */
    private Criteria criteria;
    private String provider;

   // Location myLocation = getLocation(context);

    public LocationHelper(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        //set second parameter to true to get best provider
        provider = String.valueOf(mLocationManager.getBestProvider(criteria, true)).toString();
    }

    public Location getLocation(Context c) {
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found most accurate last known location: %s", l); How it works:
                // For example, if Location.getAccuracy returns 10, then there's a 68% chance the true location of the device is within
                // 10 meters of the reported coordinates.
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}

