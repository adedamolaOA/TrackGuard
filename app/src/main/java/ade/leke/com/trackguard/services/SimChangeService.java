package ade.leke.com.trackguard.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Settings;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;

/**
 * Created by SecureUser on 10/28/2015.
 */
public class SimChangeService extends Service {


    private static String TAG = "Mobile Guard SIM Card";

    private double latitude;
    private double longitude;
    private String address;
    AppLocationService appLocationService;
    private Location location;
    LocationAddress locationAddress;
    User user;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    boolean onStartState = false;
    String sim;
    TelephonyManager telephoneMgr;

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        appLocationService = new AppLocationService(
                getBaseContext());


        //getLine1Number();
        //Toast.makeText(getBaseContext(),sim,Toast.LENGTH_LONG).show();
        user = new UserProfile(getBaseContext()).get();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sim = telephoneMgr.getSimSerialNumber();
        List<String> providerList = locationManager.getAllProviders();
        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
        try {
            if (settings.getMGS().equalsIgnoreCase("1") && !new UserProfile(getBaseContext()).get().getSim().equalsIgnoreCase(sim)) {

                if (user != null) {
                    new LongRunningGetIO().execute();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Location gpsLocation;

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        //telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @Override
        protected void onPreExecute() {

            telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            sim = telephoneMgr.getSimSerialNumber();
            gpsLocation = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation == null) {
                gpsLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null) {
                    double clatitude = gpsLocation.getLatitude();
                    double clongitude = gpsLocation.getLongitude();

                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocationSIM(clatitude, clongitude,
                            getApplicationContext(), new GeocoderHandler());
                } else {
                    gpsLocation = appLocationService
                            .getLocation(LocationManager.PASSIVE_PROVIDER);
                    if (gpsLocation != null) {
                        double clatitude = gpsLocation.getLatitude();
                        double clongitude = gpsLocation.getLongitude();

                        LocationAddress locationAddress = new LocationAddress();
                        locationAddress.getAddressFromLocationSIM(clatitude, clongitude,
                                getApplicationContext(), new GeocoderHandler());
                    }
                }

            } else {
                double clatitude = gpsLocation.getLatitude();
                double clongitude = gpsLocation.getLongitude();

                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocationSIM(clatitude, clongitude,
                        getApplicationContext(), new GeocoderHandler());
            }
        }

        @Override

        protected String doInBackground(Void... params) {


            String result = "";
            if (!user.getSim().equalsIgnoreCase(sim)) {


                if (gpsLocation != null) {


                } else {

                    sendSmsByManager(user, "that can not be retrieved");//"that can not be retrieved"

                }

            } else {
                SimChangeService.this.stopSelf();
            }


            //stopSelf();


            return UUID.randomUUID().toString();

        }

        protected void onPostExecute(String results) {
            if (results != null) {


                Log.d("Mobile Guard UUID", results);
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!user.getSim().equalsIgnoreCase(sim)) {
                    new LongRunningGetIO().execute();
                } else {
                    SimChangeService.this.stopSelf();
                }
            }


        }

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            String date;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    break;
                case 2:
                    Bundle bundle2 = message.getData();
                    locationAddress = bundle2.getString("address");
                default:
                    date = "";
                    locationAddress = "that can not be retrieved";
            }
            sendSmsByManager(user, locationAddress);//"that can not be retrieved"


        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ((intent != null)) {
            onStart(intent, startId);
            return START_STICKY;

        }
        // Do your other onStartCommand stuff..
        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        Log.d(TAG, "SIM Change Service");
    }


    public void sendSmsByManager(User user, String address) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();
            List<Contact> contacts = new ContactProfile(getBaseContext()).getAllContacts();

            for (Contact contact : contacts) {
                smsManager.sendTextMessage("32811",
                        null,
                        "mgloc sim",
                        null,
                        null);
                Log.d("Send SMS", "Sent msg to " + contact.getPhoneNumber());
                smsManager.sendTextMessage(contact.getPhoneNumber(),
                        null,
                        "Hi, " + user.getFirstname() + " " + user.getLastname() + " changed SIM from the following location " + address + ". Information Sent using Mobile Guard.",
                        null,
                        null);
                Log.d("Send SMS", "Sent msg to " + contact.getPhoneNumber());


            }

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

}
