package ade.leke.com.trackguard.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import ade.leke.com.trackguard.R;
import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.db.db.entities.Movement;
import ade.leke.com.trackguard.db.db.entities.profile.MovementProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;

/**
 * Created by SecureUser on 10/16/2015.
 */
public class LogMovementService extends Service  {

    private static String TAG = "Mobile Guard";

    double latitude ;
    double longitude;
    String uuid;
    boolean  movementState = true;

    LocationAddress locationAddress;

    AppLocationService appLocationService;
    Location location;
    GPSTracker gps;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        super.onStart(intent, startId);

        appLocationService = new AppLocationService(
                this);
        location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);


        gps  = new GPSTracker(getApplicationContext());

        locationAddress = new LocationAddress();

        uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
        if(settings.getMGS().equalsIgnoreCase("1")||settings.getGoDark().equalsIgnoreCase("1")) {
            new LongRunningGetIO().execute();
        }else{
            stopSelf();
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ((intent != null)){

            onStart(intent, startId);
            return START_STICKY;

        }
        // Do your other onStartCommand stuff..
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        movementState = false;
        System.out.println(movementState);
        stopSelf();
        //gps.stopUsingGPS();
        super.onDestroy();
        Log.d(TAG, "Movement Log destroyed");
    }










    String address="No Address Found";
    /**
     * Starting the location updates
     * */
    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            location = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);

            if(location!=null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                locationAddress.getAddressFromLocation(latitude, longitude,
                        getBaseContext(), new GeocoderHandler());
            }else{
                location = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);
                if(location!=null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getBaseContext(), new GeocoderHandler());
                }else{
                    location = appLocationService
                            .getLocation(LocationManager.PASSIVE_PROVIDER);
                    if(location!=null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getBaseContext(), new GeocoderHandler());
                    }else{
                        latitude=0D;
                        longitude=0D;
                    }
                }
            }
        }




        @Override

        protected String doInBackground(Void... params) {

           // if (gps.canGetLocation()) {
            //    gps.getLocation();
            //    latitude = gps.getLatitude();
             //   longitude = gps.getLongitude();




            //you can hard-code the lat & long if you have issues with getting it
            //remove the below if-condition and use the following couple of lines
            //double latitude = 37.422005;
            //double longitude = -122.084095

            if (location != null && longitude!=0 && latitude!=0) {






                Movement movement = new Movement();
                movement.setMid(0);
                movement.setUid(UUID.randomUUID().toString());
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                movement.setMDate(format.format(new Date()));
                movement.setLng(longitude + "");
                movement.setLat(latitude + "");
                movement.setAddress(address);
                System.out.println("================================================================was here");
                boolean state = new MovementProfile(getBaseContext()).create(movement);

                try {
                    ade.leke.com.trackguard.db.db.entities.Settings ss = new SettingsProfile(getBaseContext()).get();
                    //Log.d("Settings",ss.getmInterval());
                    Long interval = 300000L;
                    if(ss!=null) {

                        if(ss.getmInterval().equalsIgnoreCase("600000")){
                              interval = 600000L;
                        }else if(ss.getmInterval().equalsIgnoreCase("900000")){
                            interval = 900000L;
                        }else if(ss.getmInterval().equalsIgnoreCase("1200000")){
                            interval = 1200000L;
                        }else if(ss.getmInterval().equalsIgnoreCase("1800000")){
                            interval = 1800000L;
                        }else if(ss.getmInterval().equalsIgnoreCase("3600000")){
                            interval = 3600000L;
                        }
                        Log.d("Settings",ss.getmInterval());
                    }

                    Thread.sleep(interval);//15 Min : 900000
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {


                if (gps.canGetLocation()) {
                    Movement movement = new Movement();
                    movement.setMid(0);
                    movement.setUid(UUID.randomUUID().toString());
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    movement.setMDate(format.format(new Date()));
                    movement.setLng(gps.getLongitude() + "");
                    movement.setLat(gps.getLatitude() + "");
                    movement.setAddress(address);
                    if(gps.getLongitude()!=0 && gps.getLatitude()!=0) {
                        boolean state = new MovementProfile(getBaseContext()).create(movement);

                        gps.stopUsingGPS();
                        try {
                            ade.leke.com.trackguard.db.db.entities.Settings ss = new SettingsProfile(getBaseContext()).get();
                            //Log.d("Settings",ss.getmInterval());
                            Long interval = 300000L;
                            if (ss != null) {
                                interval = Long.parseLong(ss.getmInterval());
                            }
                            Thread.sleep(interval);//15 Min : 900000
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            ade.leke.com.trackguard.db.db.entities.Settings ss = new SettingsProfile(getBaseContext()).get();
                            //Log.d("Settings",ss.getmInterval());
                            Long interval = 300000L;
                            if (ss != null) {
                                interval = Long.parseLong(ss.getmInterval());
                            }
                            Thread.sleep(interval);//15 Min : 900000
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

           // } else {

           // }
            Log.d(TAG, "Movement Log 46");

            return UUID.randomUUID().toString();

        }

        protected void onPostExecute(String results) {
            if (results!=null) {


                 new LongRunningGetIO().execute();

            }



        }

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    System.out.println("--------------------------------------------------------- was here");
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    break;
                default:
                    System.out.println("--------------------------------------------------------- was here too");
                    locationAddress = getString(R.string.error_loc_latlng);
            }address = locationAddress;





        }
    }

}