package ade.leke.com.trackguard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.services.PanicService;

public class PanicAlert extends ActionBarActivity {


    FeedReaderDbHelper mDbHelper;// = new FeedReaderDbHelper(getBaseContext());
    boolean panicState = true;
    TextView countdown;
    String mmsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_alert);
        try {
            Intent intent = getIntent();
            mDbHelper = new FeedReaderDbHelper(getBaseContext());
            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                mmsg = "" + latitude + ":" + longitude;

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }


            countdown = (TextView) findViewById(R.id.txtCount);
            startCountDown();
            getSupportActionBar().hide();

        }catch (Exception e){}

        Button pc = (Button) findViewById(R.id.butPanicCancel);
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter.cancel();

                Toast.makeText(getApplicationContext(), "Panic Alert Cancelled",
                        Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent(PanicAlert.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                PanicAlert.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();


            }
        });
    }

    public ArrayList<String> setContactListData()
    {

        ArrayList<String> numbers=new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " DESC";

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));


                String phone = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber));
                numbers.add(phone);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return numbers;

    }


    CountDownTimer counter;
    public void startCountDown(){
        counter =  new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdown.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                countdown.setText("0");
                startPanicTracker();
                sendSmsByManager();

                finish();
                Log.i("Content ", " Menu layout ");
            }
        }.start();

    }

    int mt = 0;

    public void startPanicTracker(){
        startService(new Intent(this, PanicService.class));

    }

    public void stopPanicAlert(){
        try {
            if (counter != null) {
                counter.cancel();
            }
            stopService(new Intent(this, PanicAlert.class));
            finish();
        }catch (Exception e){
            finish();
        }
    }

    public void sendSmsBySIntent() {
        // add the phone number in the data
        Uri uri = Uri.parse("smsto: "+setContactListData());

        Intent smsSIntent = new Intent(Intent.ACTION_SENDTO, uri);
        // add the message at the sms_body extra field
        smsSIntent.putExtra("sms_body", "A PANIC ALERT WAS SENT TO YOU, RESPOND IMMEDIATELY");
        try{
            startActivity(smsSIntent);
            Toast.makeText(PanicAlert.this,"Panic Alert Sent",Toast.LENGTH_SHORT).show();
            this.finish();
        } catch (Exception ex) {
            Toast.makeText(PanicAlert.this, "Your sms has failed...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
            this.finish();
        }
    }

    public void sendSmsByManager() {
        try {
            // Get the default instance of the SmsManager\
            User u = new UserProfile(getBaseContext()).get();
            SmsManager smsManager = SmsManager.getDefault();
            for(String phoneNumber: setContactListData()) {

                smsManager.sendTextMessage(phoneNumber,
                        null,
                        "Hi, your friend "+u.getFirstname()+" is sending you a panic alert using Mobile Guard.",
                        null,
                        null);
                Log.d("Send SMS","Sent msg to "+phoneNumber);
            }
            Toast.makeText(getApplicationContext(), "Panic Alert Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Panic Alert Failed",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_panic_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //Toast.makeText(getBaseContext(), loc.getLongitude()+" -- "+loc.getLatitude(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
