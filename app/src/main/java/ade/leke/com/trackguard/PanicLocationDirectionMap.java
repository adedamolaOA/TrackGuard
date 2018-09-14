package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.common.Mail;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.Panic;
import ade.leke.com.trackguard.db.db.entities.profile.PanicProfile;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.model.PanicAdapter;
import ade.leke.com.trackguard.model.PanicListModel;


public class PanicLocationDirectionMap extends ActionBarActivity {
    long _id;
    String name;
    String uid;
    String lat;
    String lng;
    PanicAdapter adapter;
    ListView list;
    private GoogleMap mMap;
    public PanicLocationDirectionMap CustomListView = null;
    List<Panic> panics;
    int counter;

    ArrayList<LatLng> postions = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    public ArrayList<PanicListModel> CustomListViewValuesArr = new ArrayList<PanicListModel>();

    Handler handler;

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
        setContentView(R.layout.activity_panic_location_direction_map);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
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
        SpannableString s = new SpannableString("Panic Alert");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);


        CustomListView = this;


        uid = getIntent().getStringExtra("uid");//intentMain.putExtra("uid", msgBody.split(":")[1]);
        String phone = getIntent().getStringExtra("phoneNumber");

        Contact c = new ContactProfile(getBaseContext()).getByMobileNumber(phone);
        System.out.println("-------------_____________________------------------ " + uid);


        if (c != null) {
            TextView txtPanicingContact = (TextView) findViewById(R.id.txtPanicingContact);
            String lastname = c.getLastname();
            if (lastname.equalsIgnoreCase("None")) {
                lastname = "";
            }

            s = new SpannableString("Panic Alert From " + c.getFirstname() + " " + lastname);
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
            txtPanicingContact.setText(s);
        }

        Resources res = getResources();
        list = (ListView) findViewById(R.id.listViewPanic);  // List defined in XML ( See Below )

        PanicListModel sched = new PanicListModel();
        sched.setDate("");
        sched.setAddress("Retrieving Location Address");
        CustomListViewValuesArr.add(sched);
        //panicAddresses.add(locationAddress);
        adapter = new PanicAdapter(CustomListView, CustomListViewValuesArr, getResources());
        list.setAdapter(adapter);
        loadPanicData(uid);


        setUpMapIfNeeded(postions, dates);
        /**************** Create Custom Adapter *********/

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    ArrayList<String> panicAddresses = new ArrayList<>();

    public void loadPanicData(final String uid) {

        panics = new PanicProfile(getBaseContext()).getByPUID(uid);


        for (Panic panic : panics) {
            String result = "";


            try{
            LocationAddress locationAddress = new LocationAddress();
            double latt = Double.parseDouble(panic.getLat());
                double longg= Double.parseDouble(panic.getLng());
            locationAddress.getAddressFromLocation(panic.getDate(),latt, longg,
                    getApplicationContext(), new GeocoderHandler());

            LatLng latLng = new LatLng(Double.parseDouble(panic.getLat()), Double.parseDouble(panic.getLng()));

            postions.add(latLng);
            dates.add(panic.getDate());
            }catch (Exception e){

            }

        }




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_panic_location_direction_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)

        {


            Intent intentMain = new Intent(PanicLocationDirectionMap.this,
                    NotificationActivity.class);
            PanicLocationDirectionMap.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();


        } else {
            if (id == R.id.send_email)

            {


                sendEmail();


            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void sendEmail() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.email_layout);

        SpannableString s = new SpannableString("Email Panic Locations");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        dialog.setTitle(s);
        dialog.setCancelable(true);


        // set the custom dialog components - text, image and button
        final EditText subject = (EditText) dialog.findViewById(R.id.txtEmailSubject);
        //final EditText comments = (EditText) dialog.findViewById(R.id.txtEmailComment);


        Button saveContact = (Button) dialog.findViewById(R.id.butEmailPanic);
        // if button is clicked, close the custom dialog
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Contact con = new ContactProfile(getBaseContext()).getByMobileNumber(panics.get(0).getAddress());
                if (subject.getText().toString().isEmpty()) {
                    Toast.makeText(PanicLocationDirectionMap.this, "Subject Empty", Toast.LENGTH_LONG).show();
                } else {
                    Mail m = new Mail("wiitech.mobile.guard@gmail.com", "millinik");

                    User user = new UserProfile(PanicLocationDirectionMap.this).get();
                    String[] toArr = {user.getEmail(), "wiitech.mobile.guard@gmail.com"};//
                    m.setTo(toArr);
                    m.setFrom("wiitech.mobile.guard@gmail.com");
                    m.setSubject("Panic Alert Log: " + subject.getText());
                    StringBuilder panicMsg = new StringBuilder();
                    String lastname = con.getLastname();
                    if (lastname.equalsIgnoreCase("None")) {
                        lastname = "";
                    }
                    panicMsg.append("=================================================================================\n");
                    panicMsg.append("Panic Log for " + con.getFirstname() + " " + lastname + "\n");
                    panicMsg.append("=================================================================================\n");
                    int l = 0;
                    for (Panic panic : panics) {
                        panicMsg.append("Panic Contact: " + panic.getAddress());
                        panicMsg.append("\n");
                        panicMsg.append("Panic Date: " + panic.getDate());
                        panicMsg.append("\n");
                        panicMsg.append("Panic Location: " + panic.getLat() + "," + panic.getLng());
                        panicMsg.append("\n");
                        panicMsg.append("Panic Address: " + panicAddresses.get(l));
                        panicMsg.append("\n");
                        panicMsg.append("=================================================================================\n");

                        l++;
                    }
                    m.setBody(panicMsg.toString());

                    try {

                        if (m.send()) {
                            Toast.makeText(PanicLocationDirectionMap.this, "Panic Log mailed successfully.", Toast.LENGTH_LONG).show();

                            dialog.cancel();
                            Log.i("Content ", " Menu layout ");
                        } else {
                            Toast.makeText(PanicLocationDirectionMap.this, "Panic Log mailing failed", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                        Log.e("MailApp", "Could not send email", e);
                    }

                }

            }

        });


        dialog.show();
    }

    private void setUpMapIfNeeded(ArrayList<LatLng> location, ArrayList<String> date) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapP))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(location, date);
            }
        }
    }

    private void setUpMap(ArrayList<LatLng> location, ArrayList<String> date) {


        //mMap.addCircle(new CircleOptions().radius(100).strokeColor(Color.rgb(181,222,226)).fillColor(Color.rgb(181,222,226)).center(new LatLng(lat, lng)));
        int i = 0;
        for (LatLng latLng : location) {


            if (i == 0) {
                CameraPosition cp = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(17)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(date.get(i)));
            } else if (i == (location.size() - 2)) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(date.get(i)));
            } else {
                mMap.addMarker(new MarkerOptions().position(latLng).title(date.get(i)));
            }
            i++;
        }
        mMap.addPolyline(new PolylineOptions().addAll(location).color(Color.RED));
    }

    /*****************
     * This function used by adapter
     ****************/
    public void onItemClick(int mPosition) {
         /*ContactListModel tempValues = ( ContactListModel ) CustomListViewValuesArr.get(mPosition);
        //ArrayList<ContactListModel> arrayList = cust
        //CustomListViewValuesArr.clear();
        ContactListModel data = tempValues;
        tempValues.setImage("B");
        CustomListViewValuesArr.set(mPosition,tempValues);
        Resources res = getResources();
        list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

       *************** Create Custom Adapter ********
        adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
        list.setAdapter(adapter);*/
       /*
        Intent intentMain = new Intent(ContactActivity.this,
                ContactDisplayActivity.class);
        intentMain.putExtra("id",tempValues.getMobileNumber());
        intentMain.putExtra("name",tempValues.getName());
        intentMain.putExtra("lat","0.0");
        intentMain.putExtra("lng","0.0");
        ContactActivity.this.startActivity(intentMain);*/


        //selcetContactName = tempValues.getName();
        //selectContactMobile = tempValues.getMobileNumber();


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
                    date = bundle.getString("date");
                    break;
                case 2:
                    Bundle bundle2 = message.getData();
                    date = bundle2.getString("date");
                    locationAddress = getString(R.string.error_loc_latlng);
                default:
                    date = "";
                    locationAddress = getString(R.string.error_loc_latlng);
            }
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + panics.size());
            if(counter==0){
CustomListViewValuesArr.clear();
            }
            PanicListModel sched = new PanicListModel();
            sched.setDate(date);
            sched.setAddress(locationAddress);
            CustomListViewValuesArr.add(sched);
            panicAddresses.add(locationAddress);
            adapter = new PanicAdapter(CustomListView, CustomListViewValuesArr, getResources());
            list.setAdapter(adapter);
            counter++;

        }
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        @Override

        protected String doInBackground(Void... params) {


            return UUID.randomUUID().toString();

        }

        protected void onPostExecute(String results) {
            if (results != null) {


            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
