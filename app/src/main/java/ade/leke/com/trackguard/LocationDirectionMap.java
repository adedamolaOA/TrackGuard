package ade.leke.com.trackguard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.HttpConnection;
import ade.leke.com.trackguard.common.LocationAddress;
import ade.leke.com.trackguard.common.PathJSONParser;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;

public class LocationDirectionMap extends ActionBarActivity {

    private LatLng MY_LOCATION;
    private LatLng CONTACT_LOCATION;

    Polyline line;
    double clatitude = 0;
    double clongitude = 0;
    GoogleMap mMap;
    final String TAG = "PathGoogleMapActivity";
    TextView lblCurrentLocationAddress;
    TextView lblDestinationLocationAddress;
    TextView lblDestinationOutput;
    double dlat;//Double.parseDouble(clat);
    double dlng;//Double.parseDouble(clng);
    String name;
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
        setContentView(R.layout.activity_location_direction_map);
        Intent intent = getIntent();
        String clat = intent.getStringExtra("lat");
        String clng = intent.getStringExtra("lng");
        dlat = Double.parseDouble(clat);
        dlng = Double.parseDouble(clng);

        context = getBaseContext();
        name = intent.getStringExtra("name");
        SpannableString s = new SpannableString(name + "'s Location");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        lblDestinationOutput = (TextView) findViewById(R.id.lblDestinationOutput);
        lblCurrentLocationAddress = (TextView) findViewById(R.id.lblCurrentLocationAddress);
        lblDestinationLocationAddress = (TextView) findViewById(R.id.lblDestinationLocationAddress);
        lblDestinationLocationAddress.setText(name + "'s Location Address");
        //TextView lblLocMapLat = (TextView) findViewById(R.id.lblLocMapLat);
       // TextView lblLocMapLng = (TextView) findViewById(R.id.lblLocMapLng);

        //lblLocMapLat.setText();
        //lblLocMapLng.setText();
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppLocationService appLocationService;
        appLocationService = new AppLocationService(
                LocationDirectionMap.this);

        double clatitude = 0;
        double clongitude = 0;
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            clatitude = gpsLocation.getLatitude();
            clongitude = gpsLocation.getLongitude();


        }

        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(dlat, dlng,
                getApplicationContext(), new GeocoderHandler());

        LocationAddress locationAddressw = new LocationAddress();
        locationAddressw.getAddressFromLocation(clatitude, clongitude,
                getApplicationContext(), new MyGeocoderHandler());
        MY_LOCATION = new LatLng(clatitude, clongitude);
        CONTACT_LOCATION = new LatLng(dlat, dlng);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapL))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        MarkerOptions options = new MarkerOptions();
        options.position(MY_LOCATION);
        options.position(CONTACT_LOCATION);

        mMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();


            String urlTopass = makeURL(MY_LOCATION.latitude,
                    MY_LOCATION.longitude, CONTACT_LOCATION.latitude,
                    CONTACT_LOCATION.longitude);

        downloadTask.execute(urlTopass);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MY_LOCATION,
                12));
        addMarkers();
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;

        }
    }

    public void drawPath(String result) {
        if (line != null) {
            mMap.clear();
        }
        mMap.addMarker(new MarkerOptions().position(MY_LOCATION));
        mMap.addMarker(new MarkerOptions().position(CONTACT_LOCATION));//.icon(
        //BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)
        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            line = mMap.addPolyline(options);

            /*for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line = myMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(5).color(Color.BLUE).geodesic(true));
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_direction_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            Intent intentMain = new Intent(LocationDirectionMap.this,
                    NotificationActivity.class);
            LocationDirectionMap.this.startActivity(intentMain);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private String getMapsApiDirectionsUrl() {
        /*String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";*/
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + clatitude + "," + clongitude + "&destination=" + dlat + "," + dlng + "&%20waypoints=optimize:true|"+ clatitude + "," + clongitude +"||"+ dlat + "," + dlng +"&sensor=false";//"https://maps.googleapis.com/maps/api/directions/"
        //+ output + "?" + params;
        return url;
    }

    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(MY_LOCATION)
                    .title("My Location"));
            mMap.addMarker(new MarkerOptions().position(CONTACT_LOCATION)
                    .title(name + "'s Location"));

        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            try {
                ArrayList<LatLng> points = null;
                PolylineOptions polyLineOptions = null;

                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        System.out.println("---------------------------------------------------" + lat + " " + lng);
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(2);
                    polyLineOptions.color(Color.BLUE);
                }

                mMap.addPolyline(polyLineOptions);
            }catch (Exception e){
                Toast.makeText(LocationDirectionMap.this,"Unable to retrieve routes",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
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
            lblDestinationOutput.setText(locationAddress);

        }
    }

    private class MyGeocoderHandler extends Handler {
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
            lblCurrentLocationAddress.setText(locationAddress);

        }
    }
}
