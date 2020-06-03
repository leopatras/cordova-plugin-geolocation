/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at
         http://www.apache.org/licenses/LICENSE-2.0
       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */


package org.apache.cordova.geolocation;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import javax.security.auth.callback.Callback;

public class Geolocation extends CordovaPlugin {
    private static final long MIN_SEARCH_TIME = 10000;
    private static final float DISTANCE_IN_M  = 10;
    private static final String NOK = "location services not enabled";
    boolean mInGetLocation = false;
    boolean mPermissionGranted = false;
    String TAG = "GeolocationPlugin";
    CallbackContext context;
    LocationManager locationManager=null;

    String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };


    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(TAG, "We are entering execute");
        context = callbackContext;
        if(action.equals("getPermission"))
        {
            if(hasPermisssion())
            {
                PluginResult r = new PluginResult(PluginResult.Status.OK);
                context.sendPluginResult(r);
                return true;
            }
            else {
                PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return true;
        } else if(action.equals("getLocation")) {
            if(hasPermisssion()) {
               getLocation();
            } else {
               mInGetLocation=true;
               PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return true;
        }
        return false;
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if(context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    LOG.d(TAG, "Permission Denied!");
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return;
                }

            }
            if (mInGetLocation) {
               mInGetLocation=false;
               getLocation();
            } else {
               result = new PluginResult(PluginResult.Status.OK);
               context.sendPluginResult(result);
            }
        }
    }

    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }

    private LocationManager getLocationManager() {
        return (LocationManager) cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }
    public JSONObject l2J(Location location) throws JSONException {
                JSONObject o = new JSONObject();
                o.put("latitude", location.getLatitude());
                o.put("longitude", location.getLongitude());
                o.put("timestamp", location.getTime());
                if (location.hasAltitude()) {
                  o.put("altitude", location.getAltitude());
                }
                if (location.hasAltitude()) {
                  o.put("altitude", location.getAltitude());
                }
                if (location.hasAccuracy()) {
                  o.put("accuracy", location.getAccuracy());
                }
                return o;
    }

    private void getLocation() {
        if (locationManager==null) {
           locationManager = getLocationManager();
        }
        final LocationListener locationListener = new LocationListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onLocationChanged(Location location) {
                //Log.v("public void onLocationChanged(location='", location, "')"); // LOG GENERATED
                //Leo: don't know if this is actually needed
                locationManager.removeUpdates(this);
                JSONObject o=null;
                try {
                  o = l2J(location);
                } catch (JSONException e) {
                  e.printStackTrace();
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK, o);
                context.sendPluginResult(result);
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public void onProviderDisabled(String provider) {
                LOG.d("public void onProviderDisabled(provider='", provider, "')"); 
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public void onProviderEnabled(String provider) {
                LOG.d("public void onProviderEnabled(provider='", provider, "')"); 
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LOG.d("public void onStatusChanged(provider='", provider, "', status='", status, "', extras='", extras, "')"); 
            }
        };
        // Request location updates for GPS and Network providers
        int providers = 0;
        try {
            //noinspection ResourceType
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_SEARCH_TIME, DISTANCE_IN_M, locationListener);
            ++providers;
        } catch (IllegalArgumentException e) {
            LOG.d(TAG, "GPS not available");
        }
        try {
            //noinspection ResourceType
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_SEARCH_TIME, DISTANCE_IN_M, locationListener);
            ++providers;
        } catch (IllegalArgumentException e) {
            LOG.d(TAG, "Network location not available");
        }
        if (providers == 0) {
          PluginResult result= new PluginResult(PluginResult.Status.ERROR, "Location updated not available");
          context.sendPluginResult(result);
        }
     }
}
