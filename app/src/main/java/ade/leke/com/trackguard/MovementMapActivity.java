package ade.leke.com.trackguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.common.Mail;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Movement;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.MovementProfile;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.model.ContactListModel;

public class MovementMapActivity extends ActionBarActivity {

    private GoogleMap mMap;
    String date = "";
    String name = "";
    double lat = 9.072264;
    double lng = 7.491302;
    boolean refreshState = true;

    public MovementMapActivity CustomListView = null;
    public ArrayList<ContactListModel> CustomListViewValuesArr = new ArrayList<ContactListModel>();
    FeedReaderDbHelper mDbHelper;
    TextView address;
    ArrayList<LatLng> locations = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    boolean type = false;
    long id;
    Context context;

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
        setContentView(R.layout.activity_movement_map);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        id = intent.getLongExtra("id", 0L);
        lat = Double.parseDouble(intent.getStringExtra("lat"));
        lng = Double.parseDouble(intent.getStringExtra("lng"));
        type = intent.getBooleanExtra("map_state", false);
        context = getBaseContext();
        Typeface mgFont = Typeface.createFromAsset(getAssets(), "fonts/exo_medium.otf");

        TextView txtLat = (TextView) findViewById(R.id.lblMoveMapLat);
        TextView txtLng = (TextView) findViewById(R.id.lblMoveMapLng);
        TextView lblLat = (TextView) findViewById(R.id.lblLatLBL);
        TextView lblLng = (TextView) findViewById(R.id.lblLngLBL);
        TextView lblLocation = (TextView) findViewById(R.id.lblLocationAddressLBL);

        txtLat.setTypeface(mgFont);
        txtLng.setTypeface(mgFont);
        lblLat.setTypeface(mgFont);
        lblLng.setTypeface(mgFont);
        lblLocation.setTypeface(mgFont);

        address = (TextView) findViewById(R.id.lblMoveAddress);
        address.setTypeface(mgFont);
        txtLat.setText(lat + "");
        txtLng.setText(lng + "");

        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(lat, lng,
                getApplicationContext(), new GeocoderHandler());
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

        SpannableString s = new SpannableString(date);
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        CustomListView = this;


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (type) {
            setLocations(date);
            setUpMapIfNeeded(locations, dates);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng marker : locations) {
                builder.include(marker);
            }
            LatLngBounds bounds = builder.build();

            bounds.getCenter();
            CameraPosition cp = new CameraPosition.Builder()
                    .target(bounds.getCenter())
                    .zoom(13.5F)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));


            //registerForContextMenu(mMap);
        } else {
            setUpMapIfNeeded();
        }

    }

    List<Movement> movementList;

    public void setLocations(String endDate) {
        String[] dateArray = endDate.split(" ");
        String startDate = dateArray[0] + " 00:00:00";
        movementList = new MovementProfile(this).getMovementDate(startDate, endDate);
        for (Movement m : movementList) {

            double lats = Double.parseDouble(m.getLat());
            double lngs = Double.parseDouble(m.getLng());
            LatLng l = new LatLng(lats, lngs);
            locations.add(l);
            dates.add(m.getMDate());
        }

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapM))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMapIfNeeded(ArrayList<LatLng> location, ArrayList<String> date) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapM))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMapGrouped(location, date);
            }
        }
    }

    private void setUpMapGrouped(ArrayList<LatLng> location, ArrayList<String> date) {


        //mMap.addCircle(new CircleOptions().radius(100).strokeColor(Color.rgb(181,222,226)).fillColor(Color.rgb(181,222,226)).center(new LatLng(lat, lng)));
        int i = 0;

        ArrayList<LatLng> newLocations = new ArrayList<>();
        Bitmap arrowheadBitmap;
        int width = 26;
        int height = 26;
        String newDate = "";
        String endNewDate = "";
        ArrayList<Float> distances = new ArrayList<>();
        ArrayList<String> dateStore = new ArrayList<>();
        for (LatLng latLng : location) {

            boolean add = false;

            if (i != 0) {
                Location location1 = new Location(dates.get(i - 1));
                Location location2 = new Location(dates.get(i));
                location1.setLatitude(location.get(i - 1).longitude);
                location1.setLongitude(location.get(i - 1).longitude);
                location2.setLatitude(latLng.latitude);
                location2.setLongitude(latLng.longitude);
                float distance = location2.distanceTo(location1);
                float s = distances.get(i - 1) - distance;
                if (Math.signum(s) == -1F) {
                    s = Math.abs(s);
                }
                distances.add(distance);
                if (s >= 100) {
                    add = true;
                    newLocations.add(latLng);
                    endNewDate = dates.get(i);
                    int next = i + 1;
                    if (next < locations.size()) {
                        dateStore.add(newDate + " To " + endNewDate);
                    }else{
                        dateStore.add(newDate + " To " + dates.get(locations.size()-1));
                    }
                    newDate = dates.get(i);

                } else {
                    int next = i + 1;
                    if (next < locations.size()) {
                        //dateStore.add(newDate + " To " + endNewDate);
                    }else{
                        dateStore.add(newDate + " To " + dates.get(locations.size()-1));
                    }
                }

                if (newDate.isEmpty()) {
                    newDate = dates.get(i);

                }


                System.out.print("++++++++++++++++++++++++++++++++++++++++++++++ " + distance + "---" + dates.get(i) + "*********" + s);
            } else {
                //add = true;
                dateStore.add(dates.get(i));
                newLocations.add(latLng);
                distances.add(0F);

            }


            i++;
        }
        int j=0;
        if(newLocations.size() > 2) {

            for (LatLng latLng : newLocations) {
                if (j == 1) {

                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(dateStore.get(j)));
                    newDate = "";
                    mMap.addCircle(new CircleOptions().center(latLng).radius(100).fillColor(0x5500ff00).strokeColor(Color.GREEN).strokeWidth(1));
                    DrawArrowHead(mMap, latLng, newLocations.get(j - 1));
                } else if (j == (newLocations.size() - 1)) {

                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(dateStore.get(dateStore.size() - 1)));
                    mMap.addCircle(new CircleOptions().center(latLng).radius(100).fillColor(0x55007fff).strokeColor(Color.rgb(0, 127, 255)).strokeWidth(1));
                    DrawArrowHead(mMap, latLng, newLocations.get(newLocations.size() - 1));
                } else if (j == 0) {

                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(dateStore.get(j)));
                    newDate = "";
                    DrawArrowHead(mMap, latLng, newLocations.get(j));
                } else {

                    int next = j + 1;
                    if (next < newLocations.size()) {

                        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)).title(dateStore.get(j)));
                        mMap.addCircle(new CircleOptions().center(latLng).radius(100).fillColor(0x55C71585).strokeColor(Color.rgb(199, 21, 133)).strokeWidth(1));

                        DrawArrowHead(mMap, latLng, newLocations.get(j + 1));
                        newDate = "";
                    }
                }
                j++;
            }

        }else{

            mMap.addMarker(new MarkerOptions().position(newLocations.get(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(date.get(0)+" To "+date.get(date.size()-1)));
            newDate = "";
            mMap.addCircle(new CircleOptions().center(newLocations.get(0)).radius(200).fillColor(0x5500ff00).strokeColor(Color.GREEN).strokeWidth(1));
            CameraPosition cp = new CameraPosition.Builder()
                    .target(newLocations.get(0))
                    .zoom(17)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        }

        PolylineOptions options = new PolylineOptions();
        options.addAll(newLocations);
        options.geodesic(true);
        options.color(Color.CYAN);

        mMap.addPolyline(options);//new PolylineOptions().addAll(location).color(Color.BLUE)

    }

    private void setUpMap(ArrayList<LatLng> location, ArrayList<String> date) {

       /* CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));*/

        //mMap.addCircle(new CircleOptions().radius(100).strokeColor(Color.rgb(181,222,226)).fillColor(Color.rgb(181,222,226)).center(new LatLng(lat, lng)));
        int i = 0;

        Bitmap arrowheadBitmap;
        int width = 26;
        int height = 26;
        for (LatLng latLng : location) {


            if (i == 1) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(date.get(i)));
            } else if (i == (location.size() - 2)) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(date.get(i)));
            } else {

                int next = i + 1;
                if (next < locations.size()) {
                   /* //mMap.addMarker(new MarkerOptions().position(latLng).title(date.get(i)));
                    Matrix matrix = new Matrix();
                    double sLat = latLng.latitude - location.get(i + 1).latitude;
                    double sLng = latLng.longitude - location.get(i + 1).longitude;
                    double rotationDegrees = Math.toDegrees(Math.atan2(sLat, sLng));
                    matrix.postRotate(((float) rotationDegrees));
// Create the rotated arrowhead bitmap

                    arrowheadBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrow_forward), 0, 0,
                            width, height, matrix, true);
// Now we are gonna to add a markerBitmapDescriptorFactory.HUE_GREEN
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(arrowheadBitmap)).anchor(0.5f, 0f).title(date.get(i)));
                    */
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(date.get(i)));

                    DrawArrowHead(mMap, latLng, location.get(i + 1));

                } else {
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).title(date.get(i)));

                }
            }
            i++;
        }


        //LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //for (Marker marker : markers) {
        //  builder.include(marker.getPosition());
        //}
        //LatLngBounds bounds = builder.build();

        PolylineOptions options = new PolylineOptions();
        options.addAll(location);
        options.geodesic(true);
        options.color(Color.CYAN);

        mMap.addPolyline(options);//new PolylineOptions().addAll(location).color(Color.BLUE)
    }

    private void setUpMap() {

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        //mMap.addCircle(new CircleOptions().radius(100).strokeColor(Color.rgb(181,222,226)).fillColor(Color.rgb(181,222,226)).center(new LatLng(lat, lng)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(name));
        mMap.addCircle(new CircleOptions().center(new LatLng(lat, lng)).radius(100).fillColor(0x55007fff).strokeColor(Color.rgb(0, 127, 255)).strokeWidth(1));
    }

    MenuItem m;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movement_map, menu);
        m = menu.findItem(R.id.send_email);
        MenuItem mm = menu.findItem(R.id.delete);
        MenuItem mmm = menu.findItem(R.id.reloadmap);
        if (!type) {
            m.setIcon(R.drawable.send_my_location);
            mm.setVisible(false);
            mmm.setVisible(false);
        }
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Context Menu");
        menu.add(0, v.getId(), 0, "Normal Marker View");
        menu.add(0, v.getId(), 0, "Grouped Marker View");
        menu.add(0, v.getId(), 0, "Send Movement To Email");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Action 1") {
            Toast.makeText(this, "Action 1 invoked", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle() == "Action 2") {
            Toast.makeText(this, "Action 2 invoked", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle() == "Action 3") {
            Toast.makeText(this, "Action 3 invoked", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }

    List<Contact> pContacts;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)

        {

            if (type) {
                Intent intentMain = new Intent(MovementMapActivity.this,
                        MovementActivity.class);
                intentMain.putExtra("date",date.split(" ")[0]);
                MovementMapActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else {
                AppThemes t = new AppThemeProfile(getBaseContext()).get();
                if(t.getName().equalsIgnoreCase("Panic")) {
                    Intent intentMain = new Intent(MovementMapActivity.this,
                            MainActivity.class);
                    intentMain.putExtra("serviceState", true);
                    MovementMapActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();
                }else if(t.getName().equalsIgnoreCase("News")) {
                    Intent intentMain = new Intent(MovementMapActivity.this,
                            Main2Activity.class);
                    intentMain.putExtra("serviceState", true);
                    MovementMapActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();
                } else if(t.getName().equalsIgnoreCase("Normal")) {
                    Intent intentMain = new Intent(MovementMapActivity.this,
                            Main3Activity.class);
                    intentMain.putExtra("serviceState", true);
                    MovementMapActivity.this.startActivity(intentMain);
                    Log.i("Content ", " Menu layout ");
                    finish();
                }
            }

        } else {
            if (id == R.id.send_email)

            {

                if (type) {
                    sendEmail();
                } else {


                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(MovementMapActivity.this);
                    // builderSingle.setIcon(R.drawable.logo);
                    SpannableString s = new SpannableString("Select Panic Contact");
                    s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
                    builderSingle.setTitle(s);

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            MovementMapActivity.this,
                            android.R.layout.select_dialog_singlechoice);
                    pContacts = new ContactProfile(context).getAllContacts();
                    for (Contact contact : pContacts) {
                        String lastname = contact.getLastname();
                        if (lastname.equalsIgnoreCase("None")) {
                            lastname = "";
                        }
                        arrayAdapter.add(contact.getFirstname() + " " + lastname);

                    }

                    builderSingle.setNegativeButton(
                            "cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builderSingle.setAdapter(
                            arrayAdapter,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final int loc = which;
                                    System.out.println(loc + "------------------------------------------------------lllllllllllll");
                                    String strName = arrayAdapter.getItem(which);
                                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                            MovementMapActivity.this);

                                    SpannableString s = new SpannableString("Send Current Location");
                                    s.setSpan(new TypefaceSpan(MovementMapActivity.this, "exo_medium.otf"), 0, s.length(),
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    builderInner.setTitle(s);
                                    s = new SpannableString("Do you wish to send your current location to " + strName);
                                    s.setSpan(new TypefaceSpan(MovementMapActivity.this, "exo_medium.otf"), 0, s.length(),
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    builderInner.setMessage(s);
                                    builderInner.setPositiveButton(
                                            "Yes",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    Contact c = pContacts.get(loc);
                                                    if (c.getStatus().equalsIgnoreCase("2")) {
                                                        System.out.println("was here 0000000000000000000000000000000000000000");
                                                        sendSmsByManagerAddress(c.getPhoneNumber());
                                                    } else {
                                                        System.out.println("was here 11111111111111111111111111111111111111111");
                                                        sendSmsByManager(c.getPhoneNumber());
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
                            });
                    builderSingle.show();


                }

            } else if (id == R.id.delete) {

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.passcode_dialog);

                SpannableString s = new SpannableString("Access Authorization");
                s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
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
                        if(name.getText().toString().equalsIgnoreCase(user.getPassCode())) {
                            boolean delete = new MovementProfile(getBaseContext()).delete(MovementMapActivity.this.id);
                            if (delete) {

                                Toast.makeText(getBaseContext(), "Movement Record " + date + " deleted", Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(MovementMapActivity.this,
                                        MovementActivity.class);
                                intentMain.putExtra("date", date.split(" ")[0]);
                                MovementMapActivity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();

                            } else {
                                Toast.makeText(getBaseContext(), "failed", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(MovementMapActivity.this,"Incorrect Access Code",Toast.LENGTH_LONG).show();
                        }

                    }

                });

                dialog.show();

            } else if (id == R.id.reloadmap) {
                if(refreshState) {
                    mMap.clear();
                    setUpMap(locations, dates);
                    refreshState = false;
                    Toast.makeText(getBaseContext(),"Switched Single Marker Map View",Toast.LENGTH_SHORT).show();
                }else{
                    mMap.clear();
                    setUpMapGrouped(locations, dates);
                    refreshState = true;
                    Toast.makeText(getBaseContext(),"Switched Grouped Marker Map View",Toast.LENGTH_SHORT).show();
                }

            }//reloadmap
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendSmsByManager(String phoneNumber) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();


            smsManager.sendTextMessage(phoneNumber,
                    null,
                    "Mgps:" + lat + ":" + lng,
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + phoneNumber);
            Toast.makeText(getBaseContext(), "Current Location Sent", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }


    public void sendSmsByManagerAddress(String phoneNumber) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();


            smsManager.sendTextMessage(phoneNumber,
                    null,
                    "Mgps:" + address.getText().toString(),
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + phoneNumber);
            Toast.makeText(getBaseContext(), "Current Location Sent", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
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
                    locationAddress = getString(R.string.error_loc_latlng);
            }
            SpannableString s = new SpannableString(locationAddress);
            s.setSpan(new TypefaceSpan(MovementMapActivity.this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            address.setText(s);
            if (!locationAddress.equalsIgnoreCase(getString(R.string.error_loc_latlng))) {

                Movement movement = new MovementProfile(context).getMovementById(id);
                if (movement != null) {

                    movement.setAddress(locationAddress);
                    boolean state = new MovementProfile(context).update(movement);
                }
            }

        }
    }

    private final double degreesPerRadian = 180.0 / Math.PI;

    private void DrawArrowHead(GoogleMap mMap, LatLng from, LatLng to) {
        // obtain the bearing between the last two points
        double bearing = GetBearing(from, to);

        // round it to a multiple of 3 and cast out 120s
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the corresponding triangle marker from Google
        URL url;
        Bitmap image = null;
        int id = getResources().getIdentifier("dir_" + String.valueOf((int) adjBearing), "drawable", getPackageName());
        image = BitmapFactory.decodeResource(getResources(), id);
         /*Matrix matrix = new Matrix();
        double sLat = from.latitude - to.latitude;
        double sLng = from.longitude - to.longitude;
        double rotationDegrees = Math.toDegrees(Math.atan2(sLat, sLng));
        matrix.postRotate(((float) rotationDegrees));
        image = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_head);
        Bitmap.createBitmap(image, 0, 0,
                image.getWidth(), image.getHeight(), matrix, true);
       try {
            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
           System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
        if (image != null) {

            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
            float anchorX = 0.5f;
            float anchorY = 0.5f;

            int offsetX = 0;
            int offsetY = 0;

            // images are 24px x 24px
            // so transformed image will be 48px x 48px

            //315 range -- 22.5 either side of 315
            if (bearing >= 292.5 && bearing < 335.5) {
                offsetX = 24;
                offsetY = 24;
            }
            //270 range
            else if (bearing >= 247.5 && bearing < 292.5) {
                offsetX = 24;
                offsetY = 12;
            }
            //225 range
            else if (bearing >= 202.5 && bearing < 247.5) {
                offsetX = 24;
                offsetY = 0;
            }
            //180 range
            else if (bearing >= 157.5 && bearing < 202.5) {
                offsetX = 12;
                offsetY = 0;
            }
            //135 range
            else if (bearing >= 112.5 && bearing < 157.5) {
                offsetX = 0;
                offsetY = 0;
            }
            //90 range
            else if (bearing >= 67.5 && bearing < 112.5) {
                offsetX = 0;
                offsetY = 12;
            }
            //45 range
            else if (bearing >= 22.5 && bearing < 67.5) {
                offsetX = 0;
                offsetY = 24;
            }
            //0 range - 335.5 - 22.5
            else {
                offsetX = 12;
                offsetY = 24;
            }

            Bitmap wideBmp;
            Canvas wideBmpCanvas;
            Rect src, dest;

            // Create larger bitmap 4 times the size of arrow head image
            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());

            wideBmpCanvas = new Canvas(wideBmp);

            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            dest = new Rect(src);
            dest.offset(offsetX, offsetY);

            wideBmpCanvas.drawBitmap(image, src, dest, null);

            mMap.addMarker(new MarkerOptions()
                    .position(to)
                    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
                    .anchor(anchorX, anchorY));
        }
    }

    private double GetBearing(LatLng from, LatLng to) {
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = -Math.atan2(Math.sin(lon1 - lon2) * Math.cos(lat2), Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
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


                if (subject.getText().toString().isEmpty()) {
                    Toast.makeText(MovementMapActivity.this, "Subject Empty", Toast.LENGTH_LONG).show();
                } else {
                    String uid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    Mail m = new Mail("wiitech.mobile.guard@gmail.com", "millinik");

                    User user = new UserProfile(MovementMapActivity.this).get();
                    String[] toArr = {user.getEmail(), "wiitech.mobile.guard@gmail.com"};//
                    m.setTo(toArr);
                    m.setFrom("wiitech.mobile.guard@gmail.com");
                    m.setSubject("My Movement: " + subject.getText().toString());
                    StringBuilder panicMsg = new StringBuilder();

                    panicMsg.append("========================Mobile Guard Unit #" + uid + "=================\n");
                    panicMsg.append("My Movement Log\n");
                    panicMsg.append("=================================================================================\n");
                    int l = 0;
                    for (Movement movement : movementList) {

                        panicMsg.append("Movement Date: " + dates.get(l));
                        panicMsg.append("\n");
                        panicMsg.append("Movement Location: " + movement.getLat() + "," + movement.getLng());
                        panicMsg.append("\n");
                        panicMsg.append("Movement Address: " + movement.getAddress());
                        panicMsg.append("\n");
                        panicMsg.append("=================================================================================\n");

                        l++;
                    }
                    m.setBody(panicMsg.toString());

                    try {

                        if (m.send()) {
                            Toast.makeText(MovementMapActivity.this, "Movement Log mailed successfully.", Toast.LENGTH_LONG).show();

                            dialog.cancel();
                            Log.i("Content ", " Menu layout ");
                        } else {
                            Toast.makeText(MovementMapActivity.this, "Panic Log mailing failed", Toast.LENGTH_LONG).show();
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
}
