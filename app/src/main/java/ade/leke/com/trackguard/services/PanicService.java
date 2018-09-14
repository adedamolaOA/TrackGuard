package ade.leke.com.trackguard.services;


/**
 * Created by SecureUser on 10/28/2015.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ade.leke.com.trackguard.R;
import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;

/**
 * Created by SecureUser on 10/16/2015.
 */
public class PanicService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static String TAG = "Mobile Guard";

    double latitude ;
    double longitude;
    String uuid;
    boolean  movementState = true;
    String address;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    int count=0;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    AppLocationService appLocationService;
    Location location;
    GPSTracker gps;
    int size=0;
    LocationAddress locationAddress;
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


        appLocationService = new AppLocationService(
                this);
        location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);


        gps  = new GPSTracker(getApplicationContext());

        locationAddress = new LocationAddress();

        uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID)+UUID.randomUUID().toString();
        new LongRunningGetIO().execute();
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
                Thread.sleep(120000);
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

        @Override
        protected void onPreExecute() {

            location = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);

            if(location!=null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                List<String> providerList = locationManager.getAllProviders();
                if (location != null) {


                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocationPanic(uuid, location.getLatitude(), location.getLongitude(),
                            getApplicationContext(), new GeocoderHandler());




                } else {

                    if (gps.canGetLocation()) {
                        if (null != providerList && providerList.size() > 0) {



                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocationPanic(uuid, latitude, longitude,
                                    getApplicationContext(), new GeocoderHandler());
                            gps.stopUsingGPS();

                        }



                    }
                }

            }else{
                location = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);
                if(location!=null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                    List<String> providerList = locationManager.getAllProviders();
                    if (location != null) {


                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocationPanic(uuid, location.getLatitude(), location.getLongitude(),
                                getApplicationContext(), new GeocoderHandler());




                    } else {

                        if (gps.canGetLocation()) {
                            if (null != providerList && providerList.size() > 0) {



                                latitude = gps.getLatitude();
                                longitude = gps.getLongitude();
                                LocationAddress locationAddress = new LocationAddress();
                                locationAddress.getAddressFromLocationPanic(uuid, latitude, longitude,
                                        getApplicationContext(), new GeocoderHandler());
                                gps.stopUsingGPS();

                            }



                        }
                    }

                }else{
                    location = appLocationService
                            .getLocation(LocationManager.PASSIVE_PROVIDER);
                    if(location!=null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


                        List<String> providerList = locationManager.getAllProviders();
                        if (location != null) {


                            LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocationPanic(uuid, location.getLatitude(), location.getLongitude(),
                                    getApplicationContext(), new GeocoderHandler());




                        } else {

                            if (gps.canGetLocation()) {
                                if (null != providerList && providerList.size() > 0) {



                                    latitude = gps.getLatitude();
                                    longitude = gps.getLongitude();
                                    LocationAddress locationAddress = new LocationAddress();
                                    locationAddress.getAddressFromLocationPanic(uuid, latitude, longitude,
                                            getApplicationContext(), new GeocoderHandler());
                                    gps.stopUsingGPS();

                                }



                            }
                        }

                    }
                }
            }
        }




        @Override

        protected String doInBackground(Void... params) {


            try {
                System.out.println(count);
                count++;
                if(count==size){
                    movementState = false;
                    stopSelf();
                }else {
                    ade.leke.com.trackguard.db.db.entities.Settings ss = new SettingsProfile(getBaseContext()).get();
                    Long interval = Long.parseLong(ss.getpInterval());

                    ////5min --> 300000,10min --> 600000 milliseconds,15min --> 900000 milliseconds,20min -->1200000 milliseconds , 30min -->1800000 milliseconds, 1hr --> 3600000 milliseconds

                    if (300000 == interval) {
                        size = 12;
                        interval = 300000L;
                    } else if (600000 == interval) {
                        size = 6;
                        interval = 600000L;
                    } else if (900000 == interval) {
                        size = 4;
                        interval = 900000L;
                    } else if (1200000 == interval) {
                        size = 3;
                        interval = 1200000L;
                    } else if (1800000 == interval) {
                        size = 2;
                        interval = 1800000L;
                    } else if (3600000 == interval) {
                        size = 1;
                        interval = 3600000L;
                    }

                    Thread.sleep(interval);//15 Min : 900000
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            System.out.println(count);
            /*
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/logmovement/1/"+uuid +"/"+ latitude+"/"+longitude+"/P");
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }*/

            return UUID.randomUUID().toString();

        }

        protected void onPostExecute(String results) {
            if (results!=null) {


                if(movementState) {
                    new LongRunningGetIO().execute();
                }else{
                    stopSelf();
                }


            }



        }

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            double lat=0D;
            double lng=0D;
            switch (message.what) {
                case 1:
                    System.out.println("--------------------------------------------------------- was here");
                    Bundle bundle = message.getData();
                    lat = bundle.getDouble("lat");
                    lng = bundle.getDouble("lng");
                    locationAddress = bundle.getString("address");

                    break;
                case 2:
                    System.out.println("--------------------------------------------------------- was here 3");
                    Bundle bundle1 = message.getData();
                    lat = bundle1.getDouble("lat");
                    lng = bundle1.getDouble("lng");
                    locationAddress = bundle1.getString("address");
                    break;
                default:
                    System.out.println("--------------------------------------------------------- was here too");
                    locationAddress = getString(R.string.error_loc_latlng);

            }sendSmsByManager(uuid,locationAddress,lat,lng);





        }
    }
    public void sendSmsByManager(String uid,String address,double lat,double lng) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            List<Contact> contacts = new ContactProfile(getBaseContext()).getAllContacts();
            for(Contact c: contacts) {
                if(c.getStatus().equalsIgnoreCase("1")) {
                    smsManager.sendTextMessage(c.getPhoneNumber(),
                            null,
                            "MGpanic:" + uid + ":" + lat + ":" + lng,
                            null,
                            null);
                    Log.d("Send SMS", "Sent msg to " + c.getPhoneNumber());
                }else{
                    smsManager.sendTextMessage(c.getPhoneNumber(),
                            null,
                            "MGpanic: Panic Location:" + address,
                            null,
                            null);
                    Log.d("Send SMS", "Sent msg to " + c.getPhoneNumber());
                }
            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

}