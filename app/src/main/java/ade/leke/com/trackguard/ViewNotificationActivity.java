package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.NotificationProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.model.ContactListModel;
import ade.leke.com.trackguard.model.CustomAdapter;

public class ViewNotificationActivity extends ActionBarActivity {


    String subject = "";
    String message = "";
    double lat = 9.072264;
    double lng = 7.491302;
    String date = "";
    public ViewNotificationActivity CustomListView = null;
    public ArrayList<ContactListModel> CustomListViewValuesArr = new ArrayList<ContactListModel>();
    FeedReaderDbHelper mDbHelper;
    String type;
    AppLocationService appLocationService;
    String phoneNumber;
    String lats;
    String lngs;
    String uuid;
    long nid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppThemes t = new AppThemeProfile(getBaseContext()).get();
        if(t.getName().equalsIgnoreCase("Panic")) {

            setTheme(R.style.AppTheme_Panic);
        }else if(t.getName().equalsIgnoreCase("News")) {

            setTheme(R.style.AppTheme);
        } else if(t.getName().equalsIgnoreCase("Normal")) {

            setTheme(R.style.AppTheme_normal);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        nid = intent.getLongExtra("nid",0L);
        subject = intent.getStringExtra("subject");
        message = intent.getStringExtra("message");
        date = intent.getStringExtra("date");
        type = intent.getStringExtra("type");
        lats = intent.getStringExtra("lat");
        lngs = intent.getStringExtra("lng");
        phoneNumber = intent.getStringExtra("phoneNumber");
        appLocationService = new AppLocationService(
                getBaseContext());
        uuid = intent.getStringExtra("uid");
        SpannableString s = new SpannableString(subject);
        s.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        if(t.getName().equalsIgnoreCase("Panic")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_S));

            setTheme(R.style.AppTheme_Panic);
        }else if(t.getName().equalsIgnoreCase("News")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

            setTheme(R.style.AppTheme);
        } else if(t.getName().equalsIgnoreCase("Normal")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_N));

            setTheme(R.style.AppTheme_normal);
        }
        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        // CustomListView = this;
        TextView mess = (TextView) findViewById(R.id.lblNotificationRequest);//
        TextView dateNotification = (TextView) findViewById(R.id.lblNotificationDate);
        s = new SpannableString(message);
        s.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mess.setText(s);

        s = new SpannableString(date);
        s.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dateNotification.setText(s);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button create = (Button) findViewById(R.id.butNotificationResponseX);
        Button reject = (Button) findViewById(R.id.butNotificationRejectx);
        SpannableString spannableString = new SpannableString("Reject Location Request");
        spannableString.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        reject.setText(spannableString);
        if (type.equalsIgnoreCase("P") || type.equalsIgnoreCase("L")) {
            spannableString = new SpannableString("View Location");
            spannableString.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            create.setText(spannableString);
            reject.setVisibility(View.INVISIBLE);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    double clatitude = gpsLocation.getLatitude();
                    double clongitude = gpsLocation.getLongitude();

                    lat = clatitude;
                    lng = clongitude;

                    if (type.equalsIgnoreCase("P")) {

                        Intent intentMain = new Intent(ViewNotificationActivity.this,
                                PanicLocationDirectionMap.class);
                        intentMain.putExtra("lat", lats);
                        intentMain.putExtra("lng", lngs);
                        String[] names = message.split(" ");
                        if (names.length > 1) {
                            intentMain.putExtra("name", names[0]);
                        } else {
                            intentMain.putExtra("name", names[1]);
                        }
                        intentMain.putExtra("uid", uuid);
                        intentMain.putExtra("phoneNumber",phoneNumber);
                        ViewNotificationActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                    } else if (type.equalsIgnoreCase("L")) {
                        Intent intentMain = new Intent(ViewNotificationActivity.this,
                                LocationDirectionMap.class);
                        intentMain.putExtra("lat", lats);
                        intentMain.putExtra("lng", lngs);
                        String[] names = message.split(" ");
                        if (names.length > 1) {
                            intentMain.putExtra("name", names[0]);
                        } else {
                            intentMain.putExtra("name", names[1]);
                        }
                        ViewNotificationActivity.this.startActivity(intentMain);
                        finish();
                    } else if (type.equalsIgnoreCase("R")) {


                        sendSmsByManager(phoneNumber, lat, lng);

                    }

                } else {

                    gpsLocation = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);

                    if (gpsLocation != null) {
                        double clatitude = gpsLocation.getLatitude();
                        double clongitude = gpsLocation.getLongitude();

                        lat = clatitude;
                        lng = clongitude;

                        if (type.equalsIgnoreCase("P")) {

                            Intent intentMain = new Intent(ViewNotificationActivity.this,
                                    PanicLocationDirectionMap.class);
                            intentMain.putExtra("lat", lats);
                            intentMain.putExtra("lng", lngs);
                            String[] names = message.split(" ");
                            if (names.length > 1) {
                                intentMain.putExtra("name", names[0]);
                            } else {
                                intentMain.putExtra("name", names[1]);
                            }
                            intentMain.putExtra("uid", uuid);
                            intentMain.putExtra("phoneNumber",phoneNumber);
                            ViewNotificationActivity.this.startActivity(intentMain);
                            Log.i("Content ", " Menu layout ");
                            finish();
                        } else if (type.equalsIgnoreCase("L")) {
                            Intent intentMain = new Intent(ViewNotificationActivity.this,
                                    LocationDirectionMap.class);
                            intentMain.putExtra("lat", lats);
                            intentMain.putExtra("lng", lngs);
                            String[] names = message.split(" ");
                            if (names.length > 1) {
                                intentMain.putExtra("name", names[0]);
                            } else {
                                intentMain.putExtra("name", names[1]);
                            }
                            ViewNotificationActivity.this.startActivity(intentMain);
                            finish();
                        } else if (type.equalsIgnoreCase("R")) {

                            sendSmsByManager(phoneNumber, lat, lng);

                        }

                    }else {
                        new GPSTracker(ViewNotificationActivity.this).showSettingsAlert();
                    }
                }

            }
        });


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    public void sendSmsByManager(String number, double lat, double lng) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(number,
                    null,
                    "MGps:" + lat + ":" + lng, //requesting location from user
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + number);

            Toast.makeText(getApplicationContext(), "Location to " + subject + "",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Request Failed",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home)

        {

            Intent intentMain = new Intent(ViewNotificationActivity.this,
                    NotificationActivity.class);
            ViewNotificationActivity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();

        }else if (id == R.id.delete) {

            final Dialog dialog = new Dialog(ViewNotificationActivity.this);
            dialog.setContentView(R.layout.passcode_dialog);

            SpannableString s = new SpannableString("Access Authorization");
            s.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

            dialog.setTitle(s);
            dialog.setCancelable(true);


            // set the custom dialog components - text, image and button
            final EditText name = (EditText) dialog.findViewById(R.id.txtDialogAccessCode);



            Button saveContact = (Button) dialog.findViewById(R.id.butAuthorize);
            // if button is clicked, close the custom dialog
            saveContact.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {

                                                   User user = new UserProfile(getBaseContext()).get();
                                                   if (name.getText().toString().equalsIgnoreCase(user.getPassCode())) {
                                                       boolean state = new NotificationProfile(getBaseContext()).delete(nid);
                                                       if (state) {
                                                           Toast.makeText(ViewNotificationActivity.this,
                                                                   "" + subject + " deleted successfully"
                                                                   ,
                                                                   Toast.LENGTH_LONG)
                                                                   .show();

                                                           Intent intentMain = new Intent(ViewNotificationActivity.this,
                                                                   NotificationActivity.class);
                                                           ViewNotificationActivity.this.startActivity(intentMain);
                                                           Log.i("Content ", " Menu layout ");

                                                           finish();

                                                       }
                                                   }else{
                                                       Toast.makeText(ViewNotificationActivity.this,"Incorrect Access Code",Toast.LENGTH_LONG).show();
                                                   }

                                               }

                                           }

            );

            dialog.show();







        }


        return super.onOptionsItemSelected(item);
    }


    public SpannableString txtWithFont(String txt) {
        SpannableString spannableString = new SpannableString(txt);
        spannableString.setSpan(new TypefaceSpan(ViewNotificationActivity.this, "exo_medium.otf"), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
