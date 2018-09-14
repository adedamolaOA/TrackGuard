package ade.leke.com.trackguard.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.db.db.entities.Reg;
import ade.leke.com.trackguard.db.db.entities.profile.RegistrationDate;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;

/**
 * Created by SecureUser on 10/28/2015.
 */
public class PayService extends Service {


    private static String TAG = "Mobile Guard";

    private double latitude;
    private double longitude;
    private String address;
    AppLocationService appLocationService;
    private Location location;
    LocationAddress locationAddress;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    boolean onStartState = false;
    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
        if(settings.getMGS().equalsIgnoreCase("1")) {

            startPayService();
        }

    }

    public void startPayService(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                onStartState = true;
                while(onStartState){
                    boolean state = getDateRequests();
                    Log.d(TAG, "Movement Log Paused");
                    Reg reg = new RegistrationDate(getBaseContext()).get();

                    System.out.println("Date: =============================== "+reg.getUpdateDate());
                    if(state){
                        //sendSmsByManager();

                        reg.setUpdateDate(new Date());
                        new RegistrationDate(getBaseContext()).update(reg);
                        Log.d(TAG, "Movement Log Updated");
                    }
                    try {
                        Thread.sleep(1200000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ((intent != null)){
            startPayService();
        return START_STICKY;

    }
        // Do your other onStartCommand stuff..
        return START_STICKY;

    }

    public boolean getDateRequests(){
        boolean state =false;
        //String dateStart = new RegistrationDate(getBaseContext()).get().getRegDate();


        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = new RegistrationDate(getBaseContext()).get().getUpdateDate();
            d2 = new Date();

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            Log.d("Mobile Guard",diffDays+" ***********************************");

            if(diffDays>=7){
                state = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        Log.d(TAG, "Movement Log destroyed");
    }


    public void sendSmsByManager() {
        try {
            String shortCode ="32811";
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(shortCode,
                        null,
                        "Mgloc reg",
                        null,
                        null);

                Log.d("Send SMS", "Sent Location");


        } catch (Exception ex) {
            Log.d(TAG, "Sending Failed");
        }
    }


}
