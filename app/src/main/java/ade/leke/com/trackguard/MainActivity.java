package ade.leke.com.trackguard;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
//import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Notification;
import ade.leke.com.trackguard.db.db.entities.Panic;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.NotificationProfile;
import ade.leke.com.trackguard.db.db.entities.profile.RegistrationDate;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.services.LogMovementService;
import ade.leke.com.trackguard.services.MovementService;
import ade.leke.com.trackguard.services.PayService;
import ade.leke.com.trackguard.services.SimChangeService;

public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    FeedReaderDbHelper mDbHelper;
    LocationManager locationManager;
    String provider;
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

    private boolean panicState = false;
    private String mobile = "";
    private String cMobile = "";
    private String firstname = "";
    private String lastname = "";
    ContactProfile contactProfile;
    UserProfile userProfile;
    TextView lblWarning;
    AppLocationService appLocationService;
    Handler handler;
    boolean serviceState;
    ImageButton create;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Panic);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context cc = this.getApplicationContext();


        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        serviceState = getIntent().getBooleanExtra("serviceState", false);// intentMain.putExtra("serviceState",true);
        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        contactProfile = new ContactProfile(getBaseContext());

        SpannableString s = new SpannableString("Mobile Guard");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        SpannableString sl = new SpannableString("Mis-use of this emergency service will lead to serious legal actions.");
        sl.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, sl.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        appLocationService = new AppLocationService(
                MainActivity.this);



        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_S));

        try {
            TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String sim = telephoneMgr.getSimSerialNumber();
            if (!new UserProfile(getBaseContext()).get().getSim().equalsIgnoreCase(sim)) {
                getSIMChange();
            }
        }catch (Exception e){

        }
        // Toast.makeText(getBaseContext(),new RegistrationDate(getBaseContext()).get().getRegDate().toGMTString(),Toast.LENGTH_LONG).show();

        SpannableString sll = new SpannableString(contactProfile.contactCount() + "");
        sll.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, sll.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        lblWarning = (TextView) findViewById(R.id.lblWarning);
        lblWarning.setText(sl);
        TextView lblFriendCount = (TextView) findViewById(R.id.lblFriendCount);
        lblFriendCount.setText(sll);
        create = (ImageButton) findViewById(R.id.butHomePanicAlert);

        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
        if (settings.getMGS().equalsIgnoreCase("0")) {
            create.setImageResource(R.drawable.panic_off);
        }
        // First we need to check availability of play services
        final boolean checkPlayService = checkPlayServices();
        if (checkPlayService) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();

        }


        // Show location button click listener


        // new LongRunningPanicNotification().execute();


    }

    /*public void sendSmsByManager() {
        try {
            String shortCode = "32811";
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(shortCode,
                    null,
                    "mgloc reg",
                    null,
                    null);

            Log.d("Send SMS", "Sent Location");


        } catch (Exception ex) {
            Log.d("Mobile Guard", "Sending Failed");
        }
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//displayLocation();
                try {
                    ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
                    if (settings.getMGS().equalsIgnoreCase("1")) {
                        UserProfile x = new UserProfile(getBaseContext());
                        if (!x.getRegistration()) {

                            Intent intentMain = new Intent(MainActivity.this,
                                    RegistrationActivity.class);
                            MainActivity.this.startActivity(intentMain);
                            finish();
                        } else {
                            if (new ContactProfile(getBaseContext()).contactCount() != 0) {
                                GPSTracker gps = new GPSTracker(MainActivity.this);
                                if (gps.canGetLocation()) {

                                    panicState = false;
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    new LongRunningPanicNotification().execute();

                                    Toast.makeText(getBaseContext(), "Location Retrieved", Toast.LENGTH_LONG).show();
                                    Intent intentMain = new Intent(MainActivity.this,
                                            PanicAlert.class);
                                    intentMain.putExtra("loc", "" + latitude + ":" + longitude);
                                    MainActivity.this.startActivity(intentMain);
                                    //finish();


                                } else {
                                    double clatitude = publicLatitude;
                                    double clongitude = publicLongtuide;
                                    if (clatitude == 0 && clongitude == 0) {
                                        showSettingsAlert();
                                    } else {

                                        Intent intentMain2 = new Intent(MainActivity.this,
                                                PanicAlert.class);
                                        intentMain2.putExtra("loc", "" + clatitude + ":" + clongitude);
                                        MainActivity.this.startActivity(intentMain2);
                                        //finish();
                                    }
                                }

                            } else {
                                Toast.makeText(getBaseContext(), "No Panic Contact", Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(MainActivity.this,
                                        ContactActivity.class);
                                MainActivity.this.startActivity(intentMain);
                                finish();
                            }
                        }

                    } else {
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                MainActivity.this);

                        SpannableString s = new SpannableString("Mobile Guard Services");
                        s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderInner.setTitle(s);
                        s = new SpannableString("Mobile Guard Services have been switch off.\nDo you wish to switch the services ON?");
                        s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builderInner.setMessage(s);
                        builderInner.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(MainActivity.this).get();
                                        settings.setMGS("1");
                                        boolean update = new SettingsProfile(MainActivity.this).update(settings);
                                        if (update) {
                                            startService(
                                                    new Intent(getBaseContext(), PayService.class));
                                            startService(
                                                    new Intent(getBaseContext(), LogMovementService.class));

                                            startService(
                                                    new Intent(getBaseContext(), SimChangeService.class));
                                            //sendSmsByManager();
                                            Toast.makeText(MainActivity.this, "Mobile Guard Service ON", Toast.LENGTH_LONG).show();
                                            create.setImageResource(R.drawable.panic);
                                        }

                                    }
                                });
                        builderInner.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                }catch (Exception e){

                }
            }
        });

        ImageButton notification = (ImageButton) findViewById(R.id.butNotification);
        boolean stated = new NotificationProfile(getBaseContext()).newNotification();
        if (stated) {
            notification.setImageResource(R.drawable.new_notification);
        }
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
                if (settings.getMGS().equalsIgnoreCase("1")) {

                    //you can hard-code the lat & long if you have issues with getting it
                    //remove the below if-condition and use the following couple of lines
                    //double latitude = 37.422005;
                    //double longitude = -122.084095

                /*if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,
                            getApplicationContext(), new GeocoderHandler());
                } else {

                }*/
                    UserProfile x = new UserProfile(getBaseContext());
                    if (!x.getRegistration()) {

                        Intent intentMain = new Intent(MainActivity.this,
                                RegistrationActivity.class);
                        MainActivity.this.startActivity(intentMain);
                        finish();
                    } else {
                        Intent intentMain = new Intent(MainActivity.this,
                                NotificationActivity.class);
                        MainActivity.this.startActivity(intentMain);
                        finish();

                    }
                } else {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                            MainActivity.this);

                    SpannableString s = new SpannableString("Mobile Guard Services");
                    s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setTitle(s);
                    s = new SpannableString("Mobile Guard Services have been switch off. \nDo you wish to switch the services ON?");
                    s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setMessage(s);
                    builderInner.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(MainActivity.this).get();
                                    settings.setMGS("1");
                                    boolean update = new SettingsProfile(MainActivity.this).update(settings);
                                    if (update) {
                                        startService(
                                                new Intent(getBaseContext(), PayService.class));
                                        startService(
                                                new Intent(getBaseContext(), LogMovementService.class));

                                        startService(
                                                new Intent(getBaseContext(), SimChangeService.class));
                                        //sendSmsByManager();
                                        Toast.makeText(MainActivity.this, "Mobile Guard Service ON", Toast.LENGTH_LONG).show();
                                        create.setImageResource(R.drawable.panic);
                                    }

                                }
                            });
                    builderInner.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            });
                    builderInner.show();
                }
            }
        });


        if (settings.getMGS().equalsIgnoreCase("1")) {
            UserProfile x = new UserProfile(getBaseContext());
            if (x.getRegistration()) {

                if (!isMovementServiceRunning() && !isPayServiceRunning() && !serviceState) {
                    System.out.println("********************************************************** were here");
                    startLogMovement();
                }
            }
        } else {
            if (isMovementServiceRunning() && isPayServiceRunning()) {
                stopService(
                        new Intent(getBaseContext(), PayService.class));
                stopService(
                        new Intent(getBaseContext(), LogMovementService.class));

                stopService(
                        new Intent(getBaseContext(), SimChangeService.class));
            }
        }

        startPayService();
        startLogMovement();

        ImageButton tracker = (ImageButton) findViewById(R.id.butTrackMyMovement);
        tracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile x = new UserProfile(getBaseContext());
                if (!x.getRegistration()) {

                    Intent intentMain = new Intent(MainActivity.this,
                            RegistrationActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    finish();
                } else {

                    //togglePeriodicLocationUpdates();

                    Location gpsLocation = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        System.out.println("================================================================was here");

                        double clatitude = gpsLocation.getLatitude();
                        double clongitude = gpsLocation.getLongitude();

                        Intent intentMain = new Intent(MainActivity.this,
                                MovementMapActivity.class);
                        intentMain.putExtra("date", "My Current Location");
                        intentMain.putExtra("map_state", false);
                        intentMain.putExtra("lat", clatitude + "");
                        intentMain.putExtra("lng", clongitude + "");
                        MainActivity.this.startActivity(intentMain);
                        finish();
                    } else {

                        double clatitude = publicLatitude;
                        double clongitude = publicLongtuide;
                        if (clatitude == 0 && clongitude == 0) {
                            GPSTracker gps = new GPSTracker(MainActivity.this);
                            if (gps.canGetLocation()) {

                                panicState = false;
                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                gps.stopUsingGPS();

                                //new LongRunningPanicNotification().execute();

                                Intent intentMain = new Intent(MainActivity.this,
                                        MovementMapActivity.class);
                                intentMain.putExtra("date", "My Current Location");
                                intentMain.putExtra("map_state", false);
                                intentMain.putExtra("lat", latitude + "");
                                intentMain.putExtra("lng", longitude + "");
                                MainActivity.this.startActivity(intentMain);
                                finish();

                            } else {
                                gpsLocation = appLocationService
                                        .getLocation(LocationManager.PASSIVE_PROVIDER);
                                if (gpsLocation != null) {
                                    System.out.println("==========================*************==========================was here");

                                    double cclatitude = gpsLocation.getLatitude();
                                    double cclongitude = gpsLocation.getLongitude();

                                    Intent intentMain = new Intent(MainActivity.this,
                                            MovementMapActivity.class);
                                    intentMain.putExtra("date", "My Current Location");
                                    intentMain.putExtra("map_state", false);
                                    intentMain.putExtra("lat", cclatitude + "");
                                    intentMain.putExtra("lng", cclongitude + "");
                                    MainActivity.this.startActivity(intentMain);
                                    finish();
                                } else {
                                    showSettingsAlert();
                                }

                            }

                        } else {
                            Intent intentMain = new Intent(MainActivity.this,
                                    MovementMapActivity.class);
                            intentMain.putExtra("date", "My Current Location");
                            intentMain.putExtra("map_state", false);
                            intentMain.putExtra("lat", clatitude + "");
                            intentMain.putExtra("lng", clongitude + "");
                            MainActivity.this.startActivity(intentMain);
                            finish();
                        }
                    }
                    //startMovementTracker();
                }
            }
        });

    }


    private boolean isMovementServiceRunning() {
        boolean state = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> m = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo n : m) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++" + n.service.getClassName());
            if (n.service.getClassName().equals("ade.leke.com.trackguard.services.LogMovementService")) {//ade.leke.com.trackguard.services.PayService


                state = n.started;

            }

        }
        return state;
    }

    private boolean isPayServiceRunning() {
        boolean state = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> m = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo n : m) {
            if (n.service.getClassName().equals("ade.leke.com.trackguard.services.PayService")) {//ade.leke.com.trackguard.services.PayService
                if (n.started) {
                    state = true;
                }
            }

        }
        return state;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private class LongRunningPanicNotification extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];

                n = in.read(b);

                if (n > 0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override

        protected String doInBackground(Void... params) {
            // if(!isMovementServiceRunning()&&!isPayServiceRunning()) {
            //   startLogMovement();
            //}

            /*HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            String uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/com.eds.restful.notification/1/"+uuid +"/"+ mobile+"/"+cMobile+"/"+firstname+"/"+lastname+"/"+requestType);
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }*/
            String text = "LL";

            return text;

        }

        protected void onPostExecute(String results) {
            if (results != null) {


            }


        }

    }

    int lm = 0;
    int mt = 0;
    int nt = 0;

    public void startPayService() {
        startService(
                new Intent(getBaseContext(), PayService.class));
    }

    public void startLogMovement() {

        if (lm == 0) {
            lm = 1;

            if (new SettingsProfile(getBaseContext()).get().getGoDark().equalsIgnoreCase("0")) {
                startService(
                        new Intent(getBaseContext(), LogMovementService.class));
            }
            startService(
                    new Intent(getBaseContext(), SimChangeService.class));
        } else {
            lm = 0;
            stopService(new Intent(this, LogMovementService.class));
        }

    }

    public void startMovementTracker() {


        if (mt == 0) {
            Toast.makeText(this, "Movement tracking activated (1 Hour)", Toast.LENGTH_LONG).show();
            startService(

                    new Intent(this, MovementService.class));
            mt = 1;
        } else {
            Toast.makeText(this, "Movement tracking Stopped", Toast.LENGTH_LONG).show();
            stopService(new Intent(this, MovementService.class));
            mt = 0;
        }
    }


    public void getSIMChange() {
        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                MainActivity.this);


        SpannableString s = new SpannableString("Mobile Guard");
        s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builderInner.setTitle(s);
        s = new SpannableString("It seems the phone SIM Card has been Changed. \nDo you wish to update your SIM Card Status?");
        s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builderInner.setMessage(s);
        builderInner.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        Intent intentMain = new Intent(MainActivity.this,
                                MgSettingsActivity.class);
                        MainActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                    }
                });
        builderInner.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        dialog.dismiss();
                    }
                });
        builderInner.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.info) {


            Intent intentMain = new Intent(MainActivity.this,
                    MoreInformationActivity.class);
            MainActivity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();

        }
        ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(getBaseContext()).get();
        if (settings.getMGS().equalsIgnoreCase("1")) {
            UserProfile x = new UserProfile(getBaseContext());
            if (!x.getRegistration()) {

                Intent intentMain = new Intent(MainActivity.this,
                        RegistrationActivity.class);
                MainActivity.this.startActivity(intentMain);

            } else {


                //noinspection SimplifiableIfStatement
                if (id == R.id.contact) {
                    Intent intentMain = new Intent(MainActivity.this,
                            ContactActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");

                    finish();
                    //return true;
                } else if (id == R.id.task) {

                    Intent intentMain = new Intent(MainActivity.this,
                            MovementMainActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();

                } else if (id == R.id.mg_settings) {

                    Intent intentMain = new Intent(MainActivity.this,
                            MgSettingsActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();

                } else if (id == R.id.mg_settings) {

                    Intent intentMain = new Intent(MainActivity.this,
                            MgSettingsActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();

                } else if (id == R.id.info) {


                    Intent intentMain = new Intent(MainActivity.this,
                            MoreInformationActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();

                } else if (id == R.id.mg_profile) {


                    Intent intentMain = new Intent(MainActivity.this,
                            ProfileActivity.class);
                    MainActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();

                }else if (id == R.id.themesx) {


setTheme();
                }

            }
        } else {
            AlertDialog.Builder builderInner = new AlertDialog.Builder(
                    MainActivity.this);

            SpannableString s = new SpannableString("Mobile Guard Services");
            s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builderInner.setTitle(s);
            s = new SpannableString("Mobile Guard Services have been switch off. \nDo you wish to switch the services ON?");
            s.setSpan(new TypefaceSpan(MainActivity.this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builderInner.setMessage(s);
            builderInner.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(MainActivity.this).get();
                            settings.setMGS("1");
                            boolean update = new SettingsProfile(MainActivity.this).update(settings);
                            if (update) {
                                startService(
                                        new Intent(getBaseContext(), PayService.class));
                                startService(
                                        new Intent(getBaseContext(), LogMovementService.class));

                                startService(
                                        new Intent(getBaseContext(), SimChangeService.class));
                                //sendSmsByManager();
                                Toast.makeText(MainActivity.this, "Mobile Guard Service ON", Toast.LENGTH_LONG).show();
                                create.setImageResource(R.drawable.panic);
                            }

                        }
                    });
            builderInner.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            dialog.dismiss();
                        }
                    });
            builderInner.show();
        }
        return super.onOptionsItemSelected(item);
    }

        public void contactOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.theme_dialog);

        SpannableString s = new SpannableString("Switch Mode");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster


        dialog.setTitle(s);
        dialog.setCancelable(true);


        // set the custom dialog components - text, image and button
        final RadioButton radioPanicMode = (RadioButton) dialog.findViewById(R.id.radioPanicMode);
        final RadioButton radioNewsMode = (RadioButton) dialog.findViewById(R.id.radioNewsMode);
        final RadioButton radioNormalMode = (RadioButton) dialog.findViewById(R.id.radioNormalMode);
        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioG);
        Button butTheme = (Button) dialog.findViewById(R.id.butSetTheme);
        // if button is clicked, close the custom dialog
        butTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(radioNormalMode.isChecked()){
                    //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Normal");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){
                        Toast.makeText(getBaseContext(),"Normal Mode",Toast.LENGTH_LONG).show();
                        Intent intentMain = new Intent(MainActivity.this,
                                Main3Activity.class);
                        intentMain.putExtra("serviceState", true);
                        MainActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();}

                }else if(radioNewsMode.isChecked()){
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("News");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){Toast.makeText(getBaseContext(),"News Mode",Toast.LENGTH_LONG).show();
                        Intent   intentMain = new Intent(MainActivity.this,
                                        Main2Activity.class);
                        intentMain.putExtra("serviceState", true);
                        MainActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();}

                }else if(radioPanicMode.isChecked()){
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Panic");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){Toast.makeText(getBaseContext(),"Panic Mode",Toast.LENGTH_LONG).show();
                        dialog.dismiss();}
                }
            }

        });
/*
        Button cancel = (Button) dialog.findViewById(R.id.butManualContact);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addContactManual();

            }
        });*/
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {

        checkPlayServices();
        //handler.sendEmptyMessageAtTime(1, 1000);
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    public void setTheme(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        // builderSingle.setIcon(R.drawable.logo);
        SpannableString s = new SpannableString("Select Mode");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        builderSingle.setTitle("Select Mode");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.select_dialog_item);
        arrayAdapter.add("News Mode");
        arrayAdapter.add("Security Mode");
        arrayAdapter.add("Normal Mode");


        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int loc = which;
                        if (loc == 2) {
                            //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Normal");
                            boolean state = new AppThemeProfile(getBaseContext()).update(a);
                            if (state) {
                                Toast.makeText(getBaseContext(), "Normal Mode", Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(MainActivity.this,
                                        Main3Activity.class);
                                intentMain.putExtra("serviceState", true);
                                MainActivity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();

                                dialog.dismiss();
                            }

                        } else if (loc == 0) {
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("News");
                            boolean state = new AppThemeProfile(getBaseContext()).update(a);
                            if (state) {
                                Toast.makeText(getBaseContext(), "News Mode", Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(MainActivity.this,
                                        Main2Activity.class);
                                intentMain.putExtra("serviceState", true);
                                MainActivity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();

                                dialog.dismiss();
                            }
                        } else if (loc == 1) {
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Panic");
                            boolean state = new AppThemeProfile(getBaseContext()).update(a);
                            if (state) {
                                Toast.makeText(getBaseContext(), "Security Mode", Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();

    }


    /**
     * Method to display the location on UI
     */

    double publicLatitude = 0;
    double publicLongtuide = 0;

    private void displayLocation() {


        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            publicLatitude = latitude;
            publicLongtuide = longitude;
            // Toast.makeText(this,latitude+" "+longitude,Toast.LENGTH_LONG).show();

        } else {
            //Toast.makeText(this,"",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d("t", "Periodic location updates started!");

        } else {
            // Changing the button text

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d("t", "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } catch (Exception e) {

        }
    }


    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("t", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            lblWarning.setText(locationAddress);
            Toast.makeText(MainActivity.this, locationAddress + " tttttttt", Toast.LENGTH_LONG);
        }
    }
}
