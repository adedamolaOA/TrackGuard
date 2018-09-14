package ade.leke.com.trackguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.services.LogMovementService;
import ade.leke.com.trackguard.services.MovementService;
import ade.leke.com.trackguard.services.PayService;
import ade.leke.com.trackguard.services.SimChangeService;

public class Main3Activity extends ActionBarActivity {
    Toolbar toolbar;

    ImageButton contactx;
    ImageButton settingsx;
    ImageButton currentLocation;
    ImageButton aboutUs;
    ImageButton movementLog;
    ImageButton notification;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_normal);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        appLocationService = new AppLocationService(
                Main3Activity.this);

        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_N));

        SpannableString s = new SpannableString("Mobile Guard");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        contactx = (ImageButton) findViewById(R.id.imageContact);
        aboutUs = (ImageButton) findViewById(R.id.imageAboutUs);
        movementLog = (ImageButton) findViewById(R.id.imageMovementLog);
        currentLocation = (ImageButton) findViewById(R.id.imageCurrentLocation);
        notification = (ImageButton) findViewById(R.id.imageNotification);
        settingsx = (ImageButton) findViewById(R.id.imageSettings);

        contactx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(Main3Activity.this,
                        ContactActivity.class);
                Main3Activity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");

                finish();

            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(Main3Activity.this,
                        MoreInformationActivity.class);
                Main3Activity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");

                finish();

            }
        });

        movementLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(Main3Activity.this,
                        MovementMainActivity.class);
                Main3Activity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");

                finish();

            }
        });
        settingsx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(Main3Activity.this,
                        MgSettingsActivity.class);
                Main3Activity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");

                finish();

            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile x = new UserProfile(getBaseContext());
                if (!x.getRegistration()) {

                    Intent intentMain = new Intent(Main3Activity.this,
                            RegistrationActivity.class);
                    Main3Activity.this.startActivity(intentMain);
                    finish();
                } else {

                    //togglePeriodicLocationUpdates();

                    Location gpsLocation = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        System.out.println("================================================================was here");

                        double clatitude = gpsLocation.getLatitude();
                        double clongitude = gpsLocation.getLongitude();

                        Intent intentMain = new Intent(Main3Activity.this,
                                MovementMapActivity.class);
                        intentMain.putExtra("date", "My Current Location");
                        intentMain.putExtra("map_state", false);
                        intentMain.putExtra("lat", clatitude + "");
                        intentMain.putExtra("lng", clongitude + "");
                        Main3Activity.this.startActivity(intentMain);
                        finish();
                    } else {

                            GPSTracker gps = new GPSTracker(Main3Activity.this);
                            if (gps.canGetLocation()) {


                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                gps.stopUsingGPS();

                                //new LongRunningPanicNotification().execute();

                                Intent intentMain = new Intent(Main3Activity.this,
                                        MovementMapActivity.class);
                                intentMain.putExtra("date", "My Current Location");
                                intentMain.putExtra("map_state", false);
                                intentMain.putExtra("lat", latitude + "");
                                intentMain.putExtra("lng", longitude + "");
                                Main3Activity.this.startActivity(intentMain);
                                finish();

                            } else {
                                gpsLocation = appLocationService
                                        .getLocation(LocationManager.PASSIVE_PROVIDER);
                                if (gpsLocation != null) {
                                    System.out.println("==========================*************==========================was here");

                                    double cclatitude = gpsLocation.getLatitude();
                                    double cclongitude = gpsLocation.getLongitude();

                                    Intent intentMain = new Intent(Main3Activity.this,
                                            MovementMapActivity.class);
                                    intentMain.putExtra("date", "My Current Location");
                                    intentMain.putExtra("map_state", false);
                                    intentMain.putExtra("lat", cclatitude + "");
                                    intentMain.putExtra("lng", cclongitude + "");
                                    Main3Activity.this.startActivity(intentMain);
                                    finish();
                                } else {
                                    showSettingsAlert();
                                }

                            }


                    }
                    //startMovementTracker();
                }
            }
        });
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

                        Intent intentMain = new Intent(Main3Activity.this,
                                RegistrationActivity.class);
                        Main3Activity.this.startActivity(intentMain);
                        finish();
                    } else {
                        Intent intentMain = new Intent(Main3Activity.this,
                                NotificationActivity.class);
                        Main3Activity.this.startActivity(intentMain);
                        finish();

                    }
                } else {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                            Main3Activity.this);

                    SpannableString s = new SpannableString("Mobile Guard Services");
                    s.setSpan(new TypefaceSpan(Main3Activity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setTitle(s);
                    s = new SpannableString("Mobile Guard Services have been switch off. \nDo you wish to switch the services ON?");
                    s.setSpan(new TypefaceSpan(Main3Activity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setMessage(s);
                    builderInner.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(Main3Activity.this).get();
                                    settings.setMGS("1");
                                    boolean update = new SettingsProfile(Main3Activity.this).update(settings);
                                    if (update) {
                                        startService(
                                                new Intent(getBaseContext(), PayService.class));
                                        startService(
                                                new Intent(getBaseContext(), LogMovementService.class));

                                        startService(
                                                new Intent(getBaseContext(), SimChangeService.class));
                                        //sendSmsByManager();
                                        Toast.makeText(Main3Activity.this, "Mobile Guard Service ON", Toast.LENGTH_LONG).show();
                                        ///create.setImageResource(R.drawable.panic);
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

startPayService();
        startLogMovement();

    }

    public void startLogMovement() {


            if (new SettingsProfile(getBaseContext()).get().getGoDark().equalsIgnoreCase("0")) {
                startService(
                        new Intent(getBaseContext(), LogMovementService.class));
            }


    }

    public void startPayService() {
        startService(
                new Intent(getBaseContext(), PayService.class));
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

                if (radioNormalMode.isChecked()) {
                    //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Normal");
                    boolean state = new AppThemeProfile(getBaseContext()).update(a);
                    if (state) {
                        Toast.makeText(getBaseContext(), "Normal Mode", Toast.LENGTH_LONG).show();
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();
                    }

                } else if (radioNewsMode.isChecked()) {
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("News");
                    boolean state = new AppThemeProfile(getBaseContext()).update(a);
                    if (state) {
                        Toast.makeText(getBaseContext(), "News Mode", Toast.LENGTH_LONG).show();
                        Intent intentMain = new Intent(Main3Activity.this,
                                Main2Activity.class);
                        intentMain.putExtra("serviceState", true);
                        Main3Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();
                    }

                } else if (radioPanicMode.isChecked()) {
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Panic");
                    boolean state = new AppThemeProfile(getBaseContext()).update(a);
                    if (state) {
                        Toast.makeText(getBaseContext(), "Panic Mode", Toast.LENGTH_LONG).show();
                        Intent intentMain = new Intent(Main3Activity.this,
                                MainActivity.class);
                        intentMain.putExtra("serviceState", true);
                        Main3Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                        dialog.dismiss();
                    }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intentMain = new Intent(Main3Activity.this,
                    ProfileActivity.class);
            Main3Activity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");

            finish();

            return true;
        }else if (id == R.id.action_thememode) {
setTheme();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTheme(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Main3Activity.this);
        // builderSingle.setIcon(R.drawable.logo);
        SpannableString s = new SpannableString("Select Mode");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        builderSingle.setTitle("Select Mode");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                Main3Activity.this,
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
                        if(loc==2){
                            //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Normal");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"Normal Mode",Toast.LENGTH_LONG).show();
                                dialog.dismiss();}

                        }else if(loc==0){
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("News");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"News Mode",Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(Main3Activity.this,
                                        Main2Activity.class);
                                intentMain.putExtra("serviceState", true);
                                Main3Activity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();

                                dialog.dismiss();}
                        }else if(loc==1){
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Panic");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"Security Mode",Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(Main3Activity.this,
                                        MainActivity.class);
                                intentMain.putExtra("serviceState", true);
                                Main3Activity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();
                                dialog.dismiss();}
                        }
                    }
                });
        builderSingle.show();

    }

}
