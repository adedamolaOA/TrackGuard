package ade.leke.com.trackguard.services;

/**
 * Created by SecureUser on 10/28/2015.
 */


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;

import ade.leke.com.trackguard.common.GPSTracker;

/**
 * Created by SecureUser on 10/16/2015.
 */
public class MovementService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "Mobile Guard";

    double latitude ;
    double longitude;
    String uuid;
    boolean  movementState = true;
    GPSTracker gps;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        // First we need to check availability of play services
        if (checkPlayServices()) {


            buildGoogleApiClient();
            createLocationRequest();
        }
        //togglePeriodicLocationUpdates();


        gps = new GPSTracker(getBaseContext());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                while(movementState) {
                    i++;
                    if (checkPlayServices()) {

                        // Building the GoogleApi client
                        uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);

                        displayLocation();
                        //togglePeriodicLocationUpdates();

                    } else {


                        if (gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();


                        } else {

                        }
                        Log.d(TAG, "Movement Log: " + i);
                        new LongRunningGetIO().execute();
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(i==6){
                            movementState = false;
                            stopSelf();
                        }
                    }
                }
            }
        });
        t.start();




        //this.stopSelf();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        movementState = false;
        System.out.println(movementState);
        gps.stopUsingGPS();
        super.onDestroy();
        Log.d(TAG, "Movement Log destroyed");
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        displayLocation();
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            new LongRunningGetIO().execute();
        } else {

            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();


            } else {

            }
            Log.d(TAG, "Movement Log");
            new LongRunningGetIO().execute();
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            // Starting the location updates
            startLocationUpdates();


        } else {
            // Changing the button text

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                //PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                //finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override

        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/logmovement/1/"+uuid +"/"+ latitude+"/"+longitude+"/M");
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;

        }

        protected void onPostExecute(String results) {
            if (results!=null) {




            }



        }

    }

}